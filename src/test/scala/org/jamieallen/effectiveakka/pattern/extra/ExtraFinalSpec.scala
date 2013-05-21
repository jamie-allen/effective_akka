package org.jamieallen.effectiveakka.pattern.extra

import akka.testkit.{ TestKit, TestProbe, ImplicitSender }
import akka.actor.{ Actor, ActorLogging, ActorSystem, Props }
import org.scalatest.WordSpecLike
import org.scalatest.matchers.MustMatchers
import scala.concurrent.duration._
import org.jamieallen.effectiveakka.common._
import org.jamieallen.effectiveakka.pattern.extra.AccountBalanceRetrieverFinal._

class ExtraFinalSpec extends TestKit(ActorSystem("ExtraTestAS")) with ImplicitSender with WordSpecLike with MustMatchers {
  "An AccountBalanceRetriever" should {
    "return a list of account balances" in {
      val probe2 = TestProbe()
      val probe1 = TestProbe()
      val savingsAccountsProxy = system.actorOf(Props[SavingsAccountsProxyStub], "extra-success-savings")
      val checkingAccountsProxy = system.actorOf(Props[CheckingAccountsProxyStub], "extra-success-checkings")
      val moneyMarketAccountsProxy = system.actorOf(Props[MoneyMarketAccountsProxyStub], "extra-success-money-markets")
      val accountBalanceRetriever = system.actorOf(Props(new AccountBalanceRetrieverFinal(savingsAccountsProxy, checkingAccountsProxy, moneyMarketAccountsProxy)), "extra-retriever")

      within(300 milliseconds) {
        probe1.send(accountBalanceRetriever, GetCustomerAccountBalances(1L))
        val result = probe1.expectMsgType[AccountBalances]
        result must equal(AccountBalances(Some(List((3, 15000))), Some(List((1, 150000), (2, 29000))), Some(List())))
      }
      within(300 milliseconds) {
        probe2.send(accountBalanceRetriever, GetCustomerAccountBalances(2L))
        val result = probe2.expectMsgType[AccountBalances]
        result must equal(AccountBalances(Some(List((6, 640000), (7, 1125000), (8, 40000))), Some(List((5, 80000))), Some(List((9, 640000), (10, 1125000), (11, 40000)))))
      }
    }

    "return a TimeoutException when timeout is exceeded" in {
      val savingsAccountsProxy = system.actorOf(Props[TimingOutSavingsAccountProxyStub], "extra-timing-out-savings")
      val checkingAccountsProxy = system.actorOf(Props[CheckingAccountsProxyStub], "extra-timing-out-checkings")
      val moneyMarketAccountsProxy = system.actorOf(Props[MoneyMarketAccountsProxyStub], "extra-timing-out-money-markets")
      val accountBalanceRetriever = system.actorOf(Props(new AccountBalanceRetrieverFinal(savingsAccountsProxy, checkingAccountsProxy, moneyMarketAccountsProxy)), "extra-timing-out-retriever")
      val probe = TestProbe()

      within(250 milliseconds, 500 milliseconds) {
        probe.send(accountBalanceRetriever, GetCustomerAccountBalances(1L))
        probe.expectMsg(AccountRetrievalTimeout)
      }
    }
  }
}