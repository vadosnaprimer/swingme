package net.yura.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author Yura
 */
public class WebViewActivity extends Activity {

    WebView webview;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webview = new WebView( this );

        // this line is NEEDED as without it links do not work on 'HTC One S'
        webview.setWebViewClient( new WebViewClient() );

        //webview.getSettings().setJavaScriptEnabled(true);
        //webview.getSettings().setDomStorageEnabled(true);

        setContentView(webview);

        webview.loadUrl( String.valueOf(getIntent().getData()) );
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
