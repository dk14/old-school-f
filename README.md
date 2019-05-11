# old-school-f
Functors. Just f*** functors! 

Typesafe! NO implicits; NO type-classes; NO `F[_]`s;

ONLY CLASSIC INHERITANCE and F-bounded polymorphism!

- all compatible with "do-notations" aka "for-comprehensions" (see [examples.sc](examples.sc)) 
- zip's as a simpler alternative to applicative builders, lol
- continuation passing style (cps) for async calls (see [examples-cps.sc](examples-cps.sc)) 

Notes: 

- it could be ported only to programming languages without erasures or the ones workarounding it (like scala)
- the dispatching is dynamic, but there aren't much overrides, so `invokevirtual` in JVM shouldn't take too much time (presumably).
- technically all functors are higher-order, and F[T] is higher than `T` - though in scala-slang we usually refer to `F[_]` as a higher order type (basically coz it's abstraction we wan't to implement some time later). 
