import akka.actor.{ActorSystem, ActorRef, Actor, Props,actorRef2Scala,PoisonPill,ActorLogging}
import collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.DurationInt

case object pass

object utils{
   val md = java.security.MessageDigest.getInstance("SHA-1")
  def getUID(length:Int,m:Int):Int={
      val R_string:String = scala.util.Random.alphanumeric.take(length).mkString
      val R_sub = (md.digest(R_string.getBytes("UTF-8")).map("%02x".format(_)).mkString).substring(34,40)
      var IntID = (Integer.parseInt(R_sub,16)%(scala.math.pow(2,m))).toInt
      IntID
    }
}

// represents a Node in the finger-table--contains start_uid and both node UID ,ActorRef for the successor
class finger_table_entry(finger_start:Int,node_uid:Int,node_ref:ActorRef)
{
  var start = finger_start
  var uid = node_uid
  var ref = node_ref
}

object project3 {
  def main (args:Array[String]){
    if (args.length < 2)
    {
      println("Please enter <num of nodes> <num of requests>")
      return
    }
    else
    {
      val numNodes = args(0).toInt
      val numRequests = args(1).toInt
      // get the min value of m that can support a network with this number of nodes.
      // which would tell us the size of finger table to maintain.
      val min_m = (scala.math.ceil(scala.math.log(numNodes)/scala.math.log(2))).toInt
      // also for the simulation we have to ensure that every node has a unique identifier.
      // to this extent we scale min_m by a scaling factor 'k'.
      val scaling_factor = 1
      val m = scaling_factor * min_m
      // now it is time to generate unique ID's for every node.
      // we use a hash map to ensure uniqueness.
      val UID = new HashMap[Int,Boolean]()  { override def default(key:Int) = false }
      //generating UIDS for numNodes
      var rs = -9999 // initial value
      for (i <- 1 to numNodes)
      {
        do
        {
          rs = utils.getUID(50,m)
        }while (UID(rs)!=false)
          UID+=(rs->true)
      }
      // just print all valid keys in the map
      // MILESTONE 1
      //UID.foreach{case(key,value)=> {if (UID(key)==true) print(key)}}
      var UIDList = ArrayBuffer[Int]()
      // UIDList contains all valid UIDs
      UID.foreach{case(key,value)=> {if (UID(key)==true) UIDList+=key}}
      // Initialize Network
      // Set up an actor system with actors representing nodes in the network
      val actor_system = ActorSystem("ChordNetwork")
      // Collection for references of nodes in the network
      var myNodes = ArrayBuffer[ActorRef]()
      // Set up individual Actors
      for (i<-UIDList)
      {
        myNodes+=actor_system.actorOf(Props(new Node(i,m)),i.toString())
      }
      val Manager = actor_system.actorOf(Props(new Net_Manager(myNodes)),"Manager")
      Manager ! pass
    }
  }
}
/*This class manages the initialization and teardown of the simulated network*/
class Net_Manager(Nodelist:ArrayBuffer[ActorRef]) extends Actor
{  
  def receive = 
  {
    case `pass` =>
      {
        println("exiting")
        context.system.shutdown()
      }
  }
  
}
/*This class represents a Chord node in the network*/
class Node(uid_uid:Int,m_m:Int) extends Actor
{
  val UID = uid_uid
  val m = m_m
  var predecessor:ActorRef = null
  var pred_uid:Int = -9999
  // construct finger table
  var finger_table = ArrayBuffer[finger_table_entry]()
  var start = -9999
  for (i <- 1 to m)
  {
    var start = ((UID+scala.math.pow(2,i-1).toInt)%(scala.math.pow(2,m))).toInt
    finger_table += new finger_table_entry(start,-9999,null)
  }
  //Print initial finger table for the node
  println("*****NODE "+UID.toString()+"******")
  for (i <-1 to m)
  {
    println(finger_table(i-1).start)
    println(finger_table(i-1).uid)
  }
  def receive = 
  {
    case `pass` =>
      {
        
      }
  }
}

