package com.badoo.mobile.rms;


import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author NBW
 */
public class RMSTestMidlet extends MIDlet implements CommandListener, Runnable
{
  private static final Form
    log = new Form(null);
  private final Random
    random = new Random();
  private boolean
    exitPressed;

  public void startApp()
  {
    log.setCommandListener(this);
    log.addCommand(new Command("Exit", Command.EXIT, 1));
    Display.getDisplay(this).setCurrent(log);
    new Thread(this).start();
  }

  public void run()
  {
    try
    {
      log(System.getProperty("microedition.platform"));
      RecordStoreManager storeManager = RecordStoreManager.getInstance(256*1024, 256*1024, 16);
      log("size: " + storeManager.getMaxStoreSize()/1024 + "kb x 16 = " + storeManager.getMaxTotalSize()/1024 + "kb");
      log("written: 0kb in 0 records");
      log("size: 0kb in 0 stores");
      log("available: 0kb");
      log("func: indexRecords");
      String string;
      //data integrity test
      string = new DataInputStream(storeManager.getRecord("example key")).readUTF();
      if(!string.equals("replacement data")) throw new IllegalStateException(string);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      new DataOutputStream(outputStream).writeUTF("example data");
      storeManager.putRecord("example key", outputStream, false);
      outputStream.reset();
      new DataOutputStream(outputStream).writeUTF("replacement data");
      storeManager.putRecord("example key", outputStream, false);
      //fragmentation test
      byte[] data = new byte[(Integer.MAX_VALUE>>20) + 1024];
      long timestamp = System.currentTimeMillis();
      for(long bytes=0; !exitPressed;)
      {
        outputStream.reset();
        outputStream.write(data, 0, Math.abs(random.nextInt())>>20);
        bytes += outputStream.size();
        storeManager.putRecord("key" + (random.nextInt()>>20), outputStream, true);
        update(2, "written: " + bytes/1024 + "kb");
        update(3, "size: " + storeManager.getTotalSize()/1024 + "kb in " + storeManager.getNumStores() + " stores");
        update(4, "available: " + storeManager.getSizeAvailable()/1024 + "kb");
        Thread.sleep(1);
      }
      string = new DataInputStream(storeManager.getRecord("example key")).readUTF();
      if(!string.equals("replacement data"))
        throw new IllegalStateException(string);
      storeManager.destroy();
      notifyDestroyed();
    }
    catch(Throwable e)
    {
      log(e.toString());
      e.printStackTrace();
    }
  }

  public void pauseApp()
  {
  }

  public void destroyApp(boolean unconditional)
  {
  }

  public void commandAction(Command command, Displayable displayable)
  {
    if(command.getCommandType()==Command.EXIT)
      exitPressed = true;
  }

  private static Hashtable table = new Hashtable();

  public static void log(String text)
  {
    if(table.containsKey(text))
      return;
    Item item = new StringItem(null, text);
    item.setLayout(Item.LAYOUT_2|Item.LAYOUT_NEWLINE_AFTER);
    log.append(item);
    table.put(text, text);
  }

  private static void update(String text)
  {
    ((StringItem)log.get(log.size()-1)).setText(text);
  }

  public static void debug(String text)
  {
    try
    {
      update(5, "func: " + text);
      Thread.sleep(0);
    }
    catch(InterruptedException e)
    {
    }
  }

  private static void update(int i, String text)
  {
    ((StringItem)log.get(i)).setText(text);
  }
}
