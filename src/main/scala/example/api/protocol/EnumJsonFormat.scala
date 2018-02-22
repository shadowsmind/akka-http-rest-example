package example.api.protocol

import spray.json.{ JsString, JsValue, RootJsonFormat, _ }

class EnumJsonFormat[T <: Enumeration](val enu: T) extends RootJsonFormat[T#Value] {

  def write(obj: T#Value): JsString = JsString(obj.toString)

  def read(json: JsValue): enu.Value = {
    json match {
      case JsString(txt) ⇒ enu.withName(txt)
      case something     ⇒ deserializationError(s"Expected a value from enum $enu instead of $something")
    }
  }

}

object EnumJsonFormat {

  def apply[T <: Enumeration](enu: T): EnumJsonFormat[T] = new EnumJsonFormat[T](enu)

}
