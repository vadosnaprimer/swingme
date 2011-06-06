package net.yura.android;

import android.view.Menu;
import android.view.MenuItem;

public interface MenuSystem {
	boolean onPrepareOptionsMenu(Menu menu);
	boolean onOptionsItemSelected(MenuItem item);
}
