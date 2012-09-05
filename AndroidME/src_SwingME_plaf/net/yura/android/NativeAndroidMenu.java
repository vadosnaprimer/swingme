package net.yura.android;

import java.util.Vector;

import net.yura.android.plaf.AndroidBorder;
import net.yura.android.plaf.AndroidIcon;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.logging.Logger;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

public class NativeAndroidMenu implements MenuSystem {

    private net.yura.mobile.gui.components.Button getMenuButton() {
        net.yura.mobile.gui.components.Window currentWindow = net.yura.mobile.gui.DesktopPane.getDesktopPane().getSelectedFrame();

        if (currentWindow==null) return null;
        
        net.yura.mobile.gui.components.Button mneonicButton = currentWindow.findMnemonicButton(net.yura.mobile.gui.KeyEvent.KEY_MENU);
        if ( mneonicButton==null ) {
            mneonicButton = currentWindow.findMnemonicButton(net.yura.mobile.gui.KeyEvent.KEY_SOFTKEY1);
        }
        return mneonicButton;
    }

    /**
     * check if we need a menu
     * this is called to check if we should have a menu button on this screen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu androidMenu) {
        try {
            net.yura.mobile.gui.components.Button mneonicButton = getMenuButton();
            return (mneonicButton instanceof net.yura.mobile.gui.components.Menu);
        }
        catch (Throwable e) {
          //#debug warn
            Logger.warn(e);
        }
        return true; // default to true for old devices
    }

    /**
     * click on the menu
     * this is called just before the menu is displayed by Android
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu androidMenu) {
        try {
            net.yura.mobile.gui.components.Button mneonicButton = getMenuButton();
            if (mneonicButton==null) {
            	return false; // there is no menu!
            }
            fireActionPerformed(mneonicButton,androidMenu);
        }
	catch (Throwable e) {
	  //#debug warn
	    Logger.warn(e);
	}
	return true;
    }

    private void addItemsToMenu(Menu androidMenu,net.yura.mobile.gui.components.Menu menu) {

    	Vector menuItems = menu.getMenuComponents();
        for (int i = 0; i < menuItems.size(); i++) {
            Object item = menuItems.elementAt(i);
            if (item instanceof net.yura.mobile.gui.components.Button) {
                final net.yura.mobile.gui.components.Button menuItem = (net.yura.mobile.gui.components.Button) item;
                if (menuItem.isVisible()) {
                	final android.view.MenuItem androidMenuItem;
                	final android.view.SubMenu submenu;
                	if (menuItem instanceof net.yura.mobile.gui.components.Menu) {
                		submenu = androidMenu.addSubMenu(menuItem.getText());
                		androidMenuItem = submenu.getItem();
                	}
                	else {
		                submenu = null;
		                androidMenuItem = androidMenu.add(menuItem.getText());
                	}
                	androidMenuItem.setEnabled( menuItem.isFocusable() );
	                androidMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							try {
								fireActionPerformed(menuItem,submenu);
							}
							catch (Throwable th) {
								Logger.warn(th);
							}
							return true;
						}
					});

                	// set the icon
	                final net.yura.mobile.gui.Icon icon = menuItem.getIcon();
	                if (icon != null) {
	                	Drawable drawable = new Drawable() {
	                		//android.graphics.Bitmap bmp;
	                		private android.graphics.Paint paint = new Paint();
	                		private int tries; // number of retries to draw a valid image
							@Override
							public void setColorFilter(ColorFilter cf) {
								paint.setColorFilter(cf);
							}
							@Override
							public void setAlpha(int alpha) {
								paint.setAlpha(alpha);
							}
							@Override
							public int getOpacity() {
								return PixelFormat.TRANSLUCENT; // TODO we are guessing here, but this is a good guess
							}
							@Override
							public void draw(Canvas canvas) {
							    if (icon instanceof AndroidIcon) {
							        Drawable d = ((AndroidIcon)icon).getDrawable();
							        AndroidBorder.setDrawableState( menuItem, d);
							        d.setBounds( getBounds() );
							        d.draw(canvas);
							    }
							    else {
								javax.microedition.lcdui.Image img = icon.getImage();
								if (img!=null) {
									tries = 0;
									canvas.drawBitmap(img.getBitmap(), 0, 0, paint);
								}
								else if (tries<10) {
									//#debug debug
									System.out.println("[NativeAndroidMenu] icon in menu has no image, trying again: "+icon+" "+menuItem);
									tries++;
									invalidateSelf();
								}
								else {
									//#debug debug
									System.out.println("[NativeAndroidMenu] Failed to draw icon in menu: "+icon+" "+menuItem);
								}
							    }
							}
						    @Override
						    public int getIntrinsicWidth() {
						        return icon.getIconWidth();
						    }
						    @Override
						    public int getIntrinsicHeight() {
						        return icon.getIconHeight();
						    }
						};
						androidMenuItem.setIcon( drawable );

						// this causes lots of problems as BitmapDrawable makes the size go wrong
	                	//javax.microedition.lcdui.Image img = icon.getImage();
	                    //if (img != null) {
	                    //	android.graphics.drawable.Drawable d = new android.graphics.drawable.BitmapDrawable(img.getBitmap());
	                    //	androidMenuItem.setIcon( d );
	                    //}
	                }
                }
        	}
        }
    }

    public static final int KEY_HOME = -13;

    // this is not used
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == 16908332) { // android.R.id.home API-11 // THIS IS THE ACTION BAR ICON BUTTOM
	        DesktopPane dp = DesktopPane.getDesktopPane();
	        if (dp!=null) {
	            dp.keyPressed(KEY_HOME);
        	    dp.keyReleased(KEY_HOME);
        	    return true;
	        }
	    }
	    else {
	        //#debug warn
    	        Logger.warn("odd "+item);
	    }
	    return false;
	}

	private void fireActionPerformed(net.yura.mobile.gui.components.Button button,android.view.Menu menu) {

    	        if (menu!=null) {
    	            menu.clear(); // before we do anything, lets clear the menu
    	        }

		// we do NOT want to call fireActionPerformed on menus, as that will cause the SwingME menu to open
		// and we want to use native menus instead
		if (button instanceof net.yura.mobile.gui.components.Menu) {
        	    net.yura.mobile.gui.ActionListener[] als = button.getActionListeners();
        	    String ac = button.getActionCommand();
        	    for (int c=0;c<als.length;c++) {
        	        als[c].actionPerformed(ac);
        	    }
            	    addItemsToMenu( menu, (net.yura.mobile.gui.components.Menu)button);
		}
		else {
		    button.fireActionPerformed();
		}
	}

}
