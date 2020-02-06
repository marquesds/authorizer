package com.authorizer.cli

import cats.instances.future._
import com.authorizer.account.adt.CreditCardTransaction
import com.authorizer.cli.model.{AccountJson, AuthorizationResultJson, TransactionJson}
import com.authorizer.credit_card.adt.CreditCardAccount
import com.authorizer.credit_card.service.algebra.{AccountService, AuthorizationService}
import io.circe.syntax._
import io.circe.Printer

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.io.Source

object AuthorizerCLI extends App {

  def authorize(fileName: String) = {
    val accountService = AccountService[Future]()
    val service = AuthorizationService[Future](accountService)

    val bufferedSource = Source.fromFile(fileName)
    val fileContents = bufferedSource.getLines.toList
    bufferedSource.close

    val accounts: List[CreditCardAccount] = fileContents.map { value =>
      JsonParser.fromString(value.toString) match {
        case Some(value) => JsonParser.fromKey(value, "account").asJson.as[AccountJson] match {
          case Right(accountJson) => Some(CreditCardAccount(accountJson.activeCard, accountJson.availableLimit))
          case Left(_) => None
        }
        case _ => None
      }
    }.asInstanceOf[List[Option[CreditCardAccount]]].flatten

    val transactions: List[CreditCardTransaction] = fileContents.map { value =>
      JsonParser.fromString(value.toString) match {
        case Some(value) => JsonParser.fromKey(value, "transaction").asJson.as[TransactionJson] match {
          case Right(transactionJson) => Some(CreditCardTransaction(transactionJson.merchant, transactionJson.amount, transactionJson.time))
          case Left(_) => None
        }
        case _ => None
      }
    }.asInstanceOf[List[Option[CreditCardTransaction]]].flatten

    val result = Await.result(service.authorize(accounts, transactions), Duration.Inf)

    result.flatMap { value =>
      value.account.map { account =>
        val accountJson = AccountJson(account.activeCard, account.availableLimit)
        AuthorizationResultJson(Some(accountJson), value.violations.map(_.toString))
      }.orElse(Some(AuthorizationResultJson(None, value.violations.map(_.toString))))
    }
  }

  val printer = Printer.noSpaces.copy(dropNullValues = true)
  authorize(args(0)).map(value => println(printer.print(value.asJson)))
}
