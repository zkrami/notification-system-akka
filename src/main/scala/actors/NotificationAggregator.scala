package actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import org.openapitools.server.model.{Identifier, Notification, NotificationStatistics, NotificationStatisticsItem}


object NotificationAggregator {
  trait Command

  case object Process extends Command

  case class IdentifierWorkerResponse(identifier: Identifier, received: Boolean, delivered: Boolean) extends Command

  def apply(state: Map[String, ActorRef[IdentifierActor.Command]], notificationId: String, ref: ActorRef[System.Reply]): Behavior[Command] =
    Behaviors.setup(context => new NotificationAggregator(context, state, notificationId, ref))


}


class NotificationAggregator(context: ActorContext[NotificationAggregator.Command], state: Map[String, ActorRef[IdentifierActor.Command]], notificationId: String, ref: ActorRef[System.Reply]) extends AbstractBehavior[NotificationAggregator.Command](context) {

  import NotificationAggregator._

  var messageReceived = 0
  var result: Seq[NotificationStatisticsItem] = List[NotificationStatisticsItem]()

  def onProcess(): Behavior[Command] = {

    // send query to all IdentifierActor
    state.values.foreach(_ ! IdentifierActor.QueryNotification(this.notificationId, context.self))

    this
  }

  def allIdentifiersReplied(): Unit = {
    ref ! System.QueryNotificationReply(NotificationStatistics(this.result))

  }

  def onIdentifierWorkerResponse(identifier: Identifier, received: Boolean, delivered: Boolean): Behavior[Command] = {
    messageReceived += 1

    if (received) {
      result = result.appended(NotificationStatisticsItem(identifier.identifier, delivered))
    }

    if (messageReceived == state.size) {
      allIdentifiersReplied()
      Behaviors.stopped
    }
    else {
      this
    }
  }


  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case Process => onProcess()
      case IdentifierWorkerResponse(identifier, received, delivered) => onIdentifierWorkerResponse(identifier, received, delivered)

    }
  }
}
