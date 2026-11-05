import scala.annotation.tailrec
object AnyJsonProtocol {
  @tailrec
  def serializeValue(value: Any): JsValue = {
    value match {
      case value: List[Any] => helper(value)
      case None             => JsNull
      case Some(inner)      => serializeValue(inner)
    }
  }

  private def helper(list: List[Any]): JsArray = {
    JsArray(list.map(AnyJsonProtocol.serializeValue).toVector)
  }
}
