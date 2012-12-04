package org.jamieallen.effectiveakka.common

import akka.actor.Actor

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
      sender ! MoneyMarketAccountBalances(Some(List()))
  }
}
