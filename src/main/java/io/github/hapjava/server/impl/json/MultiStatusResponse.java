package io.github.hapjava.server.impl.json;

class MultiStatusResponse extends HapJsonResponse {
  public MultiStatusResponse(byte[] body) {
    super(body);
  }

  @Override
  public int getStatusCode() {
    return 207;
  }
}
