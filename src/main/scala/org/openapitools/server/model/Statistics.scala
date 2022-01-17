package org.openapitools.server.model

/**
 * @param sent  for example: ''null''
 * @param received  for example: ''null''
*/
final case class Statistics(
                             sent: Option[Int],
                             received: Option[Int]
)

