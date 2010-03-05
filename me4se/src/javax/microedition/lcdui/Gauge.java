// replaced by impl. based on ScmGauge

package javax.microedition.lcdui;

import javax.microedition.midlet.ApplicationManager;

/**
 * @API MIDP-1.0
 * @API MIDP-2.0
 */
public class Gauge extends Item {

	/**
	 * @API MIDP-2.0 
	 */
	public static final int INDEFINITE = -1;
	
	/**
	 * @API MIDP-2.0 
	 */
	public static final int CONTINUOUS_IDLE = 0;

	/**
	 * @API MIDP-2.0 
	 */
	public static final int INCREMENTAL_IDLE = 1;

	/**
	 * @API MIDP-2.0 
	 */
	public static final int CONTINUOUS_RUNNING = 2;

	/**
	 * @API MIDP-2.0 
	 */
	public static final int INCREMENTAL_UPDATING = 3;

	int value;
	int maximum;
	ScmGauge component;

	/**
	 * @API MIDP-1.0 
	 */
	public Gauge(java.lang.String label, boolean interactive, int maxValue, int initialValue) throws IllegalArgumentException {
		super(label);

		this.maximum = maxValue;
		this.value = initialValue;

		component = (ScmGauge) ApplicationManager.getInstance().getComponent(interactive ? "item.gauge.interactive" : "item.gauge");

		if (component == null)
			component = new ScmGauge(interactive);

		component.init(this);

		lines.addElement(component);
	}

	/**
	 * @API MIDP-1.0 
	 */
	public int getMaxValue() {
		return maximum;
	}

	/**
	 * Gets the current value of this Gauge object.
     *
	 * @API MIDP-1.0 
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @API MIDP-1.0 
	 */
	public boolean isInteractive() {
		return component.getFocusable();
	}

	/**
	 * @API MIDP-1.0 
	 */
	public void setMaxValue(int maxValue) {
		maximum = maxValue;
		component.repaint();
	}

	/**
	 * @API MIDP-1.0 
	 */
	public void setValue(int value) {
		this.value = value;
		component.repaint();
	}
}
