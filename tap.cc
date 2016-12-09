#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <net/if.h>
#include <linux/if_tun.h>
#include <net/route.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <iostream>
#include<sstream>
#include<string>
#include<signal.h>

using  std::cout;
using  std::cin;
using  std::endl;
using std::string;
#define TUN_PATH "/dev/net/tun"

static struct ifreq ifr;
static int fd = -1;
static int ipv4_configuration_socket = -1;
static int tun_fd = -1;



/*
network data structures
http://beej.us/guide/bgnet/output/html/multipage/sockaddr_inman.html
*/

int tap_open(const char* device, char* mac_addr)
{
    if ((fd = open(TUN_PATH,O_RDWR))<0)
    {
        cout << "opening " << TUN_PATH << " failed, are we not root?"<<endl;
        return -1;
    }
    ifr.ifr_flags = IFF_TAP|IFF_NO_PI;
    if (strlen(device)>= IFNAMSIZ)
    {
        cout << "device name is longer than prescribed limit" << IFNAMSIZ-1 <<endl; 
        return -1;
    }
    strcpy(ifr.ifr_name,device);
    //create the device
    if (ioctl(fd,TUNSETIFF,(void *)&ifr)<0)
    {
        cout << "could not create tun interaface are we not root." << endl;
        return -1;
    }
    
    //create a socket to be used when retrieving mac address from the kernel
    if ((ipv4_configuration_socket = socket(AF_INET,SOCK_DGRAM,0)) < 0)
    {
        cout << "socket construction failed "<< endl;
        return -1;
    }
    //get hardware/mac address of thr device, gets written to ifr.ifr_hwaddr
    //http://www.microhowto.info/howto/get_the_mac_address_of_an_ethernet_interface_in_c_using_siocgifhwaddr.html
    if ((ioctl(ipv4_configuration_socket,SIOCGIFHWADDR, &ifr))<0)
    {
        cout << "could not read device mac address "<<endl;
        return -1;
    }
    
    memcpy(mac_addr,ifr.ifr_hwaddr.sa_data,6);
    return fd;
}



void tap_plen_to_ipv4_mask(unsigned int prefix_len, struct sockaddr *writeback)
{
    uint32_t net_mask_int = ~(0u) << (32- prefix_len);
    // I haven't tested this on a big endian system, but I believe this is an
    // endian-related issue. If this suddenly fails on Android, this line might
    // be why:
    
    net_mask_int = htonl(net_mask_int);
    struct sockaddr_in netmask = {
            .sin_family = AF_INET,
            .sin_port = 0
            };
    struct in_addr netmask_addr = {.s_addr = net_mask_int};
    
    netmask.sin_addr =  netmask_addr;
    //wrap sockaddr_in into sock_addr struct
    memcpy(writeback, &netmask, sizeof(struct sockaddr));
}

int tap_set_ipv4_addr(const char* ipaddr, unsigned int prefix_len, char *my_ipv4)
{
    struct sockaddr_in socket_address = { 
                    .sin_family = AF_INET,
                    .sin_port = 0
                    };
    //convert ipv4 address provided into binary order
    //address in binnary network order is populated in .sin_addr
    //by this call.
    if (inet_pton(AF_INET, ipaddr, &socket_address.sin_addr) != 1) {
        cout << "inet_pton failed (Bad ipv4 addresss format?)"<<endl;
        return -1;
    }
    
    //wrap sockaddr_in struct to sockaddr struct, which is used conventionaly for system calls
    memcpy(&ifr.ifr_addr, &socket_address, sizeof(struct sockaddr));
    
    //copies ipv4 address to my my_ipv4. ipv4 address starts at sa_data[2]
    //and terminates at sa_data[5]
    memcpy(my_ipv4, &ifr.ifr_addr.sa_data[2],4);
    
    //configure tap device with a ipv4 address
    if (ioctl(ipv4_configuration_socket, SIOCSIFADDR, &ifr)< 0)
    {
        cout << "failed to set IPv4 address to tap." <<endl;
        return -1;
    }
    
    //get subnet mask intoi network byte order from int representation
    tap_plen_to_ipv4_mask(prefix_len, &ifr.ifr_netmask);
    
    //configure the tap device with a netmask
    if (ioctl(ipv4_configuration_socket, SIOCSIFNETMASK, &ifr) < 0)
    {
        cout <<" failed to set ipv4 netmask on tap device" << endl;
        return -1;
    }
    
    return 0;
  
     
}

/* 
    * Tells the OS to route IPv4 addresses within the subnet (determined by the
    * `presentation` and `prefix_len` args, see tap_set_ipv4_addr) through us. A
    * priority is given by metric. The Linux kernel's default metric value is 256
    * for subnets and 1024 for gateways.
*/
 
