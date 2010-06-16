package org.me4se.psi.java1.nfc;

import javax.microedition.contactless.TargetListener;
import javax.microedition.contactless.TargetType;

public class TargetListenerContainer {

  private TargetListener listener;
  private TargetType targetType;
  
  public TargetListenerContainer(TargetListener listener, TargetType targetType) {
    this.listener = listener;
    this.targetType = targetType;
  }

  public TargetListener getListener() {
    return listener;
  }

  public TargetType getTargetType() {
    return targetType;
  }
}