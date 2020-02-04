package com.authorizer.account.service.algebra

import com.authorizer.account.adt.{Account, AuthorizationResult, Transaction}
import com.authorizer.account.violation.Violation

trait AccountServiceAlgebra[F[_], A <: Account, B] {
  def process(account: A, transaction: Transaction, violations: Set[Violation] = Set.empty): F[AuthorizationResult]
}