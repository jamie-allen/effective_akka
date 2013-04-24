package org.jamieallen.effectiveakka.pattern.extra

import java.util.concurrent.TimeoutException
import scala.concurrent.{ ExecutionContext, Promise }
import scala.concurrent.duration._
import org.jamieallen.effectiveakka.common._
import akka.actor.{ Actor, ActorRef, Props, ActorLogging }
import akka.event.LoggingReceive

class AccountBalanceRetrieverFinal(savingsAccounts: ActorRef, checkingAccounts: ActorRef, moneyMarketAccounts: ActorRef) extends Actor with ActorLogging {
  def receive = {
    case GetCustomerAccountBalances(id) => {
      log.debug(s"Received GetCustomerAccountBalances for ID: $id from $sender")
      val originalSender = sender
      implicit val ec: ExecutionContext = context.dispatcher

      context.actorOf(Props(new Actor() {
        val promisedResult = Promise[AccountBalances]()
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
        }

        def collectBalances = (checkingBalances, savingsBalances, mmBalances) match {
          case (Some(c), Some(s), Some(m)) =>
            log.debug(s"Values received for all three account types")
            if (promisedResult.trySuccess(AccountBalances(checkingBalances, savingsBalances, mmBalances)))
              sendResponseAndShutdown
          case _ =>
        }

        def sendResponseAndShutdown = {
          log.debug(s"Sending timeout failure back to original sender")
          originalSender ! promisedResult.future
          log.debug(s"Stopping context capturing actor")
          context.system.stop(self)
        }

        savingsAccounts ! GetCustomerAccountBalances(id)
        checkingAccounts ! GetCustomerAccountBalances(id)
        moneyMarketAccounts ! GetCustomerAccountBalances(id)
        context.system.scheduler.scheduleOnce(250 milliseconds) {
          if (promisedResult.tryFailure(new TimeoutException))
            sendResponseAndShutdown
        }
      }))

      log.debug("REMOVING MY BEHAVIOR!")
      context.become(Actor.emptyBehavior, true)
    }
  }
}
