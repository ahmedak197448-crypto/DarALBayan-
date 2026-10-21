package com.daralbayan.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.webkit.WebView;

public class MainActivity extends Activity {
  private WebView webView;
  @Override public void onCreate(Bundle state) {
    super.onCreate(state);
    webView = new WebView(this);
    setContentView(webView);
    WebSettings s = webView.getSettings();
    s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setAllowFileAccess(true);
    s.setDefaultTextEncodingName("UTF-8");
    webView.setWebChromeClient(new WebChromeClient());
    webView.setWebViewClient(new WebViewClient());
    webView.addJavascriptInterface(new AppBridge(this, webView), "DarAlBayanBridge");
    webView.loadUrl("file:///android_asset/index.html");
  }
  @Override public void onBackPressed() { if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
  public static class AppBridge {
    private final Activity activity; private final WebView view;
    AppBridge(Activity a, WebView w) { activity=a; view=w; }
    @JavascriptInterface public void printCurrentPage() {
      activity.runOnUiThread(() -> { PrintManager pm=(PrintManager)activity.getSystemService(Context.PRINT_SERVICE); if(pm!=null){ android.print.PrintDocumentAdapter adapter=view.createPrintDocumentAdapter("DarAlBayan"); pm.print("DarAlBayan",adapter,new PrintAttributes.Builder().build()); }});
    }
    @JavascriptInterface public void shareText(String title, String text) {
      activity.runOnUiThread(() -> { Intent i=new Intent(Intent.ACTION_SEND); i.setType("text/plain"); i.putExtra(Intent.EXTRA_SUBJECT,title); i.putExtra(Intent.EXTRA_TEXT,text); activity.startActivity(Intent.createChooser(i,"مشاركة عبر")); });
    }
    @JavascriptInterface public void openUrl(String url) {
      try { Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse(url)); activity.startActivity(i); } catch(Exception ignored) {}
    }
  }
}
