sealed trait JsValue
case class JsString(v: String) extends JsValue
case class JsNumber(v: BigDecimal) extends JsValue
case class JsBoolean(v: Boolean) extends JsValue
case class JsArray(v: Vector[JsValue]) extends JsValue
case class JsObject(v: Map[String, JsValue]) extends JsValue
case object JsNull extends JsValue
