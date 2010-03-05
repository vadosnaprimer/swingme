package org.me4se;

import java.io.*;
import java.net.URL;
import java.util.*;

public class JadFile {

  /**
   * Inner class that represents the definition of a single MIDlet.
   */
  public class MIDletData {

    /**
     * The MIDlet's number.
     */
    private int number;

    /**
     * The MIDlet's name.
     */
    private String name;

    /**
     * The MIDlet's icon.
     */
    private String icon;

    /**
     * The MIDlet's main class.
     */
    private String cls;

    /**
     * Creates a new instance of the inner class.
     */
    MIDletData(int number, String name, String icon, String cls) {
      this.number = number;
      this.name = name;
      this.icon = icon;
      if (cls != null)
        this.cls = cls; // .replace('/', '.');
    }

    /**
     * Returns the MIDlet's number.
     */
    public int getNumber() {
      return number;
    }

    /**
     * Returns the MIDlet's name, or null, if the MIDlet doesn't have a name.
     */
    public String getName() {
      return name;
    }

    /**
     * Returns the MIDlet's main class, or null, if the MIDlet doesn't have a
     * name (which would be an error in the JAR file, of course.
     */
    public String getClassName() {
      return cls;
    }

    /**
     * Returns the MIDlet's icon, or null, if the MIDlet doesn't have an icon.
     */
    public String getIcon() {
      return icon;
    }
  }

  /**
   * Holds the lines of the JAD file.
   */
  private Hashtable values = new Hashtable();
  private String url;

  /**
   * Gets the value belonging to the given key, or null if the key is not found.
   */
  public String getValue(String name) {
    return (String) values.get(name);
  }

  /** Returns the URL if the JAD was loaded from an URL, null otherwise */

  public String getURL() {
    return url;
  }

  /**
   * Sets the value of the given key, replacing a previous definition if one
   * exists.
   */
  public void setValue(String name, String value) {
    values.put(name, value);
  }

  public Enumeration keys() {
    return values.keys();
  }

  /**
   * Returns the number of MIDlet's in the JAD file.
   */
  public int getMIDletCount() {
    int i = 1;
    while (getValue("MIDlet-" + i) != null) {
      i++;
    }
    return i - 1;
  }

  /**
   * Returns the definition of the given MIDlet, or null, if the MIDlet doesn't
   * exist. Note that MIDlet numbering starts at 1.
   */
  public MIDletData getMIDlet(int i) {
    String value = getValue("MIDlet-" + i);

    if (value == null)
      return null;

    int p1 = value.indexOf(',');

    String name = null;
    String icon = null;
    String cls = null;

    if (p1 != -1) {
      name = value.substring(0, p1).trim();
      int p2 = value.indexOf(',', p1 + 1);

      if (p2 != -1) {
        icon = value.substring(p1 + 1, p2).trim();
        cls = value.substring(p2 + 1).trim();
      } else {
        icon = value.substring(p1 + 1).trim();
      }
    } else {
      name = value.trim();
    }

    if ("".equals(name))
      name = null;
    if ("".equals(cls))
      cls = null;
    if ("".equals(icon))
      icon = null;

    return new MIDletData(i, name, icon, cls);
  }

  public void load(InputStream is) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String key = "";
    String s = null;
    while ((s = reader.readLine()) != null) {
      if (s.startsWith(" ")) {
        String val = getValue(key) + s.substring(1);
        setValue(key, val);
      } else {
        int cut = s.indexOf(':');
        if (cut != -1) {
          key = s.substring(0, cut).trim();
          String val = s.substring(cut + 1).trim();
          setValue(key, val);
        }
      }
    }

    reader.close();
  }

  /**
   * Loads the JAD file from a URL or resource. The URL is used as base url for
   * the JAR parameter
   */

  public void load(String url) throws IOException {
    this.url = url;
    int col = url.indexOf(':');
    if (col > 1 && col < 5) {
      load(new URL(url).openStream());
    } else {
      if (new File(url).exists()) {
        System.out.println("Loading JAD from file : " + url);
        FileInputStream fin = new FileInputStream(url);
        try {
          load(fin);
        } finally {
          fin.close();
        }
      } else {
        InputStream in = getClass().getResourceAsStream(url);
        if (in != null) {
          System.out.println("Loading JAD from classpath : " + url);
          load(in);
        } else {
          throw new IOException("\"" + url
              + "\" was found in file system or classpath");
        }
      }
    }
  }
}