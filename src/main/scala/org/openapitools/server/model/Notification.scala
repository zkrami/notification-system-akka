package org.openapitools.server.model

/**
 * @param message  for example: ''null''
 * @param `type`  for example: ''null''
 * @param date  for example: ''null''
 * @param id  for example: ''null''
*/
final case class Notification (
  message: String,
  `type`: String,
  date: Any,
  id: String
)

