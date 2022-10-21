package dev.appkr.circuitBreaker;

import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

public class CircuitBreakerRegistry {

  private static CircuitBreakerRegistry INSTANCE;

  private final ConcurrentHashMap<String, State> state = new ConcurrentHashMap<>();

  private final CircuitBreakerConfig config;

  public static CircuitBreakerRegistry getInstance(String id, CircuitBreakerConfig config) {
    if (INSTANCE == null) {
      INSTANCE = new CircuitBreakerRegistry(config);
    }
    if (!INSTANCE.state.containsKey(id)) {
      INSTANCE.state.computeIfAbsent(id, k -> new State());
    }
    return INSTANCE;
  }

  private CircuitBreakerRegistry(CircuitBreakerConfig config) {
    this.config = config;
  }

  public CircuitBreakerConfig getConfig() {
    return config;
  }

  public State getStateOf(String id) {
    return state.get(id);
  }

  public void recordFail(String id) {
    getStateOf(id).increaseNumberOfFailedCalls();
  }

  public boolean canAcceptCall(String id) {
    return !getStateOf(id).isOpenState();
  }

  public void updateStateOf(String id, long startTimestamp) {
    final State currentState = getStateOf(id);
    // numberOfBufferedCalls 값을 증가시킨다
    currentState.increaseNumberOfBufferedCalls();

    // 느리게 작동했는 지 확인한다
    final long elapsedTime = System.currentTimeMillis() - startTimestamp;
    if (elapsedTime > config.getSlowThreshold()) {
      // numberOfSlowCalls 값을 증가시킨다
      getStateOf(id).increaseNumberOfSlowCalls();
    }

    final boolean satisfiesMinCalls = currentState.isNumberOfBufferedCallsGreaterThanOrEqualTo(config.getMinNumberOfCalls());
    final double failureRate = currentState.getFailRate();

    // OPEN으로 전환할지 검사한다
    final boolean failedTooMuch = failureRate >= config.getFailureRateThreshold();
    if (satisfiesMinCalls && failedTooMuch) {
      currentState.transitionTo(State.OPEN);
    }

    // HALF_OPEN으로 전환할지 검사한다
    final boolean enoughTimeHasPassedUnderOpenState = currentState.isOpenDurationIsLongerThan(config.getTransitionToHalfOpenStateIn());
    if (currentState.isOpenState() && enoughTimeHasPassedUnderOpenState) {
      currentState.transitionTo(State.HALF_OPEN);
    }

    // CLOSED로 전환할지 검사한다
    final boolean readyToClose = failureRate < config.getFailureRateThreshold();
    if (currentState.isHalfOpenState() && satisfiesMinCalls && readyToClose) {
      currentState.transitionTo(State.CLOSED);
    }
  }

  @Override
  public String toString() {
    return "CircuitBreakerRegistry{" +
        "state=" + state +
        ", config=" + config +
        '}';
  }
}
