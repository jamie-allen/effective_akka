package org.jamieallen.effectiveakka.pattern.extra

import java.util.concurrent.TimeoutException
import scala.concurrent.{ ExecutionContext, Promise }
import scala.concurrent.duration._
import org.jamieallen.effectiveakka.common._
import akka.actor.{ Actor, ActorRef, Props }

class AccountBalanceRetrieverFinal(savingsAccounts: ActorRef, checkingAccounts: ActorRef, moneyMarketAccounts: ActorRef) extends Actor {
  def receive = {
    case GetCustomerAccountBalances(id) => {
      val originalSender = sender
      implicit val ec: ExecutionContext = context.dispatcher

      context.actorOf(Props(new Actor() {
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
            if (promisedResult.trySuccess(AccountBalances(checkingBalances, savingsBalances, mmBalances))) {
              originalSender ! promisedResult.future
              context.system.stop(self)
            }
          case _ =>
        }

        savingsAccounts ! GetCustomerAccountBalances(id)
        checkingAccounts ! GetCustomerAccountBalances(id)
        moneyMarketAccounts ! GetCustomerAccountBalances(id)
        context.system.scheduler.scheduleOnce(250 milliseconds) {
          if (promisedResult.tryFailure(new TimeoutException)) {
            originalSender ! promisedResult.future
            context.system.stop(self)
          }
        }
      }))
    }
  }
}
