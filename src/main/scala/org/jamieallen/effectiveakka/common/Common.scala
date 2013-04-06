package org.jamieallen.effectiveakka.common

import akka.actor.Actor
import akka.actor.ActorLogging

case class GetCustomerAccountBalances(id: Long)
case class AccountBalances(
  checking: Option[List[(Long, BigDecimal)]],
  savings: Option[List[(Long, BigDecimal)]],
  moneyMarket: Option[List[(Long, BigDecimal)]])
case class CheckingAccountBalances(
  balances: Option[List[(Long, BigDecimal)]])
case class SavingsAccountBalances(
  balances: Option[List[(Long, BigDecimal)]])
case class MoneyMarketAccountBalances(
  balances: Option[List[(Long, BigDecimal)]])

class SavingsAccountProxy extends Actor with ActorLogging {
  def receive = {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $id")
      sender ! SavingsAccountBalances(Some(List((1, 150000), (2, 29000))))
  }
}
class CheckingAccountProxy extends Actor with ActorLogging {
  def receive = {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $id")
      sender ! CheckingAccountBalances(Some(List((3, 15000))))
  }
}
class MoneyMarketAccountsProxy extends Actor with ActorLogging {
  def receive = {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $id")
      sender ! MoneyMarketAccountBalances(Some(List()))
  }
}
