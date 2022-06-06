package io.github.hapjava.server.impl.connections.session;

public interface SessionNotifier {
    void userRegistered(String user);

    void userRemoved(String user);

    void addListener(SessionNotificationListener listener);

    public interface SessionNotificationListener {
        void countUpdated(int registeredDevices, int activeDevices, int inActiveDevices);
    }
}
