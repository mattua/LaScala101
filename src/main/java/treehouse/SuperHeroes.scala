package treehouse

object SuperHeroes {
  
  def main(args: Array[String]): Unit = {
    
    println("Hello World")
    
    val superman = new SuperHero("Rusty","male")
    
    superman.describeName()
    
    val superman2 = new SuperHero("Dusty")
    
    superman2.age=4
    println(superman2.age) 
    
    println(superman2.age_=(6))
    println(superman2.age) 
     
  }
  
}