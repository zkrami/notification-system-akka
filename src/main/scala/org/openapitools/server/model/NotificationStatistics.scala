package org.openapitools.server.model

/**
 * @param sent  for example: ''null''
 * @param received  for example: ''null''
*/
final case class NotificationStatistics(
                                      sent: Option[Seq[String]],
                                      received: Option[Seq[String]]
)

