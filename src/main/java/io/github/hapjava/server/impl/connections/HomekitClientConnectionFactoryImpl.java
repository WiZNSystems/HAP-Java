package io.github.hapjava.server.impl.connections;

import io.github.hapjava.server.HomekitAuthInfo;
import io.github.hapjava.server.impl.HomekitRegistry;
import io.github.hapjava.server.impl.connections.sessionnotifier.SessionNotifier;
import io.github.hapjava.server.impl.http.HomekitClientConnection;
import io.github.hapjava.server.impl.http.HomekitClientConnectionFactory;
import io.github.hapjava.server.impl.http.HttpResponse;
import io.github.hapjava.server.impl.jmdns.JmdnsHomekitAdvertiser;

import java.util.function.Consumer;

public class HomekitClientConnectionFactoryImpl implements HomekitClientConnectionFactory {

    private final HomekitAuthInfo authInfo;
    private final HomekitRegistry registry;
    private final SubscriptionManager subscriptions;
    private final JmdnsHomekitAdvertiser advertiser;
    private final SessionNotifier notifier;

    public HomekitClientConnectionFactoryImpl(
            HomekitAuthInfo authInfo,
            HomekitRegistry registry,
            SubscriptionManager subscriptions,
            JmdnsHomekitAdvertiser advertiser,
            SessionNotifier notifier) {
        this.registry = registry;
        this.authInfo = authInfo;
        this.subscriptions = subscriptions;
        this.advertiser = advertiser;
        this.notifier = notifier;
    }

    @Override
    public HomekitClientConnection createConnection(Consumer<HttpResponse> outOfBandMessageCallback) {
        return new ConnectionImpl(
                authInfo, registry, outOfBandMessageCallback, subscriptions, advertiser, notifier);
    }
}
