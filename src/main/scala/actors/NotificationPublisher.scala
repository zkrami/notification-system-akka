package actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import org.openapitools.server.model.{Identifier, Notification}


object NotificationPublisher {
  trait Command

  case object Process extends Command

  case object IdentifierWorkerAck extends Command

  def apply(state: Map[String, ActorRef[IdentifierActor.Command]], recipients: Seq[Identifier], notification: Notification, ref: ActorRef[System.Reply]): Behavior[Command] =
    Behaviors.setup(context => new NotificationPublisher(context, state, recipients, notification, ref))


}

class NotificationPublisher(context: ActorContext[NotificationPublisher.Command], state: Map[String, ActorRef[IdentifierActor.Command]], recipients: Seq[Identifier], notification: Notification, ref: ActorRef[System.Reply]) extends AbstractBehavior[NotificationPublisher.Command](context) {

  import NotificationPublisher._

  var identifiersActor: Seq[ActorRef[IdentifierActor.Command]] = Seq.empty
  var messageReceived = 0

  def onProcess(): Behavior[Command] = {

    // if an identifier doesn't exist return failure
    if (!recipients.forall(id => state.contains(id.identifier))) {
      ref ! System.FailureReply
    } else {
      identifiersActor = recipients.map(id => state(id.identifier))

      identifiersActor.foreach(actor => {
        actor ! IdentifierActor.AddNotification(notification, context.self)
      })
    }

    this
  }

  def allIdentifiersReplied(): Unit = {
    ref ! System.SuccessReply

  }

  def onIdentifierWorkerAck(): Behavior[Command] = {
    messageReceived += 1

    if (messageReceived == identifiersActor.size) {
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
      case IdentifierWorkerAck => onIdentifierWorkerAck()

    }
  }
}
