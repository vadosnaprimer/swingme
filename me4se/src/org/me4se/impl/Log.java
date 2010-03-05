package org.me4se.impl;

public class Log {
  
  public static final int DRAW_EVENTS = 1; // F5
  public static final int INPUT_EVENTS = 2; // F6 usw...
  public static final int IO = 4;

  static final String[] NAMES = { "draw events", "input events", "io" };

  public static int mask;

  public static void toggle(int flags) {
    mask ^= flags;
    System.out.print("Logging mask set to: ");
    for (int i = 0; i < NAMES.length; i++) {
      if ((mask & (1 << i)) != 0) {
        System.out.print(NAMES[i]);
      }
    }
    System.out.println();
  }

  public static void log(int type, String text) {
    if ((type & mask) != 0) {
      System.out.println(text);
    }
  }
}