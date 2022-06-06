package io.github.hapjava.server.impl.connections.session;

public interface SessionNotifier {
    void userRegistered(String user);

    void userRemoved(String user);

    public interface SessionNotificationListener {
        void countUpdated(int registeredDevices, int activeDevices, int inActiveDevices);
    }
}
