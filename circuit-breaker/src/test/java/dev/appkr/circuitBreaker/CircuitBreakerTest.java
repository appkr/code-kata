package dev.appkr.circuitBreaker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircuitBreakerTest {

  @Test
  void successCase() {
    final String ID = "successCase";
    final CircuitBreaker circuitBreaker = CircuitBreakerFactory.create(ID);
    final String greeting = circuitBreaker.run(() -> {
      return "Hello CircuitBreaker";
    }, throwable -> {
      return "Fallback greeting";
    });

    System.out.println("registry = " + circuitBreaker.getRegistry());
    assertEquals("Hello CircuitBreaker", greeting);
  }

  @Test
  void failCase() {
    final String ID = "failCase";
    final CircuitBreaker circuitBreaker = CircuitBreakerFactory.create(ID);
    final CircuitBreakerRegistry registry = circuitBreaker.getRegistry();
    final CircuitBreakerConfig config = registry.getConfig();
    for (int i = 0; i < config.getMinNumberOfCalls(); i++) {
      circuitBreaker.run(() -> {
        if (true) throw new RuntimeException("ERROR");
        return "Hello CircuitBreaker";
      }, throwable -> {
        return "Fallback greeting";
      });
      System.out.println(registry);
    }

    assertFalse(registry.canAcceptCall(ID));
  }
}