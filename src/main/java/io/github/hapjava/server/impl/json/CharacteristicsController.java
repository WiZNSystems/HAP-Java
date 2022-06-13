package io.github.hapjava.server.impl.json;

import io.github.hapjava.accessories.HomekitAccessory;
import io.github.hapjava.characteristics.Characteristic;
import io.github.hapjava.characteristics.EventableCharacteristic;
import io.github.hapjava.server.impl.HomekitRegistry;
import io.github.hapjava.server.impl.connections.SubscriptionManager;
import io.github.hapjava.server.impl.http.HomekitClientConnection;
import io.github.hapjava.server.impl.http.HttpRequest;
import io.github.hapjava.server.impl.http.HttpResponse;
import io.github.hapjava.server.impl.responses.NotFoundResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import javax.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacteristicsController {

  Logger logger = LoggerFactory.getLogger(CharacteristicsController.class);

  private final HomekitRegistry registry;
  private final SubscriptionManager subscriptions;

  public CharacteristicsController(HomekitRegistry registry, SubscriptionManager subscriptions) {
    this.registry = registry;
    this.subscriptions = subscriptions;
  }

  public HttpResponse get(HttpRequest request) throws Exception {
    String uri = request.getUri();
    // Characteristics are requested with /characteristics?id=1.1,2.1,3.1
    String query = uri.substring("/characteristics?id=".length());
    String[] ids = query.split(",");
    JsonArrayBuilder characteristics = Json.createArrayBuilder();
    for (String id : ids) {
      String[] parts = id.split("\\.");
      if (parts.length != 2) {
        logger.warn("Unexpected characteristics request: " + uri);
        return new NotFoundResponse();
      }
      int aid = Integer.parseInt(parts[0]);
      int iid = Integer.parseInt(parts[1]);
      JsonObjectBuilder characteristic = Json.createObjectBuilder();
      HomekitAccessory accessory = registry.getAccessoryById(aid);
      boolean isOffline = accessory.isOffline();
      Map<Integer, Characteristic> characteristicMap = registry.getCharacteristics(aid);
      if (!characteristicMap.isEmpty()) {
        Characteristic targetCharacteristic = characteristicMap.get(iid);
        if (targetCharacteristic != null) {
          if (isOffline) {
            characteristic.add("status", -70402);
          } else {
            targetCharacteristic.supplyValue(characteristic);
            characteristic.add("status", 0);
          }
          characteristics.add(characteristic.add("aid", aid).add("iid", iid).build());
        } else {
          logger.warn(
              "Accessory " + aid + " does not have characteristic " + iid + "Request: " + uri);
        }
      } else {
        logger.warn(
            "Accessory " + aid + " has no characteristics or does not exist. Request: " + uri);
      }
    }
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos)) {
      jsonWriter.write(
          Json.createObjectBuilder().add("characteristics", characteristics.build()).build());
      return new MultiStatusResponse(baos.toByteArray());
    }
  }

  public HttpResponse put(HttpRequest request, HomekitClientConnection connection)
      throws Exception {
    subscriptions.batchUpdate();
    boolean anyAccessoryOffline = false;
    JsonArrayBuilder characteristicsIfAnyAccessoryIsOffline = Json.createArrayBuilder();
    try {
      try (ByteArrayInputStream bais = new ByteArrayInputStream(request.getBody())) {
        JsonArray jsonCharacteristics =
            Json.createReader(bais).readObject().getJsonArray("characteristics");

        for (JsonValue value : jsonCharacteristics) {
          JsonObject jsonCharacteristic = (JsonObject) value;
          int aid = jsonCharacteristic.getInt("aid");
          int iid = jsonCharacteristic.getInt("iid");

          HomekitAccessory accessory = registry.getAccessoryById(aid);

          Characteristic characteristic = registry.getCharacteristics(aid).get(iid);

          JsonObjectBuilder characteristicJsonObjectIfAnyAccessoryIsOffline =
              Json.createObjectBuilder();

          if (jsonCharacteristic.containsKey("value")) {
            if (accessory.isOffline()) {
              anyAccessoryOffline = true;
              characteristicJsonObjectIfAnyAccessoryIsOffline.add("status", -70402);
            } else {
              characteristic.setValue(jsonCharacteristic.get("value"));
              characteristicJsonObjectIfAnyAccessoryIsOffline.add("status", 0);
            }
            characteristicsIfAnyAccessoryIsOffline.add(
                characteristicJsonObjectIfAnyAccessoryIsOffline
                    .add("aid", aid)
                    .add("iid", iid)
                    .build());
          }
          if (jsonCharacteristic.containsKey("ev")
              && characteristic instanceof EventableCharacteristic) {
            if (jsonCharacteristic.getBoolean("ev")) {
              subscriptions.addSubscription(
                  aid, iid, (EventableCharacteristic) characteristic, connection);
            } else {
              subscriptions.removeSubscription(
                  (EventableCharacteristic) characteristic, connection);
            }
          }
        }
      }
    } finally {
      subscriptions.completeUpdateBatch();
    }
    if (anyAccessoryOffline) {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
          JsonWriter jsonWriter = Json.createWriter(baos)) {
        jsonWriter.write(
            Json.createObjectBuilder()
                .add("characteristics", characteristicsIfAnyAccessoryIsOffline.build())
                .build());
        return new MultiStatusResponse(baos.toByteArray());
      }
    } else {
      return new HapJsonNoContentResponse();
    }
  }
}

// do i need to consider the case when  accessory is offline, and
// The controller registers for notifications against the ”current temperature” characteristic
// p.g. 78
// rn, not considering
