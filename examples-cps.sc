import $file.instances-cps

def module(elevator: LiftToCps with LiftToForkedCpsMixin): CpsMonad[Int] = for{
  four <- elevator.fork{Thread.sleep(1000);5}.map(_ - 1)
  _ = println(four)
  six <- elevator.lift(6)
} yield four + six

import java.util.concurrent._
val cpsProgram = new LiftToForkedCpsJvm(Executors.newFixedThreadPool(10))
val m = module(cpsProgram)
cpsProgram.finalizeComputation(m)(println)
