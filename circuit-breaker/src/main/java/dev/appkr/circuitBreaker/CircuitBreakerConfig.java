package dev.appkr.circuitBreaker;

public class CircuitBreakerConfig {

  private int minNumberOfCalls = 10;
  private long slowThreshold = 1_000; // millis
  private double failureRateThreshold = 0.5;
  private long transitionToHalfOpenStateIn = 10_000; // millis

  public CircuitBreakerConfig() {
  }

  public int getMinNumberOfCalls() {
    return minNumberOfCalls;
  }

  public long getSlowThreshold() {
    return slowThreshold;
  }

  public double getFailureRateThreshold() {
    return failureRateThreshold;
  }

  public long getTransitionToHalfOpenStateIn() {
    return transitionToHalfOpenStateIn;
  }

  @Override
  public String toString() {
    return "CircuitBreakerConfig{" +
        "minNumberOfCalls=" + minNumberOfCalls +
        ", slowThreshold=" + slowThreshold +
        ", failureRateThreshold=" + failureRateThreshold +
        ", transitionToHalfOpenStateIn=" + transitionToHalfOpenStateIn +
        '}';
  }
}
