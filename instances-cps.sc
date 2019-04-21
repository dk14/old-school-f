import $file.model

//This allows transparent cps for async APIs
//---------------------------------

case class CallBack[T](submit: T => Unit)

//TODO implement trampolines to support safe loops (should be implemeented separately in order to preserve stacktraces for non-loops)
class CpsMonad[T](val continue: CallBack[T] => Unit) extends Monad[T, CpsMonad] {
  def map[U](f: T => U): CpsMonad[U] = new CpsMonad(newcb => continue(CallBack(x => newcb.submit(f(x)))))
  def flatMap[U](f: T => CpsMonad[U]): CpsMonad[U] = 
    new CpsMonad(newcb => continue(CallBack(x => f(x).continue(CallBack(y => newcb.submit(y))))))
  def foreach(f: T => Unit): Unit = continue(CallBack(f)) //TODO: incapsulate?
}

class LiftToCps extends Lift[CpsMonad] {
  def lift[T](value: => T): CpsMonad[T] = liftContinuation(cb => cb.submit(value))
  def liftContinuation[T](continue: CallBack[T] => Unit): CpsMonad[T] = new CpsMonad(continue) 
}


//--------Multithreading (determinstic)----------------
//TODO non-deterministic zip
trait LiftToForkedCpsMixin extends LiftToCps {
  def fork[T](worker: => T): CpsMonad[T]
}

import java.util.concurrent._
class LiftToForkedCpsJvm(exec: Executor) extends LiftToCps with LiftToForkedCpsMixin {
  def fork[T](worker: => T): CpsMonad[T] = liftContinuation[T]{ cb => 
    exec.execute(new Runnable { //TODO preserve stacktrace in here
      def run = cb.submit(worker)
    })
  }
  
  import locks._
    
  //should be called from `main` thread
  def finalizeComputation[T](monad: CpsMonad[T])(postShutdownHandler: T => Unit) = {
    println("Shutting down Cps")
    val lock = new ReentrantLock()
    lock.lock()
    val finalized = lock.newCondition()
    monad.foreach { x => //TODO try catch
      lock.lock()
      postShutdownHandler(x)
      finalized.signal()
      lock.unlock() //TODO try catch
    }
    finalized.await()
    lock.unlock()
  } 
}
