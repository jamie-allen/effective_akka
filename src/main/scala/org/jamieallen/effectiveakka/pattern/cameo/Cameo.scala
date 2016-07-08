package org.jamieallen.effectiveakka.pattern.cameo

import scala.concurrent.duration.DurationInt

import org.jamieallen.effectiveakka.common._

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.event.LoggingReceive

object AccountBalanceResponseHandler {
  case object AccountRetrievalTimeout

  def props(savingsAccounts: ActorRef, checkingAccounts: ActorRef,
    moneyMarketAccounts: ActorRef, originalSender: ActorRef): Props = {
    Props(new AccountBalanceResponseHandler(savingsAccounts, checkingAccounts,
      moneyMarketAccounts, originalSender))
  }
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
      sendResponseAndShutdown(AccountRetrievalTimeout)
  }

  def collectBalances = (checkingBalances, savingsBalances, mmBalances) match {
    case (Some(c), Some(s), Some(m)) =>
      log.debug(s"Values received for all three account types")
      timeoutMessager.cancel
      sendResponseAndShutdown(AccountBalances(checkingBalances, savingsBalances, mmBalances))
    case _ =>
  }

  def sendResponseAndShutdown(response: Any) = {
    originalSender ! response
    log.debug("Stopping context capturing actor")
    context.stop(self)
  }

  import context.dispatcher
  val timeoutMessager = context.system.scheduler.scheduleOnce(
    250 milliseconds, self, AccountRetrievalTimeout)
}

class AccountBalanceRetriever(savingsAccounts: ActorRef, checkingAccounts: ActorRef, moneyMarketAccounts: ActorRef) extends Actor {
  def receive = {
    case GetCustomerAccountBalances(id) =>
      val originalSender = sender
      val handler = context.actorOf(AccountBalanceResponseHandler.props(savingsAccounts, checkingAccounts, moneyMarketAccounts, originalSender), "cameo-message-handler")
      savingsAccounts.tell(GetCustomerAccountBalances(id), handler)
      checkingAccounts.tell(GetCustomerAccountBalances(id), handler)
      moneyMarketAccounts.tell(GetCustomerAccountBalances(id), handler)
  }
}
