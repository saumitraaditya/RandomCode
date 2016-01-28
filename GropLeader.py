#!/usr/bi/env python
import controller.framework.fxlib as fxlib
from controller.framework.ControllerModule import ControllerModule

class NetworkLeaderElector(ControllerModule):
    def __init__(self, CFxHandle, paramDict):
        super(NetworkLeaderElector,self).__init__()
        self.CFxHandle = CFxHandle
        self.CMConfig = paramDict
        self.peers = {} # populated after notifcations from tincan.
        self.uid = "" 
        self.ip4 = ""
        
    ############################################################################
    #   Messaging functions-Adopted from BaseTopologyManager                   #
	############################################################################
	
	# Send message over XMPP
	# - msg_type -> message type attribute    
	# - uid -> UID of destination node
	# - mag -> message
	def send_msg_srv(self, msg_type,uid, msg):
	    cbtdata = {"method": msg_type, "overlay_id":1, "uid":uid, "data": msg}
	    self.registerCBT('TincanSender', 'DO_SEND_MSG', cbtdata)
	    
    # Send message through ICC
    # - uid -> UID of destination peer (a tincan link must exist)
    # - msg -> message
    def send_msg_icc(self, uid, msg):
        if uid in self.peers:
            if "ip6" in self.peers[uid]:
                cbtdata = { 
                            }                                                         
