package org.openapitools.server.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import akka.http.scaladsl.unmarshalling.FromStringUnmarshaller
import org.openapitools.server.AkkaHttpHelper._
import org.openapitools.server.model.{Identifier, IdentifierNotification, Notification, NotificationStatistics, PublishNotificationPayload, Statistics}


class DefaultApi(
                  defaultService: DefaultApiService,
                  defaultMarshaller: DefaultApiMarshaller
                ) {


  import defaultMarshaller._

  lazy val route: Route =
    path("identifiers") {
      get {
        defaultService.identifiersGet()
      }
    } ~
      path("identifiers" / Segment) { (identifier) =>
        delete {
          defaultService.identifiersIdentifierDelete(identifier = identifier)
        }
      } ~
      path("identifiers") {
        post {
          entity(as[Identifier]) { identifier =>
            defaultService.identifiersPost(identifier = identifier)
          }
        }
      } ~
      path("notifications") {
        get {
          defaultService.notificationsGet()
        }
      } ~
      path("notifications" / "identifier") {
        get {
          parameters("identifier".as[String], "key".as[String]) { (identifier, key) =>
            defaultService.notificationsIdentifierGet(identifier = identifier, key = key)
          }
        }
      } ~
      path("notifications") {
        post {
          entity(as[PublishNotificationPayload]) { inlineObject =>
            defaultService.notificationsPost(inlineObject = inlineObject)
          }
        }
      } ~
      path("stats") {
        get {
          defaultService.statsGet()
        }
      } ~
      path("stats" / Segment) { (id) =>
        get {
          defaultService.statsIdGet(id = id)
        }
      }
}


trait DefaultApiService {

  def identifiersGet200(responseIdentifierarray: Seq[Identifier])(implicit toEntityMarshallerIdentifierarray: ToEntityMarshaller[Seq[Identifier]]): Route =
    complete((200, responseIdentifierarray))

  /**
   * Code: 200, Message: List of identifiers, DataType: Seq[Identifier]
   */
  def identifiersGet()
                    (implicit toEntityMarshallerIdentifierarray: ToEntityMarshaller[Seq[Identifier]]): Route

  def identifiersIdentifierDelete200: Route =
    complete((200, "Identifier created"))

  def identifiersIdentifierDelete400: Route =
    complete((400, "Identifier could not be created"))

  /**
   * Code: 200, Message: Identifier created
   * Code: 400, Message: Identifier could not be created
   */
  def identifiersIdentifierDelete(identifier: String): Route

  def identifiersDeleted200: Route =
    complete((200, "Identifier Deleted"))

  def identifiersDeleted400: Route =
    complete((400, "Identifier could not be deleted"))

  def identifiersPost200: Route =
    complete((200, "Identifier created"))

  def identifiersPost400: Route =
    complete((400, "Identifier could not be created"))

  /**
   * Code: 200, Message: Identifier created
   * Code: 400, Message: Identifier could not be created
   */
  def identifiersPost(identifier: Identifier): Route

  def notificationsGet200(responseNotificationarray: Seq[Notification])(implicit toEntityMarshallerNotificationarray: ToEntityMarshaller[Seq[Notification]]): Route =
    complete((200, responseNotificationarray))

  /**
   * Code: 200, Message: List of all notifications sent through the system, DataType: Seq[Notification]
   */
  def notificationsGet()
                      (implicit toEntityMarshallerNotificationarray: ToEntityMarshaller[Seq[Notification]]): Route

  def notificationsIdentifierGet200(responseNotificationarray: Seq[IdentifierNotification])(implicit toEntityMarshallerNotificationarray:  ToEntityMarshaller[Seq[IdentifierNotification]] ): Route =
    complete((200, responseNotificationarray))


  def notificationsIdentifierGet400 : Route =
    complete((400, "Identifier could not be recognized"))

  /**
   * Code: 200, Message: The notifications designated to the identifier, DataType: Seq[Notification]
   */
  def notificationsIdentifierGet(identifier: String, key: String)
                                (implicit toEntityMarshallerIdentificationNotificationArray: ToEntityMarshaller[Seq[IdentifierNotification]]): Route

  def notificationsPost200: Route =
    complete((200, "Notifications created"))

  def notificationsPost400: Route =
    complete((400, "Notifications could not be created"))

  /**
   * Code: 200, Message: Notifications created
   * Code: 400, Message: Notifications could not be created
   */
  def notificationsPost(inlineObject: PublishNotificationPayload): Route

  def statsGet200(responseInlineResponse200: Statistics)(implicit toEntityMarshallerInlineResponse200: ToEntityMarshaller[Statistics]): Route =
    complete((200, responseInlineResponse200))

  /**
   * Code: 200, Message: Statistics of all notifications, DataType: InlineResponse200
   */
  def statsGet()
              (implicit toEntityMarshallerInlineResponse200: ToEntityMarshaller[Statistics]): Route

  def statsIdGet200(responseInlineResponse2001: NotificationStatistics)(implicit toEntityMarshallerInlineResponse2001: ToEntityMarshaller[NotificationStatistics]): Route =
    complete((200, responseInlineResponse2001))

  /**
   * Code: 200, Message: List of all notifications sent through the system, DataType: InlineResponse2001
   */
  def statsIdGet(id: String)
                (implicit toEntityMarshallerInlineResponse2001: ToEntityMarshaller[NotificationStatistics]): Route

}

trait DefaultApiMarshaller {
  implicit def fromEntityUnmarshallerIdentifier: FromEntityUnmarshaller[Identifier]

  implicit def fromEntityUnmarshallerInlineObject: FromEntityUnmarshaller[PublishNotificationPayload]


  implicit def toEntityMarshallerNotificationarray: ToEntityMarshaller[Seq[Notification]]

  implicit def toEntityMarshallerInlineResponse200: ToEntityMarshaller[Statistics]

  implicit def toEntityMarshallerInlineResponse2001: ToEntityMarshaller[NotificationStatistics]

  implicit def toEntityMarshallerIdentifierarray: ToEntityMarshaller[Seq[Identifier]]

  implicit def toEntityMarshallerIdentificationNotificationArray: ToEntityMarshaller[Seq[IdentifierNotification]]
}

