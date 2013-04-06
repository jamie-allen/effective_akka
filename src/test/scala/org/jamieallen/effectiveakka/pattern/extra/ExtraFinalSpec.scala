package org.jamieallen.effectiveakka.pattern.extra

import akka.testkit.{ TestKit, ImplicitSender }
import akka.actor.{ ActorSystem, Props }
import org.junit.runner.RunWith
import akka.testkit.TestProbe
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import scala.concurrent.{ Await, Promise }
import scala.concurrent.duration._
import org.scalatest.junit.JUnitRunner
import org.jamieallen.effectiveakka.common._

@RunWith(classOf[JUnitRunner])
class ExtraFinalSpec extends TestKit(ActorSystem("TestAS")) with ImplicitSender with WordSpec with MustMatchers {
  "An AccountBalanceRetriever" should {
    "return a list of account balances" in {
      val savingsAccountProxy = system.actorOf(Props[SavingsAccountProxy])
      val checkingAccountProxy = system.actorOf(Props[CheckingAccountProxy])
      val moneyMarketAccountProxy = system.actorOf(Props[MoneyMarketAccountsProxy])
      val probe = TestProbe()

      val accountBalanceRetriever = system.actorOf(Props(new AccountBalanceRetrieverFinal(savingsAccountProxy, checkingAccountProxy, moneyMarketAccountProxy)))
      accountBalanceRetriever.tell(GetCustomerAccountBalances(1L), probe.ref)
      val promise = probe.expectMsgType[Promise[AccountBalances]]
      val result = Await.result(promise.future, 2 seconds)
      result must equal(AccountBalances(Some(List((3, 15000))), Some(List((1, 150000), (2, 29000))), Some(List())))
    }
  }
}