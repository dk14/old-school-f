# old-school-f
Functors. Just f*** functors! 

Typesafe! NO implicits; NO type-classes; NO higher-orders;

ONLY CLASSIC INHERITANCE and F-bounded polymorphism!

- all compatible with "do-notations" aka "for-comprehensions" (see examples.sc) 
- zip's as a simpler alternative to applicative builders

Notes: 

- it could be ported only to programming languages without erasures or the ones workarounding it (like scala)
- the dispatching is dynamic, but there aren't much overrides, so `invokestatic` in JVM shouldn't take too much time.
