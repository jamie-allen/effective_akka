package org.jamieallen.effectiveakka.extra1

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

case class GetCustomerAccountBalances(id: Long)
case class AccountBalances(
  val checking: Option[List[(Long, BigDecimal)]],
  val savings: Option[List[(Long, BigDecimal)]],
  val moneyMarket: Option[List[(Long, BigDecimal)]])
case class CheckingAccountBalances(
  val balances: Option[List[(Long, BigDecimal)]])
case class SavingsAccountBalances(
  val balances: Option[List[(Long, BigDecimal)]])
case class MoneyMarketAccountBalances(
  val balances: Option[List[(Long, BigDecimal)]])

class SavingsAccountProxy extends Actor {
  def receive = {
    case GetCustomerAccountBalances(id: Long) =>
      sender ! SavingsAccountBalances(Some(List((1, 150000), (2, 29000))))
  }
}
class CheckingAccountProxy extends Actor {
  def receive = {
    case GetCustomerAccountBalances(id: Long) =>
      sender ! CheckingAccountBalances(Some(List((3, 15000))))
  }
}
class MoneyMarketAccountsProxy extends Actor {
  def receive = {
    case GetCustomerAccountBalances(id: Long) =>
      sender ! MoneyMarketAccountBalances(None)
  }
}

class AccountBalanceRetriever(savingsAccounts: ActorRef, checkingAccounts: ActorRef, moneyMarketAccounts: ActorRef) extends Actor {
  implicit val timeout: Timeout = 100 milliseconds
  implicit val ec: ExecutionContext = context.dispatcher
  def receive = {
    case GetCustomerAccountBalances(id) =>
      val futSavings = savingsAccounts ? GetCustomerAccountBalances(id)
      val futChecking = checkingAccounts ? GetCustomerAccountBalances(id)
      val futMM = moneyMarketAccounts ? GetCustomerAccountBalances(id)
      val futBalances = for {
        savings <- futSavings.mapTo[Option[List[(Long, BigDecimal)]]]
        checking <- futChecking.mapTo[Option[List[(Long, BigDecimal)]]]
        mm <- futMM.mapTo[Option[List[(Long, BigDecimal)]]]
      } yield AccountBalances(savings, checking, mm)
      futBalances map (sender ! _)
  }
}
