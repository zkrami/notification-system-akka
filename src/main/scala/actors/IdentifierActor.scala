package actors

import actors.System.GetIdentifierNotificationsReply
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import org.openapitools.server.model.{Identifier, IdentifierNotification, Notification}


object IdentifierActor {
  trait Command

  case class AddNotification(notification: Notification, sender: ActorRef[NotificationPublisher.Command]) extends Command

  case class GetNotifications(replyTo: ActorRef[System.Reply]) extends Command

  def apply(identifier: Identifier): Behavior[Command] =
    Behaviors.setup(context => new IdentifierActor(context, identifier))
}

class IdentifierActor(context: ActorContext[IdentifierActor.Command], val identifier: Identifier) extends AbstractBehavior[IdentifierActor.Command](context) {

  import IdentifierActor._

  var notifications: Set[IdentifierNotification] = Set.empty

  override def onMessage(msg: Command): Behavior[Command] = msg match {
    case AddNotification(notification, sender) => {
      notifications += IdentifierNotification(delivered = false, notification)
      sender ! NotificationPublisher.IdentifierWorkerAck
      this
    }
    case GetNotifications(replyTo) => {
      replyTo ! GetIdentifierNotificationsReply(notifications.toSeq)
      notifications = notifications.map(notification => IdentifierNotification(delivered = true, notification.notification))
      this
    }

  }
}