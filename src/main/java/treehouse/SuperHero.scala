package treehouse

// this created these parameters as instance variables automatically
class SuperHero(val name:String, val gender:String) {
  
 // this is run as part of the constructor
 println("A new hero was created")

 // this is part of the class def
 def describeName(){
   
   println(name)
       
 }
 
 // to add other constructors use this - called an auxiliary constrcutor
 def this(name:String){
   this(name,"") // calling the primary constructor
   
 }
 
 // this is anothe instance varibale
 var heroAge = 0
 
 // this is the "getter"
 def age = heroAge
 
 // this is the setter
 def age_=(newAge:Int): Unit = {
   if (newAge>heroAge)
       heroAge = newAge
 
 }
   
 
 
  
}