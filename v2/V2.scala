import scala.annotation.tailrec
object AnyJsonProtocol {
  @tailrec
  def serializeValue(value: Any): JsValue = {
    value match {
      case value: List[Any] => JsArray(value.map(AnyJsonProtocol.serializeValue).toVector)
      case None             => JsNull
      case Some(inner)      => serializeValue(inner)
    }
  }
}
