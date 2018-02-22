package example.common.utils

import java.sql.Timestamp
import java.time.LocalDateTime

import example.common.domain.DateTime

object DateHelper {

  def now: DateTime = LocalDateTime.now()

  def nowTimestamp: Long = System.currentTimeMillis()

  def toTimestamp(date: DateTime): Long = Timestamp.valueOf(date).getTime

  def isPassed(date: DateTime, expirySeconds: Long): Boolean = date.plusSeconds(expirySeconds).isAfter(now)

}
