package com.authorizer.cli.model

import java.time.ZonedDateTime

import io.circe.generic.extras._

@ConfiguredJsonCodec
case class TransactionJson(merchant: String, amount: BigDecimal, time: ZonedDateTime)

object TransactionJson {
  implicit val customConfig: Configuration = Configuration.default.withKebabCaseMemberNames
}
