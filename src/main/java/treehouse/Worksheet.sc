package treehouse

object Worksheet {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  
  val s = "Hello"                                 //> s  : String = Hello
  
  def multiplyByTwo(x:Int) = if (x==0) x else x*2 //> multiplyByTwo: (x: Int)Int
  
  // last expression evaluated in scala is always implicitlt returned
  
  multiplyByTwo(2)                                //> res0: Int = 4
  
  // :Unit means void return type
  def greeting():Unit = println("Hello World")    //> greeting: ()Unit
  
  greeting()                                      //> Hello World
  
  // <- means in
  
  // loops to is inclusive
  for (i <-1 to 10) println(i)                    //> 1
                                                  //| 2
                                                  //| 3
                                                  //| 4
                                                  //| 5
                                                  //| 6
                                                  //| 7
                                                  //| 8
                                                  //| 9
                                                  //| 10
  
  // until is exclusive
  for (i <-1 until 10) println(i)                 //> 1
                                                  //| 2
                                                  //| 3
                                                  //| 4
                                                  //| 5
                                                  //| 6
                                                  //| 7
                                                  //| 8
                                                  //| 9
  
  
// condition guard inside loop
for (i <- 1 to 10 if i%2 ==0) println(i)          //> 2
                                                  //| 4
                                                  //| 6
                                                  //| 8
                                                  //| 10
 
// yield outputs into an immutable collection
val numbers = for (i<-1 to 10) yield i*10         //> numbers  : scala.collection.immutable.IndexedSeq[Int] = Vector(10, 20, 30, 4
                                                  //| 0, 50, 60, 70, 80, 90, 100)

 // no fallthrough - no break
 val age = 20                                     //> age  : Int = 20
 age match {
 	case 10 => println("ten")
 	case 20 => println("twenty")
 	case _ => println("no match")
 }                                                //> twenty
 

   def findAge(age: Int): String = age match {
 	case 10 => "ten"
 	case 20 => "twenty"
 	case _ => "no match"
 }                                                //> findAge: (age: Int)String
 
 val ageAsString = findAge(20)                    //> ageAsString  : String = twenty
 
 
 
 
 
  
}