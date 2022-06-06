package io.github.hapjava.server.impl.connections.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SessionNotifierImpl implements SessionNotifier {

    private final ArrayList<SessionNotificationListener> listeners = new ArrayList<>();

    private final ArrayList<String> registeredDevices = new ArrayList<>();
    private final Map<byte[], byte[]> activeDevices = new HashMap<>();
    private final Map<byte[], byte[]> inActiveDevices = new HashMap<>();

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

    @Override
    public void setActive(byte[] readKey, byte[] writeKey) {
        activeDevices.put(readKey, writeKey);
        inActiveDevices.remove(readKey, writeKey);
        notifyListeners();
    }

    @Override
    public void setInactive(byte[] readKey, byte[] writeKey) {
        inActiveDevices.put(readKey, writeKey);
        activeDevices.remove(readKey, writeKey);
        notifyListeners();
    }

    @Override
    public void removeDevice(byte[] readKey, byte[] writeKey) {
        inActiveDevices.remove(readKey, writeKey);
        activeDevices.remove(readKey, writeKey);
        notifyListeners();
    }

    private void notifyListeners() {
        for (SessionNotificationListener listener : listeners) {
            listener.countUpdated(registeredDevices.size(), activeDevices.size(), inActiveDevices.size());
        }
    }
}
