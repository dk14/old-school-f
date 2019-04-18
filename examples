import $file.model

class Service[F[T] <: Monad[T, F]](elev: Lift[F]) {
  import elev._
  def get(id: Int): F[Int] = lift{println("GET"); Thread.sleep(3000); 100}
  def put(id: Int, value: Int): F[Unit] = lift{ Thread.sleep(3000); println("PUT")}
}


def module[F[T] <: Monad[T, F]](elev: Lift[F], service: Service): F[Int] = for {
    elems <- elev.lift(5) zip elev.lift(7)
    (a, b) = elems
    c = a + b
    
    _ <- service.put(1, 100)
    results <- service.get(1) zip service.get(1)
    (l, r) = results
} yield l + r

module(IdLift).show
module(new FutureLift(ExecutionContext.Implicits.global)).show
