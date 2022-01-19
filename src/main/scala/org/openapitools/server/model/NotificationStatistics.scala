package org.openapitools.server.model


final case class NotificationStatisticsItem(
                                             identifier: String,
                                             delivered: Boolean
                                           )


final case class NotificationStatistics(stats: Seq[NotificationStatisticsItem])
