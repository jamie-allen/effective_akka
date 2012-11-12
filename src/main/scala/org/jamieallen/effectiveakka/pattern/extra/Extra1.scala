package org.jamieallen.effectiveakka.pattern.extra

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.util.Timeout
import org.jamieallen.effectiveakka.common._
import akka.actor.{ Actor, ActorRef }
import akka.pattern.ask

class AccountBalanceRetriever1(savingsAccounts: ActorRef, checkingAccounts: ActorRef, moneyMarketAccounts: ActorRef) extends Actor {
  implicit val timeout: Timeout = 100 milliseconds
  implicit val ec: ExecutionContext = context.dispatcher
  def receive = {
    case GetCustomerAccountBalances(id) =>
      val futSavings = savingsAccounts ? GetCustomerAccountBalances(id)
      val futChecking = checkingAccounts ? GetCustomerAccountBalances(id)
      val futMM = moneyMarketAccounts ? GetCustomerAccountBalances(id)
      val futBalances = for {
        savings <- futSavings.mapTo[Option[List[(Long, BigDecimal)]]]
        checking <- futChecking.mapTo[Option[List[(Long, BigDecimal)]]]
        mm <- futMM.mapTo[Option[List[(Long, BigDecimal)]]]
      } yield AccountBalances(savings, checking, mm)
      futBalances map (sender ! _)
  }
}
