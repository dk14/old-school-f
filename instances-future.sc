import $file.model
import scala.concurrent._

class FutureMonad[T](val underlying: Future[T], ec: ExecutionContext) extends Monad[T, FutureMonad] {
  def map[U](f: T => U): FutureMonad[U] = new FutureMonad(underlying.map(f)(ec), ec)
  def flatMap[U](f: T => FutureMonad[U]): FutureMonad[U] = 
    new FutureMonad(underlying.flatMap(x => f(x).underlying)(ec), ec)
  def show = Await.result(underlying, duration.Duration.Inf) 
}

class FutureParZip[T](
    override val underlying: Future[T], 
    ec: ExecutionContext) extends FutureMonad[T](underlying, ec){
    
  override def zip[U](right: FutureMonad[U]): FutureMonad[(T, U)] = 
    new FutureMonad(underlying.zip(right.underlying), ec)
}

class FutureLift(ec: ExecutionContext, parZip: Boolean = true) extends Lift[FutureMonad] {
  private def monadFactory[T](f: Future[T]) = 
    if (parZip) new FutureParZip[T](f, ec) else new FutureMonad(f, ec)
    
  def lift[T](value: => T): FutureMonad[T] = monadFactory(Future(value)(ec))
  def assimilate[T](future: Future[T]): FutureMonad[T] = monadFactory(future) 
}
