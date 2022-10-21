package dev.appkr.circuitBreaker;

import java.util.function.Function;
import java.util.function.Supplier;

public class CircuitBreaker {

  private final String id;
  private final CircuitBreakerRegistry registry;

  public CircuitBreaker(String id, CircuitBreakerRegistry registry) {
    this.id = id;
    this.registry = registry;
  }

  public <T> T run(Supplier<T> businessLogic, Function<Throwable, T> fallback) {
    final long startTime = System.currentTimeMillis();

    T result;
    try {
      if (!registry.canAcceptCall(id)) {
        throw new RuntimeException("회로차단기가 오픈되었습니다");
      }

      result = businessLogic.get();
      registry.updateStateOf(id, startTime);
    } catch (Throwable t) {
      registry.recordFail(id);
      registry.updateStateOf(id, startTime);
      result = fallback.apply(t);
    }

    return result;
  }

  public CircuitBreakerRegistry getRegistry() {
    return registry;
  }
}
