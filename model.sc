//Functorial contracts 
trait Functor[T, F[T] <: Functor[T, F]]{
  def map[U](f: T => U): F[U]
}

trait ApFunctor[T, F[T] <: ApFunctor[T, F]] extends Functor[T, F]{
  def ap[U](f: F[T => U]): F[U]
  def zip[U](right: F[U]): F[(T, U)] = right.ap(map(x => (y: U) => (x, y)))
}

trait Monad[T, F[T] <: Monad[T, F]] extends ApFunctor[T, F]{  
  def flatMap[U](f: T => F[U]): F[U]
  def ap[U](f: F[T => U]): F[U] = flatMap(x => f.map(func => func(x)))
}

trait Lift[F[T] <: Monad[T, F]] {
  def lift[T](value: => T): F[T]
}


trait NaturalTransformation[F[T] <: Functor[T, F], G[T] <: Functor[T, G]]{
    def transform(source: F[T]): G[T]
}

trait Adjoint[F[T] <: Functor[T, F], G[T] <: Functor[T, G], X, Y]{
  type HomC = F[X] => Y //is it a distict set or do I need an index?
  type HomD = X => G[Y]
  
  def there(from: HomC): HomD
  def back(from: HomD): HomC
  
  //This looks same: https://alvinalexander.com/java/jwarehouse/scalaz-7.3/core/src/main/scala/scalaz/Adjunction.scala.shtml
}

//Dual

trait ContraFunctor[T, G[T] <: Functor[T, G]]{
   def contramap[U](f: U => T): G[U]
}

//Helpers

trait Showable[T]{
  def show: T //unsafe
}