int tap_set_ipv4_route(const char* ipv4_addr, unsigned short prefix_len, unsigned int metric)
{
    struct rtentry rte = {};  
    rte.rt_flags = RTF_UP; rte.rt_dev = ifr.ifr_name; rte.rt_metric = metric;  
    tap_plen_to_ipv4_mask(prefix_len,&rte.rt_genmask);
    
    if (inet_pton(AF_INET,ipv4_addr, &rte.rt_dst) != 1)
    {
        cout << " inet_pton failed (Bad IPv4 address format ?)" << endl;
        return -1;
    }
    
    //when mask is the whole address
    if (prefix_len == 32) rte.rt_flags |= RTF_HOST;
    
    if (ioctl(ipv4_configuration_socket, SIOCADDRT, &rte)< 0)
    {
        cout << "could not add back route" << endl;
        return -1;
    }
    return 0;
}

/**
 * Given some flags to enable and disable, reads the current flags for the
 * network device, and then ensures the high bits in enable are also high in
 * ifr_flags, and the high bits in disable are low in ifr_flags. The results are
 * then written back. For a list of valid flags, read the "SIOCGIFFLAGS,
 * SIOCSIFFLAGS" section of the 'man netdevice' page. You can pass `(short)0` if
 * you don't want to enable or disable any flags.
 */
 
 int tap_set_flags(short enable, short disable)
 {
    if (ioctl(ipv4_configuration_socket, SIOCGIFFLAGS, &ifr)<0)
    {
        cout << "could not read device flags for TAP device, (is the device \
        not open)" << endl;
        return -1;
    }
    //set or unset the right flags
    ifr.ifr_flags |= enable; 
    ifr.ifr_flags &= ~disable;
    //write back the modified flag states
    if (ioctl(ipv4_configuration_socket, SIOCSIFFLAGS, &ifr)<0)
    {
        cout << "Could not write back device flags for TAP device. \
                        (Are we not root?) "<< endl;
        return -1;
    }
    
    return 0;
 }
 
 /* enable the tap device as UP*/
 int tap_set_up()
 {
    return tap_set_flags(IFF_UP | IFF_RUNNING, (short) 0);
 }
 
 /**
 * Sets the maximum supported packet size for a device. IPv6 requires a minimum
 * MTU of 1280, so keep that in mind if you plan to use IPv6. Additionally,
 * real-world ethernet connections typically have an MTU of 1500, so it might
 * not make sense to make the MTU above that if you are sending data from the
 * TAP device over ethernet, because then packet fragmentation would be
 * required, degrading performance.
 */
int tap_set_mtu(int mtu)
{
    ifr.ifr_mtu = mtu;
    if (ioctl(ipv4_configuration_socket, SIOCSIFMTU, &ifr) < 0) {
        cout << "Set MTU failed" << endl; 
        return -1;
    }
    return 0;
}

/**
 * Sets and unsets some common flags used on a TAP device, namely, it sets the
 * IFF_NOARP flag, and unsets IFF_MULTICAST and IFF_BROADCAST. Notably, if
 * IFF_NOARP is not set, when using an IPv6 TAP, applications will have trouble
 * routing their data through the TAP device (Because they'd expect an ARP
 * response, which we aren't really willing to provide).
 */
int tap_set_base_flags()
{
    //return tap_set_flags(IFF_NOARP, IFF_MULTICAST | IFF_BROADCAST);
    return tap_set_flags(IFF_NOARP, (short)0);
}

void sig_io(int sig, siginfo_t* sig_info, void * context)
{
    char buf[1500];
    cout << "file descriptor "<< sig_info->si_fd << "is ready for read" << endl;
    cout << POLL_IN << " "<< POLL_OUT << " " << POLL_MSG << " " << POLL_ERR << " " << POLL_PRI << " " << POLL_HUP << endl;
    cout << sig_info->si_code << ", " << POLL_IN << ", " << POLL_OUT<< endl;
    // Is data input available on the buffers
    if (sig_info->si_code == POLL_IN)
    {
        unsigned int r = 0;
        r = read(tun_fd, buf, sizeof(buf));
        if (r > 0)
                cout << "read "<< r << " bytes from the tap. " <<endl;
    }
    //Are the output buffers available
    if (sig_info->si_code == POLL_OUT) 
    {   
        cout << "I can write into the tap buffer !!" << endl;
    }
            
    
    
}


int main()
{
    cout << "Hello, going to create a tap device." << endl;
    char tun_name[IFNAMSIZ];
    char data_read[1500];
    unsigned int bytes_read = 0;
    char myip[4];
    char* eth_addr = (char*) malloc(sizeof(char)*6);
    strcpy(tun_name,"myTap");
    tun_fd = tap_open(tun_name, eth_addr);
    cout << "file descriptor of opened tap device is " << tun_fd << endl;
    tap_set_ipv4_addr("192.168.1.30", 24, myip);
    tap_set_mtu(1500);
    tap_set_base_flags();
    tap_set_up();
    tap_set_ipv4_route("192.168.1.30",24,256);
    struct sigaction sa;
    fcntl(tun_fd,F_SETFL, O_NONBLOCK | O_ASYNC);
    memset(&sa, 0, sizeof(sa));
    sa.sa_sigaction = sig_io;
    sa.sa_flags = SA_SIGINFO;
    if (sigaction(SIGPOLL, &sa, NULL) == 0)
    {
        cout << "successfuly regsiterd IO handler for the tap device" << endl;
    }
    while(1)
    {
        sleep(1000);
        
        
    }
    
}
