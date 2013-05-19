package org.jamieallen.effectiveakka.common

import akka.actor.{ Actor, ActorLogging }
import akka.event.LoggingReceive

class CheckingAccountsProxyStub extends CheckingAccountsProxy with ActorLogging {
  val accountData = Map[Long, List[(Long, BigDecimal)]](
    1L -> List((3, 15000)),
    2L -> List((6, 640000), (7, 1125000), (8, 40000)))

  def receive = LoggingReceive {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $id")
      accountData.get(id) match {
        case Some(data) => sender ! CheckingAccountBalances(Some(data))
        case None => sender ! CheckingAccountBalances(Some(List()))
      }
  }
}

class SavingsAccountsProxyStub extends SavingsAccountsProxy with ActorLogging {
  val accountData = Map[Long, List[(Long, BigDecimal)]](
    1L -> (List((1, 150000), (2, 29000))),
    2L -> (List((5, 80000))))

  def receive = LoggingReceive {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $id")
      accountData.get(id) match {
        case Some(data) => sender ! SavingsAccountBalances(Some(data))
        case None => sender ! SavingsAccountBalances(Some(List()))
      }
  }
}

class MoneyMarketAccountsProxyStub extends MoneyMarketAccountsProxy with ActorLogging {
  val accountData = Map[Long, List[(Long, BigDecimal)]](
    2L -> List((9, 640000), (10, 1125000), (11, 40000)))

  def receive = LoggingReceive {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $id")
      accountData.get(id) match {
        case Some(data) => sender ! MoneyMarketAccountBalances(Some(data))
        case None => sender ! MoneyMarketAccountBalances(Some(List()))
      }
  }
}

class TimingOutSavingsAccountProxyStub extends SavingsAccountsProxy with ActorLogging {
  def receive = LoggingReceive {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Forcing timeout by not responding!")
  }
}
