package akka

import java.security.MessageDigest
import java.util.EnumMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration

import akka.TeacherProtocol._
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.pattern.AskTimeoutException
import akka.pattern.ask
import akka.util.Timeout
import scala.collection.mutable.HashMap


object STAGE extends Enumeration {
   val CLIENT_INITIATE,REQ_RECEIVED,RESP_GENERATED,SERVICE_EXCEPTION,RESP_RECEIVED,FUTURE_TIMEOUT,ASK_TIMEOUT =Value 
}



object StudentSimulatorApp extends App {


  var incidents:HashMap[STAGE.Value,Int] = HashMap[STAGE.Value,Int]()  
  for (stage <- STAGE.values) incidents.put(stage,0);
  
  
  
  println(incidents.size);
  
  def hash(inputStr: String): String = {
    val md: MessageDigest = MessageDigest.getInstance("MD5")
    md.digest(inputStr.getBytes()).map(0xFF & _).map { "%02x".format(_) }.foldLeft("") { _ + _ }
  }

  println(hash("Hello"))

  //Initialize the ActorSystem

  /*
   * This is the entry point into the actor system - actors are created, destroyed etc through
   * this system.
   * 
   * The actor system is the root of all actors
   * 
   */
  val actorSystem = ActorSystem("UniversityMessageSystem")

  /*
   * This will be just one level below the ActorSystem - top level actor
   * ActorRef acts as a proxy for the actual actors.
   * 
   */
  //construct the Teacher Actor Ref
  val teacherActorRef = actorSystem.actorOf(Props[TeacherActor])

  //send a message to the Teacher Actor
  teacherActorRef ! QuoteRequest("please give me a godam quote")

  /*
   * the ! is the same as the "tell" method
   */

  teacherActorRef.tell(QuoteRequest("please give me a godam quote NOW"), null)

  /*
  for (i <-1 to 10) 
  {
    teacherActorRef.tell(QuoteRequest("please give me a godam quote NOW"),null)
  }
  */

 // println("About to do the await thing")

  //Let's wait for a couple of seconds before we shut down the system
  Thread.sleep(2000)

  //println("Goodbye")

  def incrementEvent(stage:STAGE.Value){
    
    val i:Int=incidents.getOrElse(stage,0)+1
    
    incidents.put(stage,i)
  }
  
  
  for (i <- 1 to 10){
    
    
    
    val r = QuoteRequest("please give me a godam quote WITHIN 5s   ")
   
    println("REQ(" + r.id + ") "+STAGE.CLIENT_INITIATE)
    incrementEvent(STAGE.CLIENT_INITIATE)
    
    try {

      val future = teacherActorRef.ask(r)(new Timeout(new FiniteDuration(4, TimeUnit.SECONDS)));

      val resp: QuoteResponse = Await.result(future, Duration.apply("5 seconds")).asInstanceOf[QuoteResponse]

      println("REQ(" + r.id + ") "+STAGE.RESP_RECEIVED+ " RESP(" + resp.id+")=>"+resp.request.id)
      incrementEvent(STAGE.RESP_RECEIVED)
      //println("Managed to get back from the Teacher into the main " + resp.quoteString)

    } catch {
      case te: TimeoutException    => 
        {
          incrementEvent(STAGE.FUTURE_TIMEOUT)
          println("REQ(" + r.id + ") "+STAGE.FUTURE_TIMEOUT)
        }
      case ae: AskTimeoutException => 
        {
          incrementEvent(STAGE.ASK_TIMEOUT)
          println("REQ(" + r.id + ") "+STAGE.ASK_TIMEOUT)
        }
    }
  }
  
  println("-----SUMMARY----")
  for ((k,v) <- incidents) println(k +"   " +v)
  
  
  /*
  * Note there are two timeouts here - one on the future variable and one on the Await call
  * 
  * 
  
 	 If the FUTURE/ASK one times out the you get
 	 
  	Exception in thread "main" akka.pattern.AskTimeoutException: Ask timed out on [Actor[akka://UniversityMessageSystem/user/$a#418942702]] after [5000 ms]. Sender[null] sent message of type "akka.TeacherProtocol$QuoteRequest".
	at akka.pattern.PromiseActorRef$$anonfun$1.apply$mcV$sp(AskSupport.scala:604)
	at akka.actor.Scheduler$$anon$4.run(Scheduler.scala:126)
	at scala.concurrent.Future$InternalCallbackExecutor$.unbatchedExecute(Future.scala:601)
	at scala.concurrent.BatchingExecutor$class.execute(BatchingExecutor.scala:109)
	at scala.concurrent.Future$InternalCallbackExecutor$.execute(Future.scala:599)
	at akka.actor.LightArrayRevolverScheduler$TaskHolder.executeTask(LightArrayRevolverScheduler.scala:329)
	at akka.actor.LightArrayRevolverScheduler$$anon$4.executeBucket$1(LightArrayRevolverScheduler.scala:280)
	at akka.actor.LightArrayRevolverScheduler$$anon$4.nextTick(LightArrayRevolverScheduler.scala:284)
	at akka.actor.LightArrayRevolverScheduler$$anon$4.run(LightArrayRevolverScheduler.scala:236)
	at java.lang.Thread.run(Thread.java:745)
  
  If the AWAIT one times out you get
  
  Exception in thread "main" java.util.concurrent.TimeoutException: Futures timed out after [5 seconds]
	at scala.concurrent.impl.Promise$DefaultPromise.ready(Promise.scala:219)
	at scala.concurrent.impl.Promise$DefaultPromise.result(Promise.scala:223)
	at scala.concurrent.Await$$anonfun$result$1.apply(package.scala:190)
	at scala.concurrent.BlockContext$DefaultBlockContext$.blockOn(BlockContext.scala:53)
	at scala.concurrent.Await$.result(package.scala:190)
	at akka.StudentSimulatorApp$.delayedEndpoint$akka$StudentSimulatorApp$1(StudentSimulatorApp.scala:67)
	at akka.StudentSimulatorApp$delayedInit$body.apply(StudentSimulatorApp.scala:18)
	at scala.Function0$class.apply$mcV$sp(Function0.scala:34)
	at scala.runtime.AbstractFunction0.apply$mcV$sp(AbstractFunction0.scala:12)
	at scala.App$$anonfun$main$1.apply(App.scala:76)
	at scala.App$$anonfun$main$1.apply(App.scala:76)
	at scala.collection.immutable.List.foreach(List.scala:381)
	at scala.collection.generic.TraversableForwarder$class.foreach(TraversableForwarder.scala:35)
	at scala.App$class.main(App.scala:76)
	at akka.StudentSimulatorApp$.main(StudentSimulatorApp.scala:18)
	at akka.StudentSimulatorApp.main(StudentSimulatorApp.scala)
  
  
  
  */

  // Let's see if we can get a unique messageID for the messages

  //http://stackoverflow.com/questions/16542335/akka-receive-reference-to-message

  //Shut down the ActorSystem.
  actorSystem.terminate()

  def sayHello() {

  }

  def sayHello1() {

  }

}