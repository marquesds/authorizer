package com.authorizer.cli

import io.circe.Json
import io.circe.parser._

object JsonParser {
  def fromString(value: String): Option[Json] = {
    parse(value) match {
      case Right(json) => Some(json)
      case Left(error) => println(error); None
    }
  }

  def fromKey(json: Json, key: String): Option[Json] = {
    json.hcursor.downField(key).as[Json] match {
      case Right(value) => Some(value)
      case Left(_) => None
    }
  }
}
