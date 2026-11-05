> LLM disclosure: this repository was built with Claude Code (an LLM-based tool). The compile results and the stack overflow demo below were produced by running the code in this repo, not taken from model output.

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

Compile one variant on one Scala version and read the compiler output:

```shell
sbt "++3.9.0! v1/compile"
```

Check every variant against the expected outcome on every Scala version, then run
the stack overflow demo on every version. Either one fails the build on a mismatch:

```shell
sbt "+verifyMatrix ; +demo/run"
```

With sbt 2.0, batch arguments are joined into one line, so separate commands with `;`.

## Results

Compiler output for the three variants, as observed:

| Version | v1 (helper) | v2 (lambda) | v3 (local def) | demo: v1 at runtime, 200000 deep   |
| ------- | ----------- | ----------- | -------------- | ---------------------------------- |
| 2.12.20 | silent      | error       | error          | Some: ok; List: StackOverflowError |
| 2.13.16 | silent      | error       | error          | Some: ok; List: StackOverflowError |
| 3.3.6   | silent      | error       | error          | Some: ok; List: StackOverflowError |
| 3.4.3   | silent      | error       | error          | Some: ok; List: StackOverflowError |
| 3.5.0   | silent      | error       | error          | Some: ok; List: StackOverflowError |
| 3.5.1   | silent      | error       | warning E199   | Some: ok; List: StackOverflowError |
| 3.5.2   | silent      | error       | warning E199   | Some: ok; List: StackOverflowError |
| 3.7.4   | silent      | error       | warning E199   | Some: ok; List: StackOverflowError |
| 3.9.0   | silent      | error       | warning E199   | Some: ok; List: StackOverflowError |
| 3.10.0  | silent      | error       | warning E199   | Some: ok; List: StackOverflowError |


The demo uses the default JVM thread stack. The depth at which `List` overflows depends on the machine.
