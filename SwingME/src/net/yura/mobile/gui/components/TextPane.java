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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.layout.XHTMLLoader;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 * @see javax.swing.text.TextPane
 */
public class TextPane extends Component {

    private String text="";
    private Vector sortedElemsList = new Vector();
    private Vector lineFragments = new Vector();
    private Vector focusableElems = new Vector();
    private int lastLineX;
    private int focusComponentIdx;
    private ActionListener actionListener;

    protected String getDefaultName() {
        return "TextPane";
    }

    // Overwrites Component.paintComponent()
    public void paintComponent(Graphics2D g) {

        // TODO: Use binary search to find the first fragment with a visible y
        // TODO: Stop paint if y is no longer visible... Take care that in the same
        //       line, some y may be visible and others not...

        TextStyle focusElem = getFocusElementStyle();
        int bgColor = getCurrentBackground();
        int topClipY = g.getClipY();
        int bottomClipY = topClipY + g.getClipHeight();

        int numLineFrags = lineFragments.size();
        for (int i = 0; i < numLineFrags; i++) {
            LineFragment lineFrag = (LineFragment) lineFragments.elementAt(i);
            if (lineFrag.y + lineFrag.h < topClipY ||
                lineFrag.y > bottomClipY) {
                continue;
            }

            TextStyle style = lineFrag.style;
            Icon icon = style.getIcon();

            int state = (style == focusElem) ? Style.FOCUSED : Style.ALL;
            int bgStyleColor = style.getBackground(state);

            if (bgColor != bgStyleColor) {
                g.setColor(bgStyleColor);
                g.fillRect(lineFrag.x, lineFrag.y, lineFrag.w, lineFrag.h);
            }

            if (icon == null) {
                String str = text.substring(lineFrag.startOffset, lineFrag.endOffset);

                g.setColor(style.getForeground(state));
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

        height = doLayout();
    }

    // Overwrites Component.setSize()
    public void setSize(int w, int h) {
        super.setSize(w, h);

        height = doLayout();
    }

    // from JEditorPane
    public String getText() {
        return text;
    }

    // from JEditorPane
    public void setText(String text) {
        if (text.startsWith("<html>")) {
            XHTMLLoader loader = new XHTMLLoader();
            loader.gotResult( this, text );
        }
        else {
            this.text = text;
        }
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

    // from DefaultStyledDocument
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
            elem.isParagraph = true;

            insertSortedElement(sortedElemsList, elem);
        }
    }

    public void setActionListener(ActionListener l) {
        this.actionListener = l;
    }

    public void focusLost() {
        super.focusLost();
        repaint();
    }

    public void focusGained() {
        super.focusGained();

        makeVisible(focusComponentIdx, false);
        repaint();
    }

    public boolean processKeyEvent(KeyEvent event) {

        int key = event.getIsDownKey();
        int action = event.getKeyAction(key);

        if (action == Canvas.FIRE) {
            TextStyle style = getFocusElementStyle();
            if (style != null && actionListener != null) {
                actionListener.actionPerformed(style.getAction());
            }
            return true;
        }

        int next = focusComponentIdx;

        next = (action == Canvas.DOWN || action == Canvas.RIGHT) ? next + 1 :
               (action == Canvas.UP || action == Canvas.LEFT) ? next - 1 : next;

        next = (next < 0) ? 0 :
               (next >= focusableElems.size()) ? focusableElems.size() - 1 : next;

        if (next != focusComponentIdx) {

            if (makeVisible(next, true)) {
                focusComponentIdx = next;
            }

            System.out.println("KEY PRESSED: " + focusComponentIdx);

            // TODO: Need to scroll, to make focusable element visible.
            repaint();
            return true;
        }

        return false;
    }

    private boolean makeVisible(int styleIdx, boolean smart) {
        int MAX = Integer.MAX_VALUE;
        int leftX = MAX, rightX = 0;
        int topY = MAX, bottomY = 0;

        TextStyle style = (TextStyle) focusableElems.elementAt(styleIdx);
        for (int i = 0; i < lineFragments.size(); i++) {
            LineFragment frag = (LineFragment) lineFragments.elementAt(i);
            if (frag.style == style) {
                leftX = Math.min(leftX, frag.x);
                rightX = Math.max(rightX, frag.x + frag.w);
                topY = Math.min(topY, frag.y);
                bottomY = Math.max(bottomY, frag.y + frag.h);
            } else if (leftX < MAX) {
                // If we find a block (blockX will become less that MAX), we
                // want to stop search as soon that blocks ends...
                break;
            }
        }

        return scrollRectToVisible(leftX, topY, rightX - leftX, bottomY - topY, smart);
    }

    public void processMouseEvent(int type, int x, int y, KeyEvent keys) {
        super.processMouseEvent(type, x, y, keys);

        if (type == DesktopPane.PRESSED && isFocusOwner()) {

            for (int i = 0; i < lineFragments.size(); i++) {
                LineFragment frag = (LineFragment) lineFragments.elementAt(i);
                String action = frag.style.getAction();
                if (action != null) {
                    if (x >= frag.x && x <= frag.x + frag.w &&
                        y >= frag.y && y <= frag.y + frag.h) {

                        int focusIdx = focusableElems.indexOf(frag.style);
                        makeVisible(focusIdx, false);

                        if (focusIdx != focusComponentIdx) {
                            focusComponentIdx = focusIdx;
                            repaint();
                        }

                        if (actionListener != null) {
                            actionListener.actionPerformed(action);
                        }
                        break;
                    }
                }
            }
        }
    }

    private TextStyle getFocusElementStyle() {
        int idx = focusComponentIdx;
        return (idx < 0 || idx >= focusableElems.size() || !isFocusOwner()) ?
                null : (TextStyle) focusableElems.elementAt(idx);
    }

    private int doLayout() {

        lineFragments.removeAllElements();
        focusableElems.removeAllElements();

        if (text == null || text.length() == 0 || width <= 0) {
            return 0;
        }

        // Reset last line coordinates
        lastLineX = 0;

        Vector elemStyleSortedStack = new Vector();
        int startFragIdx = 0;

        int numElems = sortedElemsList.size();
        for (int i = 0; i < numElems; i++) {

            // 1 - Find a open Element. If more than one starts at the same
            // place, add them to the Element Style stack
            Element currentElem = (Element) sortedElemsList.elementAt(i);
            int nextStartFragIdx = currentElem.startOffset;

            if (nextStartFragIdx == startFragIdx) {
                elemStyleSortedStack.addElement(currentElem);
                continue;
            }

            // 2 - Does any of the stack elements closes?
            startFragIdx = addClosingLineFragments(elemStyleSortedStack, startFragIdx, nextStartFragIdx);

            // 3 - Finally, add the segment from the last closed one, to the one that just starts
            startFragIdx = addLineFragments(elemStyleSortedStack, startFragIdx, nextStartFragIdx);

            // Update the current list of style elements
            insertSortedElement(elemStyleSortedStack, currentElem);
        }

        // 4 - Finally, finish anything that is still on the style stack
        startFragIdx = addClosingLineFragments(elemStyleSortedStack, startFragIdx, text.length());
        addLineFragments(elemStyleSortedStack, startFragIdx, text.length());

        focusable = (focusableElems.size() > 0);

        return layoutVerticaly();
    }

    private int addClosingLineFragments(Vector elemStyleSortedStack, int startFragIdx, int nextStartFragIdx) {
        int nClosesFound;
        do {
            nClosesFound = 0;
            int smallerEndOffset = nextStartFragIdx;
            int smallerEndOffsetIdx = -1;

            // Find the one that closes with the smaller end offset
            for (int j = elemStyleSortedStack.size() - 1; j >= 0 ; j--) {
                Element elem = (Element) elemStyleSortedStack.elementAt(j);
                int elemEndOffset = elem.endOffset;

                if (elemEndOffset <= nextStartFragIdx) {
                    nClosesFound++;
                }

                if (elemEndOffset <= smallerEndOffset) {
                    smallerEndOffset = elemEndOffset;
                    smallerEndOffsetIdx = j;
                }
            }

            // If some Element closes, add the line fragments
            if (nClosesFound > 0) {
                startFragIdx = addLineFragments(elemStyleSortedStack, startFragIdx, smallerEndOffset);
                elemStyleSortedStack.removeElementAt(smallerEndOffsetIdx);
            }

            // Repeat if more that one closes.
        } while (nClosesFound > 1);

        return startFragIdx;
    }

    private int addLineFragments(Vector currentElemStack, int startIndex, int endIndex) {

        if (startIndex >= endIndex) {
            return startIndex;
        }

        TextStyle style = getCombinedStyle(currentElemStack);
        int nOldFragments = lineFragments.size();

        // TODO BUG: If there a "word" is broken at the middle, that entire
        // word may end up split between to lines, instead of moving all to a new line
        String elemText = text.substring(startIndex, endIndex);

        if (style.getIcon() == null)  {
            addLineTextFragments(elemText, style, startIndex);
        }
        else {
            addLineImageFragments(style);
        }

        // Check if a Focusable Line Fragment was added
        if (style.getAction() != null && nOldFragments != lineFragments.size()) {
            focusableElems.addElement(style);
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
                        lineFragments.addElement(new LineFragment(0, 0, 0, 0, style, 0, 0));
                    }
                    lastLineX = 0;

                } else {
                    if (j < lines.length) {
                        // We can safely trim, since there will be more lines
                        lastLineText = trimStringRightSide(lastLineText);
                    }

                    int fragW = f.getWidth(lastLineText);

                    if (fragW > 0) {
                        // Note: First fragments on a line have negative width
                        LineFragment lineFrag = new LineFragment(lastLineX, 0, fragW, fragH, style, startFragIdx, startFragIdx + lastLineText.length());
                        lineFragments.addElement(lineFrag);
                    }

                    lastLineX = (j == lines.length) ? lastLineX + fragW : 0;
                }
            } else {
                // TODO: trim the previous element
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

            LineFragment lineFrag = new LineFragment(lastLineX, 0, imgW, imgH, style, 0, 0);
            lineFragments.addElement(lineFrag);

            lastLineX += imgW;

            if (lastLineX >= width || imgW >= width) {
                // Force a new line
                lastLineX = 0;
            }
        }
    }

    private int layoutVerticaly() {
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

                // Now that we know the height of the entire line, calculate
                // the baseline
                int lineX = 0;

                for (int j = startLineFragIdx; j < i; j++) {
                    LineFragment frag = (LineFragment) lineFragments.elementAt(j);
                    int leftPadding = getParagraphLeftPadding(frag.style, lineW);
                    int fragW = frag.w;

                    frag.y = lineY + (lineH - frag.h);
                    frag.x = lineX + leftPadding;

                    lineX += fragW;
                }

                // This is the start of a new line, increase Y
                lineY += lineH + padding;

                startLineFragIdx = i;
                lineH = lineFrag.h;
                lineW = lineFrag.w;
            }
            else {
                // Note: lineFrag.y holds the fragment height
                lineH = Math.max(lineH, lineFrag.h);
                lineW += lineFrag.w;
            }
        }

        return lineY;
    }

    private int getParagraphLeftPadding(TextStyle style, int lineW) {
        int align = style.getAlignment();
        return (align == TextStyle.ALIGN_CENTER) ? (width - lineW) / 2 :
               (align == TextStyle.ALIGN_RIGHT)? (width - lineW) : 0;
    }

    private String trimStringRightSide(String str) {
        // Note: May need a faster version
        return ("-" + str).trim().substring(1);
    }

    /**
     * Takes a list of Elements and uses its styles to produce the "merged"
     * result.
     */
    private TextStyle getCombinedStyle(Vector elemList) {
        TextStyle paragStyle = new TextStyle();
        TextStyle charsStyle = new TextStyle();

        // Set default paragraph style
        paragStyle.setBackground(getBackground());
        paragStyle.setForeground(getCurrentForeground());
        paragStyle.setAlignment(TextStyle.ALIGN_LEFT);

        for (int i = 0; i < elemList.size(); i++) {
            Element elem = (Element) elemList.elementAt(i);

            // Combine styles
            TextStyle s = (elem.isParagraph) ? paragStyle : charsStyle;
            s.addAttributes(elem.style);
        }

        // Paragraphs cannot have an icon style
        paragStyle.setIcon(null);

        // Merge paragraph and character style. Paragraph style has less
        // priority, but alignment cannot be overloaded
        int align = paragStyle.getAlignment();
        paragStyle.addAttributes(charsStyle);
        paragStyle.setAlignment(align);     // restore alignment if overloaded

        return paragStyle;
    }


    // TODO: ChatTextArea needs TextArea to make getLines() an instance method,
    // instead of static method... For now we create an object of TextArea so
    // we can access the getLines() method.
    TextArea ta = new TextArea();
    private int[] getLines(String str, Font f, int startX, int w) {

        return ta.getLines(str, f, 0, w - startX, w);
    }

    /**
     * Inserts the element inside the vector, sorted in growing order by
     * element startOffeset. If there is already an element with the same
     * startOffeset, it will add it with the highest index as possible.
     * @param v Vector used to insert the new element. Vector must be empty or
     * sorted.
     * @param elem New element to insert.
     */
    private void insertSortedElement(Vector v, Element elem) {
        int low = 0;
        int high = v.size();

        while (low < high) {
            int midle = (low + high) / 2;
            Element midElem = (Element) v.elementAt(midle);

            // Note: Search progresses even if found an element of same value
            if (elem.startOffset < midElem.startOffset) {
                high = midle; // repeat search for bottom half.
            }
            else {
                low = midle + 1; // Repeat search for top half.
            }
        }

        v.insertElementAt(elem, low);
    }

    // Methods we want
    // from JEditorPane,
    // addHyperlinkListener()
    // ??insertString(int offset, String str, Style a)  ?




 // ------------ Inner classes ---------------

   /**
    * @see javax.TextStyle.text.AttributeSet
    */
    static public class TextStyle extends Style {

        private int alignment = -1;
        private byte textStyle; // Bitmap of Bold, Italic, Underline, etc
        private Icon icon;
        private String action;


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
            return getBackground(ALL);
        }

        public void setBackground(int c) {
            addBackground(c, ALL);
        }

        public int getForeground() {
            return getForeground(ALL);
        }

        public void setForeground(int c) {
            addForeground(c, ALL);
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

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        // From MutableAttributeSet
        public void addAttributes(TextStyle attributes) {

            putAll(attributes);

            // Merge all text style bits
            textStyle |= attributes.textStyle;

            // Merge (copy if set) all other attributes
            if (attributes.alignment != -1) {
                alignment = attributes.alignment;
            }

            if (attributes.action != null) {
                action = attributes.action;
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
            return new Font(face, style, size);
        }
    } // End Style class




    /**
     * @see javax.swing.text.Element
     */
     static private class Element {
         boolean isParagraph;
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

         int x, y, w, h;
         TextStyle style;
         int startOffset;
         int endOffset;

         public LineFragment(int x, int y, int w, int h, TextStyle style, int startOffset, int endOffset) {
             this.x = x;
             this.y = y;
             this.w = w;
             this.h = h;
             this.style = style;
             this.startOffset = startOffset;
             this.endOffset = endOffset;
         }
     }
}

