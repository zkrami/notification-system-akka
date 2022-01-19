package actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.http.scaladsl.model.DateTime
import org.openapitools.server.model.{Identifier, IdentifierNotification, Notification}


object System {
  trait Command

  case object Start extends Command

  case class CreateIdentifier(identifier: Identifier, replyTo: ActorRef[Reply]) extends Command

  case class GetIdentifiers(replyTo: ActorRef[Reply]) extends Command

  case class DeleteIdentifier(identifier: String, replyTo: ActorRef[Reply]) extends Command

  case class GetNotifications(replyTo: ActorRef[Reply]) extends Command

  case class GetIdentifierNotifications(identifier: String, key: String, replyTo: ActorRef[Reply]) extends Command

  case class SendNotification(recipients: Seq[Identifier], message: String, replyTo: ActorRef[Reply]) extends Command


  trait Reply

  case object SuccessReply extends Reply

  case object FailureReply extends Reply

  case class GetIdentifiersReply(identifiers: Seq[Identifier]) extends Reply

  case class GetNotificationsReply(notifications: Seq[Notification]) extends Reply

  case class GetIdentifierNotificationsReply(notification: Seq[IdentifierNotification]) extends Reply

  def apply(): Behavior[Command] =
    Behaviors.setup(context => new System(context))
}

class System(context: ActorContext[System.Command]) extends AbstractBehavior[System.Command](context) {

  import System._

  var identifiers = Map.empty[String, ActorRef[IdentifierActor.Command]]
  var notifications: Set[Notification] = Set.empty

  override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case Start =>
        this
      case CreateIdentifier(identifier, ref) =>
        // if the identifier exists already send failure
        if (identifiers.contains(identifier.identifier)) {
          ref ! FailureReply
        } else {
          identifiers += identifier.identifier -> context.spawn(IdentifierActor(identifier), s"identifier-${identifier.identifier}")
          ref ! SuccessReply
        }
        this

      case GetIdentifiers(ref) =>
        ref ! GetIdentifiersReply(identifiers.keys.map(s => Identifier(s)).toSeq)
        this

      case SendNotification(recipients, message, replyTo) =>
        val notification = Notification(message, "not_implemented", new java.util.Date(), notifications.size.toString)
        notifications += notification
        val publisher = context.spawn(NotificationPublisher(this.identifiers, recipients, notification, replyTo), s"notification-publisher-${notification.id}")
        publisher ! NotificationPublisher.Process
        this

      case GetNotifications(replyTo) =>
        replyTo ! GetNotificationsReply(notifications.toSeq)
        this

      case GetIdentifierNotifications(identifier, key, replyTo) =>
        if (identifiers.contains(identifier)) {
          identifiers(identifier) ! IdentifierActor.GetNotifications(replyTo)
        } else {
          replyTo ! FailureReply
        }
        this
      case DeleteIdentifier(identifier, replyTo) =>
        if (identifiers.contains(identifier)) {
          context.stop(identifiers(identifier))
          identifiers -= identifier
          replyTo ! SuccessReply
        } else {
          replyTo ! FailureReply
        }
        this

    }
}
