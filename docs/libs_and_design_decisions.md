## Libs and Design Decisions

This program was developed using functional programming paradigm as much as possible. Scala case classes works with immutable values as default. Here you will see Algebraic Data Types (ADTs) as data type definitions. I also defined abstract algebras for service definitions.

I decided to use the lib [cats](https://typelevel.org/cats/) to use cats.Monad as an abstract way to sequence computations. So, I can work with scala.concurrent.Future for run the project in parallel and use cats.Id for run unit tests without side effects.

I'm using the lib [circe](https://circe.github.io/circe/) too to parse input json and generate output json.

Finally, I'm using [scalatest](http://www.scalatest.org/) for unit and integration tests. I decided to not use any lib to generate fixtures, I'm using them programmatically.

You can see all the dependencies here: [project/Dependencies.scala](../project/Dependencies.scala).