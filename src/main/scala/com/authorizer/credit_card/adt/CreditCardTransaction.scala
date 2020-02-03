package com.authorizer.credit_card.adt

import java.time.ZonedDateTime

import com.authorizer.account.adt.Transaction

case class CreditCardTransaction(merchant: String, amount: BigDecimal, time: ZonedDateTime) extends Transaction
