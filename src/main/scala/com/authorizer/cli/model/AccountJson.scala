package com.authorizer.cli.model

import io.circe.generic.extras._

@ConfiguredJsonCodec
case class AccountJson(activeCard: Boolean, availableLimit: BigDecimal)

object AccountJson {
  implicit val customConfig: Configuration = Configuration.default.withKebabCaseMemberNames
}