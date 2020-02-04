package com.authorizer.account.service.algebra

import com.authorizer.account.adt.{AuthorizationResult, Transaction}
import com.authorizer.account.violation.Violation

trait AccountServiceAlgebra[F[_], B] {
  def process(transaction: Transaction, violations: Set[Violation] = Set.empty): F[AuthorizationResult]
}