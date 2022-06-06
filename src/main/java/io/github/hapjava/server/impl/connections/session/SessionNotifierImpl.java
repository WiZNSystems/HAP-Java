package io.github.hapjava.server.impl.connections.session;

import java.util.ArrayList;

public class SessionNotifierImpl implements SessionNotifier {

    private final ArrayList<SessionNotificationListener> listeners = new ArrayList<>();

    private final ArrayList<String> registeredDevices = new ArrayList<>();

    @Override
    public void userRegistered(String user) {
        registeredDevices.add(user);
        notifyListeners();
    }

    @Override
    public void userRemoved(String user) {
        registeredDevices.remove(user);
        notifyListeners();
    }

    @Override
    public void addListener(SessionNotificationListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (SessionNotificationListener listener : listeners) {
            listener.countUpdated(registeredDevices.size(), 0, 0);
        }
    }
}
