package org.jamieallen.effectiveakka.pattern.cameo

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import org.jamieallen.effectiveakka.common._
import akka.actor._
import akka.event.LoggingReceive

object AccountBalanceResponseHandler {
  case object AccountRetrievalTimeout
}

class AccountBalanceResponseHandler(savingsAccounts: ActorRef, checkingAccounts: ActorRef,
  moneyMarketAccounts: ActorRef, originalSender: ActorRef) extends Actor with ActorLogging {

  import AccountBalanceResponseHandler._
  var checkingBalances, savingsBalances, mmBalances: Option[List[(Long, BigDecimal)]] = None
  def receive = LoggingReceive {
    case CheckingAccountBalances(balances) =>
      log.debug(s"Received checking account balances: $balances")
      checkingBalances = balances
      collectBalances
    case SavingsAccountBalances(balances) =>
      log.debug(s"Received savings account balances: $balances")
      savingsBalances = balances
      collectBalances
    case MoneyMarketAccountBalances(balances) =>
      log.debug(s"Received money market account balances: $balances")
      mmBalances = balances
      collectBalances
    case AccountRetrievalTimeout =>
      log.debug("Timeout occurred")
  }

  def collectBalances = (checkingBalances, savingsBalances, mmBalances) match {
    case (Some(c), Some(s), Some(m)) =>
      log.debug(s"Values received for all three account types")
      timeoutMessager.cancel
      originalSender ! AccountBalances(checkingBalances, savingsBalances, mmBalances)
      log.debug(s"Stopping context capturing actor")
      context.stop(self)
    case _ =>
  }

  import context.dispatcher
  val timeoutMessager = context.system.scheduler.scheduleOnce(250 milliseconds) {
    originalSender ! AccountRetrievalTimeout
    log.debug(s"Stopping context capturing actor")
    context.stop(self)
  }
}

class AccountBalanceRetriever(savingsAccounts: ActorRef, checkingAccounts: ActorRef, moneyMarketAccounts: ActorRef) extends Actor {
  def receive = {
    case GetCustomerAccountBalances(id) =>
      val originalSender = sender
      val handler = context.actorOf(Props(new AccountBalanceResponseHandler(savingsAccounts, checkingAccounts, moneyMarketAccounts, originalSender)))
      savingsAccounts.tell(GetCustomerAccountBalances(id), handler)
      checkingAccounts.tell(GetCustomerAccountBalances(id), handler)
      moneyMarketAccounts.tell(GetCustomerAccountBalances(id), handler)
  }
}
