package net.yura.mobile.gui.layout;

import java.util.Hashtable;
import java.util.Vector;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.layout.Layout;

/**
 * @author Nathan
 */
public class GridBagLayout implements Layout {

    int columns;
    int gap;
    int top,bottom,right,left;

    public GridBagLayout(int columns, int gap, int top, int bottom, int left, int right) {
        this.columns = columns;
        this.gap = gap;
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    private static int getSum(int[] values, int from, int length, int gap, boolean last) {
            if (length <= 0) {
                    return 0;
            }
            int value = 0;
            for (int i = 0; i < length; i++) {
                    value += values[from + i];
            }
            return value + (length - (last ? 0 : 1)) * gap;
    }

    private int[][] getGrid(Panel component) {
            Vector components = component.getComponents();
            Hashtable constraints = component.getConstraints();
            int count = 0; // count of the visible subcomponents
//            for (Object comp = get(component, ":comp"); comp != null; comp = get(
//                            comp, ":next")) {
//                    if (getBoolean(comp, "visible", true)) {
//                            count++;
//                    }
//            }
            count = component.getComponentCount();
            if (count == 0) {
                    return null;
            } // zero subcomponent
            //int columns = getInteger(component, "columns", 0);
            int icols = (columns != 0) ? columns : count;
            int irows = (columns != 0) ? ((count + columns - 1) / columns) : 1;
            int[][] grid = { new int[icols], new int[irows], // columnwidths,
                            // rowheights
                            new int[icols], new int[irows], // columnweights, rowweights
                            new int[count], new int[count], // gridx, gridy
                            new int[count], new int[count] }; // gridwidth, gridheight
            int[] columnheight = new int[icols];
            int[][] cache = null; // preferredwidth, height, columnweight, rowweight

            int i = 0;
            int x = 0;
            int y = 0;
            int nextsize = 0;
            for (int compi=0; compi < count; compi++) {
                Component subComponent = (Component) components.elementAt(compi);
                GridBagConstraints subConstraint = (GridBagConstraints) constraints.get(subComponent);
//                    if (!getBoolean(comp, "visible", true)) {
//                            continue;
//                    }
                    int colspan = ((columns != 0) && (columns < count)) ? Math.min(
                                    subConstraint.colSpan, columns) : 1;
                    int rowspan = (columns != 1) ? subConstraint.rowSpan : 1;

                    for (int j = 0; j < colspan; j++) {
                            if ((columns != 0) && (x + colspan > columns)) {
                                    x = 0;
                                    y++;
                                    j = -1;
                            } else if (columnheight[x + j] > y) {
                                    x += (j + 1);
                                    j = -1;
                            }
                    }
                    if (y + rowspan > grid[1].length) {
                            int[] rowheights = new int[y + rowspan];
                            System.arraycopy(grid[1], 0, rowheights, 0, grid[1].length);
                            grid[1] = rowheights;
                            int[] rowweights = new int[y + rowspan];
                            System.arraycopy(grid[3], 0, rowweights, 0, grid[3].length);
                            grid[3] = rowweights;
                    }
                    for (int j = 0; j < colspan; j++) {
                            columnheight[x + j] = y + rowspan;
                    }

                    int weightx = subConstraint.weightx;//getInteger(comp, "weightx", 0);
                    int weighty = subConstraint.weighty;//getInteger(comp, "weighty", 0);
                    //Dimension d = getPreferredSize(comp);

                    if (colspan == 1) {
                            grid[0][x] = Math.max(grid[0][x], subComponent.getWidthWithBorder()); // columnwidths
                            grid[2][x] = Math.max(grid[2][x], weightx); // columnweights
                    } else {
                            if (cache == null) {
                                    cache = new int[4][count];
                            }
                            cache[0][i] = subComponent.getWidthWithBorder();
                            cache[2][i] = weightx;
                            if ((nextsize == 0) || (colspan < nextsize)) {
                                    nextsize = colspan;
                            }
                    }
                    if (rowspan == 1) {
                            grid[1][y] = Math.max(grid[1][y], subComponent.getHeightWithBorder()); // rowheights
                            grid[3][y] = Math.max(grid[3][y], weighty); // rowweights
                    } else {
                            if (cache == null) {
                                    cache = new int[4][count];
                            }
                            cache[1][i] = subComponent.getHeightWithBorder();
                            cache[3][i] = weighty;
                            if ((nextsize == 0) || (rowspan < nextsize)) {
                                    nextsize = rowspan;
                            }
                    }
                    grid[4][i] = x; //gridx
                    grid[5][i] = y; //gridy
                    grid[6][i] = colspan; //gridwidth
                    grid[7][i] = rowspan; //gridheight

                    x += colspan;
                    i++;
            }

            while (nextsize != 0) {
                    int size = nextsize;
                    nextsize = 0;
                    for (int j = 0; j < 2; j++) { // horizontal, vertical
                            for (int k = 0; k < count; k++) {
                                    if (grid[6 + j][k] == size) { // gridwidth, gridheight
                                            int gridpoint = grid[4 + j][k]; // gridx, gridy

                                            int weightdiff = cache[2 + j][k];
                                            for (int m = 0; (weightdiff > 0) && (m < size); m++) {
                                                    weightdiff -= grid[2 + j][gridpoint + m];
                                            }
                                            if (weightdiff > 0) {
                                                    int weightsum = cache[2 + j][k] - weightdiff;
                                                    for (int m = 0; (weightsum > 0) && (m < size); m++) {
                                                            int weight = grid[2 + j][gridpoint + m];
                                                            if (weight > 0) {
                                                                    int weightinc = weight * weightdiff
                                                                                    / weightsum;
                                                                    grid[2 + j][gridpoint + m] += weightinc;
                                                                    weightdiff -= weightinc;
                                                                    weightsum -= weightinc;
                                                            }
                                                    }
                                                    grid[2 + j][gridpoint + size - 1] += weightdiff;
                                            }

                                            int sizediff = cache[j][k];
                                            int weightsum = 0;
                                            for (int m = 0; (sizediff > 0) && (m < size); m++) {
                                                    sizediff -= grid[j][gridpoint + m];
                                                    weightsum += grid[2 + j][gridpoint + m];
                                            }
                                            if (sizediff > 0) {
                                                    for (int m = 0; (weightsum > 0) && (m < size); m++) {
                                                            int weight = grid[2 + j][gridpoint + m];
                                                            if (weight > 0) {
                                                                    int sizeinc = weight * sizediff / weightsum;
                                                                    grid[j][gridpoint + m] += sizeinc;
                                                                    sizediff -= sizeinc;
                                                                    weightsum -= weight;
                                                            }
                                                    }
                                                    grid[j][gridpoint + size - 1] += sizediff;
                                            }
                                    } else if ((grid[6 + j][k] > size)
                                                    && ((nextsize == 0) || (grid[6 + j][k] < nextsize))) {
                                            nextsize = grid[6 + j][k];
                                    }
                            }
                    }
            }
            return grid;
    }


    public void layoutPanel(Panel component) {
        Vector components = component.getComponents();
        Hashtable constraints = component.getConstraints();

        int[][] grid = getGrid(component);

        int contentwidth = 0;
        int contentheight = 0;
        if (grid != null) { // has subcomponents

                // sums the preferred size of cell widths and heights, gaps
                contentwidth = left
                                + getSum(grid[0], 0, grid[0].length, gap, false)
                                + right;
                contentheight = top
                                + getSum(grid[1], 0, grid[1].length, gap, false)
                                + bottom;
        }

        int titleheight = 0; //component.getHeight(); // title text and
        // icon
        //setInteger(component, ":titleheight", titleheight, 0);
        //boolean scrollable = getBoolean(component, "scrollable", false);
//        boolean border = ("panel" == classname)
//                        && getBoolean(component, "border", false);
        int iborder = 0;
//        if (scrollable) { // set scrollpane areas
//                if ("panel" == classname) {
//                        int head = titleheight / 2;
//                        int headgap = (titleheight > 0) ? (titleheight - head - iborder)
//                                        : 0;
//                        scrollable = layoutScroll(component, contentwidth,
//                                        contentheight, head, 0, 0, 0, border, headgap);
//                } else { // dialog
//                        scrollable = layoutScroll(component, contentwidth,
//                                        contentheight, 3 + titleheight, 3, 3, 3, true, 0);
//                }
//        }
//        if (!scrollable) { // clear scrollpane bounds //+
//                set(component, ":view", null);
//                set(component, ":port", null);
//        }

        if (grid != null) {
                int areax = 0;
                int areay = 0;
                int areawidth = 0;
                int areaheight = 0;
//                if (scrollable) {
//                        // components are relative to the viewport
//                        Rectangle view = getRectangle(component, ":view");
//                        areawidth = view.width;
//                        areaheight = view.height;
//                } else { // scrollpane isn't required
                        // components are relative to top/left corner
//                        Rectangle bounds = getRectangle(component, "bounds");
                        areawidth = component.getWidthWithBorder();//bounds.width;
                        areaheight = component.getHeightWithBorder();//bounds.height;
//                        if ("panel" == classname) {
                                areax = iborder;
                                areay = Math.max(iborder, titleheight);
                                areawidth -= 2 * iborder;
                                areaheight -= areay + iborder;
//                        } else { // dialog
//                                areax = 4;
//                                areay = 4 + titleheight;
//                                areawidth -= 8;
//                                areaheight -= areay + 4;
//                        }
//                }

                for (int i = 0; i < 2; i++) { // i=0: horizontal, i=1: vertical
                        // remaining space
                        int d = ((i == 0) ? (areawidth - contentwidth)
                                        : (areaheight - contentheight));
                        if (d != 0) { //+ > 0
                                int w = getSum(grid[2 + i], 0, grid[2 + i].length, 0,
                                                false);
                                if (w > 0) {
                                        for (int j = 0; j < grid[i].length; j++) {
                                                if (grid[2 + i][j] != 0) {
                                                        grid[i][j] += d * grid[2 + i][j] / w;
                                                }
                                        }
                                }
                        }
                }


                for (int compi=0; compi < component.getComponentCount(); compi++) {
                    Component subComponent = (Component) components.elementAt(compi);
                    GridBagConstraints subConstraint = (GridBagConstraints) constraints.get(subComponent);
//                        if (!getBoolean(comp, "visible", true)) {
//                                continue;
//                        }
                        int ix = areax + left
                                        + getSum(grid[0], 0, grid[4][compi], gap, true);
                        int iy = areay + top
                                        + getSum(grid[1], 0, grid[5][compi], gap, true);
                        int iwidth = getSum(grid[0], grid[4][compi], grid[6][compi], gap,
                                        false);
                        int iheight = getSum(grid[1], grid[5][compi], grid[7][compi], gap,
                                        false);
                        String halign = subConstraint.getHalign();//getString(comp, "halign", "fill");
                        String valign = subConstraint.getValign();//getString(comp, "valign", "fill");
//                        System.out.println("HAlign for "+subComponent.getName()+" = "+halign);
                        if ((!"fill".equals(halign)) || (!"fill".equals(valign))) {
                                //Dimension d = getPreferredSize(comp);
                                if (!"fill".equals(halign)) {
                                        int dw = Math.max(0, iwidth - subComponent.getWidthWithBorder());
                                        if ("center".equals(halign)) {
                                                ix += dw / 2;
                                        } else if ("right".equals(halign)) {
                                                ix += dw;
                                        }
                                        iwidth -= dw;
                                }
                                if (!"fill".equals(valign)) {
                                        int dh = Math.max(0, iheight - subComponent.getHeightWithBorder());
                                        if ("center".equals(valign)) {
                                                iy += dh / 2;
                                        } else if ("bottom".equals(valign)) {
                                                iy += dh;
                                        }
                                        iheight -= dh;
                                }
                        }
                        //setRectangle(comp, "bounds", ix, iy, iwidth, iheight);
//                        System.out.println("Setting "+subComponent.getName()+" = "+iwidth);
                        subComponent.setBoundsWithBorder(ix, iy, iwidth, iheight);
                        //doLayout(comp);
                }
        }
    }


    public int[] getPreferredSize(Panel component) {

        // title text and icon height
        int[] size = new int[2];
        // add paddings
        size[0] += left
                        + right;
        size[1] += top
                        + bottom;
        // add content preferred size

        int[][] grid = getGrid(component);
        if (grid != null) { // has components
                size[0] += getSum(grid[0], 0, grid[0].length, gap, false);
                size[1] += getSum(grid[1], 0, grid[1].length, gap, false);
        }
        return size;

    }

    public int getPreferredWidth(Panel arg0) {
        return getPreferredSize(arg0)[0];
    }

    public int getPreferredHeight(Panel arg0) {
        return getPreferredSize(arg0)[1];
    }

}
