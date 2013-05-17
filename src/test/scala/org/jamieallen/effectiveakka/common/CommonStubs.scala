package org.jamieallen.effectiveakka.common

import akka.actor.{ Actor, ActorLogging }
import akka.event.LoggingReceive

class SavingsAccountsProxyStub extends SavingsAccountsProxy with ActorLogging {
  def receive = LoggingReceive {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $id")
      sender ! SavingsAccountBalances(Some(List((1, 150000), (2, 29000))))
  }
}
class CheckingAccountsProxyStub extends CheckingAccountsProxy with ActorLogging {
  def receive = LoggingReceive {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $id")
      sender ! CheckingAccountBalances(Some(List((3, 15000))))
  }
}
class MoneyMarketAccountsProxyStub extends MoneyMarketAccountsProxy with ActorLogging {
  def receive = LoggingReceive {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $id")
      sender ! MoneyMarketAccountBalances(Some(List()))
  }
}

class TimingOutSavingsAccountProxyStub extends SavingsAccountsProxy with ActorLogging {
  def receive = {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Forcing timeout")
      Thread.sleep(1000)
  }
}
