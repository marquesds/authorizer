package com.authorizer.service

import cats.Monad
import com.authorizer.model.Transaction

trait TransactionServiceAlgebra[F[_], A <: Transaction] {
  def process(transaction: A)
}

case class TransactionService[F[_] : Monad, A <: Transaction]() extends TransactionServiceAlgebra[F, A] {
  def process(transaction: A) = ???
}
