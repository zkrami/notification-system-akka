package actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import org.openapitools.server.model.{Identifier, Notification}


object IdentifierActor {
  trait Command

  case class AddNotification(notification: Notification, sender: ActorRef[NotificationPublisher.Command]) extends Command

  def apply(identifier: Identifier): Behavior[Command] =
    Behaviors.setup(context => new IdentifierActor(context, identifier))
}

class IdentifierActor(context: ActorContext[IdentifierActor.Command], val identifier: Identifier) extends AbstractBehavior[IdentifierActor.Command](context) {

  import IdentifierActor._

  var notifications: Set[Notification] = Set.empty

  override def onMessage(msg: Command): Behavior[Command] = msg match {
    case AddNotification(notification, sender) => {
      notifications += notification
      sender ! NotificationPublisher.IdentifierWorkerAck
      this
    }
  }
}