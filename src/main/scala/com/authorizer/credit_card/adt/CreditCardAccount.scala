package com.authorizer.credit_card.adt

import com.authorizer.account.adt.Account

case class CreditCardAccount(activeCard: Boolean, availableLimit: BigDecimal) extends Account
