package actors

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import org.openapitools.server.model.Statistics


object StatisticsAggregator {
  trait Command

  case object Process extends Command

  case class IdentifierWorkerResponse(received: Int, delivered: Int) extends Command

  def apply(state: Map[String, ActorRef[IdentifierActor.Command]], ref: ActorRef[System.Reply]): Behavior[Command] =
    Behaviors.setup(context => new StatisticsAggregator(context, state, ref))


}


class StatisticsAggregator(context: ActorContext[StatisticsAggregator.Command], state: Map[String, ActorRef[IdentifierActor.Command]], ref: ActorRef[System.Reply]) extends AbstractBehavior[StatisticsAggregator.Command](context) {

  import StatisticsAggregator._

  var messageReceived = 0
  var sent = 0
  var delivered = 0

  def onProcess(): Behavior[Command] = {

    // send query to all IdentifierActor
    state.values.foreach(_ ! IdentifierActor.QueryStatistics(context.self))

    this
  }

  def allIdentifiersReplied(): Unit = {
    ref ! System.QueryStatisticsReply(Statistics(sent, delivered))

  }

  def onIdentifierWorkerResponse(received: Int, delivered: Int): Behavior[Command] = {
    messageReceived += 1
    this.sent += received
    this.delivered += delivered

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
      case IdentifierWorkerResponse(received, delivered) => onIdentifierWorkerResponse(received, delivered)
    }
  }
}
