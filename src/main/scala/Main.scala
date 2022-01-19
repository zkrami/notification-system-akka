import actors.System
import actors.System.{FailureReply, SuccessReply}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import scala.io.StdIn
import akka.util.Timeout

import scala.concurrent.duration._
import org.openapitools.server.api._
import org.openapitools.server.model._
import akka.actor.typed.scaladsl.AskPattern._
import marshaller.DateMarshaller.DateFormat
import spray.json.RootJsonFormat


object Main extends App {

  implicit val timeout: Timeout = 5.seconds

  // init Actor System
  implicit val system = ActorSystem(System(), "notification-system")
  implicit val executionContext = system.executionContext


  object DefaultMarshaller extends DefaultApiMarshaller {
    implicit val identifiersFormat: RootJsonFormat[Seq[Identifier]] = immSeqFormat(jsonFormat1(Identifier))
    implicit val notificationJsonFormat: RootJsonFormat[Notification] = jsonFormat4(Notification)
    implicit val notificationStatisticsItemJsonFormat: RootJsonFormat[NotificationStatisticsItem] = jsonFormat2(NotificationStatisticsItem)


    override implicit def fromEntityUnmarshallerIdentifier: FromEntityUnmarshaller[Identifier] = jsonFormat1(Identifier)

    override implicit def fromEntityUnmarshallerInlineObject: FromEntityUnmarshaller[PublishNotificationPayload] = jsonFormat2(PublishNotificationPayload)

    override implicit def toEntityMarshallerNotificationarray: ToEntityMarshaller[Seq[Notification]] = immSeqFormat(jsonFormat4(Notification))

    override implicit def toEntityMarshallerInlineResponse200: ToEntityMarshaller[Statistics] = jsonFormat2(Statistics)

    override implicit def toEntityMarshallerInlineResponse2001: ToEntityMarshaller[NotificationStatistics] = jsonFormat1(NotificationStatistics)

    override implicit def toEntityMarshallerIdentifierarray: ToEntityMarshaller[Seq[Identifier]] = immSeqFormat(jsonFormat1(Identifier))

    override implicit def toEntityMarshallerIdentificationNotificationArray: ToEntityMarshaller[Seq[IdentifierNotification]] = immSeqFormat(jsonFormat2(IdentifierNotification))
  }

  object DefaultService extends DefaultApiService {
    /**
     * Code: 200, Message: List of identifiers, DataType: Seq[Identifier]
     */
    override def identifiersGet()(implicit toEntityMarshallerIdentifierarray: ToEntityMarshaller[Seq[Identifier]]): Route =
      requestContext => {
        val result = system ? (ref => System.GetIdentifiers(ref))
        result.flatMap {
          case System.GetIdentifiersReply(identifiers) => identifiersGet200(identifiers)(toEntityMarshallerIdentifierarray)(requestContext)
        }
      }

    /**
     * Code: 200, Message: Identifier deleted
     * Code: 400, Message: Identifier could not be deleted
     */
    override def identifiersIdentifierDelete(identifier: String): Route = requestContext => {
      val result = system ? (ref => System.DeleteIdentifier(identifier, ref))
      result.flatMap {
        case SuccessReply => identifiersDeleted200(requestContext)
        case FailureReply => identifiersDeleted400(requestContext)
      }
    }

    /**
     * Code: 200, Message: Identifier created
     * Code: 400, Message: Identifier could not be created
     */
    override def identifiersPost(identifier: Identifier): Route = requestContext => {
      val result = system ? (ref => System.CreateIdentifier(identifier, ref))
      result.flatMap {
        case SuccessReply => identifiersPost200(requestContext)
        case FailureReply => identifiersPost400(requestContext)
      }
    }


    /**
     * Code: 200, Message: List of all notifications sent through the system, DataType: Seq[Notification]
     */
    override def notificationsGet()(implicit toEntityMarshallerNotificationarray: ToEntityMarshaller[Seq[Notification]]): Route = requestContext => {
      val result = system ? (ref => System.GetNotifications(ref))
      result.flatMap {
        case System.GetNotificationsReply(notifications) => notificationsGet200(notifications)(toEntityMarshallerNotificationarray)(requestContext)
      }
    }


    /**
     * Code: 200, Message: Notifications created
     * Code: 400, Message: Notifications could not be created
     */
    override def notificationsPost(payload: PublishNotificationPayload): Route = requestContext => {
      val result = system ? (ref => System.SendNotification(payload.identifiers, payload.message, ref))
      result.flatMap {
        case SuccessReply => identifiersPost200(requestContext)
        case FailureReply => identifiersPost400(requestContext)
      }
    }

    /**
     * Code: 200, Message: Statistics of all notifications, DataType: InlineResponse200
     */
    override def statsGet()(implicit toEntityMarshallerInlineResponse200: ToEntityMarshaller[Statistics]): Route = requestContext => {
      val result = system ? (ref => System.GetNotifications(ref))
      result.flatMap {
        case System.QueryStatisticsReply(statistics) => statsGet200(statistics)(toEntityMarshallerInlineResponse200)(requestContext)
      }
    }

      /**
       * Code: 200, Message: List of identifier to whom notification has been sent
       */
      override def statsIdGet(id: String)(implicit toEntityMarshallerInlineResponse2001: ToEntityMarshaller[NotificationStatistics]): Route = requestContext => {
        val result = system ? (ref => System.QueryNotification(id, ref))
        result.flatMap {
          case System.QueryNotificationReply(stats) => statsIdGet200(stats)(toEntityMarshallerInlineResponse2001)(requestContext)
        }
      }

      /**
       * Code: 200, Message: The notifications designated to the identifier, DataType: Seq[Notification]
       */
      override def notificationsIdentifierGet(identifier: String, key: String)(implicit toEntityMarshallerIdentificationNotificationArray: ToEntityMarshaller[Seq[IdentifierNotification]]): Route = requestContext => {
        val result = system ? (ref => System.GetIdentifierNotifications(identifier, key, ref))
        result.flatMap {
          case System.GetIdentifierNotificationsReply(notifications) => notificationsIdentifierGet200(notifications)(toEntityMarshallerIdentificationNotificationArray)(requestContext)
          case FailureReply => notificationsIdentifierGet400(requestContext)
        }
      }
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
