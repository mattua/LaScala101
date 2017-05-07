package akka

import akka.actor.ActorSystem  
import akka.actor.Props  
import akka.actor.actorRef2Scala  
import akka.TeacherProtocol._


object StudentSimulatorApp extends App {
  
  //Initialize the ActorSystem
  val actorSystem=ActorSystem("UniversityMessageSystem")

  //construct the Teacher Actor Ref
  val teacherActorRef=actorSystem.actorOf(Props[TeacherActor])

  //send a message to the Teacher Actor
  teacherActorRef!QuoteRequest

  //Let's wait for a couple of seconds before we shut down the system
  Thread.sleep (2000) 

  //Shut down the ActorSystem.
  actorSystem.terminate()
  
  def sayHello(){
    
  }
  
  def sayHello1(){
    
  }
  
  
}