package net.yura.android;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import net.yura.android.lcdui.Toolkit;
import net.yura.android.log.LogOutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;

public class AndroidMeMIDlet extends Activity implements Toolkit {
	private static final String MIDLET_PROPERTY = "midlet";

	private MIDlet midlet;
	private View defaultView;
	private Handler handler;
	private Thread eventThread;
	private Object lock = new Object();

	public static AndroidMeMIDlet DEFAULT_ACTIVITY;

	public AndroidMeMIDlet() {
		DEFAULT_ACTIVITY = this;
	}

	public MIDlet getMIDlet() {
		return this.midlet;
	}

	public Handler getHandler() {
		return handler;
	}

	public void invokeAndWait(final Runnable runnable) {
		if (Thread.currentThread() == this.eventThread) {
			runnable.run();
		} else {
			Runnable r = new Runnable() {
				public void run() {
					synchronized (AndroidMeMIDlet.this.lock) {
						runnable.run();
						AndroidMeMIDlet.this.lock.notify();
					}
				}
			};
			synchronized (this.lock) {
				this.handler.post(r);
				try {
					this.lock.wait();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.handler = new Handler();
		this.eventThread = Thread.currentThread();

		View splash = this.getLayoutInflater().inflate(R.layout.main, null,	false);
		this.defaultView = splash;
		setContentView(splash);

//		PrintStream log = new PrintStream(new LogOutputStream("AndroidMe"));
//		System.setErr(log);
//		System.setOut(log);
	}

	private MIDlet createMIDlet() {
		System.setProperty("microedition.platform", "microemulator-android");
		System.setProperty("microedition.locale", Locale.getDefault().toString());
		System.setProperty("microedition.configuration", "CLDC-1.1");
		System.setProperty("microedition.profiles", "MIDP-2.0");

		Properties properties = new Properties();

		try {
        	InputStream is = getAssets().open("BadooMobile.jad");
        	properties.load(is);

		} catch (Throwable e) {
			e.printStackTrace();
		}

		System.out.println(">>>1 " + System.getProperty("microedition.platform"));

		// create a new class loader that correctly handles getResourceAsStream!


		 properties.put(MIDLET_PROPERTY, "net.yura.mobile.test.MyMidlet");
		// properties.put(MIDLET_PROPERTY, "com.badoo.locator.BadooMidlet");

//		System.out.println(">>> " + net.java.dev.lwuit.speed.SpeedMIDlet.class);
//		properties.put(MIDLET_PROPERTY, "net.java.dev.lwuit.speed.SpeedMIDlet");

		String midletClassName = properties.getProperty(MIDLET_PROPERTY);
		ClassLoader classLoader;
		classLoader = this.getClassLoader();
		MIDlet midlet;
		MIDlet.DEFAULT_ACTIVITY = this;
		MIDlet.DEFAULT_TOOLKIT = this;
		MIDlet.DEFAULT_APPLICATION_PROPERTIES = properties;
		try {
			Class midletClass = Class.forName(midletClassName, true, classLoader);
			midlet = (MIDlet) midletClass.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("unable to load class "
					+ midletClassName, ex);
		}
		return midlet;
	}

	@Override
	protected void onDestroy() {
		try {
			if (this.midlet != null) {
				this.midlet.doDestroyApp(true);
				this.midlet = null;
			}
		} catch (Exception ex) {
			throw new RuntimeException("unable to destroy", ex);
		}
		// this.resources.getAssets().release();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		try {
			if (this.midlet != null) {
				this.midlet.doPauseApp();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("unable to freeze app", ex);
		}
		super.onPause();
	}

	@Override
	public void onStop() {
		// TODO: JP
		onDestroy();
	}


	@Override
	protected void onResume() {
		super.onResume();

		if (this.midlet == null) {
			Thread thread = new Thread() {
				public void run() {
					while (defaultView.getWidth() == 0) {
						try {
							Thread.sleep(500);
							System.out.println("W:" + defaultView.getWidth()
									+ ",H:" + defaultView.getHeight() + ",MW:"
									+ defaultView.getMeasuredWidth() + ",MH:"
									+ defaultView.getMeasuredHeight());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					MIDlet midlet = createMIDlet();
					AndroidMeMIDlet.this.midlet = midlet;
					try {
						if (midlet != null) {
							midlet.doStartApp();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			};
			thread.start();
		} else {
			try {
				this.midlet.doStartApp();
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new RuntimeException("couldn't start MIDlet");
			}
		}

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		boolean result = false;
		Display display = Display.getDisplay(midlet);
		Displayable current = display.getCurrent();
		if (current != null) {
			// load the menu items
			menu.clear();
			Vector<Command> commands = current.getCommands();
			for (int i = 0; i < commands.size(); i++) {
				result = true;
				Command cmd = commands.get(i);
				if (cmd.getCommandType() != Command.BACK && cmd.getCommandType() != Command.EXIT) {
					menu.addSubMenu(Menu.NONE, i + Menu.FIRST, Menu.NONE, cmd.getLabel());
				}
			}
		}
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Display display = Display.getDisplay(midlet);
		Displayable current = display.getCurrent();
		if (current != null) {
			Vector<Command> commands = current.getCommands();

			int commandIndex = item.getItemId() - Menu.FIRST;
			Command c = commands.get(commandIndex);
			CommandListener l = current.getCommandListener();

			if (c != null && l != null) {
				l.commandAction(c, current);
				return true;
			}
		}

		return false;
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		if (this.midlet != null) {
//			this.midlet.setMenu(menu);
//		} else {
//			this.menu = menu;
//		}
//		return super.onCreateOptionsMenu(menu);
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Displayable displayable = Display.getDisplay(this.midlet).getCurrent();

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// we need to see if there is a back option in the current display
			for (Command c : displayable.getCommands()) {
				if (c.getCommandType() == Command.BACK) {
					// manually call the back operation: YECH!
					displayable.getCommandListener().commandAction(c,
							displayable);
					return true;
				}
			}
		}

		if (displayable instanceof OnKeyListener) {
			OnKeyListener keyListener = (OnKeyListener) displayable;
			keyListener.onKey(null, keyCode, event);
		}

		return super.onKeyDown(keyCode, event);
	}

	public int getResourceId(String resourceName) {
		// slow, slow, slow (cache?)
		// mmmmm, introspection
		String[] splits = resourceName.split("\\.");
		Class c = R.class;
		for (int i = 0; i < splits.length; i++) {
			String name = splits[i];
			if (i < splits.length - 1) {
				Class[] childClasses = c.getDeclaredClasses();
				Class found = null;
				for (int j = 0; j < childClasses.length; j++) {
					Class childClass = childClasses[j];
					String separator;

					if (childClass.getName().endsWith("$" + name)) {
						found = childClass;
						break;
					}
				}
				if (found == null) {
					throw new RuntimeException("no class " + resourceName + "("
							+ name + ") in " + c.getName());
				} else {
					c = found;
				}
			} else {
				try {
					Field field = c.getField(name);
					return field.getInt(null);
				} catch (Exception ex) {
					throw new RuntimeException("no field " + name + " in "
							+ c.getName());
				}
			}
		}
		throw new RuntimeException("no resource " + resourceName);
	}

	public int getScreenHeight() {
		return this.defaultView.getHeight();
	}

	public int getScreenWidth() {
		return this.defaultView.getWidth();
	}

	public View inflate(int resourceId) {
		return this.getLayoutInflater().inflate(resourceId, null, false);
	}

	public static InputStream getResourceAsStream(Class origClass, String name)
			throws IOException {

		long time = System.currentTimeMillis();
		System.out.println(">>> getResourceAsStream (" + origClass.getName() + ")" + name);
		long time2 = System.currentTimeMillis() - time;

		InputStream is = null;
		try {
			if (name.startsWith("/")) {
				name = name.substring(1);
			}

			is = DEFAULT_ACTIVITY.getAssets().open(name);
		} catch (Throwable e) {
			// e.printStackTrace();
		}

		if (is == null) {
			long elapsed = System.currentTimeMillis() - time;
			System.out.println(">>> getResourceAsStream: NOT FOUND. " + time2 + " " + elapsed);
		}

		return is;
	}

}
