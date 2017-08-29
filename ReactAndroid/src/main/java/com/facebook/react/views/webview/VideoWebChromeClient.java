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

import static android.view.ViewGroup.LayoutParams;

/**
 * Provides support for full-screen video on Android
 */
public class VideoWebChromeClient extends WebChromeClient {

  private final FrameLayout.LayoutParams FULLSCREEN_LAYOUT_PARAMS = new FrameLayout.LayoutParams(
    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER);

  private WebChromeClient.CustomViewCallback mCustomViewCallback;

  private Activity mActivity;
  private View mWebView;
  private View mVideoView;

  public VideoWebChromeClient(Activity activity, WebView webView) {
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
    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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
    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  }

  private ViewGroup getRootView() {
    return ((ViewGroup) mActivity.findViewById(android.R.id.content));
  }
}
