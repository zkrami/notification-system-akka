package actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}


object IdentifierActor {
  trait Command


  def apply(identifier: String): Behavior[Command] =
    Behaviors.setup(context => new IdentifierActor(context, identifier))
}

class IdentifierActor(context: ActorContext[IdentifierActor.Command], val identifier: String) extends AbstractBehavior[IdentifierActor.Command](context) {

  import IdentifierActor._


  override def onMessage(msg: Command): Behavior[Command] = ???
}