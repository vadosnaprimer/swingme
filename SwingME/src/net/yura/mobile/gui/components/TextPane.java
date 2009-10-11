/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */
package net.yura.mobile.gui.components;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;

/**
 * @author Yura Mamyrin
 * @see javax.swing.text.TextPane
 */
public class TextPane extends Component {

    private String text;
    private Vector sortedElemsList = new Vector();
    private Vector lineFragments = new Vector();
    private int lastLineX;

    public TextPane() {
        super();
        focusable = false;
    }

    protected String getDefaultName() {
        return "TextPane";
    }

    // Overwrites Component.paintComponent()
    public void paintComponent(Graphics2D g) {


        // TODO: Use binary search to find the first fragment with a visible y
        // TODO: Stop paint if y is no longer visible... Take care that in the same
        //       line, some y may be visible and others not...
        // TODO: get font, foreground/background color, etc from the style


        int numLineFrags = lineFragments.size();
        for (int i = 0; i < numLineFrags; i++) {
            LineFragment lineFrag = (LineFragment) lineFragments.elementAt(i);
            TextStyle style = lineFrag.style;
            Icon icon = style.getIcon();

            if (icon == null) {
                String str = text.substring(lineFrag.startOffset, lineFrag.endOffset);

                g.setColor(style.getForeground()); // TODO: Color
                g.setFont(style.getFont());

                g.drawString(str, lineFrag.x, lineFrag.y);
            } else {
                Image img = icon.getImage();
                g.drawImage(img, lineFrag.x, lineFrag.y, Graphics.TOP | Graphics.LEFT);
            }
        }
    }

    // Overwrites Component.workoutMinimumSize()
    public void workoutMinimumSize() {
        // we assume that the scrollPane size is already setup and correct
        // this saves lots of unneeded calls to getLines
        if (parent instanceof ScrollPane) {
                width = ((ScrollPane)parent).getViewPortWidth();
        }
        else if (width==0) {
            // make a guess at the width
            // to work out the height
            // width will be reset back to 0
            width = (int)(DesktopPane.getDesktopPane().getWidth()* 0.9 );
            // this guess here is the root of all problems
            // as we need to make a guess, even though we have no idea
        }

        doLayout();

        //TODO: Calculate the height, using the width as parameter
        //TODO: Needs to do the layout to figure out the height
        height = 600;
    }

    // Overwrites Component.setSize()
    public void setSize(int w, int h) {
        super.setSize(w, h);

        doLayout();
    }

    // from JEditorPane
    public String getText() {
        return text;
    }

    // from JEditorPane
    public void setText(String text) {
        this.text = text;
    }

    // from DefaultStyledDocument
    public void setCharacterAttributes(int offset, int length, TextStyle style) {
        if (offset >= 0 && length >= 1 && text != null && offset < text.length()) {

            int endOffset = Math.min(offset + length, text.length());

            // Note: Swing copies the style, instead of keeping the reference
            Element elem = new Element(style, offset, endOffset);
            insertSortedElement(sortedElemsList, elem);
        }
    }

    public void setParagraphAttributes(int offset, int length, TextStyle style) {
        if (offset >= 0 && length >= 0 && text != null && offset < text.length()) {

            int lowerLfIdx = 0;
            int higherLfIdx = text.length();
            int lineFeedIdx = 0;
            while (true) {

                lineFeedIdx = text.indexOf('\n', lineFeedIdx);

                if (lineFeedIdx <= 0) {
                    break;
                } else if (lineFeedIdx < offset) {
                    lowerLfIdx = lineFeedIdx;
                } else if (lineFeedIdx > offset + length) {
                    higherLfIdx = lineFeedIdx + 1;
                    break;
                }

                lineFeedIdx++;
            }

            // Note: Swing copies the style, instead of keeping the reference
            Element elem = new Element(style, lowerLfIdx, higherLfIdx);

            // TODO: Should use a different vector, otherwise paragraph
            // styles will be mixed with Character styles
            // TODO: When applying the style, first apply the paragraph
            // (including color, font, etc, etc) and then character style
            insertSortedElement(sortedElemsList, elem);
        }
    }



