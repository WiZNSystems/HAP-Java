package io.github.hapjava.server.impl.connections.session;

import io.github.hapjava.server.HomekitAuthInfo;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AuthInfoSessionDecoratorTest {

    private final SessionNotifier sessionNotifier = mock(SessionNotifier.class);
    private final HomekitAuthInfo mockAuthInfo = mock(HomekitAuthInfo.class);
    private final AuthInfoSessionDecorator info = new AuthInfoSessionDecorator(mockAuthInfo, sessionNotifier);

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


        verify(mockAuthInfo).getPin();
        verify(mockAuthInfo).getSetupId();
        verify(mockAuthInfo).getMac();
        verify(mockAuthInfo).getSalt();
        verify(mockAuthInfo).getPrivateKey();
        verify(mockAuthInfo).createUser("user", publicKey);
        verify(mockAuthInfo).removeUser("user");
        verify(mockAuthInfo).getUserPublicKey("user");
        verify(mockAuthInfo).hasUser();
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


}