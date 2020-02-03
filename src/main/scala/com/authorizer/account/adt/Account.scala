package com.authorizer.account.adt

trait Account {
  def activeCard: Boolean

  def availableLimit: BigDecimal
}
