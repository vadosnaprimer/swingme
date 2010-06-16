package javax.microedition.pim;

import org.kobjects.pim.*;

import java.util.*;

/**
 * @author haustein
 */
class ItemEnumeration implements Enumeration {

  PIMListImpl list;
  Enumeration base;

  ItemEnumeration(PIMListImpl list, Enumeration base, PIMItem match, String filter, String category) {
    this.list = list;
    this.base = base;
  }

  /**
   * @see com.sun.tools.javac.v8.util.Enumeration#hasMoreElements()
   */
  public boolean hasMoreElements() {
    return base.hasMoreElements();
  }

  /**
   * @see com.sun.tools.javac.v8.util.Enumeration#nextElement()
   */
  public Object nextElement() {
    return new ContactImpl(list, (PimItem) base.nextElement());
  }

}
