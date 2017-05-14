package akka

import akka.actor.TypedActor.MethodCall

object TeacherProtocol {

  // Get next ID
  object ReqIDGenerator {
    private val n = new java.util.concurrent.atomic.AtomicLong

    def next = n.getAndIncrement()
  }

  object IncidentCounter {
    private val n = new java.util.concurrent.atomic.AtomicLong

    def next = n.getAndIncrement()
  }
  
  object RespIDGenerator {
    private val n = new java.util.concurrent.atomic.AtomicLong

    def next = n.getAndIncrement()
  }


  case class QuoteRequest(request: String) {

    val id: Long = ReqIDGenerator.next
  }

  // we can bind the response to the request
  case class QuoteResponse(quoteString: String, request: QuoteRequest) {

    val id: Long = RespIDGenerator.next

  }
  
  case class MethodRequestEnvelope(methodCall:MethodCall){
    
    val id: Long = RespIDGenerator.next
    
  }
  
  case class MethodResponseEnvelope(methodCall:MethodCall){
    
    val id: Long = RespIDGenerator.next
    
  }
  
  
  
  
  

}