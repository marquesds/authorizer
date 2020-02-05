package com.authorizer.account.service.algebra

import com.authorizer.account.adt.{Account, AuthorizationResult, Transaction}

trait AuthorizationServiceAlgebra[F[_], B] {
  def authorize(accounts: List[Account], transactions: List[Transaction]): F[List[AuthorizationResult]]
}