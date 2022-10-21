package dev.appkr.circuitBreaker;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircuitBreakerRegistryTest {

  @Test
  void recordFail() {
    final String ID = "foo";
    final CircuitBreakerRegistry sut = CircuitBreakerRegistry.getInstance(ID, new CircuitBreakerConfig());

    sut.recordFail(ID);
    System.out.println("sut = " + sut);
    assertEquals(1, sut.getStateOf(ID).getNumberOfFailedCalls());
  }

  @Test
  void transitionToOpenState_dueToSlowCalls() {
    final String ID = "bar";
    final CircuitBreakerRegistry sut = CircuitBreakerRegistry.getInstance(ID, new CircuitBreakerConfig());
    final CircuitBreakerConfig config = sut.getConfig();

    // 10번 느린 호출을 시뮬레이션하여 OPEN 상태로 변경
    final long startTime = System.currentTimeMillis() - config.getSlowThreshold() - 1_000;
    for (int i = 0; i < config.getMinNumberOfCalls(); i++) {
      sut.updateStateOf(ID, startTime);
    }

    System.out.println("sut = " + sut);
    assertTrue(sut.getStateOf(ID).isOpenState());

    // canAcceptCall 실패 시나리오 검증
    assertFalse(sut.canAcceptCall(ID));
  }

  @Test
  @Disabled("HALF_OPEN 검증을 위해 강제 지연을 시뮬레이션하므로, 꼭 필요할 때만 테스트할 것")
  void transitionToHalfOpenState_afterTimePasses() throws InterruptedException {
    final String ID = "baz";
    final CircuitBreakerRegistry sut = CircuitBreakerRegistry.getInstance(ID, new CircuitBreakerConfig());
    final CircuitBreakerConfig config = sut.getConfig();

    // 10번 느린 호출을 시뮬레이션하여 OPEN 상태로 변경
    final long startTime = System.currentTimeMillis() - config.getSlowThreshold() - 1_000;
    for (int i = 0; i < config.getMinNumberOfCalls(); i++) {
      sut.updateStateOf(ID, startTime);
    }

    // 일정 시간 블록킹하여 HALF_OPEN 상태로 변경
    Thread.sleep(config.getTransitionToHalfOpenStateIn() + 1);
    sut.updateStateOf(ID, System.currentTimeMillis());

    System.out.println("sut = " + sut);
    assertTrue(sut.getStateOf(ID).isHalfOpenState());
  }

  @Test
  void canAcceptCall() {
    final String ID = "qux";
    final CircuitBreakerRegistry sut = CircuitBreakerRegistry.getInstance(ID, new CircuitBreakerConfig());

    System.out.println("sut = " + sut);
    assertTrue(sut.getStateOf(ID).isClosedState());
    assertTrue(sut.canAcceptCall(ID));
  }
}
