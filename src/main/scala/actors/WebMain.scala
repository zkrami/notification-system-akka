package actors
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext



object WebMain {
  trait Command
  case object Start extends Command
  case class GetStats(msg: String, replyTo: ActorRef[Reply]) extends Command
  case class GetStatsList(replyTo: ActorRef[Reply]) extends Command
  case class PostCreerClients(consommation: String, nombre: Int, replyTo: ActorRef[Reply]) extends Command

  trait Reply
  case class Stats(conso: String, quantite: Int, produit: Int) extends Reply
  case class ListStats(stats: List[Stats]) extends Reply
  case object FailedCreation extends Reply
  case object SuccessfulCreation extends Reply

  def apply(): Behavior[Command] =
    Behaviors.setup(context => new WebMain(context))
}

class WebMain(context: ActorContext[WebMain.Command]) extends AbstractBehavior[WebMain.Command](context) {

  import WebMain._

  override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case Start =>
        this
    }
}
