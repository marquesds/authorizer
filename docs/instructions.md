## Running on local machine

First you have to download and install `sbt` tool. After that, you can run:
```shell
$ sbt "run FILENAME"
```

For example:
```shell
$ sbt "run resources/input/account_transactions.txt"
```

## Running on a Docker container

## Running unit tests
```shell
$ sbt test
```

## Running integration tests
```shell
$ sbt it:test
```