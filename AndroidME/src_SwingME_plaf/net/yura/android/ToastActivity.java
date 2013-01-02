package net.yura.android;

import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.Url;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class ToastActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // there is a bug on android older versions so we use our Url to decode. 
            Intent intent = getIntent();
            Uri uri = intent.getData();
            Url url = new Url( uri.toString() );

            Toast t = Toast.makeText(this, url.getQueryParameter("message"), 
                "SHORT".equals(url.getQueryParameter("duration"))?Toast.LENGTH_SHORT:Toast.LENGTH_LONG);
            t.show();
        }
        catch (Throwable ex) {
            //#debug warn
            Logger.warn(ex);
        }
        finish();
    }
}
