package org.jamieallen.effectiveakka.extra3

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.actor._

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
  val checkingBalances, savingsBalances, mmBalances: Option[List[(Long, BigDecimal)]] = None
  var originalSender: Option[ActorRef] = None
  def receive = {
    case GetCustomerAccountBalances(id) => {
      context.actorOf(Props(new Actor() {
        var checkingBalances, savingsBalances, mmBalances: Option[List[(Long, BigDecimal)]] = None
        val originalSender = sender
        def receive = {
          case CheckingAccountBalances(balances) =>
            checkingBalances = balances
            isDone
          case SavingsAccountBalances(balances) =>
            savingsBalances = balances
            isDone
          case MoneyMarketAccountBalances(balances) =>
            mmBalances = balances
            isDone
        }

        def isDone =
          (checkingBalances, savingsBalances, mmBalances) match {
            case (Some(c), Some(s), Some(m)) =>
              originalSender ! AccountBalances(checkingBalances, savingsBalances, mmBalances)
              context.system.stop(self)
            case _ =>
          }

        savingsAccounts ! GetCustomerAccountBalances(id)
        checkingAccounts ! GetCustomerAccountBalances(id)
        moneyMarketAccounts ! GetCustomerAccountBalances(id)
      }))
    }
  }
}
