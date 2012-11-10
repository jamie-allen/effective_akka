package org.jamieallen.effectiveakka.cameo

import akka.testkit.{ TestKit, ImplicitSender }
import akka.actor.{ ActorSystem, Props }
import org.junit.runner.RunWith
import akka.testkit.TestProbe
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import scala.concurrent.Promise
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CameoSpec extends TestKit(ActorSystem()) with ImplicitSender with WordSpec with MustMatchers {
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