package akka



import scala.concurrent.Future
import akka.actor.TypedProps
import java.util.concurrent.TimeUnit
import akka.actor.ActorSystem
import scala.concurrent.duration.FiniteDuration
import akka.actor.TypedActor
import akka.util.Timeout
import akka.actor.Props




//http://doc.akka.io/docs/akka/current/scala/typed-actors.html
object TypedActorTut2 extends App {

  
   print("Hello")
  
  val actorSystem = ActorSystem("UniversityMessageSystem")

   println(1)
  
 
  // return Typed Actor extension
  val extension = TypedActor(actorSystem)

  val teacherActorRef = actorSystem.actorOf(Props[TeacherActor])
  println(2)
 
 
 // most trivial way of creating the squarer
  val mySquarer: Squarer = 
    TypedActor(actorSystem).typedActorOf(TypedProps[SquarerImpl]())
    
  
  // 
  val otherSquarer: Squarer =
      TypedActor(actorSystem).typedActorOf(
          TypedProps(classOf[Squarer],
              new SquarerImpl("foo")).withTimeout(new Timeout(new FiniteDuration(4, TimeUnit.SECONDS))),"name")
   
             println(3)
 
          
  /*
   As simple as that! The method will be executed on another thread; asynchronously.
	this return unit so it will be executed on another thread, async

   */
  mySquarer.squareDontCare(10)
  
  
  println(mySquarer.squareNowPlease(3))
  
  
   actorSystem.terminate()
  
  
  

}



// interface
trait Squarer {

  def squareDontCare(i: Int): Unit
  
  def square(i: Int): Future[Int]
  
  def squareNowPlease(i: Int): Option[Int]
  
  def squareNow(i: Int): Int
  
  @throws(classOf[Exception])
  def squareTry(i: Int): Int
  
  
  
  
  
}


// Implementations
class SquarerImpl(val name: String) extends Squarer {

  def this() = this("default")
  
  def squareDontCare(i: Int): Unit = i * i // doesnt return anything
  
  def square(i: Int): Future[Int] = Future.successful(i * i)
  
  def squareNowPlease(i: Int): Option[Int] = Some(i * i)
  
  def squareNow(i: Int): Int = i * i
  
  def squareTry(i: Int): Int = throw new Exception("Catch me if you can!")

}