    private void doLayout() {

        lineFragments.removeAllElements();

        if (text == null || text.length() == 0 || width <= 0) {
            return;
        }

        // Reset last line coordinates
        lastLineX = 0;

        Vector currentElemSortedStack = new Vector();
        Element currentElem = null;;
        int startFragIdx = 0;
        int nextStartFragIdx = 0;

        int numElems = sortedElemsList.size();
        for (int i = 0; i < numElems + 1; i++) {

            if (i < numElems) {
                currentElem = (Element) sortedElemsList.elementAt(i);

                if (currentElem.startOffset == startFragIdx) {
                    currentElemSortedStack.addElement(currentElem);
                    continue;
                }
            }

            // If there is no more Elements, we set the end of the text as
            // the start of the next one
            nextStartFragIdx = (i < numElems) ? currentElem.startOffset : text.length();

            // Does any of the stack elements ends?
            for (int j = currentElemSortedStack.size() - 1; j >= 0 ; j--) {
                //TODO: Assuming that they are ending in FIFO order. OK only for html

                Element elem = (Element) currentElemSortedStack.elementAt(j);
                if (elem.endOffset <= nextStartFragIdx) {

                    startFragIdx = addLineFragments(currentElemSortedStack, startFragIdx, elem.endOffset);
                    currentElemSortedStack.removeElementAt(j);
                }
            }

            if (startFragIdx < nextStartFragIdx) {
                startFragIdx = addLineFragments(currentElemSortedStack, startFragIdx, nextStartFragIdx);
            }

            // TODO: This sort may need to be done on currentElem.endOffset field...
            // (instead of startOffset) -> we may need a insertSortedByStartOffset() and
            // a insertSortedByEndOffset()
            insertSortedElement(currentElemSortedStack, currentElem);
        }

        layoutVerticaly();
    }

    private int addLineFragments(Vector currentElemStack, int startIndex, int endIndex) {
        TextStyle style = new TextStyle();
        calculateStyle(style, currentElemStack);

        // TODO BUG: If there a "word" is broken at the middle, that entire
        // word may end up split between to lines, instead of moving all to a new line
        String elemText = text.substring(startIndex, endIndex);

        if (style.getIcon() == null)  {
            addLineTextFragments(elemText, style, startIndex);
        }
        else {
            addLineImageFragments(style);
        }

        return endIndex;
    }

    private void addLineTextFragments(String elemText, TextStyle style, int startIndex) {

        Font f = style.getFont();

        int fragH = f.getHeight();

        // Break the lines using the Element style
        int[] lines = getLines(elemText, f, lastLineX, width);

        int startFragIdx = startIndex;

        // Create a new Line Fragment per each line of text we got
        for (int j = 0; j < lines.length + 1;j++) {
            int endFragIdx = (j == lines.length) ? elemText.length() : lines[j];
            endFragIdx += startIndex;

            // It's possible that some fragments have zero length.
            // (If the last line width was to small for a word). Don't add them
            if (endFragIdx > startFragIdx) {
                String lastLineText = text.substring(startFragIdx, endFragIdx);

                if ("\n".equals(lastLineText)) {
                    if (lastLineX <= 0) {
                        lineFragments.addElement(new LineFragment(0, 0, style, 0, 0));
                    }
                    lastLineX = 0;

                } else {
                    int fragW = f.getWidth(lastLineText);

                    if (fragW > 0) {
                        // Note: First fragments on a line have negative width
                        LineFragment lineFrag = new LineFragment((lastLineX == 0) ? -fragW : fragW, fragH, style, startFragIdx, endFragIdx);
                        lineFragments.addElement(lineFrag);
                    }

                    lastLineX = (j == lines.length) ? lastLineX + fragW : 0;
                }
            }

            startFragIdx = endFragIdx;
        }
    }

    private void addLineImageFragments(TextStyle style) {
        Icon icon = style.getIcon();
        Image img = icon.getImage();

        if (img != null) {
            int imgW = img.getWidth();
            int imgH = img.getHeight();
            img = null; // Allow GC to collect

            // Can the image be added to last line?
            if (lastLineX + imgW > width) {
                // Force a new line
                lastLineX = 0;
            }

            // Note: First fragments on a line have negative width
            LineFragment lineFrag = new LineFragment((lastLineX == 0) ? -imgW : imgW, imgH, style, 0, 0);
            lineFragments.addElement(lineFrag);

            lastLineX += imgW;

            if (lastLineX >= width || imgW >= width) {
                // Force a new line
                lastLineX = 0;
            }
        }
    }

