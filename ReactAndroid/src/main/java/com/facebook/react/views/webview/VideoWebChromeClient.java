package com.facebook.react.views.webview;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.content.pm.ActivityInfo;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import static android.view.ViewGroup.LayoutParams;

/**
 * Provides support for full-screen video on Android
 */
public class VideoWebChromeClient extends WebChromeClient {

  private final FrameLayout.LayoutParams FULLSCREEN_LAYOUT_PARAMS = new FrameLayout.LayoutParams(
    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER);

  private WebChromeClient.CustomViewCallback mCustomViewCallback;

  private ReactContext mReactContext;
  private Activity mActivity;
  private View mWebView;
  private View mVideoView;

  public VideoWebChromeClient(ReactContext context, Activity activity, WebView webView) {
    mReactContext = context;
    mWebView = webView;
    mActivity = activity;
  }

  @Override
  public void onShowCustomView(View view, CustomViewCallback callback) {
    if (mVideoView != null) {
      return;
    }

    // Store the view and it's callback for later, so we can dispose of them correctly
    mVideoView = view;
    mCustomViewCallback = callback;

    mVideoView.setVisibility(View.VISIBLE);

    mWebView.setVisibility(View.GONE);

    view.setBackgroundColor(Color.BLACK);

    getRootView().addView(view, FULLSCREEN_LAYOUT_PARAMS);

    mReactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("VideoBecameFullscreen", null);
  }

  @Override
  public void onHideCustomView() {
    if (mVideoView == null) {
      return;
    }

    mWebView.setVisibility(View.VISIBLE);

    mVideoView.setVisibility(View.GONE);

    // Remove the custom view from its container.
    getRootView().removeView(mVideoView);
    mVideoView = null;
    mCustomViewCallback.onCustomViewHidden();

    mReactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("VideoResignedFullscreen", null);
  }

  private ViewGroup getRootView() {
    return ((ViewGroup) mActivity.findViewById(android.R.id.content));
  }
}
