package io.github.hapjava.server.impl.connections.session;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SessionNotifierTest {

    private final SessionNotifierImpl notifier = new SessionNotifierImpl();
    private final SessionNotifierImpl.SessionNotificationListener listener = mock(SessionNotifierImpl.SessionNotificationListener.class);


    @Before
    public void setUp() {
        registerUser();
    }

    @Test
    public void notifiesOnRegistrationStateChange() {
        notifier.addListener(listener);

        notifier.userRegistered("user");
        verify(listener).countUpdated(2, 0, 0);

        notifier.userRemoved("user");
        verify(listener).countUpdated(1, 0, 0);
    }

    @Test
    public void notifiesWhenDeviceBecomeActive() {
        byte[] readKey = new byte[]{(byte) 0x96, (byte) 0x9c, (byte) 0xcb};
        byte[] writeKey = new byte[]{0x7a, 0x31, 0x51};

        notifier.addListener(listener);

        notifier.setActive(readKey, writeKey);
        notifier.setInactive(readKey, writeKey);
        notifier.setActive(readKey, writeKey);
        verify(listener, times(2)).countUpdated(1, 1, 0);
    }

    @Test
    public void notifiesWhenDeviceBecomesInActive() {
        byte[] readKey = new byte[]{(byte) 0x96, (byte) 0x9c, (byte) 0xcb};
        byte[] writeKey = new byte[]{0x7a, 0x31, 0x51};

        notifier.addListener(listener);

        notifier.setActive(readKey, writeKey);
        notifier.setInactive(readKey, writeKey);
        notifier.setActive(readKey, writeKey);
        notifier.setInactive(readKey, writeKey);
        verify(listener, times(2)).countUpdated(1, 0, 1);
    }

    @Test
    public void notifiesWhenDeviceIsRemoved() {
        byte[] readKey = new byte[]{(byte) 0x96, (byte) 0x9c, (byte) 0xcb};
        byte[] writeKey = new byte[]{0x7a, 0x31, 0x51};

        notifier.addListener(listener);

        notifier.setActive(readKey, writeKey);
        notifier.setInactive(readKey, writeKey);
        notifier.removeDevice(readKey, writeKey);

        verify(listener).countUpdated(1, 0, 0);
    }

    private void registerUser() {
        notifier.userRegistered("pre added User");
    }

}