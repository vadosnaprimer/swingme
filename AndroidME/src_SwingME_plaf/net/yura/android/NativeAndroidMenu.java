package net.yura.android;

import java.util.Vector;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

public class NativeAndroidMenu implements MenuSystem {

	


    @Override
    public boolean onPrepareOptionsMenu(Menu androidMenu) {

        try {
            androidMenu.clear();

            net.yura.mobile.gui.components.Window currentWindow = net.yura.mobile.gui.DesktopPane.getDesktopPane().getSelectedFrame();
            
            net.yura.mobile.gui.components.Button mneonicButton = currentWindow.findMneonicButton(net.yura.mobile.gui.KeyEvent.KEY_MENU);
            if ( mneonicButton==null ) {
                mneonicButton = currentWindow.findMneonicButton(net.yura.mobile.gui.KeyEvent.KEY_SOFTKEY1);
            }
            if (mneonicButton==null) {
            	return false; // there is no menu!
            }
            
            fireActionPerformed(mneonicButton,androidMenu);

        }
	    catch (Throwable e) {
	        e.printStackTrace();
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
	                androidMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							try {
								fireActionPerformed(menuItem,submenu);
							}
							catch (Throwable th) {
								th.printStackTrace();
							}
							return true;
						}
					});

                	// set the icon
	                net.yura.mobile.gui.Icon icon = menuItem.getIcon();
	                if (icon != null) {
	                	javax.microedition.lcdui.Image img = icon.getImage();
	                    if (img != null) {
	                    	android.graphics.drawable.Drawable d = new android.graphics.drawable.BitmapDrawable(img.getBitmap());
	                    	androidMenuItem.setIcon( d );
	                    }
	                }
                }
        	}
        }
    }

    // this is not used
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		throw new RuntimeException();
	}
	
	private void fireActionPerformed(net.yura.mobile.gui.components.Button button,android.view.Menu menu) {
		
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
