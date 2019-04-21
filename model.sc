//--------Functorial contracts--------------------------------------------------------------

trait Functor[T, F[T] <: Functor[T, F]] {
  def map[U](f: T => U): F[U]
}

trait ApFunctor[T, F[T] <: ApFunctor[T, F]] extends Functor[T, F] {
  def ap[U](f: F[T => U]): F[U]
  def zip[U](right: F[U]): F[(T, U)] = ap(right.map(y => x => (x, y)))
}

trait Monad[T, F[T] <: Monad[T, F]] extends ApFunctor[T, F] {  
  def flatMap[U](f: T => F[U]): F[U]
  def ap[U](f: F[T => U]): F[U] = flatMap(x => f.map(func => func(x)))
}

trait Lift[F[T] <: Monad[T, F]] {
  def lift[T](value: => T): F[T]
}

trait EndoFunctor[C, F[T] <: EndoFunctor[T, F]] {
  def map[A <: C, B <: C](f: A => B): F[B] 
}

//----------------------------------------------------------------------------------------------

trait FAlgebra[C, A <: C, B <: C, F[T] <: EndoFunctor[T, F]] { //A and B are objects in C
  
  type CMorphism[X] = F[X] => X //selection
  
  type LeftPath = CMorphism[A] => (A => B)
  type RightPath = F[A => B] => CMorphism[B]
  
  val algebraInstanceA: (A, CMorphism[A])
  val algebraInstanceB: (B, CMorphism[B])  

  //Commutativity: both are going to get us to B
  val leftPathInstance: LeftPath
  val rightPathInstance: RightPath
  
  //TODO: realias pathes, like (F[A] => A => A) => B vs (F[A => B] => F[B]) => B
  //TODO: show that both pathes can get us from F[A] to B
  //TODO: add initial algebras with Instances like lists and trees
      
}

trait NaturalTransformation[F[T] <: Functor[T, F], G[T] <: Functor[T, G]] {
  def transform[T](source: F[T]): G[T]
}

trait Adjunction[C, D, X <: C, Y <: D, F[T] <: Functor[T, F], G[T] <: Functor[T, G]] {
  type HomC = F[X] => Y //is it a distict set or do I need an index? invoke axiom of choice?
  type HomD = X => G[Y]
  
  def there(from: HomC): HomD
  def back(from: HomD): HomC
  
  //This looks same: https://alvinalexander.com/java/jwarehouse/scalaz-7.3/core/src/main/scala/scalaz/Adjunction.scala.shtml
}

//TODO: monad diagrams?

//===================Duals==========================

trait ContraFunctor[T, G[T] <: Functor[T, G]] {
  def contramap[U](f: U => T): G[U]
}

//==================Helpers=========================

trait Showable[T] {
  def show: T //unsafe
}

