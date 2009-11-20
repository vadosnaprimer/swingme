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

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.celleditor.TableCellEditor;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.util.Option;
import net.yura.mobile.gui.ButtonGroup;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.io.NativeUtil;
import net.yura.mobile.util.ImageUtil;

/**
 * @author Yura Mamyrin
 */
public class FileChooser extends Frame implements Runnable, ActionListener {

    private SelectableFileRenderer thumbOptionRenderer;
    private List fileList;
    private FileGrid fileTable;
    private ActionListener actionListener;
    private String action;
    private String dir = NativeUtil.ROOT_PREX;
    private int filter = NativeUtil.TYPE_ALL;
    private boolean multiSelect;
//    private boolean useGridView;
//    private boolean showOnlyNew;
    private Button doneButton;
    private Label listTitle;
    private ScrollPane scroll;
    private Vector files;
    private Vector lastFewImages;
    private int thumbSize;
    private RadioButton showAll,  showNew,  listView,  gridView;
    private Label addressBar;

    public FileChooser() {

        //setActionListener(this);

        setName("Dialog");
        listTitle = new Label();
        //VistoPane.addTitleToWindow(this, listTitle);

        addressBar = new Label(dir);
        scroll = new ScrollPane();
        Panel mainPanel = new Panel(new BorderLayout());
        mainPanel.add(scroll);
        mainPanel.add(addressBar, Graphics.TOP);

        add(mainPanel);

        Menu popupMenu = new Menu("Menu");
        popupMenu.addActionListener(this);

        doneButton = new Button();
        doneButton.setActionCommand("done");
        doneButton.addActionListener(popupMenu);
        popupMenu.add(doneButton);

        Menu showMenu = new Menu("Show");
        showMenu.addActionListener(this);
        ButtonGroup group1 = new ButtonGroup();
        showAll = addMenuCheckBox(showMenu, group1, "All", "show", true);
        showNew = addMenuCheckBox(showMenu, group1, "New", "show", false);
        popupMenu.add(showMenu);

        Menu viewMenu = new Menu("View");
        viewMenu.addActionListener(this);
        ButtonGroup group2 = new ButtonGroup();
        listView = addMenuCheckBox(viewMenu, group2, "List", "view", true);
        gridView = addMenuCheckBox(viewMenu, group2, "Grid", "view", false);
        popupMenu.add(viewMenu);


        //setWindowCommand(0, new CommandButton(popupMenu));
        //setWindowCommand(1, new CommandButton(,"cancel"));

        Button close = new Button("Cancel");
        close.setActionCommand("cancel");
        close.addActionListener(this);

        MenuBar bar = new MenuBar();
        bar.add(popupMenu);
        bar.add(close);

        setMenuBar(bar);

        files = new Vector(0);
        lastFewImages = new Vector();
        thumbOptionRenderer = new SelectableFileRenderer();

    }

    public void setSize(int w, int h) {
        // called on revalidate

        calcThumbSize(w,h);

        super.setSize(w, h);
    }

    private void calcThumbSize(int w,int h) {
        if (gridView.isSelected()) {

            int a = (h > w) ? w : h;

            thumbSize = (int)((a * 0.1) + 30);
            // a nice way to work out the best icon size to use

            fileTable.setFixedCellHeight( thumbSize );


        }
        else {

            thumbSize = thumbOptionRenderer.getFont().getHeight();
            fileList.setFixedCellHeight( thumbSize + 6 ); // some padding
        }
    }

    public void setCurrentDirectory(String string) {

        // does not accept .. in the path
        int index = string.indexOf("/../");
        while (index != -1) {
            string = string.substring(0, string.lastIndexOf('/', index - 1)) + string.substring(index + 3);
            index = string.indexOf("/../");
        }
        dir = string;
        addressBar.setText(dir);
    }

