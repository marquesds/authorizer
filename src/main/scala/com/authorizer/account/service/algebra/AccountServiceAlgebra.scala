package com.authorizer.account.service.algebra

import com.authorizer.account.adt.Account
import com.authorizer.account.violation.Violation

trait AccountServiceAlgebra[F[_], B] {
  def subtract(amount: BigDecimal): F[Either[Violation, Account]]
}