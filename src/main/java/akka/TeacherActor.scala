package akka

import scala.util.Random

import akka.actor.Actor
import akka.TeacherProtocol.QuoteRequest
import akka.TeacherProtocol.QuoteResponse
import akka.actor.ActorLogging

class TeacherActor extends Actor with ActorLogging  {
  
  val quotes = List(
    "Moderation is for cowards",
    "Anything worth doing is worth overdoing",
    "The trouble is you think you have time",
    "You never gonna know if you never even try")

  
   
  def receive = {

    
    case QuoteRequest => {

      //Get a random Quote from the list and construct a response
      val quoteResponse=QuoteResponse(quotes(Random.nextInt(quotes.size)))

      log.info(quoteResponse.toString())
      //println (quoteResponse)
	
    
    }

  }

}