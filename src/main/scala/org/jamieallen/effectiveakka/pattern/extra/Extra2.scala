package org.jamieallen.effectiveakka.pattern.extra

import scala.concurrent.duration._
import org.jamieallen.effectiveakka.common._
import akka.actor.{ Actor, ActorRef }

class AccountBalanceRetriever2(savingsAccounts: ActorRef, checkingAccounts: ActorRef, moneyMarketAccounts: ActorRef) extends Actor {
  val checkingBalances, savingsBalances, mmBalances: Option[List[(Long, BigDecimal)]] = None
  var originalSender: Option[ActorRef] = None
  def receive = {
    case GetCustomerAccountBalances(id) =>
      originalSender = Some(sender)
      savingsAccounts ! GetCustomerAccountBalances(id)
      checkingAccounts ! GetCustomerAccountBalances(id)
      moneyMarketAccounts ! GetCustomerAccountBalances(id)
    case AccountBalances(cBalances, sBalances, mmBalances) =>
      (checkingBalances, savingsBalances, mmBalances) match {
        case (Some(c), Some(s), Some(m)) => originalSender.get ! AccountBalances(checkingBalances, savingsBalances,
          mmBalances)
        case _ =>
      }
  }
}
