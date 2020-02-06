package com.authorizer.cli.model

import io.circe.generic.extras._

@ConfiguredJsonCodec
case class AuthorizationResultJson(account: Option[AccountJson], violations: Set[String])

object AuthorizationResultJson {
  implicit val customConfig: Configuration = Configuration.default.withKebabCaseMemberNames
}
