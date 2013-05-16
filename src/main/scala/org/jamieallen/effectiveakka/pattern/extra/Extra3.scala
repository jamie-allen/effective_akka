package org.jamieallen.effectiveakka.pattern.extra

import org.jamieallen.effectiveakka.common._
import akka.actor.{ Actor, ActorRef, Props }

class AccountBalanceRetriever3(savingsAccounts: ActorRef, checkingAccounts: ActorRef, moneyMarketAccounts: ActorRef) extends Actor {
  val checkingBalances, savingsBalances, mmBalances: Option[List[(Long, BigDecimal)]] = None
  var originalSender: Option[ActorRef] = None
  def receive = {
    case GetCustomerAccountBalances(id) => {
      context.actorOf(Props(new Actor() {
        var checkingBalances, savingsBalances, mmBalances: Option[List[(Long, BigDecimal)]] = None
        val originalSender = sender
        def receive = {
          case CheckingAccountBalances(balances) =>
            checkingBalances = balances
            isDone
          case SavingsAccountBalances(balances) =>
            savingsBalances = balances
            isDone
          case MoneyMarketAccountBalances(balances) =>
            mmBalances = balances
            isDone
        }

        def isDone =
          (checkingBalances, savingsBalances, mmBalances) match {
            case (Some(c), Some(s), Some(m)) =>
              originalSender ! AccountBalances(checkingBalances, savingsBalances, mmBalances)
              context.stop(self)
            case _ =>
          }

        savingsAccounts ! GetCustomerAccountBalances(id)
        checkingAccounts ! GetCustomerAccountBalances(id)
        moneyMarketAccounts ! GetCustomerAccountBalances(id)
      }))
    }
  }
}
