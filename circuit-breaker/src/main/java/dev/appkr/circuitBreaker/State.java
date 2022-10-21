package dev.appkr.circuitBreaker;

public class State {

  public static final int CLOSED = 1;
  public static final int OPEN = 2;
  public static final int HALF_OPEN = 3;

  int numberOfBufferedCalls = 0;
  int numberOfFailedCalls = 0;
  int numberOfSlowCalls = 0;
  int currentState = 1;
  long openAt = 0;

  public State() {
  }

  public void increaseNumberOfBufferedCalls() {
    this.numberOfBufferedCalls++;
  }

  public void increaseNumberOfFailedCalls() {
    this.numberOfFailedCalls++;
  }

  public void increaseNumberOfSlowCalls() {
    this.numberOfSlowCalls++;
  }

  public void transitionTo(int newState) {
    this.numberOfBufferedCalls = 0;
    this.numberOfFailedCalls = 0;
    this.numberOfSlowCalls = 0;
    this.currentState = newState;
    this.openAt = 0;
    if (newState == OPEN) {
      this.openAt = System.currentTimeMillis();
    }
  }

  public boolean isClosedState() {
    return currentState == CLOSED;
  }

  public boolean isOpenState() {
    return currentState == OPEN;
  }

  public boolean isHalfOpenState() {
    return currentState == HALF_OPEN;
  }

  public boolean isNumberOfBufferedCallsGreaterThanOrEqualTo(int baseline) {
    return numberOfBufferedCalls >= baseline;
  }

  public double getFailRate() {
    return (numberOfFailedCalls + numberOfSlowCalls) / (double)numberOfBufferedCalls;
  }

  public boolean isOpenDurationIsLongerThan(long baseline) {
    return (System.currentTimeMillis() - openAt) > baseline;
  }

  public int getNumberOfBufferedCalls() {
    return numberOfBufferedCalls;
  }

  public int getNumberOfFailedCalls() {
    return numberOfFailedCalls;
  }

  public int getNumberOfSlowCalls() {
    return numberOfSlowCalls;
  }

  @Override
  public String toString() {
    return "State{" +
        "numberOfBufferedCalls=" + numberOfBufferedCalls +
        ", numberOfFailedCalls=" + numberOfFailedCalls +
        ", numberOfSlowCalls=" + numberOfSlowCalls +
        ", currentState=" + currentState +
        ", openAt=" + openAt +
        '}';
  }
}
