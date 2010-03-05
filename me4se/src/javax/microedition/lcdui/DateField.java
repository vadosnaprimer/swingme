// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors:
//
// STATUS: Dummy class with complete API
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the
// License, or (at your option) any later version. This program is
// distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details. You should have received a copy of the
// GNU General Public License along with this program; if not, write
// to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA 02111-1307, USA.

package javax.microedition.lcdui;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @API MIDP-1.0
 */
public class DateField extends Item {

  /**
   * @API MIDP-1.0
   */
  public static final int DATE = 1;

  /**
   * @API MIDP-1.0
   */
  public static final int TIME = 2;

  /**
   * @API MIDP-1.0
   */
  public static final int DATE_TIME = 3;

  private int mode;

  private TimeZone timeZone = TimeZone.getDefault();
  private ScmTextComponent dateField = new ScmTextComponent(this, "dateField", true);
  private ScmTextComponent timeField = new ScmTextComponent(this, "dateField", true);

  /**
   * @API MIDP-1.0
   */
  public DateField(String label, int mode, TimeZone timeZone) {
    super(label);
    this.timeZone = timeZone;

    setDate(null);

    if ((mode & DATE) != 0)
      lines.addElement(dateField);

    if ((mode & TIME) != 0)
      lines.addElement(timeField);

    this.mode = mode;
  }

  /**
   * @API MIDP-1.0
   */
  public DateField(String label, int mode) {
    this(label, mode, TimeZone.getDefault());
  }

  /**
   * @API MIDP-1.0
   */
  public Date getDate() {
    try {
      Calendar c = Calendar.getInstance(timeZone);

      if ((mode & DATE) != 0) {
        String s = dateField.getText();
        c.set(Calendar.YEAR, Integer.parseInt(s.substring(0, 4)));
        c.set(Calendar.MONTH, Integer.parseInt(s.substring(5, 7)) - 1
            + Calendar.JANUARY);
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(8, 10)));
      } else
        c.setTime(new Date(0));

      if ((mode & TIME) != 0) {
        String s = timeField.getText();
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.substring(0, 2)));
        c.set(Calendar.MINUTE, Integer.parseInt(s.substring(3, 5)));
      } else {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
      }
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);

      return c.getTime();
    } catch (Exception e) {
      return null;
    }
  }

  private String digits(int i, int n) {
    String result = "" + i;
    while (result.length() < n)
      result = "0" + result;
    return result;
  }

  /**
   * @API MIDP-1.0
   */
  public int getInputMode() {
    return mode;
  }

  /**
   * @API MIDP-1.0
   */
  public void setInputMode(int mode) {
    this.mode = mode;
  }

  /**
   * @API MIDP-1.0
   */
  public void setDate(Date date) {

    if (date == null) {
      dateField.setText("yyyy-mm-dd");
      timeField.setText("hh:mm");
    } else {
      Calendar c = Calendar.getInstance(timeZone);
      c.setTime(date);
      dateField.setText(digits(c.get(Calendar.YEAR), 4) + "-"
          + digits(c.get(Calendar.MONTH) + 1 - Calendar.JANUARY, 2) + "-"
          + digits(c.get(Calendar.DAY_OF_MONTH) + 1, 2));

      timeField.setText(digits(c.get(Calendar.HOUR_OF_DAY), 2) + ":"
          + digits(c.get(Calendar.MINUTE), 2));
    }
  }
}