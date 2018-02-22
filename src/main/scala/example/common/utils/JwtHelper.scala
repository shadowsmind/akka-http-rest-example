package example.common.utils

import pdi.jwt.{ Jwt, JwtAlgorithm, JwtClaim }

import scala.util.Try

object JwtHelper {

  private val algorithm = JwtAlgorithm.HS512

  def encode(content: String, secret: String): String = {
    val claim = JwtClaim(content = content)

    Jwt.encode(claim, secret, algorithm)
  }

  def decode(token: String, secret: String): Try[String] = {
    Jwt.decode(token, secret, Seq(algorithm))
  }

}
