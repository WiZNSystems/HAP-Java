package io.github.hapjava.server.impl.connections.sessionnotifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class SessionNotifierTest {

  private final SessionNotifierImpl notifier = new SessionNotifierImpl(1);
  private final SessionNotifierImpl.SessionNotificationListener listener =
      mock(SessionNotifierImpl.SessionNotificationListener.class);

  @Before
  public void setUp() {
    notifier.addListener(listener);
    clearInvocations(listener);
  }

  @Test
  public void notifiesOnRegistrationStateChange() {
    notifier.userRegistered();
    verify(listener).countUpdated(2, 0, 0);

    notifier.userRemoved();
    verify(listener).countUpdated(1, 0, 0);
  }

  @Test
  public void notifiesWhenDeviceBecomeActive() {
    byte[] readKey = new byte[] {(byte) 0x96, (byte) 0x9c, (byte) 0xcb};
    byte[] writeKey = new byte[] {0x7a, 0x31, 0x51};

    notifier.setActive(readKey, writeKey);
    notifier.setInactive(readKey, writeKey);
    notifier.setActive(readKey, writeKey);
    verify(listener, times(2)).countUpdated(1, 1, 0);
  }

  @Test
  public void notifiesWhenDeviceBecomesInActive() {
    byte[] readKey = new byte[] {(byte) 0x96, (byte) 0x9c, (byte) 0xcb};
    byte[] writeKey = new byte[] {0x7a, 0x31, 0x51};

    notifier.setActive(readKey, writeKey);
    notifier.setInactive(readKey, writeKey);
    notifier.setActive(readKey, writeKey);
    notifier.setInactive(readKey, writeKey);
    verify(listener, times(2)).countUpdated(1, 0, 1);
  }

  @Test
  public void notifiesWhenDeviceIsRemoved() {
    byte[] readKey = new byte[] {(byte) 0x96, (byte) 0x9c, (byte) 0xcb};
    byte[] writeKey = new byte[] {0x7a, 0x31, 0x51};

    notifier.setActive(readKey, writeKey);
    notifier.setInactive(readKey, writeKey);
    notifier.removeDevice(readKey, writeKey);

    verify(listener).countUpdated(1, 0, 0);
  }

  @Test
  public void notifiesWithSumOfActiveAndInactiveAlwaysLessThanRegistered() {
    byte[] readKey = new byte[] {(byte) 0x96, (byte) 0x9c, (byte) 0xcb};
    byte[] writeKey = new byte[] {0x7a, 0x31, 0x51};
    notifier.setActive(readKey, writeKey);
    notifier.setInactive(readKey, writeKey);

    byte[] readKey2 = new byte[] {(byte) 0x95, (byte) 0x9d, (byte) 0xeb};
    byte[] writeKey2 = new byte[] {0x72, 0x3f, 0x41};

    notifier.setActive(readKey2, writeKey2);

    verify(listener, never()).countUpdated(1, 1, 1);
    verify(listener, never()).countUpdated(eq(1), gt(1), gt(1));
  }

  @Test
  public void notifiesWithActiveCountNeverMoreThanRegistered() {
    byte[] readKey = new byte[] {(byte) 0x96, (byte) 0x9c, (byte) 0xcb};
    byte[] writeKey = new byte[] {0x7a, 0x31, 0x51};

    notifier.setActive(readKey, writeKey);

    byte[] readKey2 = new byte[] {(byte) 0x36, (byte) 0x92, (byte) 0x3b};
    byte[] writeKey2 = new byte[] {0x7d, 0x41, 0x53};

    notifier.setActive(readKey2, writeKey2);

    ArgumentCaptor<Integer> registered = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Integer> active = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Integer> inactive = ArgumentCaptor.forClass(Integer.class);

    verify(listener, atLeastOnce())
        .countUpdated(registered.capture(), active.capture(), inactive.capture());

    assertThat(registered.getAllValues().get(1)).isEqualTo(1);
    assertThat(active.getAllValues().get(1)).isLessThanOrEqualTo(1);
    assertThat(inactive.getAllValues().get(1)).isGreaterThanOrEqualTo(0).isLessThanOrEqualTo(1);
  }

  @Test
  public void notifiesNewListeners() {
    byte[] readKey = new byte[] {(byte) 0x96, (byte) 0x9c, (byte) 0xcb};
    byte[] writeKey = new byte[] {0x7a, 0x31, 0x51};

    notifier.setActive(readKey, writeKey);

    SessionNotifier.SessionNotificationListener listener2 =
        mock(SessionNotifier.SessionNotificationListener.class);
    notifier.addListener(listener2);

    verify(listener2).countUpdated(1, 1, 0);
  }
}
