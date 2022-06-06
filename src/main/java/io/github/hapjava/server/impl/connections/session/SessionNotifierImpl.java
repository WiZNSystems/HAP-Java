package io.github.hapjava.server.impl.connections.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.min;

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
        int registeredDevicesCount = this.registeredDevices.size();

        // activeCount should never be more than registeredDevices
        // if it is there are definitely some broken connections,
        // but we don't know which one are those, so can't remove them or move to inactive connections
        int activeDevicesCount = min(activeDevices.size(), registeredDevicesCount);

        // inActiveCount should never be more than registeredDevices - activeDevices.size
        // if it is there are definitely some broken connections,
        // but unfortunately we don't know which are those
        int inActiveDevicesCount = min(inActiveDevices.size(), registeredDevicesCount - activeDevicesCount);


        for (SessionNotificationListener listener : listeners) {
            listener.countUpdated(registeredDevicesCount, activeDevicesCount, inActiveDevicesCount);
        }
    }
}
