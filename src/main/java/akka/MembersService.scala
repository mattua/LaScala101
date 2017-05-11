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
import akka.actor.ActorRef

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

  val mySquarer3: MySquarer =
    TypedActor(system).typedActorOf(TypedProps[MySquarerImpl](), "remote-worker3")

  println(Await.result(mySquarer3.square(5), Duration.apply("5 seconds")).asInstanceOf[Int])

  Thread.sleep(90000)

  system.terminate();

}

object MembersServiceLookup extends App {

  println("Hello lookup")

  val system = ActorSystem("MembersServiceLookup", ConfigFactory.load.getConfig("MembersServiceLookup") // we do need the config, even on client side
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
  val mySquarer3: MySquarer =
    TypedActor(system).typedActorOf(TypedProps[MySquarerImpl](), system.actorFor("akka.tcp://MembersService@127.0.0.1:5150/user/remote-worker3"))
  println("Got typed remote response from " + Await.result(mySquarer3.square(6), Duration.apply("5 seconds")).asInstanceOf[Int])

    
  // this is the better way to do it since actorFor is deprecated and this one basically ensures only one remote actorRef is returned
  val worker4: ActorRef = Await.result(worker3.resolveOne(new FiniteDuration(4, TimeUnit.SECONDS)), new FiniteDuration(4, TimeUnit.SECONDS))
  val mySquarer4: MySquarer =
    TypedActor(system).typedActorOf(TypedProps[MySquarerImpl](), worker4)

  println("Got typed remote response from " + Await.result(mySquarer4.square(7), Duration.apply("5 seconds")).asInstanceOf[Int])

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

// interface
trait MySquarer {

  def squareDontCare(i: Int): Unit

  def square(i: Int): Future[Int]

  def squareNowPlease(i: Int): Option[Int]

  def squareNow(i: Int): Int

  @throws(classOf[Exception])
  def squareTry(i: Int): Int

}

// Implementations
class MySquarerImpl(val name: String) extends MySquarer {

  def this() = this("default")

  def squareDontCare(i: Int): Unit = i * i // doesnt return anything

  def square(i: Int): Future[Int] = {

    Future.successful(i * i)
  }

  def squareNowPlease(i: Int): Option[Int] = Some(i * i)

  def squareNow(i: Int): Int = i * i

  def squareTry(i: Int): Int = throw new Exception("Catch me if you can!")

}
