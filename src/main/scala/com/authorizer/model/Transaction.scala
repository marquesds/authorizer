package com.authorizer.model

import java.time.ZonedDateTime

sealed trait Transaction

case class CommonTransaction(merchant: String, amount: BigDecimal, time: ZonedDateTime) extends Transaction
