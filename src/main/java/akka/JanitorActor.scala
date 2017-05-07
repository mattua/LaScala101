package akka

import scala.util.Random

import akka.actor.Actor
import akka.TeacherProtocol.QuoteRequest
import akka.TeacherProtocol.QuoteResponse
import akka.actor.ActorLogging

class JanitorActor extends Actor with ActorLogging  {
  
  
  
  def receive() = {
 
    
    case msg:QuoteRequest => {

      //Get a random Quote from the list and construct a response
      //val quoteResponse=QuoteResponse("And tell that pupil of yours I just got done mopping the floor",msg)

      
      //log.info(quoteResponse.toString())
     // println (quoteResponse)
	    
       // val quoteResponse2=QuoteResponse("And tell that pupil of yours I just got done mopping the floorrrrrr",msg)

      
    
    }

  }

}