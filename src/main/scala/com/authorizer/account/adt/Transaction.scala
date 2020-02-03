package com.authorizer.account.adt

import java.time.ZonedDateTime

trait Transaction {
  def merchant: String

  def amount: BigDecimal

  def time: ZonedDateTime
}

case class CreditCardTransaction(merchant: String, amount: BigDecimal, time: ZonedDateTime) extends Transaction
