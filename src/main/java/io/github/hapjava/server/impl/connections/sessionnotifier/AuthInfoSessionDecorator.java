package io.github.hapjava.server.impl.connections.sessionnotifier;

import io.github.hapjava.server.HomekitAuthInfo;
import java.math.BigInteger;

public class AuthInfoSessionDecorator implements HomekitAuthInfo {

  private final HomekitAuthInfo info;
  private final SessionNotifier notifier;

  public AuthInfoSessionDecorator(HomekitAuthInfo info, SessionNotifier notifier) {
    this.info = info;
    this.notifier = notifier;
  }

  @Override
  public String getPin() {
    return info.getPin();
  }

  @Override
  public String getSetupId() {
    return info.getSetupId();
  }

  @Override
  public String getMac() {
    return info.getMac();
  }

  @Override
  public BigInteger getSalt() {
    return info.getSalt();
  }

  @Override
  public byte[] getPrivateKey() {
    return info.getPrivateKey();
  }

  @Override
  public void createUser(String username, byte[] publicKey) {
    info.createUser(username, publicKey);
    notifier.userRegistered();
  }

  @Override
  public void removeUser(String username) {
    info.removeUser(username);
    notifier.userRemoved();
  }

  @Override
  public byte[] getUserPublicKey(String username) {
    return info.getUserPublicKey(username);
  }

  @Override
  public boolean hasUser() {
    return info.hasUser();
  }

  @Override
  public int getUsersCount() {
    return info.getUsersCount();
  }
}
