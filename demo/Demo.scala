object Demo {
  private def nestedLists(depth: Int): Any = (1 to depth).foldLeft(Nil: Any)((acc, _) => List(acc))
  private def nestedSomes(depth: Int): Any = (1 to depth).foldLeft(None: Any)((acc, _) => Some(acc))

  private def attempt(label: String)(body: => Any): Unit =
    try { body; println(s"$label: ok") }
    catch { case _: StackOverflowError => println(s"$label: StackOverflowError") }

  def main(args: Array[String]): Unit = {
    val depth = 200000
    attempt(s"Some nested $depth deep (real tail call)")(AnyJsonProtocol.serializeValue(nestedSomes(depth)))
    attempt(s"List nested $depth deep (recursion through helper)")(AnyJsonProtocol.serializeValue(nestedLists(depth)))
  }
}
