package dev.appkr.circuitBreaker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

public class ThreadSafetyTest {

  static final String ID = "threadSafetyTest";
  static final int NUMBER_OF_TEST = 10;
  final Executor executor = Executors.newFixedThreadPool(NUMBER_OF_TEST);

  @Test
  void run() {
    final CircuitBreaker circuitBreaker = CircuitBreakerFactory.create(ID);
    final List<CompletableFuture<Object>> futures = new ArrayList<>();

    // When: 멀티스레드에서 같은 CircuitBreaker 인스턴스 사용
    for (int i = 0; i < NUMBER_OF_TEST; i++) {
      final CompletableFuture<Object> future = CompletableFuture
          .supplyAsync(() -> {
            System.out.println("Running business logic in " + Thread.currentThread().getName());
            return circuitBreaker.run(this::businessLogic, this::fallback);
          }, executor);
      futures.add(future);
    }

    while (true) {
      final boolean allDone = futures.stream().allMatch(Future::isDone);
      if (allDone) {
        break;
      }
    }

    // Then: CircuitBreakerRegistry에 값이 정확히 기록되었나 확인한다
    System.out.println("registry = " + circuitBreaker.getRegistry());
    assertEquals(NUMBER_OF_TEST, circuitBreaker.getRegistry().getStateOf(ID).getNumberOfBufferedCalls());
  }

  Supplier<String> businessLogic() {
    return () -> "Hello CircuitBreaker";
  }

  String fallback(Throwable t) {
    return "Fallback greeting";
  }
}
