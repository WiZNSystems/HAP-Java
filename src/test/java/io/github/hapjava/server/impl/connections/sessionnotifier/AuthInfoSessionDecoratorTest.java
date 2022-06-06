package io.github.hapjava.server.impl.connections.sessionnotifier;

import io.github.hapjava.server.HomekitAuthInfo;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class AuthInfoSessionDecoratorTest {

    private final SessionNotifier sessionNotifier = mock(SessionNotifier.class);
    private final HomekitAuthInfo mockAuthInfo = mock(HomekitAuthInfo.class);
    private final AuthInfoSessionDecorator info =
            new AuthInfoSessionDecorator(mockAuthInfo, sessionNotifier);

    @Test
    public void callsUnderlyingAuthInfo() {
        info.getPin();
        info.getSetupId();
        info.getMac();
        info.getSalt();
        info.getPrivateKey();

        byte[] publicKey = {0x44, 0x33, 0x22, 0x11};
        info.createUser("user", publicKey);
        info.removeUser("user");
        info.getUserPublicKey("user");

        info.hasUser();
        info.getUsersCount();

        verify(mockAuthInfo).getPin();
        verify(mockAuthInfo).getSetupId();
        verify(mockAuthInfo).getMac();
        verify(mockAuthInfo).getSalt();
        verify(mockAuthInfo).getPrivateKey();
        verify(mockAuthInfo).createUser("user", publicKey);
        verify(mockAuthInfo).removeUser("user");
        verify(mockAuthInfo).getUserPublicKey("user");
        verify(mockAuthInfo).hasUser();
        verify(mockAuthInfo, atLeastOnce()).getUsersCount();
    }

    @Test
    public void callsSessionNotifierOnRegistrationStateChange() {
        byte[] publicKey = {0x44, 0x33, 0x22, 0x11};
        info.createUser("user", publicKey);

        InOrder inOrder = Mockito.inOrder(sessionNotifier, mockAuthInfo);
        inOrder.verify(mockAuthInfo).createUser("user", publicKey);
        inOrder.verify(sessionNotifier).userRegistered("user");

        info.removeUser("user");
        inOrder.verify(mockAuthInfo).removeUser("user");
        inOrder.verify(sessionNotifier).userRemoved("user");
    }

    @Test
    public void callsSessionNotifierOnInitiation() {
        final SessionNotifier sessionNotifier2 = mock(SessionNotifier.class);
        final HomekitAuthInfo mockAuthInfo2 = mock(HomekitAuthInfo.class);

        when(mockAuthInfo2.getUsersCount()).thenReturn(1);

        final AuthInfoSessionDecorator info2 =
                new AuthInfoSessionDecorator(mockAuthInfo2, sessionNotifier2);

        verify(sessionNotifier2).registeredDeviceCount(1);
    }
}
