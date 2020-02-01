package com.authorizer.model

sealed trait Account

case class CreditAccount(activeCard: Boolean, availableLimit: BigDecimal) extends Account
