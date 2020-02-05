package com.authorizer.shared.rich

import java.time.ZonedDateTime

object RichZonedDateTime {

  implicit class Conversion(value: ZonedDateTime) {
    def toMilliSeconds: Long = value.toInstant.toEpochMilli

    def toSeconds: Long = toMilliSeconds / 1000
  }

}
