# Benchmarks

The benchmarks can be executed via `mvn clean compile exec:exec`. It is possible to execute all benchmarks as well as
only specific benchmarks by executing the command in the corresponding module folder.

All benchmarks use [Java Microbenchmark Harness (JMH)](https://github.com/openjdk/jmh). By default, 10 forks with 10
warmup iterations and 10 relevant iterations are executed for each benchmark. This ensures reproducible and reliable
results, but can take many hours. However, the Maven profile `quick-benchmarks` can be activated for using only 1 fork
with 0 warmup iterations and 1 relevant iteration for getting quick results.