    private void layoutVerticaly() {
        int padding = 2;  // TODO: Spacing
        int lineY = -padding;
        int lineH = 0;
        int lineW = 0;
        int startLineFragIdx = 0;
        int numFrags = lineFragments.size();

        LineFragment lineFrag = null;

        for (int i = 0; i <= numFrags; i++) {
            if (i < numFrags) {
                lineFrag = (LineFragment) lineFragments.elementAt(i);
            }

            if (lineFrag.x <= 0 || i == numFrags) {

                lineFrag.x = -lineFrag.x;
                // Now that we know the height of the entire line, calculate
                // the baseline
                int lineX = 0;

                for (int j = startLineFragIdx; j < i; j++) {
                    LineFragment frag = (LineFragment) lineFragments.elementAt(j);
                    int leftPadding = getParagraphLeftPadding(frag.style, lineW);
                    int fragW = frag.x;

                    frag.y = lineY + (lineH - frag.y);
                    frag.x = lineX + leftPadding;

                    lineX += fragW;
                }

                // This is the start of a new line, increase Y
                lineY += lineH + padding;

                startLineFragIdx = i;
                lineH = lineFrag.y;
                lineW = lineFrag.x;
            }
            else {
                // Note: lineFrag.y holds the fragment height
                lineH = Math.max(lineH, lineFrag.y);
                lineW += lineFrag.x;
            }
        }
    }

    private int getParagraphLeftPadding(TextStyle style, int lineW) {
        int align = style.getAlignment();
        return (align == TextStyle.ALIGN_CENTER) ? (width - lineW) / 2 :
               (align == TextStyle.ALIGN_RIGHT)? (width - lineW) : 0;
    }

    private void calculateStyle(TextStyle defAttr, Vector elemList) {
        setDefaultStyle(defAttr);

        for (int i = 0; i < elemList.size(); i++) {
            Element elem = (Element) elemList.elementAt(i);
            defAttr.addAttributes(elem.style); // Combine styles
        }
    }



    private void setDefaultStyle(TextStyle def) {

        // Set default attribute to some sensible values.
        def.setAlignment(-1);
        def.setBackground(getBackground());
        def.setForeground(getCurrentForeground());
        def.setBold(false);
        def.setItalic(false);
        def.setItalic(false);
        def.setIcon(null);
    }

    // TODO: ChatTextArea needs TextArea to make getLines() an instance method,
    // instead of static method... For now we create an object of TextArea so
    // we can access the getLines() method.
    TextArea ta = new TextArea();
    private int[] getLines(String str, Font f, int startX, int w) {

        return ta.getLines(str, f, 0, w - startX, w);
    }

    private void insertSortedElement(Vector v, Element elem) {

        /* TODO:
         * Needs to Insert sorted, but also put it at the beginning of any
         * element that is equal -> Latest inserted elements have higher priority (instead of random)
         * FOR NOW: Assume the user is already inserting in sorted order (true for a html like parser)
         */
        v.addElement(elem);

/*
        int elemStartOff = elem.startOffset;
        int low = 0;
        int high = v.size();

        while (low < high) {
            int middle = (low + high) / 2;
            int middleStartOff = ((Element) v.elementAt(middle)).startOffset;

            if (middleStartOff > elemStartOff) {
                high = middle;
            } else if (middleStartOff < elemStartOff) {
                low = middle + 1;
            } else { // They are equal
                low = middle;
                break;
            }
        }

        while (low > 0) {
            int middleStartOff = ((Element) v.elementAt(low)).startOffset;

            if (middleStartOff != elemStartOff) {
                break;
            }

            low--;
        }

        v.insertElementAt(elem, low);
*/
        /*
        int middle = low;
        int lastMidle = high;

        while (lastMidle != middle) {
            lastMidle = middle;
            middle = (high - low) / 2;
            Element elem = (Element) v.elementAt(middle);

            // Note: Don't check for == to make sure we get the first of the
            // list if there are more than one with the same offset
            if (elem.startOffset > offset) {
                high = middle;
            } else {
                low = middle;
            }
        }

        return low or high;
        */
    }

    // Methods we want
    // from JEditorPane,
    // addHyperlinkListener()
    // ??insertString(int offset, String str, Style a)  ?




 // ------------ Inner classes ---------------

   /**
    * @see javax.TextStyle.text.AttributeSet
    */
    static public class TextStyle {

        private int alignment = -1; // default is ALIGN_LEFT (0)
        private byte textStyle; // Bitmap of Bold, Italic, Underline, etc
        private int backgroundColor = -1;
        private int foregroundColor = -1;
        private Icon icon;


