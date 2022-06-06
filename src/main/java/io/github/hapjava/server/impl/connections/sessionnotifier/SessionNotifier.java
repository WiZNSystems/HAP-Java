package io.github.hapjava.server.impl.connections.sessionnotifier;

public interface SessionNotifier {
  void userRegistered();

  void userRemoved();

  void addListener(SessionNotificationListener listener);

  void setActive(byte[] readKey, byte[] writeKey);

  void setInactive(byte[] readKey, byte[] writeKey);

  void removeDevice(byte[] readKey, byte[] writeKey);

  public interface SessionNotificationListener {
    void countUpdated(int registeredDevices, int activeDevices, int inActiveDevices);
  }
}
