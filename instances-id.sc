//Id implementations:
class IdMonad[T](data: T) extends Monad[T, IdMonad] with Showable[T]{
  def map[U](f: T => U): IdMonad[U] = new IdMonad[U](f(data))
  def flatMap[U](f: T => IdMonad[U]): IdMonad[U] = f(data)
  def show = data
}

object IdLift extends Lift[IdMonad] { 
  def lift[T](value: => T): IdMonad[T] = new IdMonad[T](value)
}