        /**
         * @see javax.swing.text.StyleConstants#ALIGN_CENTER StyleConstants.ALIGN_CENTER
         */
        public static final int ALIGN_CENTER = 1;

        /**
         * @see javax.swing.text.StyleConstants#ALIGN_JUSTIFIED StyleConstants.ALIGN_JUSTIFIED
         */
        public static final int ALIGN_JUSTIFIED = 3;

        /**
         * @see javax.swing.text.StyleConstants#ALIGN_LEFT StyleConstants.ALIGN_LEFT
         */
        public static final int ALIGN_LEFT = 0;

        /**
         * @see javax.swing.text.StyleConstants#ALIGN_RIGHT StyleConstants.ALIGN_RIGHT
         */
        public static final int ALIGN_RIGHT = 2;

        /**
         * @see javax.swing.text.StyleConstants#getAlignment() StyleConstants.getAlignment
         */
        public int getAlignment() {
            return alignment;
        }

        /**
         * @see javax.swing.text.StyleConstants#getAlignment() StyleConstants.getAlignment
         */
        public void setAlignment(int alignment) {
            this.alignment = alignment;
        }

        public int getBackground() {
            return backgroundColor;
        }

        public void setBackground(int c) {
            backgroundColor = c;
        }

        public int getForeground() {
            return foregroundColor;
        }

        public void setForeground(int c) {
            foregroundColor = c;
        }

        private boolean isBitSet(byte b, int pos) {
            return ((b >> pos) & 0x01) == 0x01;
        }

        private byte setBit(byte b, int pos, boolean val) {
            byte bit = (byte) (1 << pos);
            return (val) ? (byte)(b | bit) : (byte)((b ^ 0xff) & bit);
        }

        public boolean isBold() {
            return isBitSet(textStyle, 0);
        }

        public void setBold(boolean b) {
            textStyle = setBit(textStyle, 0, b);
        }

        public boolean isItalic() {
            return isBitSet(textStyle, 1);
        }

        public void setItalic(boolean b) {
            textStyle = setBit(textStyle, 1, b);
        }

        public boolean isUnderline() {
            return isBitSet(textStyle, 2);
        }

        public void setUnderline(boolean b) {
            textStyle = setBit(textStyle, 2, b);
        }

        public Icon getIcon() {
            return icon;
        }

        public void setIcon(Icon icon) {
            this.icon = icon;
        }

        // From MutableAttributeSet
        public void addAttributes(TextStyle attributes) {
            // Merge all text style bits
            textStyle |= attributes.textStyle;

            // Merge (copy if set) all other attributes
            if (attributes.alignment != -1) {
                alignment = attributes.alignment;
            }

            if (attributes.backgroundColor != -1) {
                backgroundColor = attributes.backgroundColor;
            }

            if (attributes.foregroundColor != -1) {
                foregroundColor = attributes.foregroundColor;
            }

            if (attributes.icon != null) {
                icon = attributes.icon;
            }
        }

        public Font getFont() {

            int face = javax.microedition.lcdui.Font.FACE_SYSTEM;
            int style = javax.microedition.lcdui.Font.STYLE_PLAIN;
            if (isBold()) {
                style |= javax.microedition.lcdui.Font.STYLE_BOLD;
            }
            if (isItalic()) {
                style |= javax.microedition.lcdui.Font.STYLE_ITALIC;
            }
            if (isUnderline()) {
                style |= javax.microedition.lcdui.Font.STYLE_UNDERLINED;
            }

            int size = javax.microedition.lcdui.Font.SIZE_MEDIUM;

            // TODO: Font should take care of all this details... and use bitmaps if needed
            // TODO: creating a new Object every time...
            return new Font(javax.microedition.lcdui.Font.getFont(face, style, size));
        }
    } // End Style class




    /**
     * @see javax.swing.text.Element
     */
     static private class Element {
         private TextStyle style;
         private int startOffset;
         private int endOffset;

         // javax.swing.text.AbstractDocument.AbstractElement constructor
         public Element(TextStyle style, int startOffset, int endOffset) {
             this.style = style;
             this.startOffset = startOffset;
             this.endOffset = endOffset;
         }
     }

     // Inner class
     static private class LineFragment {

         int x;
         int y;
         TextStyle style;
         int startOffset;
         int endOffset;

         public LineFragment(int x, int y, TextStyle style, int startOffset, int endOffset) {
             this.x = x;
             this.y = y;
             this.style = style;
             this.startOffset = startOffset;
             this.endOffset = endOffset;
         }
     }
}





