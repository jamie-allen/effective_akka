package org.jamieallen.effectiveakka.pattern.cameo

import akka.testkit.{ TestKit, TestProbe, ImplicitSender }
import akka.actor.{ Actor, ActorLogging, ActorSystem, Props }
import org.scalatest.WordSpecLike
import org.scalatest.matchers.MustMatchers
import scala.concurrent.duration._
import org.jamieallen.effectiveakka.common._
import org.jamieallen.effectiveakka.pattern.cameo.AccountBalanceResponseHandler._

class CameoSpec extends TestKit(ActorSystem()) with ImplicitSender with WordSpecLike with MustMatchers {
  "An AccountBalanceRetriever" should {
    "return a list of account balances" in {
      val savingsAccountsProxy = system.actorOf(Props[SavingsAccountsProxyStub])
      val checkingAccountsProxy = system.actorOf(Props[CheckingAccountsProxyStub])
      val moneyMarketAccountsProxy = system.actorOf(Props[MoneyMarketAccountsProxyStub])
      val probe = TestProbe()

      val accountBalanceRetriever = system.actorOf(Props(new AccountBalanceRetriever(savingsAccountsProxy, checkingAccountsProxy, moneyMarketAccountsProxy)))
      accountBalanceRetriever.tell(GetCustomerAccountBalances(1L), probe.ref)
      val result = probe.expectMsgType[AccountBalances]
      result must equal(AccountBalances(Some(List((3, 15000))), Some(List((1, 150000), (2, 29000))), Some(List())))
    }

    "return a TimeoutException when timeout is exceeded" in {
      // Write a local stub to inject that will cause a timeout to occur
      val savingsAccountsProxy = system.actorOf(Props[TimingOutSavingsAccountProxyStub])
      val checkingAccountsProxy = system.actorOf(Props[CheckingAccountsProxyStub])
      val moneyMarketAccountsProxy = system.actorOf(Props[MoneyMarketAccountsProxyStub])
      val probe = TestProbe()

      val accountBalanceRetriever = system.actorOf(Props(new AccountBalanceRetriever(savingsAccountsProxy, checkingAccountsProxy, moneyMarketAccountsProxy)))
      accountBalanceRetriever.tell(GetCustomerAccountBalances(1L), probe.ref)
      probe.expectMsgType[AccountRetrievalTimeout.type]
    }
  }
}