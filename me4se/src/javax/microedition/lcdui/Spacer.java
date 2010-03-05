
package javax.microedition.lcdui;



/**
 * @author omry Created on 24/06/2004
 * @API MIDP-2.0
 */
public class Spacer extends Item
{
    ScmDeviceComponent component = new ScmDeviceComponent(this, "spacer", false);
    /**
    * @API MIDP-2.0
    */

	public Spacer(int minWidth, int minHeight) {
        lines.add(component);
		updateSizes(minWidth, minHeight);
	}

    /**
        * @API MIDP-2.0
        */
	public void setMinimumSize(int minWidth, int minHeight){
		updateSizes(minWidth, minHeight);
	}

    /**
        * @API MIDP-2.0
        */
	public void addCommand(Command cmd)
	{
		throw new IllegalStateException();
	}

    /**
        * @API MIDP-2.0
        */
	public void setDefaultCommand(Command cmd)
	{
		throw new IllegalStateException();
	}

    /**
        * @API MIDP-2.0
        */
	public void setLabel(String label)
	{
		throw new IllegalStateException();
	}


	private void updateSizes(int minW, int minH)
	{
		if(minW < 0 || minH < 0){
			throw new IllegalArgumentException();
        }

        component.setWidth(minW);
        component.setHeight(minH);
			//         invalidate();
	}
}