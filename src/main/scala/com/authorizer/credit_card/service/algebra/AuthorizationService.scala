package com.authorizer.credit_card.service.algebra

import cats.Monad
import cats.implicits._
import com.authorizer.account.adt.{Account, AuthorizationResult, Transaction}
import com.authorizer.account.service.algebra.AuthorizationServiceAlgebra
import com.authorizer.account.violation.Violation
import com.authorizer.credit_card.adt.CreditCardAccount
import com.authorizer.shared.rich.RichZonedDateTime._

case class AuthorizationService[F[_] : Monad](accountService: AccountService[F]) extends AuthorizationServiceAlgebra[F, Unit] {

  val violations: Set[Violation] = Set.empty

  def authorize(accounts: List[Account], transactions: List[Transaction]): F[List[AuthorizationResult]] = {
    val creationResult = create(accounts)
    val sortedTransactions: List[Transaction] = transactions.sortWith(_.time.toSeconds < _.time.toSeconds)

    creationResult.account match {
      case Some(account) => processTransactions(account, sortedTransactions, creationResult.violations)
      case _ => Monad[F].pure(List(creationResult))
    }
  }

  def create(accounts: List[Account]): AuthorizationResult = {
    val creationViolations = Set.empty ++
      AuthorizationRule.accountNotInitializedRule(accounts) ++
      AuthorizationRule.accountAlreadyInitializedRule(accounts)
    if (accounts.isEmpty) AuthorizationResult(None, creationViolations) else AuthorizationResult(accounts.headOption, creationViolations)
  }

  def processTransactions(account: Account, transactions: List[Transaction], violations: Set[Violation] = Set.empty,
    results: List[AuthorizationResult] = List.empty, throttling: List[Violation] = List.empty): F[List[AuthorizationResult]] = {
    if (transactions.isEmpty) {
      Monad[F].pure(results)
    } else {
      accountService.process(account.asInstanceOf[CreditCardAccount], transactions.head, violations).flatMap { result =>
        result.account.map { account =>
          val throttlingAcc = AuthorizationRule.accountAlreadyInitializedRule(transactions.head, transactions.tail, throttling)
          val violations = Set.empty ++ AuthorizationRule.doubledTransactionRule(transactions.head, transactions.tail) ++ throttlingAcc._1
          processTransactions(account, transactions.tail, violations, results :+ result, throttlingAcc._2)
        }.getOrElse(Monad[F].pure(results))
      }
    }
  }
}

object AuthorizationRule {

  def accountNotInitializedRule(accounts: List[Account]): Option[Violation] = if (accounts.isEmpty) Some(Violation("account-not-initialized")) else None

  def accountAlreadyInitializedRule(
    accounts: List[Account]): Option[Violation] = if (accounts.length > 1) Some(Violation("account-already-initialized")) else None

  def doubledTransactionRule(transaction: Transaction, transactions: List[Transaction]): Option[Violation] = {
    transactions.headOption.flatMap { secondTransaction =>
      val areTransactionsEqual = transaction.merchant === secondTransaction.merchant && transaction.amount === secondTransaction.amount
      val difference = (transaction.time.toSeconds - secondTransaction.time.toSeconds).abs

      if (areTransactionsEqual && difference <= 120) Some(Violation("doubled-transaction")) else None
    }
  }

  def accountAlreadyInitializedRule(transaction: Transaction, transactions: List[Transaction],
    violations: List[Violation] = List.empty): (Option[Violation], List[Violation]) = {
    transactions.headOption.flatMap { secondTransaction =>
      val difference = (transaction.time.toSeconds - secondTransaction.time.toSeconds).abs
      val violationsAcc = if (difference <= 120) violations :+ Violation("high-frequency-small-interval") else violations

      val result = if (violationsAcc.length > 2) (Some(Violation("high-frequency-small-interval")), List.empty[Violation]) else (None, violationsAcc)
      Some(result)
    }
    }.getOrElse((None, List.empty))
}