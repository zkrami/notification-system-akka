package actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import org.openapitools.server.model.Identifier


object System {
  trait Command

  case object Start extends Command

  case class CreateIdentifier(identifier: String, replyTo: ActorRef[Reply]) extends Command

  case class GetIdentifiers(replyTo: ActorRef[Reply]) extends Command

  trait Reply

  case object SuccessReply extends Reply

  case object FailureReply extends Reply
  case class GetIdentifiersReply(identifiers: Seq[Identifier]) extends  Reply
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
      case CreateIdentifier(identifier, ref) =>
        // if the identifier exists already send failure
        if (identifiers.contains(identifier)){
          ref ! FailureReply
        }else{
          identifiers += identifier -> context.spawn(IdentifierActor(identifier), s"identifier-$identifier")
          ref ! SuccessReply
        }
        this
      case GetIdentifiers(ref) =>
        ref ! GetIdentifiersReply(identifiers.keys.map( s => Identifier(s)).toSeq)
        this
    }
}
