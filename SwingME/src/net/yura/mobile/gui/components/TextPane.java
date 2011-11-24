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

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.Border;
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
        int bgColor = getBackground();
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

            int fragState = (style == focusElem) ? Style.FOCUSED : Style.ALL;
            int bgStyleColor = style.getBackground(fragState);

            Border boder = style.getBorder(fragState);
            if (boder != null) {
                g.translate(lineFrag.x, lineFrag.y);
                boder.paintBorder(this, g, lineFrag.w, lineFrag.h);
                g.translate(-lineFrag.x, -lineFrag.y);
            }

            if (bgColor != bgStyleColor && bgStyleColor!=Style.NO_COLOR) {
                g.setColor(bgStyleColor);
                g.fillRect(lineFrag.x, lineFrag.y, lineFrag.w, lineFrag.h);
            }

            if (icon == null) {
                String str = text.substring(lineFrag.startOffset, lineFrag.endOffset);

                if (str.length() > 0) {
                    g.setColor(style.getForeground(fragState));
                    Font f = getFont(style,fragState);
                    g.setFont(f);

                    g.drawString(str, lineFrag.x, lineFrag.y);
                }
            } else {
                icon.paintIcon(this, g, lineFrag.x, lineFrag.y);
            }
        }
    }

    // Overwrites Component.workoutMinimumSize()
    protected void workoutMinimumSize() {

            if (getPreferredWidth()!=-1) {
                // this method can be used to determine the size of a dialog during a pack
                // so here we need to give a reasonable response
                width = getPreferredWidth();
                if (width != widthUsed) {
                    height = doLayout();
                    heightUsed = height;
                }
                height = heightUsed;
            }
            else  {
                width = 10;
                height = (widthUsed < 0) ? 10 : heightUsed;
            }
    }

    int widthUsed=-1;
    int heightUsed=-1;
    // Overwrites Component.setSize()
    public void setSize(int w, int h) {
        super.setSize(w, h);

        if (width != widthUsed) {
            int oldh = height;
            height = doLayout();
            heightUsed = height;

            if (oldh != height) {

                    DesktopPane.mySizeChanged(this);
            }
        }
    }

    // from JEditorPane
    public String getText() {
        return text;
    }

    // from JEditorPane
    /**
     * like Swing this method IS thread safe
     * @param text1 the text to set, if the text starts with &lt;html&gt; then it will be passed as html
     * @see javax.bluetooth.JEditorPane#setText(java.lang.String) JEditorPane.setText
     */
    public void setText(String text1) {

        sortedElemsList.removeAllElements();
        lineFragments.removeAllElements();
        focusableElems.removeAllElements();

        widthUsed = -1;
        heightUsed = -1;

        if (text1.startsWith("<html>")) {
            text = "";
            XHTMLLoader loader = new XHTMLLoader();
            loader.gotResult( this, text1 );
        }
        else {
            text = text1;
        }

    }

    public void setValue(Object obj) {
        if (obj instanceof String) {
            setText( (String)obj );
        }
    }

    public void append(String text1) {
        // TODO: When we call setText() we do a lot more stuff than just setting the width
        widthUsed = -1;

        if (text1.startsWith("<html>")) {
            XHTMLLoader loader = new XHTMLLoader();
            loader.gotResult( this, text1 );
        }
        else {
            text = text + text1;
        }

        // TODO recalc line fragments for new text

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
    }

    public boolean processKeyEvent(KeyEvent event) {

        int key = event.getIsDownKey();

        // we can NOT call getKeyAction with a 0 keycode
        if (key==0) {
            return false;
        }

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

            repaint();
            return true;
        }

        return false;
    }

    // Overloads Component.updateUI()
    public void updateUI() {
        super.updateUI();
        if(ta!=null)
        ta.updateUI(); // TODO get rid of this hack

        if (sortedElemsList != null) {
            for (int i = 0; i < sortedElemsList.size(); i++) {
                Element elem = (Element) sortedElemsList.elementAt(i);
                elem.style.updateUI();
            }

            // If the UI theme changed, we may longer have the same size...
            widthUsed = -1;
            heightUsed = -1;
        }
    }

    private boolean makeVisible(int styleIdx, boolean smart) {
        // Do we have anything focusable?
        if (focusableElems.size() == 0) {
            return false;
        }

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

        widthUsed = width;

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

        Font f = getFont(style,Style.ALL);

        int borderH = getBorderHeight(style);
        int borderW = getBorderWidth(style);
        int fragH = f.getHeight() + borderH;

        // Break the lines using the Element style
        int[] lines = getLines(elemText, f, lastLineX, width - borderW);

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
                        lineFragments.addElement(new LineFragment(0, 0, 0, fragH, style, 0, 0));
                    }
                    lastLineX = 0;

                } else {

                    // We can safely trim the last space, if we have more lines
                    boolean trimSpace = (j < lines.length);
                    // Trim right hard returns...
                    lastLineText = trimStringRightSide(lastLineText, trimSpace);

                    int fragW = f.getWidth(lastLineText);

                    if (fragW > 0) {
                        fragW += borderW;
                        LineFragment lineFrag = new LineFragment(lastLineX, 0, fragW, fragH, style, startFragIdx, startFragIdx + lastLineText.length());
                        lineFragments.addElement(lineFrag);
                    }

                    lastLineX = (j == lines.length) ? lastLineX + fragW : 0;
                }
            } else {
                lastLineX = 0;
                // TODO: trim the previous element
            }

            startFragIdx = endFragIdx;
        }
    }

    private int getBorderWidth(TextStyle style) {
        Border b = style.getBorder(Style.ALL);
        return (b == null) ? 0 : (b.getLeft() + b.getRight());
    }

    private int getBorderHeight(TextStyle style) {
        Border b = style.getBorder(Style.ALL);
        return (b == null) ? 0 : (b.getTop() + b.getBottom());
    }

    private void addLineImageFragments(TextStyle style) {
        Icon icon = style.getIcon();
        int imgW = icon.getIconWidth();
        int imgH = icon.getIconHeight();

        if (imgH > 0 && imgW > 0) {
            imgW += getBorderWidth(style);
            imgH += getBorderHeight(style);

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
        int numFrags = lineFragments.size();

        if (numFrags == 0) {
            return 0;
        }

        int padding = 2;  // TODO: Spacing
        int lineY = -padding;
        int lineH = 0;
        int lineW = 0;
        int startLineFragIdx = 0;

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

                    Border b = frag.style.getBorder(Style.ALL);
                    if (b != null) {
                        frag.y += b.getTop();
                        frag.x += b.getLeft();
                        frag.h -= b.getTop() + b.getBottom();
                        frag.w -= b.getLeft() + b.getRight();
                    }

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

    private String trimStringRightSide(String str, boolean trimSpace) {

        int startLen = str.length();
        int endLen = startLen;

        if (endLen > 0 && str.charAt(endLen - 1) == '\n') {
            endLen--;
        }

        if (trimSpace && endLen > 0 && str.charAt(endLen - 1) == ' ') {
            endLen--;
        }

        return (endLen != startLen) ? str.substring(0, endLen) : str;
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
        paragStyle.setForeground(getForeground());
        paragStyle.setAlignment(TextStyle.ALIGN_LEFT);

        for (int i = 0; i < elemList.size(); i++) {
            Element elem = (Element) elemList.elementAt(i);

            // Combine styles
            TextStyle s = (elem.isParagraph) ? paragStyle : charsStyle;
            s.putAll(elem.style);
        }

        // Paragraphs cannot have an icon style
        paragStyle.setIcon(null);

        // Merge paragraph and character style. Paragraph style has less
        // priority, but alignment cannot be overloaded
        int align = paragStyle.getAlignment();
        paragStyle.putAll(charsStyle);
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


    private Font getFont(TextStyle ts, int state) {

        Font f = ts.getFont(state);
        if (f != null) {
            return f;
        }

        f = theme.getFont(state);

        // some default values
        int face = javax.microedition.lcdui.Font.FACE_SYSTEM;
        int size = javax.microedition.lcdui.Font.SIZE_MEDIUM;

        javax.microedition.lcdui.Font sysf = f.getFont();
        if (sysf!=null) {
            face = sysf.getFace();
            size = sysf.getSize();
        }

        int style = javax.microedition.lcdui.Font.STYLE_PLAIN;
        if (ts.isBold()) {
            style |= javax.microedition.lcdui.Font.STYLE_BOLD;
        }
        if (ts.isItalic()) {
            style |= javax.microedition.lcdui.Font.STYLE_ITALIC;
        }
        if (ts.isUnderline()) {
            style |= javax.microedition.lcdui.Font.STYLE_UNDERLINED;
        }

        // TODO: Font should take care of all this details... and use bitmaps if needed
        if (style!=javax.microedition.lcdui.Font.STYLE_PLAIN) {
            // TODO: creating a new Object every time...
            return new Font(face, style, size);
        }

        return f;
    }

 // ------------ Inner classes ---------------

   /**
    * @see javax.TextStyle.text.AttributeSet
    */
    static public class TextStyle extends Style {

        private int alignment = -1;
        private byte textStyle; // Bitmap of Bold, Italic, Underline, etc
        private Icon icon;
        private String action;
        private String name;


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
            //#mdebug debug
            if (alignment < 0 ||  alignment > 3) {
                throw new IllegalArgumentException("setAlignment");
            }
            //#enddebug
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // From MutableAttributeSet
        public void putAll(Style attributes) {

            super.putAll(attributes);

            if (attributes instanceof TextStyle) {
                TextStyle a = (TextStyle)attributes;
                // Merge all text style bits
                textStyle |= a.textStyle;

                // Merge (copy if set) all other attributes
                if (a.alignment != -1) {
                    alignment = a.alignment;
                }

                if (a.action != null) {
                    action = a.action;
                }

                if (a.icon != null) {
                    icon = a.icon;
                }

                if (a.name != null) {
                    name = a.name;
                }
            }
        }

        public void updateUI() {
            if (name != null) {
                Style newStyle = DesktopPane.getDesktopPane().getLookAndFeel().getStyle(name);
                if (newStyle != null) {
                    reset(); // Reset parent style
                    super.putAll(newStyle);
                }
            }
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

