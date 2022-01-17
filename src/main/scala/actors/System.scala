package actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext


object System {
  trait Command

  case object Start extends Command

  case class CreateIdentifier(identifier: String , replyTo : ActorRef[Reply]) extends Command

  trait Reply
  case object SuccessReply extends Reply
  case object FailureReply extends Reply

  def apply(): Behavior[Command] =
    Behaviors.setup(context => new System(context))
}

class System(context: ActorContext[System.Command]) extends AbstractBehavior[System.Command](context) {

  import System._

  var identifiers = Map.empty[String, ActorRef[IdentifierActor.Command]]

  override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case Start =>
        this
      case CreateIdentifier(identifier , ref) =>
        identifiers += identifier -> context.spawn(IdentifierActor(identifier), s"identifier-$identifier")
        ref ! SuccessReply
        this
    }
}
