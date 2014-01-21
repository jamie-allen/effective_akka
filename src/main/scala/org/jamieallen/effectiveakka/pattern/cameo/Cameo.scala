package org.jamieallen.effectiveakka.pattern.cameo

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import org.jamieallen.effectiveakka.common._
import akka.actor._
import akka.event.LoggingReceive

class Worker extends Actor {
  def receive = {
    case s: Seq[Int] => sender ! s.reduce(_ + _)
  }
}

class Delegator extends Actor {
  val worker = context.actorOf(Props[Worker])
  def receive = {
    case _ =>
      val responseHandler = context.actorOf(Props(new Actor() {
        def receive = {
          case x =>
            println("Got value: %d".format(x))
            context.stop(self)
        }
      }))
      worker.tell((1 to 100), responseHandler)
  }
}

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
  val timeoutMessager = context.system.scheduler.scheduleOnce(250 milliseconds) {
    self ! AccountRetrievalTimeout
  }
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
