package com.authorizer.account.service.algebra

import com.authorizer.account.adt.{Account, Transaction}
import com.authorizer.account.violation.Violation

trait TransactionServiceAlgebra[F[_], B] {
  def process(transaction: Transaction): F[Either[List[Violation], Account]]
}