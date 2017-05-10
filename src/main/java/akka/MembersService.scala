package akka

import scala.concurrent.Future
import akka.actor.TypedProps
import java.util.concurrent.TimeUnit
import akka.actor.ActorSystem
import scala.concurrent.duration.FiniteDuration
import akka.actor.TypedActor
import akka.util.Timeout
import akka.actor.Props
import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import scala.concurrent.duration.Duration
import scala.concurrent.Await

//http://doc.akka.io/docs/akka/current/scala/typed-actors.html
object MembersService extends App {

  // loads from application.conf

  print("Hello")

  val system = ActorSystem("MembersService", ConfigFactory.load.getConfig("MembersService"))

  val worker1 = system.actorOf(Props[Worker], "remote-worker1")
  val worker2 = system.actorOf(Props[Worker], "remote-worker2")

  println(s"Worker actor path is ${worker1.path}")

  import Worker._
  worker1 ! Work("START worker 1")
  worker2 ! Work("START worker 2")

  val mySquarer3: Squarer =
    TypedActor(system).typedActorOf(TypedProps[SquarerImpl](), "remote-worker3")

  println(Await.result(mySquarer3.square(5), Duration.apply("5 seconds")).asInstanceOf[Int])

  Thread.sleep(90000)

  system.terminate();

}

object MembersServiceLookup extends App {

  println("Hello lookup")

  val system = ActorSystem("MembersServiceLookup"
      ,ConfigFactory.load.getConfig("MembersServiceLookup") // we do need the config, even on client side
      )

  val worker1 = system.actorSelection("akka.tcp://MembersService@127.0.0.1:5150/user/remote-worker1") // the local actor

  val worker3 = system.actorSelection("akka.tcp://MembersService@127.0.0.1:5150/user/remote-worker3") // the local actor

  println("about to contact the remote actor")
  worker1 ! Worker.Work("Hi remote Actor")

  println(Await.result(worker1.ask(Worker.Work("Hi remote Actor"))(new Timeout(new FiniteDuration(4, TimeUnit.SECONDS))), Duration.apply("5 seconds")).asInstanceOf[Worker.WorkResp])

  /* 
   This is a great result - we have got an end to end akka remoting example working
   with typed actors - 
      
   */
  val mySquarer3: Squarer =
    TypedActor(system).typedActorOf(TypedProps[SquarerImpl](), system.actorFor("akka.tcp://MembersService@127.0.0.1:5150/user/remote-worker3"))

  println("Got typed remote response from " + Await.result(mySquarer3.square(6), Duration.apply("5 seconds")).asInstanceOf[Int])

}

class Worker extends Actor {

  import Worker._

  def receive = {
    case msg: Work =>

      context.sender() ! Worker.WorkResp("Acknowledge")

      // s enables that funny dollar sign thing
      println(s"Work work work work work  ${self}")
  }

}

object Worker {
  case class Work(message: String)
  case class WorkResp(message: String)
}
