package io.github.hapjava.server.impl.connections.sessionnotifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.min;

public class SessionNotifierImpl implements SessionNotifier {

    private final ArrayList<SessionNotificationListener> listeners = new ArrayList<>();

    private int registeredUsersCount = 0;

    private final Map<byte[], byte[]> activeDevices = new HashMap<>();
    private final Map<byte[], byte[]> inActiveDevices = new HashMap<>();

    public SessionNotifierImpl(int alreadyRegisteredUsers) {
        registeredUsersCount = alreadyRegisteredUsers;
    }

    @Override
    public void userRegistered() {
        registeredUsersCount++;
        notifyListeners();
    }

    @Override
    public void userRemoved() {
        registeredUsersCount--;
        notifyListeners();
    }

    @Override
    public void addListener(SessionNotificationListener listener) {
        listeners.add(listener);
        notifyListeners();
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

    @Override
    public void unregisterListener(SessionNotificationListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        // activeCount should never be more than registeredDevices
        // if it is there are definitely some broken connections,
        // but we don't know which one are those, so can't remove them or move to inactive
        // connections
        int activeDevicesCount = min(activeDevices.size(), registeredUsersCount);

        // inActiveCount should never be more than registeredDevices - activeDevices.size
        // if it is there are definitely some broken connections,
        // but unfortunately we don't know which are those
        int inActiveDevicesCount =
                min(inActiveDevices.size(), registeredUsersCount - activeDevicesCount);

        for (SessionNotificationListener listener : listeners) {
            listener.countUpdated(registeredUsersCount, activeDevicesCount, inActiveDevicesCount);
        }
    }
}
