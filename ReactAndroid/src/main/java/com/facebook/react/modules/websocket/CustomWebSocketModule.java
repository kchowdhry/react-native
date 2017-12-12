package com.facebook.react.modules.websocket;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import okhttp3.WebSocket;

public class CustomWebSocketModule extends WebSocketModule {

  private Map<Integer, WebSocket> superWebSocketConnections;
  private Method notifyWebSocketFailedMethod;

  public CustomWebSocketModule(final ReactApplicationContext context) {
    super(context);
    try {
      Field mWebSocketConnectionsField = getClass().getSuperclass().getDeclaredField("mWebSocketConnections");
      mWebSocketConnectionsField.setAccessible(true);
      this.superWebSocketConnections = (Map<Integer, WebSocket>) mWebSocketConnectionsField.get(this);
      Class[] types = {Integer.TYPE, String.class};
      this.notifyWebSocketFailedMethod = getClass().getSuperclass().getDeclaredMethod("notifyWebSocketFailed", types);
      this.notifyWebSocketFailedMethod.setAccessible(true);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Override
  @ReactMethod
  public void connect(String url, @javax.annotation.Nullable ReadableArray protocols, @javax.annotation.Nullable ReadableMap headers, int id) {
    super.connect(url, protocols, headers, id);
  }

  @Override
  @ReactMethod
  public void close(int code, String reason, int id) {
    super.close(code, reason, id);
  }

  @Override
  @ReactMethod
  public void sendBinary(String base64String, int id) {
    super.sendBinary(base64String, id);
    try {
      WebSocket client = this.superWebSocketConnections.get(id);
      if (client == null) {
        this.superWebSocketConnections.remove(id);
        this.notifyWebSocketFailedMethod.invoke(this, id, "Unknown WebSocket id");
        return;
      } else {
        super.sendBinary(base64String, id);
      }
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Override
  @ReactMethod
  public void ping(int id) {
    super.ping(id);
  }

  @Override
  @ReactMethod
  public void send(String message, int id) {
    try {
      WebSocket client = this.superWebSocketConnections.get(id);
      if (client == null) {
        this.superWebSocketConnections.remove(id);
        this.notifyWebSocketFailedMethod.invoke(this, id, "Unknown WebSocket id");
        return;
      } else {
        super.send(message, id);
      }
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }
}
