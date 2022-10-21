package dev.appkr.circuitBreaker;

public class CircuitBreakerFactory {

  public static CircuitBreaker create(String id) {
    final CircuitBreakerRegistry registry = CircuitBreakerRegistry.getInstance(id, new CircuitBreakerConfig());
    return new CircuitBreaker(id, registry);
  }
}
