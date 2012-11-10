package org.jamieallen.effectiveakka.extrafinal

import akka.testkit.TestKit
import akka.testkit.ImplicitSender
import akka.actor.ActorSystem
import org.junit.runner.RunWith
import akka.actor.Props
import akka.testkit.TestProbe
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import scala.concurrent.Promise
import org.jamieallen.effectiveakka.extra1.AccountBalanceRetriever
import org.jamieallen.effectiveakka.extra1.AccountBalances
import org.jamieallen.effectiveakka.extra1.CheckingAccountProxy
import org.jamieallen.effectiveakka.extra1.MoneyMarketAccountsProxy
import org.jamieallen.effectiveakka.extra1.SavingsAccountProxy

@RunWith(classOf[JUnitRunner])
class ExtraFinalSpec extends TestKit(ActorSystem()) with ImplicitSender with WordSpec with MustMatchers {
  "An AccountBalanceRetriever" should {
    "return a list of account balances" in {
      val savingsAccountProxy = system.actorOf(Props[SavingsAccountProxy])
      val checkingAccountProxy = system.actorOf(Props[CheckingAccountProxy])
      val moneyMarketAccountProxy = system.actorOf(Props[MoneyMarketAccountsProxy])
      val probe = TestProbe()

      val accountBalanceRetriever = system.actorOf(Props(new AccountBalanceRetriever(savingsAccountProxy, checkingAccountProxy, moneyMarketAccountProxy)))
      accountBalanceRetriever.tell(GetCustomerAccountBalances(1L), probe.ref)
      probe.expectMsgType[Promise[AccountBalances]]
    }
  }
}