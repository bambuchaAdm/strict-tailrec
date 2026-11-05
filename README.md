# tailrec-strict-repro

Minimal reproducers for how `@tailrec` treats recursion that goes through a helper
method, a lambda, or a local `def`, across Scala 2.12, 2.13 and 3.x.

`stubs/Stubs.scala` stands in for the spray-json types (`JsValue`, `JsArray`, `JsNull`).

| Project | Where the recursive call sits |
|---------|-------------------------------|
| `v1`    | `private def` helper that calls back into the `@tailrec` method |
| `v2`    | lambda inside the `@tailrec` method |
| `v3`    | local `def` inside the `@tailrec` method |
| `demo`  | runs `v1` on deeply nested `Some` and `List` values |

## Run

One variant on one version:

```shell
sbt "++3.9.0!" v1/compile
```

Every variant on every version. A failing compile is expected for some cells, so run
them as separate sbt invocations:

```shell
for ver in 2.12.20 2.13.16 3.3.6 3.4.3 3.5.0 3.5.1 3.5.2 3.7.4 3.9.0 3.10.0; do
  for v in v1 v2 v3; do
    echo "##### $ver $v"
    sbt -batch "++$ver!" "$v/clean" "$v/compile"
  done
done
```

Stack overflow demo:

```shell
sbt "++3.9.0!" demo/run
```

## Results

Compiler output for the three variants, as observed:

| Version | v1 (helper) | v2 (lambda) | v3 (local def) |
|---------|-------------|-------------|----------------|
| 2.12.20 | silent      | error       | error          |
| 2.13.16 | silent      | error       | error          |
| 3.3.6   | silent      | error       | error          |
| 3.4.3   | silent      | error       | error          |
| 3.5.0   | silent      | error       | error          |
| 3.5.1   | silent      | error       | warning E199   |
| 3.5.2   | silent      | error       | warning E199   |
| 3.7.4   | silent      | error       | warning E199   |
| 3.9.0   | silent      | error       | warning E199   |
| 3.10.0  | silent      | error       | warning E199   |

`demo` on 3.9.0 (JVM default stack): `Some` nested 200000 deep completes,
`List` nested 200000 deep throws `StackOverflowError`. Other versions are not run.
