package com.authorizer.cli

import cats.instances.future._
import com.authorizer.account.adt.CreditCardTransaction
import com.authorizer.cli.model.{AccountJson, TransactionJson}
import com.authorizer.credit_card.adt.CreditCardAccount
import com.authorizer.credit_card.service.algebra.{AccountService, AuthorizationService}
import io.circe.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AuthorizerCLI extends App {

  println("opaaa, to aqui")

  val accountService = AccountService[Future]()
  val service = AuthorizationService[Future](accountService)

  val accounts: List[CreditCardAccount] = args.map { value =>
    JsonParser.fromString(value) match {
      case Some(value) =>
        println(value)
        JsonParser.fromKey(value, "account").get.asJson.as[AccountJson] match {
          case Right(accountJson) => accountJson.asInstanceOf[CreditCardAccount]
          case Left(_) => None
        }
      case _ => None
    }
  }.asInstanceOf[List[CreditCardAccount]]

  val transactions: List[CreditCardTransaction] = args.map { value =>
    JsonParser.fromString(value) match {
      case Some(value) => JsonParser.fromKey(value, "transaction").asJson.as[TransactionJson] match {
        case Right(transactionJson) => transactionJson.asInstanceOf[CreditCardTransaction]
        case Left(_) => None
      }
      case _ => None
    }
  }.asInstanceOf[List[CreditCardTransaction]]

  println(accounts, transactions)
}
