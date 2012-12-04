package org.jamieallen.effectiveakka.pattern.stuntdouble

import akka.actor.Actor
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._

case object Start

class StuntDouble extends Actor {
  def receive = {
    case _ =>
  }
}

class StuntDoubleSupervisor extends Actor {
  override val supervisorStrategy = OneForOneStrategy() {
    case _ => Restart
  }

  def receive = {
    case Start =>
  }
}