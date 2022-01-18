package org.openapitools.server.model


import java.util.Date
/**
 * @param message for example: ''null''
 * @param `type`  for example: ''null''
 * @param date    for example: ''null''
 * @param id      for example: ''null''
 */
final case class Notification(
                               message: String,
                               `type`: String,
                               date: Date,
                               id: String
                             )

