package com.authorizer.account.violation

sealed trait Violation {
  def value: String
}

case class AccountNotInitialized(value: String = "account-not-initialized") extends Violation

case class AccountAlreadyInitialized(value: String = "account-already-initialized") extends Violation

case class CardNotActive(value: String = "card-not-active") extends Violation

case class InsufficientLimit(value: String = "insufficient-limit") extends Violation

case class HighFrequencySmallInterval(value: String = "high-frequency-small-interval") extends Violation

case class DoubledTransaction(value: String = "doubled-transaction") extends Violation

case class Unknown(value: String = "unknown-violation") extends Violation

object Violation {
  def apply(value: String): Violation = value match {
    case "account-not-initialized" => AccountNotInitialized()
    case "account-already-initialized" => AccountAlreadyInitialized()
    case "card-not-active" => CardNotActive()
    case "insufficient-limit" => InsufficientLimit()
    case "high-frequency-small-interval" => HighFrequencySmallInterval()
    case "doubled-transaction" => DoubledTransaction()
    case _ => Unknown()
  }
}
