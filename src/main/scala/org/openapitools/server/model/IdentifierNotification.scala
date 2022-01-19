package org.openapitools.server.model

import java.util.Date

/**
 * @param delivered      for example: ''null''
 * @param `notification` for example: ''null''
 */
final case class IdentifierNotification(
                                         delivered: Boolean,
                                         notification: Notification
                                       )

