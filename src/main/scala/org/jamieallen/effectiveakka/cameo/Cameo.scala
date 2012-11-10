package org.jamieallen.effectiveakka.cameo

import java.util.concurrent.TimeoutException
import scala.concurrent.{ ExecutionContext, Promise }
import scala.concurrent.duration._
import akka.actor._
import scala.math.BigDecimal.int2bigDecimal

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

class AccountBalanceResponseHandler(savingsAccounts: ActorRef, checkingAccounts: ActorRef,
  moneyMarketAccounts: ActorRef, originalSender: ActorRef) extends Actor {

  val promisedResult = Promise[AccountBalances]()
  var checkingBalances, savingsBalances, mmBalances: Option[List[(Long, BigDecimal)]] = None
  def receive = {
    case CheckingAccountBalances(balances) =>
      checkingBalances = balances
      collectBalances
    case SavingsAccountBalances(balances) =>
      savingsBalances = balances
      collectBalances
    case MoneyMarketAccountBalances(balances) =>
      mmBalances = balances
      collectBalances
  }

  def collectBalances = (checkingBalances, savingsBalances, mmBalances) match {
    case (Some(c), Some(s), Some(m)) =>
      if (promisedResult.trySuccess(AccountBalances(checkingBalances, savingsBalances, mmBalances)))
        sendResults
    case _ =>
  }

  implicit val ec: ExecutionContext = context.dispatcher
  def sendResults = {
    originalSender ! ((promisedResult.future.map(x => x)) recover { case t: TimeoutException => t })
    context.system.stop(self)
  }

  context.system.scheduler.scheduleOnce(250 milliseconds) {
    if (promisedResult.tryFailure(new TimeoutException))
      sendResults
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