    private RadioButton addMenuCheckBox(Menu m, ButtonGroup g, String l, String a, boolean sel) {
        RadioButton rb = new RadioButton(l);
        if (sel) {
            rb.setSelected(true);
        }
        rb.setActionCommand(a);
        g.add(rb);
        m.add(rb);
        rb.addActionListener(this);
        return rb;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public void setShowRecent(boolean recent) {
        if (recent) {
            showNew.setSelected(true);
        } else {
            showAll.setSelected(true);
        }
    }

    public void setGridView(boolean g) {
        if (g) {
            gridView.setSelected(true);
        } else {
            listView.setSelected(true);
        }
    }

    public void setFilter(int f) {
        filter = f;
    }

    /**
     * @param al
     * @param a
     * @param title
     * @param approveButtonText
     */
    public void showDialog(ActionListener al, String a, String title, String approveButtonText) {
        actionListener = al;
        action = a;

        listTitle.setText(title);
        doneButton.setText(approveButtonText);

        setUpView();


        setMaximum(true);

        setVisible(true);

        new Thread(this).start();
    }


    private void setUpView() {
        scroll.removeAll();

        if (gridView.isSelected()) {
            if (fileTable == null) {
                fileTable = new FileGrid(10);
                SelectableFileRenderer editor = new SelectableFileRenderer();
                editor.addActionListener(FileChooser.this);
                editor.setActionCommand("tableClick");
                fileTable.setDefaultEditor(SelectableFile.class, editor);
                fileTable.setDefaultRenderer(SelectableFile.class, thumbOptionRenderer);
            }
            fileTable.setListData(files);
            scroll.add(fileTable);
        }
        else {
            if (fileList == null) {
                fileList = new List();
                fileList.setCellRenderer(thumbOptionRenderer);
                fileList.setActionCommand("listSelect");
                fileList.addActionListener(this);
            }
            fileList.setListData(files);
            scroll.add(fileList);
        }
        calcThumbSize(getWidth(),getHeight());
        revalidate();
        repaint();
    }

    public void run() {
        this.files.removeAllElements();

        Vector fileNames = NativeUtil.listFiles(dir, filter, showNew.isSelected());

        for (int c = 0; c < fileNames.size(); c++) {

            String name = (String) fileNames.elementAt(c);

            int id = NativeUtil.getFileType(name);
            Image icon = null;
            try {
                icon = getImage(id);
            }
            catch (IOException ex) {
                //#debug
                System.out.println("can not find image for file type: "+id);
            }

            SelectableFile tbo = new SelectableFile(name, icon );
            files.addElement(tbo);

        }

        if (gridView.isSelected()) {
            fileTable.setListData(files);
        }
        else {
            fileList.setListData(files);
        }
        revalidate();
        repaint();
    }

    public Vector getSelectedFiles() {
        Vector rs = new Vector();
        Vector dataVector;
        if (gridView.isSelected()) {
            dataVector = fileTable.getItems();
        }
        else {
            dataVector = fileList.getItems();
        }

        for (Enumeration en = dataVector.elements(); en.hasMoreElements();) {
            SelectableFile tbOpt = (SelectableFile) en.nextElement();
            if (tbOpt.isSelected()) {
                rs.addElement(tbOpt.getAbsolutePath());
            }
        }
        return rs;
    }

    public String getSelectedFile() {
        SelectableFile tbOpt;
        if (gridView.isSelected()) {
            tbOpt = (SelectableFile) fileTable.getSelectedValue();
        } else {
            tbOpt = (SelectableFile) fileList.getSelectedValue();
        }
        return tbOpt.getAbsolutePath();
    }

    public void actionPerformed(String myaction) {

        if ("cancel".equals(myaction)) {
            setVisible(false);
        }
        else if ("mainMenu".equals(myaction)) {
            if (fileTable != null) {
                fileTable.removeEditor();
            }
            doneButton.setFocusable((multiSelect && getSelectedFiles().size() > 0) || (!multiSelect && getSelectedFile() != null && (filter == NativeUtil.TYPE_FOLDER || !NativeUtil.isFileType(getSelectedFile(), NativeUtil.TYPE_FOLDER))));
        }
        else if ("done".equals(myaction)) {
            actionListener.actionPerformed(action);
            setVisible(false);
        }
        else if ("view".equals(myaction)) {
            setUpView();
        }
        else if ("show".equals(myaction)) {
            new Thread(this).start();
        }
        else if ("listSelect".equals(myaction)) {
            SelectableFile to = (SelectableFile) fileList.getSelectedValue();
            if (NativeUtil.isFileType(to.getName(), NativeUtil.TYPE_FOLDER)) {
                // drill down into another dir!
                gotoDir(to);
            } else {
                if (multiSelect) {
                    to.setSelected(!to.isSelected());
                    fileList.repaint();
                } else {
                    actionPerformed("done");
                }
            }
        }
        else if ("tableClick".equals(myaction)) {
            SelectableFile to = (SelectableFile) fileTable.getSelectedValue();
            if (NativeUtil.isFileType(to.getName(), NativeUtil.TYPE_FOLDER)) {
                // drill down into another dir!
                gotoDir(to);
            } else {
                if (multiSelect) {
                    // the editor does this
                } else {
                    actionPerformed("done");
                }
            }
        }
        else {
            //#debug
            System.out.println("unknown action in file browser: " + myaction);
        }

    }

    private void gotoDir(SelectableFile to) {
        if (fileTable != null) {
            fileTable.removeEditor();
        }
        setCurrentDirectory(to.getAbsolutePath());
        new Thread(this).start();
    }

    /**
     * Takes care of Picture Selection screen flow.
     * 
     * @author emarcato
     * 
     */
    public static class FileGrid extends Table {

        private int colWidth;

        public FileGrid(int cellSize) {
            setFixedCellHeight(cellSize);
        }

        public Object getSelectedValue() {
            return getValueAt(getSelectedRow(), getSelectedColumn());
        }

        public int getSelectedIndex() {
            return getSelectedRow() * getColumnCount() + getSelectedColumn();

        }

        public Vector getItems() {
            return dataVector;
        }

        public void setListData(Vector files) {
            dataVector = files;
        }

        private int convertLin(int rowIndex, int columnIndex) {
            return (rowIndex * getColumnCount()) + columnIndex;
        }

        public Class getColumnClass(int columnIndex) {
            return super.getColumnClass(0);
        }

        public Object getValueAt(int rowIndex, int columnIndex) {

            int i = convertLin(rowIndex, columnIndex);
            if (i >= dataVector.size()) {
                return null;
            }

            return dataVector.elementAt(i);
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            int index = convertLin(rowIndex, columnIndex);
            return index < dataVector.size(); // true by default
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            dataVector.setElementAt(aValue, convertLin(rowIndex, columnIndex));
        }

        public int getColumnCount() {

            // TODO width depends on number of cols
            // and number of cols depends on width
            if (width == 0) {
                return 1;
            }

            return width / colWidth;
        }

        public int getRowCount() {
            int c = getColumnCount();
            return (dataVector.size() + (c - 1)) / c;
        }

        private void setFixedCellHeight(int thumbSize) {
            colWidth = thumbSize;
            setRowHeight(thumbSize);
        }
    }

    class SelectableFile {

        private WeakReference thumb;
        private int currentThumbSize;
        private boolean toggleSelected = false;
        private String filename;
        private Image defaultImage;

        /**
         * Constructor as required by Option. Notice ThumbOptions are actually
         * created by fetchSelectedPictures()
         * @param key
         * @param val
         * @param img
         */
        public SelectableFile(String name, Image img) {
            //super(name, null, img);
            filename = name;
            defaultImage = img;

        }

        public boolean isSelected() {
            return toggleSelected;
        }

        public void setSelected(boolean toggleSelected) {
            this.toggleSelected = toggleSelected;
        }

        public String getAbsolutePath() {
            return dir + filename;
        }

        public String getName() {
            return filename;
        }

        public String getPath() {
            return dir;
        }

        /**
         * Returns the cached image representing this option
         * (non-Javadoc)
         * @see net.yura.mobile.util.Option#getIcon()
         */
        public Image getIcon() {

            int type = NativeUtil.getFileType(filename);
            if (type == NativeUtil.TYPE_PICTURE) {

                Image img = (currentThumbSize == thumbSize && thumb != null) ? (Image) thumb.get() : null;

                if (img == null) {
                    String absoultePath = getAbsolutePath();
                    //Should use a separate thread for this imo.
                    img = NativeUtil.getThumbnailFromFile(absoultePath);

                    if (img == null) {
                        final InputStream is = NativeUtil.getInputStreamFromFileConnector(getAbsolutePath());
                        if (is != null) {
                            try {
                                img = Image.createImage(is);
                                img = ImageUtil.scaleImage(img, thumbSize, thumbSize);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } else {
                        //Do sampling mb or display default blank thumbnail.
                    }

                    currentThumbSize = thumbSize;
                    thumb = new WeakReference(img);
                }

                if (!lastFewImages.contains(img)) {
                    lastFewImages.addElement(img);
                }
                if (lastFewImages.size() > 15) { // KEEP TRACK OF LAST 15 thumbs used!
                    lastFewImages.removeElementAt(0);
                }

                return img;
            }
            else {
                return defaultImage;
            }

        }
    }

    /**
     * * Renders ThumbOption object
     * 
     * @author emarcato
     * 
     */
    class SelectableFileRenderer extends CheckBox implements ListCellRenderer, TableCellEditor {

        private SelectableFile tbOption;
        private int state;

        public SelectableFileRenderer() {
            setVerticalAlignment(Graphics.BOTTOM);
            setHorizontalAlignment(Graphics.RIGHT);
        }

        public void paintComponent(Graphics2D g) {

            Image img = tbOption.getIcon();

            boolean dir = false;
            String name = tbOption.getName();
            int i = name.lastIndexOf('/');
            if (i == name.length() - 1) {
                dir = true;
                name = name.substring(0, name.length() - 1);
            }

            g.setColor(foreground);

            if (gridView.isSelected()) {
                if (img != null) {
                    g.drawImage(img, (width - img.getWidth()) / 2, (height - img.getHeight()) / 2);
                }
                if (dir) {
                    g.setFont(font);
                    g.drawString(name, (width - font.getWidth(name)) / 2, height - font.getHeight());
                }
            }
            else {
                if (img != null) {
                    g.drawImage(img, (thumbSize - img.getWidth()) / 2, (height - img.getHeight()) / 2);
                }
                else {
                    // thumbSize = 0;
                }
                g.setFont(font);
                g.drawString(name, thumbSize, (height - font.getHeight()) / 2);
            }
            if (multiSelect && !dir) {
                // render the checkbox
                super.paintComponent(g);
            }
        }

        public Component getListCellRendererComponent(Component list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            state = Style.ALL;
            if (list != null && !list.isFocusable()) {
                state |= Style.DISABLED;
            }
            if (cellHasFocus) {
                state |= Style.FOCUSED;
            }
            if (isSelected) {
                state |= Style.SELECTED;
            }

            //setBorder(isSelected?activeBorder:normalBorder);
            //setForeground(isSelected?activeForeground:normalForeground);
            return getTableCellEditorComponent(null, value, isSelected, 0, 0);
        }

        public int getState() {
            return state;
        }

        public Component getTableCellEditorComponent(Table table, Object value, boolean isSelected, int row, int column) {

            tbOption = (SelectableFile) value;

            if (tbOption != null) {
                setSelected(tbOption.isSelected());
            } else {
                return null;
            }

            return this;
        }

        public Object getCellEditorValue() {
            tbOption.setSelected(isSelected());
            return tbOption;
        }
    }

    private static Image imgDir,  imgPic,  imgAudio,  imgVid,  imgUnknown;

    public static Image getImage(int ftype) throws IOException {
        if (ftype == NativeUtil.TYPE_FOLDER) {
            if (imgDir == null) {
                    imgDir = Image.createImage("/directory.gif");
            }
            return imgDir;
        }
        else if (ftype == NativeUtil.TYPE_PICTURE) {
            if (imgPic == null) {
                    imgPic = Image.createImage("/image.gif");
            }
            return imgPic;
        }
        else if (ftype == NativeUtil.TYPE_AUDIO) {
            if (imgAudio == null) {
                    imgAudio = Image.createImage("/sound.gif");
            }
            return imgAudio;
        }
        else if (ftype == NativeUtil.TYPE_VIDEO) {
            if (imgVid == null) {
                    imgVid = Image.createImage("/movie.gif");
            }
            return imgVid;
        }
        else {
            if (imgUnknown == null) {
                    imgUnknown = Image.createImage("/unknown.gif");
            }
            return imgUnknown;
        }
    }
}
