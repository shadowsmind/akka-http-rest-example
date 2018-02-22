package example.api.directives

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller

trait CommonDirectives {

  def postJson[T](um: FromRequestUnmarshaller[T]): Directive1[T] =
    post & entity(um)

  def deletingIdPath: Directive1[Long] = path(LongNumber) & delete

  def idPath: Directive1[Long] = path(LongNumber) & get

}

object CommonDirectives extends CommonDirectives
