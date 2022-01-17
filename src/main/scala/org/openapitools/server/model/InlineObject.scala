package org.openapitools.server.model

/**
 * @param message  for example: ''null''
 * @param identifiers  for example: ''null''
*/
final case class InlineObject (
  message: String,
  identifiers: Seq[NotificationsIdentifiers]
)

