package com.authorizer.credit_card.service.algebra

import cats.Monad
import com.authorizer.account.adt.Account
import com.authorizer.account.service.algebra.AccountServiceAlgebra
import com.authorizer.account.violation.Violation
import com.authorizer.credit_card.adt.CreditCardAccount

import scala.math.BigDecimal

case class AccountService[F[_] : Monad, B](account: CreditCardAccount) extends AccountServiceAlgebra[F, Unit] {
  def subtract(amount: BigDecimal): F[Either[Violation, Account]] = {
    val result = account.availableLimit - amount

    Monad[F].pure(if (result >= 0) Right(account.copy(availableLimit = result)) else Left(Violation("insufficient-limit")))
  }
}