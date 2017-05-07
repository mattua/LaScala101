package akka



import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import scala.util.Random
import akka.TeacherProtocol.QuoteResponse
import akka.TeacherProtocol.QuoteRequest


class TeacherActor extends Actor with ActorLogging {
  
  val quotes = List(
    "Moderation is for cowards",
    "Anything worth doing is worth overdoing",
    "The trouble is you think you have time",
    "You never gonna know if you never even try")

  
  var counter = 0;
    
  // the receive method gets called when you tell a message to an actor ref
  def receive() = {
 
    // this defines what types of messages this Actor can handle - nothing else
    
   // This is a nicer way of getting a reference to the message
    case msg:QuoteRequest => {

      val t1 = System.currentTimeMillis
      println("REQ("+msg.id +") "+STAGE.REQ_RECEIVED)
      
      
      //Get a random Quote from the list and construct a response
      val quoteResponse=QuoteResponse(quotes(Random.nextInt(quotes.size)),msg)

      counter=counter+1;
      
     // println("received: " +msg.request+ counter)
      //log.info(quoteResponse.toString())
     // println (quoteResponse)
	
      /* not the actor system is implicitly available as "context"*/
     val janitorActor = context.actorOf(Props[JanitorActor])
     janitorActor!QuoteRequest("where is my pupil did you see her in the corridor")
     
     val returnValue = quoteResponse
     
     Thread.sleep(Math.round(Math.random()*5000))
     
     if (Math.random()<0.4){
       println("REQ("+msg.id +") "+STAGE.SERVICE_EXCEPTION)
       throw new Exception("Random Screwup");
     }
     
     
     // KABOOM - this is how we send the result back to the sender.
     
      val t2 = System.currentTimeMillis
      
      val duration:Double = (t2-t1)/1000d
     println("REQ("+msg.id +") "+STAGE.RESP_GENERATED +" RESP("+returnValue.id+") => "+returnValue.request.id+" in "+duration+" s")
     sender()!returnValue
     
    }

  }

}