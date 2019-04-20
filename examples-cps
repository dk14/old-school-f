import $file.instances-cps

def module(elevator: LiftToForkedCps): CpsMonad[Int] = for{
  four <- elevator.fork(5).map(_ - 1)
  _ = println(four)
  six <- elevator.lift(6)
} yield four + six

val cpsProgram = new LiftToForkedCps(ExecutionContext.global)
val m = module(cpsProgram)
cpsProgram.finalize(m)(println)
