import $file.model

//This allows transparent cps for async APIs
//---------------------------------

case class CallBack[T](submit: T => Unit)

//TODO trampolines
class CpsMonad[T](val continue: CallBack[T] => Unit) extends Monad[T, CpsMonad]{
  def map[U](f: T => U): CpsMonad[U] = new CpsMonad(newcb => continue(CallBack(x => newcb.submit(f(x)))))
  def flatMap[U](f: T => CpsMonad[U]): CpsMonad[U] = 
    new CpsMonad(newcb => continue(CallBack(x => f(x).continue(CallBack(y => newcb.submit(y))))))
  def foreach(f: T => Unit): Unit = continue(CallBack(f))
}

class LiftToCps extends Lift[CpsMonad] {
  def lift[T](value: => T) = liftContinuation(cb => cb.submit(value))
  def liftContinuation[T](continue: CallBack[T] => Unit): CpsMonad[T] = new CpsMonad(continue) 
}
 
import scala.concurrent._
import scala.util._

class LiftToForkedCps(ec: ExecutionContext) extends LiftToCps {
  def fork[T](worker: => T) = liftContinuation[T]{cb => 
    ec.execute(new Runnable{
      def run = cb.submit(worker)
    })
  }
    
  //should be called only once
  def finalize[T](monad: CpsMonad[T])(postShutdownHandler: T => Unit) = {
      val monitor = new Object
      monad.foreach{x => //TODO make foreach private package
        postShutdownHandler(x)
        monitor.notifyAll()
      }
      monitor.synchronized(monitor.wait(3000))     
  } 
}
