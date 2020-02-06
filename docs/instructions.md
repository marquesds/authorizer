## Running with the built-in jar
```shell
$ java -jar authorizer.jar authorizer FILENAME
```

For example:
```shell
$ java -jar resources/input/account_transactions.txt
```

## Build the jar file
```
$ sbt assembly
```

The file will be created at `target/scala-2.12/authorizer.jar`

## Running on local machine with sbt

First you have to download and install `sbt` tool. After that, you can run:
```shell
$ sbt "run FILENAME"
```

For example:
```shell
$ sbt "run resources/input/account_transactions.txt"
```

## Running unit tests
```shell
$ sbt test
```

## Running integration tests
```shell
$ sbt it:test
```

## Testing on a Docker container

```shell
$ docker build -t authorizer .
$ docker run authorizer sbt test . # for running unit tests
$ docker run authorizer sbt test . # for running integration tests
```