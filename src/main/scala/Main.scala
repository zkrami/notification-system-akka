import actors.System
import actors.System.{FailureReply, SuccessReply}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import akka.http.scaladsl.unmarshalling.FromStringUnmarshaller
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import scala.io.StdIn
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import org.openapitools.server.api._
import org.openapitools.server.model._
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.Behavior
import spray.json.RootJsonFormat

object Main extends App {

  implicit val timeout: Timeout = Timeout.zero

  // init Actor System
  implicit val system = ActorSystem(System(), "notification-system")
  implicit val executionContext = system.executionContext


  object DefaultMarshaller extends DefaultApiMarshaller {
    override implicit def fromEntityUnmarshallerIdentifier: FromEntityUnmarshaller[Identifier] = jsonFormat1(Identifier)

    override implicit def fromEntityUnmarshallerInlineObject: FromEntityUnmarshaller[InlineObject] = ???

    override implicit def toEntityMarshallerNotificationarray: ToEntityMarshaller[Seq[Notification]] = ???

    override implicit def toEntityMarshallerInlineResponse200: ToEntityMarshaller[Statistics] = ???

    override implicit def toEntityMarshallerInlineResponse2001: ToEntityMarshaller[NotificationStatistics] = ???

    override implicit def toEntityMarshallerIdentifierarray: ToEntityMarshaller[Seq[Identifier]] = immSeqFormat(jsonFormat1(Identifier))
  }

  object DefaultService extends DefaultApiService {
    /**
     * Code: 200, Message: List of identifiers, DataType: Seq[Identifier]
     */
    override def identifiersGet()(implicit toEntityMarshallerIdentifierarray: ToEntityMarshaller[Seq[Identifier]]): Route =
      requestContext => identifiersGet200(Seq(Identifier("test1")))(toEntityMarshallerIdentifierarray)(requestContext)

    /**
     * Code: 200, Message: Identifier created
     * Code: 400, Message: Identifier could not be created
     */
    override def identifiersIdentifierDelete(identifier: String): Route = ???

    /**
     * Code: 200, Message: Identifier created
     * Code: 400, Message: Identifier could not be created
     */
    override def identifiersPost(identifier: Identifier): Route = requestContext => {
      val result = system ? (ref => System.CreateIdentifier(identifier.identifier , ref ))
      result.flatMap{
        case SuccessReply => identifiersPost200(requestContext)
        case FailureReply => identifiersPost400(requestContext)
      }
    }


    /**
     * Code: 200, Message: List of all notifications sent through the system, DataType: Seq[Notification]
     */
    override def notificationsGet()(implicit toEntityMarshallerNotificationarray: ToEntityMarshaller[Seq[Notification]]): Route = ???

    /**
     * Code: 200, Message: The notifications designated to the identifier, DataType: Seq[Notification]
     */
    override def notificationsIdentifierGet(identifier: String, key: String)(implicit toEntityMarshallerNotificationarray: ToEntityMarshaller[Seq[Notification]]): Route = ???

    /**
     * Code: 200, Message: Notifications created
     * Code: 400, Message: Notifications could not be created
     */
    override def notificationsPost(inlineObject: Option[InlineObject]): Route = ???

    /**
     * Code: 200, Message: Statistics of all notifications, DataType: InlineResponse200
     */
    override def statsGet()(implicit toEntityMarshallerInlineResponse200: ToEntityMarshaller[Statistics]): Route = ???

    /**
     * Code: 200, Message: List of all notifications sent through the system, DataType: InlineResponse2001
     */
    override def statsIdGet(id: String)(implicit toEntityMarshallerInlineResponse2001: ToEntityMarshaller[NotificationStatistics]): Route = ???
  }

  val api = new DefaultApi(DefaultService, DefaultMarshaller)

  val host = "localhost"
  val port = 8001

  val bindingFuture = Http().newServerAt(host, port).bind(pathPrefix("api") {
    api.route
  })
  println(s"Server online at http://${host}:${port}/\nPress RETURN to stop...")

  // bindingFuture.failed.foreach { ex =>
  //  println(s"${ex} Failed to bind to ${host}:${port}!")
  //}

  StdIn.readLine() // let it run until user presses return
  // bindingFuture
  //  .flatMap(_.unbind()) // trigger unbinding from the port
  //.onComplete(_ => system.terminate()) // and shutdown when done
}