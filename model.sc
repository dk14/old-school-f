//Functorial contracts 
trait Showable[T]{
   def show: T
}

trait Functor[T, F[T] <: Functor[T, F]]{
   def map[U](f: T => U): F[U]
}

trait ApFunctor[T, F[T] <: ApFunctor[T, F]] extends Functor[T, F]{
   def ap[U](f: F[T => U]): F[U]
   def zip[U](right: F[U]): F[(T, U)] = right.ap(this.map(x => (y: U) => (x, y)))
}

trait Monad[T, F[T] <: Monad[T, F]] extends ApFunctor[T, F]{  
   def flatMap[U](f: T => F[U]): F[U]
   def ap[U](f: F[T => U]): F[U] = flatMap(x => f.map(func => func(x)))
}

trait Lift[F[T] <: Monad[T, F]] {
   def lift[T](value: => T): F[T]
}
