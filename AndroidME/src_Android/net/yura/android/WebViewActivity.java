package net.yura.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;

/**
 * @author Yura
 */
public class WebViewActivity extends Activity {

    WebView webview;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webview = new WebView( this );
        //webview.setWebViewClient( new WebViewClient() );
        //webview.getSettings().setJavaScriptEnabled(true);
        //webview.getSettings().setDomStorageEnabled(true);

        setContentView(webview);
        
        webview.loadUrl( getIntent().getData().toString() );

    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
