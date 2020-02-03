package com.authorizer.credit_card.service.algebra

import cats.Monad
import com.authorizer.account.adt.{Account, Transaction}
import com.authorizer.account.service.algebra.TransactionServiceAlgebra
import com.authorizer.account.violation.Violation

case class TransactionService[F[_] : Monad, B]() extends TransactionServiceAlgebra[F, Unit] {
  override def process(transaction: Transaction): F[Either[List[Violation], Account]] = ???
}