package com.authorizer.service

import cats.Monad
import com.authorizer.model.Account

trait AccountServiceAlgebra[F[_], A <: Account] {
  def create(account: A): F[A]
}

case class AccountService[F[_] : Monad, A <: Account]() extends AccountServiceAlgebra[F, A] {
  def create(account: A): F[A] = ???
}
