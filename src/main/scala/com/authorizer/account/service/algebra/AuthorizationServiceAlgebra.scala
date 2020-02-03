package com.authorizer.account.service.algebra

import com.authorizer.account.adt.{Account, Transaction}
import com.authorizer.credit_card.adt.TransactionResult

trait AuthorizationServiceAlgebra[F[_], B] {
  def authorize(account: Account, transaction: Transaction): F[TransactionResult]
}