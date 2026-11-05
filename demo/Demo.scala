object Demo {
  private def nestedLists(depth: Int): Any = (1 to depth).foldLeft(Nil: Any)((acc, _) => List(acc))
  private def nestedSomes(depth: Int): Any = (1 to depth).foldLeft(None: Any)((acc, _) => Some(acc))

  private def attempt(label: String)(body: => Any): String = {
    val outcome =
      try { body; "ok" }
      catch { case _: StackOverflowError => "StackOverflowError" }
    println(s"$label: $outcome")
    outcome
  }

  def main(args: Array[String]): Unit = {
    val depth = 200000
    val some = attempt(s"Some nested $depth deep (real tail call)")(AnyJsonProtocol.serializeValue(nestedSomes(depth)))
    val list = attempt(s"List nested $depth deep (recursion through helper)")(AnyJsonProtocol.serializeValue(nestedLists(depth)))
    if (some != "ok" || list != "StackOverflowError") sys.exit(1)
  }
}
