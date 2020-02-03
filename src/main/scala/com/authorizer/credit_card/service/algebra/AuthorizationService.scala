package com.authorizer.credit_card.service.algebra

import cats.Monad
import com.authorizer.account.adt.{Account, Transaction}
import com.authorizer.account.service.algebra.AuthorizationServiceAlgebra
import com.authorizer.credit_card.adt.TransactionResult

case class AuthorizationService[F[_] : Monad, B]() extends AuthorizationServiceAlgebra[F, Unit] {
  def authorize(account: Account, transaction: Transaction): F[TransactionResult] = ???
}