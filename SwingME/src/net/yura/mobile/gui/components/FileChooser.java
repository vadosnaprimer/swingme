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

import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.ButtonGroup;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.celleditor.TableCellEditor;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.io.FileUtil;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.ImageUtil;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JFileChooser
 */
public class FileChooser extends Frame implements Runnable, ActionListener {

    /**
     * @see javax.swing.JFileChooser#CANCEL_OPTION JFileChooser.CANCEL_OPTION
     */
    public static final String NO_FILE_SELECTED = "no_file_selected";

    private SelectableFileRenderer thumbOptionRenderer;
    private List fileList;
    private GridList fileTable;
    private ActionListener actionListener;
    private String action;
    private String dir = FileUtil.ROOT_PREX;
    private int filter = FileUtil.TYPE_ALL;
    private boolean multiSelect;
//    private boolean useGridView;
//    private boolean showOnlyNew;
    private Button doneButton;
//    private Label listTitle;
    private ScrollPane scroll;
    private Vector files;
    private Vector lastFewImages;
    private int thumbSize;
    private RadioButton showAll,  showNew,  listView,  gridView;
    private Label addressBar;
    private int hardImages = 15;

    /**
     * @see javax.swing.JFileChooser#JFileChooser() JFileChooser.JFileChooser
     */
    public FileChooser() {

        //setActionListener(this);

        setName("Dialog");
//        listTitle = new Label();
        //VistoPane.addTitleToWindow(this, listTitle);

        addressBar = new Label(dir);
        scroll = new ScrollPane();
        Panel mainPanel = new Panel(new BorderLayout());
        mainPanel.add(scroll);
        mainPanel.add(addressBar, Graphics.TOP);

        add(mainPanel);

        Menu popupMenu = new Menu( (String)DesktopPane.get("menuText") );
        popupMenu.setActionCommand("mainMenu");
        popupMenu.addActionListener(this);

        doneButton = new Button();
        doneButton.setActionCommand("done");
        doneButton.addActionListener(this);
        popupMenu.add(doneButton);

        Menu showMenu = new Menu((String)DesktopPane.get("showText"));
        //showMenu.addActionListener(this);
        ButtonGroup group1 = new ButtonGroup();
        showAll = addMenuCheckBox(showMenu, group1, (String)DesktopPane.get("allText"), "show", true);
        showNew = addMenuCheckBox(showMenu, group1, (String)DesktopPane.get("newText"), "show", false);
        popupMenu.add(showMenu);

        Menu viewMenu = new Menu((String)DesktopPane.get("viewText"));
        //viewMenu.addActionListener(this);
        ButtonGroup group2 = new ButtonGroup();
        listView = addMenuCheckBox(viewMenu, group2, (String)DesktopPane.get("listText"), "view", true);
        gridView = addMenuCheckBox(viewMenu, group2, (String)DesktopPane.get("gridText"), "view", false);
        popupMenu.add(viewMenu);


        //setWindowCommand(0, new CommandButton(popupMenu));
        //setWindowCommand(1, new CommandButton(,"cancel"));

        Button close = new Button((String)DesktopPane.get("cancelText"));
        close.setActionCommand("cancel");
        close.addActionListener(this);
        close.setMnemonic(KeyEvent.KEY_END);

        MenuBar bar = new MenuBar();
        setMenuBar(bar);
        // we need to add it to the window before we add the buttons so they get
        // auto softkey assigned
        bar.add(popupMenu);
        bar.add(close);

        files = new Vector(0);
        lastFewImages = new Vector();
        thumbOptionRenderer = new SelectableFileRenderer();

    }

    /**
     * @see javax.swing.JFileChooser#JFileChooser(java.io.File) JFileChooser.JFileChooser
     */
    public FileChooser(String string) {
        this();
        setCurrentDirectory(string);
    }

    public void doLayout() {
        // called on revalidate
        calcThumbSize(width,height);
        super.doLayout();
        calcHardImages();
    }

    private void calcThumbSize(int w,int h) {
        if (gridView.isSelected()) {
            thumbSize = ( (h > w) ? w : h )/10 + 30;
            fileTable.setRowHeight( thumbSize + 5 ); // some padding
        }
        else {
            thumbSize = thumbOptionRenderer.getFont().getHeight();
            fileList.setFixedCellHeight( thumbSize + 6 ); // some padding
        }
    }
    private void calcHardImages() {
        if (gridView.isSelected()) {
            hardImages = fileTable.getColumnCount() * getHeight()/fileTable.getRowHeight(0);
        }
        else {
            hardImages = getHeight()/fileList.getFixedCellHeight();
        }
        //#debug debug
        Logger.debug("hardImages = " + hardImages);
    }

    /**
     * @see javax.swing.JFileChooser#setCurrentDirectory(java.io.File) JFileChooser.setCurrentDirectory
     */
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

    /**
     * @see javax.swing.JFileChooser#setMultiSelectionEnabled(boolean) JFileChooser.setMultiSelectionEnabled
     */
    public void setMultiSelectionEnabled(boolean multiSelect) {
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

    /**
     * @see javax.swing.JFileChooser#setFileFilter(javax.swing.filechooser.FileFilter) JFileChooser.setFileFilter
     */
    public void setFileFilter(int f) {
        filter = f;
    }

    /**
     * @param al the action listoner that will recieve the action
     * @param a the string that will be fired on the action listoner
     * @param title the title of the dialog
     * @param approveButtonText the text that will be on the button to select the file
     * @see javax.swing.JFileChooser#showDialog(java.awt.Component, java.lang.String) JFileChooser.showDialog
     */
    public void showDialog(ActionListener al, String action, String title, String approveButtonText) {
        actionListener = al;
        this.action = action;

        setTitle(title);
        doneButton.setText(approveButtonText);

        setMaximum(true);

        setUpView();

        setVisible(true);

        startFileLoadingThread();
    }


    private void setUpView() {
        scroll.removeAll();

        if (gridView.isSelected()) {
            if (fileTable == null) {
                fileTable = new GridList(10);
                SelectableFileRenderer editor = new SelectableFileRenderer();
                editor.addActionListener(FileChooser.this);
                editor.setActionCommand("tableClick");
                fileTable.setDefaultEditor(SelectableFile.class, editor);
                fileTable.setDefaultRenderer(SelectableFile.class, thumbOptionRenderer);
            }

            fileTable.setSelectedValues( multiSelect?new Vector():null );

            fileTable.setListData(files);
            scroll.add(fileTable);
        }
        else {
            if (fileList == null) {
                fileList = new List();
                fileList.setCellRenderer(thumbOptionRenderer);
                fileList.setActionCommand("listSelect");
                fileList.addActionListener(this);
                if (Midlet.getPlatform()==Midlet.PLATFORM_ME4SE) {
                    fileList.setDoubleClick(true);
                }
            }

            fileList.setSelectedValues( multiSelect?new Vector():null );

            fileList.setListData(files);
            scroll.add(fileList);
        }
        calcThumbSize(getWidth(),getHeight());
        revalidate();
        repaint();
    }

    private void startFileLoadingThread() {
        this.files.removeAllElements();
        new Thread(this).start();
    }

    private void startImageLoadingThread(SelectableFile file) {
        boolean doStart = requestImage.isEmpty();
        if (!requestImage.contains(file)) {
            requestImage.addElement(file);
        }
        if (doStart) {
            new Thread(this).start();
        }
    }

    final private Vector requestImage = new Vector();

    private void yield() {
        try {
            Thread.yield();
            Thread.sleep(0);
        }
        catch (InterruptedException ex) {
          Logger.info(null, ex);
        }
    }

    public void run() {
      try {
          //#debug debug
          Logger.debug("FC START");
          Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

          if (files.isEmpty()) {
              requestImage.removeAllElements();
              Vector fileNames = FileUtil.listFiles(dir, filter, showNew.isSelected());

              for (int c = 0; c < fileNames.size(); c++) {

                  String name = (String) fileNames.elementAt(c);

                  SelectableFile tbo = new SelectableFile(name);
                  files.addElement(tbo);

              }

              if (gridView.isSelected()) {
                  fileTable.setSelectedIndex(-1);
                  fileTable.setListData(files);
                  fileTable.setSelectedIndex(0);
                  fileTable.setLocation(scroll.getViewPortX(), scroll.getViewPortY());
              }
              else {
                  fileList.setSelectedIndex(-1);
                  fileList.setListData(files);
                  fileList.setSelectedIndex(0);
                  fileList.setLocation(scroll.getViewPortX(), scroll.getViewPortY());
              }

              revalidate();
              repaint();
              //#debug debug
              Logger.debug("FC END 1");
              return;
          }

          while (true) {

              SelectableFile file;

              synchronized (requestImage) {
                  if (!requestImage.isEmpty() && !files.isEmpty() ) {
                      file = (SelectableFile)requestImage.firstElement();
                  }
                  else {
                      //#debug debug
                      Logger.debug("FC END 2");
                      return;
                  }
              }

              String absoultePath = file.getAbsolutePath();
  //#debug debug
  //Logger.debug("start load 1: "+absoultePath);
              yield();
              Image image = ImageUtil.getThumbnailFromFile(absoultePath);
  //#debug debug
  //Logger.debug("start load 2: "+absoultePath);
              if (image == null) {
                  yield();
                  image = ImageUtil.getImageFromFile( absoultePath );
              }
  //#debug debug
  //Logger.debug("start load 3: "+absoultePath);
              int th = thumbSize;
              if (image!=null) {
                  yield();
                  image = ImageUtil.scaleImage(image, th, th);
              }
  //#debug debug
  //Logger.debug("end load: "+absoultePath);
              synchronized (requestImage) {
                  if (image!=null) {
                      file.thumb = new WeakReference( new Icon(image) );
                  }
                  else {
                      file.loadFailed = true;
                  }
                  file.currentThumbSize = th;
                  if (!requestImage.isEmpty()) {
                      requestImage.removeElementAt(0);
                  }
              }

              repaint();
          }
      }
      catch(Throwable t) {
            //#debug warn
            Logger.warn("faital error in FileChooser thread", t);
      }
    }

    /**
     * @see javax.swing.JFileChooser#getSelectedFiles() JFileChooser.getSelectedFiles
     */
    public Vector getSelectedFiles() {
        Vector rs = new Vector();
        Vector dataVector;
        if (gridView.isSelected()) {
            dataVector = fileTable.getSelectedValues();
        }
        else {
            dataVector = fileList.getSelectedValues();
        }

        for (Enumeration en = dataVector.elements(); en.hasMoreElements();) {
            SelectableFile tbOpt = (SelectableFile) en.nextElement();
            rs.addElement(tbOpt.getAbsolutePath());
        }
        return rs;
    }

    /**
     * @see javax.swing.JFileChooser#getSelectedFile() JFileChooser.getSelectedFile
     */
    public String getSelectedFile() {
        SelectableFile tbOpt;
        if (gridView.isSelected()) {
            tbOpt = (SelectableFile) fileTable.getSelectedValue();
        } else {
            tbOpt = (SelectableFile) fileList.getSelectedValue();
        }
        return tbOpt==null?null:tbOpt.getAbsolutePath();
    }

    public void actionPerformed(String myaction) {

        if ("cancel".equals(myaction)) {
            setVisible(false);
            lastFewImages.removeAllElements();
            actionListener.actionPerformed(NO_FILE_SELECTED);
        }
        else if ("mainMenu".equals(myaction)) {
            if (fileTable != null) {
                fileTable.removeEditor();
            }
            doneButton.setFocusable((multiSelect && getSelectedFiles().size() > 0) || (!multiSelect && getSelectedFile() != null && (filter == FileUtil.TYPE_FOLDER || !FileUtil.isFileType(getSelectedFile(), FileUtil.TYPE_FOLDER))));
        }
        else if ("done".equals(myaction)) {
            actionListener.actionPerformed(action);
            setVisible(false);
            lastFewImages.removeAllElements();
        }
        else if ("view".equals(myaction)) {
            setUpView();
        }
        else if ("show".equals(myaction)) {
            startFileLoadingThread();
        }
        else if ("listSelect".equals(myaction)) {
            SelectableFile to = (SelectableFile) fileList.getSelectedValue();
            if (to!=null) { // this is needed as even empty list can be clicked
                if (FileUtil.isFileType(to.getAbsolutePath(), FileUtil.TYPE_FOLDER)) {
                    // drill down into another dir!
                    gotoDir(to);
                }
                else {
                    if (multiSelect) {
                        Vector selectedItems = fileList.getSelectedValues();
                        if ( selectedItems.contains(to) ) {
                            selectedItems.removeElement(to);
                        }
                        else {
                            selectedItems.addElement(to);
                        }
                        fileList.repaint();
                    }
                    else {
                        actionPerformed("done");
                    }
                }
            }
        }
        else if ("tableClick".equals(myaction)) {
            SelectableFile to = (SelectableFile) fileTable.getSelectedValue();
            if (FileUtil.isFileType(to.getAbsolutePath(), FileUtil.TYPE_FOLDER)) {
                // drill down into another dir!
                gotoDir(to);
            }
            else {
                if (multiSelect) {
                    Vector selectedItems = fileTable.getSelectedValues();
                    if ( selectedItems.contains(to) ) {
                        selectedItems.removeElement(to);
                    }
                    else {
                        selectedItems.addElement(to);
                    }
                    fileTable.repaint();
                }
                else {
                    actionPerformed("done");
                }
            }
        }
        else {
            //#debug warn
            Logger.warn("unknown action in file browser: " + myaction);
        }

    }

    private void gotoDir(SelectableFile to) {
        if (fileTable != null) {
            fileTable.removeEditor();
        }
        setCurrentDirectory(to.getAbsolutePath());
        startFileLoadingThread();
    }

    /**
     * Takes care of Picture Selection screen flow.
     * @author Yura Mamyrin
     */
    public static class GridList extends Table {

        public GridList(int cellSize) {
            setRowHeight(cellSize);
        }

        public String getDefaultName() {
            return "List";
        }

        /**
         * @see List#getSelectedValue()
         */
        public Object getSelectedValue() {
            int row = getSelectedRow();
            int col = getSelectedColumn();
            if (row == -1 && col == -1) return null;
            return getValueAt(row, col);
        }
        /**
         * @see List#getSelectedIndex()
         */
        public int getSelectedIndex() {
            int row = getSelectedRow();
            int col = getSelectedColumn();
            if (row == -1 && col == -1) return -1;
            return convertLin(row, col);
        }
        /**
         * @see List#getItems()
         */
        public Vector getItems() {
            return dataVector;
        }
        /**
         * @see List#setListData(java.util.Vector)
         */
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
            if (i >= getSize()) {
                return null;
            }

            return getElementAt(i);
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            int index = convertLin(rowIndex, columnIndex);
            return index < getSize(); // true by default
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            setElementAt(aValue, convertLin(rowIndex, columnIndex));
        }

        public int getColumnCount() {
            int cc = widthUsed / getRowHeight(0);
            return cc==0?1:cc;
        }

        public int getRowCount() {
            int c = getColumnCount();
            return (getSize() + (c - 1)) / c;
        }

        public void setRowHeight(int h) {
            removeEditor(); // make sure editor is closed
            int index = getSelectedIndex();
            super.setRowHeight(h);
            setSelectedIndex(index);
        }

        private int widthUsed = -1;
        protected void workoutMinimumSize() {
            if (getPreferredWidth()!=-1) {
                width = getPreferredWidth();
                widthUsed = width;
                height = workoutHeight();
            }
            else {
                if (widthUsed==-1) {
                    width = getRowHeight(0);
                    height = getRowHeight(0);
                }
                else {
                    width = getRowHeight(0);
                    height = workoutHeight();
                }
            }
        }
        public void setSize(int w,int h) {
            super.setSize(w, h);

            if (width!=widthUsed) {

                int oldh = height;

                removeEditor(); // make sure editor is closed
                int index = getSelectedIndex();
                widthUsed = width;
                setSelectedIndex(index);

                height = workoutHeight();

                if (height!=oldh) {

                    DesktopPane.mySizeChanged(this);

                }

            }
        }
        /**
         * @see List#setSelectedIndex(int)
         */
        public void setSelectedIndex(int a) {
            if (a==-1) {
                setSelectedCell(-1,-1);
            }
            else {
                setSelectedCell( a/getColumnCount(), a%getColumnCount() );
            }
        }

        public Component prepareRenderer(int r,int c) {
            if (convertLin(r, c)>=getSize()) {
                return null;
            }
            return super.prepareRenderer(r, c);
        }

        private int[] getVisibleIndexs() {
            int[] v = getVisibleRect();

            int top = v[1];
            int bottom = (v[1]+v[3])-1;
            int h = getRowHeight(0);
            int row1 = top/h;
            int row2 = bottom/h;
            int rows = getRowCount();

            if ((top>bottom)||(row1<0&&row2<0)||(row1>=rows&&row2>=rows)) {
                row1=-1;
                row2=-1;
            }
            else {
                if (row1<0) {
                    row1=0;
                }
                if (row2>=rows) {
                    row2=rows-1;
                }
            }

            //#mdebug debug
            if ((v[2]<=0||v[3]<=0)&&(row1!=-1||row2!=-1)) {
                throw new RuntimeException("getVisibleIndexs wrong result "+row1+" "+row2);
            }
            //#enddebug

            return new int[] {row1,row2};
        }

        /**
         * @see List#getFirstVisibleIndex()
         * @see javax.swing.JList#getFirstVisibleIndex() JList.getFirstVisibleIndex
         */
        public int getFirstVisibleIndex() {
            int r = getVisibleIndexs()[0];
            if (r==-1) return -1;
            return convertLin(r,0);
        }

        /**
         * @see List#getLastVisibleIndex()
         * @see javax.swing.JList#getLastVisibleIndex() JList.getLastVisibleIndex
         */
        public int getLastVisibleIndex() {
            int r = getVisibleIndexs()[1];
            if (r==-1) return -1;
            int i = convertLin(r,getColumnCount()-1);
            int s = getSize();
            return i>=s?s-1:i;
        }

public void fireActionPerformed() {
    if (editingRow>=0 && editingColumn>=0) {
        editCellAt(editingRow, editingColumn);
        if (editorComp instanceof Button) {
            ((Button)editorComp).fireActionPerformed();
        }
    }
}


    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== DefaultListModel ====================================================
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

        /**
         * @param index
         * @return the element
         * @see List#getElementAt(int) List.getElementAt
         * @see javax.swing.ListModel#getElementAt(int) ListModel.getElementAt
         */
        public Object getElementAt(int index) {
            return dataVector.elementAt(index);
        }

        /**
         * @return the size of the list
         * @see List#getSize() List.getSize
         * @see javax.swing.ListModel#getSize() ListModel.getSize
         */
        public int getSize() {
            return dataVector.size();
        }

        /**
         * @param index
         * @return the element
         * @see List#setElementAt(java.lang.Object, int) List.setElementAt
         * @see javax.swing.DefaultListModel#setElementAt(int) ListModel.setElementAt
         */
        public void setElementAt(Object object, int index) {
            dataVector.setElementAt(object, index);
        }


        //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
        //==== ListSelectionModel ==================================================
        //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

        
        /**
         * @see javax.swing.JList#getSelectedValues() JList.getSelectedValues
         */
        public Vector getSelectedValues() {
            if (selected==null) {
                Vector v = new Vector(1);
                int current = getSelectedIndex();
                if (current>=0) {
                    v.addElement(getElementAt(current));
                }
                return v;
            }
            return selected;
        }

        /**
         * @see javax.swing.JList#setSelectedValue(java.lang.Object, boolean) JList.setSelectedValue
         */
        public void setSelectedValues(Vector v) {
        	removeEditor();
            selected = v;
        }
        
        private Vector selected;

        /**
         * @see javax.swing.ListSelectionModel#isSelectedIndex(int) ListSelectionModel.isSelectedIndex
         */
        public boolean isSelectedIndex(int index) {
            return selected==null? getSelectedIndex() == index:selected.contains(getElementAt(index));
        }

        /**
         * @see javax.swing.JList#clearSelection() JList.clearSelection
         * @see javax.swing.ListSelectionModel#clearSelection() ListSelectionModel.clearSelection
         */
        public void clearSelection() {
            selected = null;
        }
        
        
    }

    class SelectableFile {

        private WeakReference thumb;
        private int currentThumbSize;
        //private boolean toggleSelected = false;
        private String filename;
        private boolean loadFailed;

        /**
         * Constructor as required by Option. Notice ThumbOptions are actually
         * created by fetchSelectedPictures()
         * @param key
         * @param val
         * @param img
         */
        public SelectableFile(String name) {
            //super(name, null, img);
            filename = name;
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
         * @see net.yura.mobile.util.Option#getIcon()
         */
        public Icon getIcon() {

            int type = FileUtil.getFileType(filename);
            if (type == FileUtil.TYPE_PICTURE) {

                Icon img;

                synchronized (requestImage) {
                    img = (currentThumbSize == thumbSize && thumb != null) ? (Icon) thumb.get() : null;
                    if (img == null && !loadFailed) {
                        startImageLoadingThread(this);
                    }
                }

                if (img!=null) {
                    if (!lastFewImages.contains(img)) {
                        lastFewImages.addElement(img);
                    }
                    if (lastFewImages.size() > hardImages) { // KEEP TRACK OF LAST 15 thumbs used!
                        lastFewImages.removeElementAt(0);
                    }
                }

                return img;
            }
            return null;
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

        public SelectableFileRenderer() {
            setVerticalAlignment(Graphics.BOTTOM);
            setHorizontalAlignment(Graphics.RIGHT);
        }

        public void paintComponent(Graphics2D g) {

            Icon img = tbOption.getIcon();

            // the file does not seem to have a icon, must use some default
            if (img == null) {
                int id = FileUtil.getFileType( tbOption.getName() );

                if (id == FileUtil.TYPE_FOLDER) {
                    img = folderIcon;
                }
                else if (id == FileUtil.TYPE_PICTURE) {
                    img = imageIcon;
                }
                else if (id == FileUtil.TYPE_AUDIO) {
                    img = soundIcon;
                }
                else if (id == FileUtil.TYPE_VIDEO) {
                    img = videoIcon;
                }

                if (img == null ){
                    img = unknownIcon;
                }
            }

            boolean dir = false;
            String name = tbOption.getName();
            int lastCharIdx = name.length() - 1;
            if (lastCharIdx >= 1 && name.charAt(lastCharIdx) == '/') {
                dir = true;
                name = name.substring(0, name.length() - 1);
            }

            g.setColor( getForeground() );

            if (gridView.isSelected()) {
                if (img != null) {
                    img.paintIcon(this, g, (width - img.getIconWidth()) / 2, (height - img.getIconHeight()) / 2);
                }
                if (dir) {
                    g.setFont(font);
                    int length = font.getWidth(name);
                    if (length < width) {
                        g.drawString(name, (width - length) / 2, height - font.getHeight());
                    }
                    else {
                        int offset = TextComponent.searchStringCharOffset(name, font, width -(padding*2) -font.getWidth(Label.extension));
                        g.drawString(name.substring(0, offset) + Label.extension, padding, height - font.getHeight());
                    }

                }
            }
            else {
                if (img != null) {
                    img.paintIcon(this, g, (thumbSize - img.getIconWidth()) / 2, (height - img.getIconHeight()) / 2);
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
            tbOption = (SelectableFile) value;
            
            if (list instanceof GridList) {
                isSelected = ((GridList)list).isSelectedIndex( index );
            }

            setupState(list, isSelected, cellHasFocus);

            if (getForeground()==Style.NO_COLOR && list!=null) {// if our theme does not give us a foreground, then fall back to parent
                setForeground( list.getForeground() );
            }

            return this;
        }

        public Component getTableCellEditorComponent(Table table, Object value, boolean isSelected_IGNORE, int row, int column) {
            tbOption = (SelectableFile) value;
            
            GridList grid = (GridList)table;
            
            setSelected( grid.isSelectedIndex( grid.convertLin(row, column) ) );
            return this;
        }

        public Object getCellEditorValue() {
            return tbOption;
        }

        public String getDefaultName() {
            return "CheckBoxRenderer";
        }

        private Icon folderIcon;
        private Icon unknownIcon;
        private Icon imageIcon;
        private Icon soundIcon;
        private Icon videoIcon;
        public void updateUI() {
            super.updateUI();

            folderIcon = (Icon)theme.getProperty("folderIcon", Style.ALL);
            unknownIcon = (Icon)theme.getProperty("unknownIcon", Style.ALL);
            imageIcon = (Icon)theme.getProperty("imageIcon", Style.ALL);
            soundIcon = (Icon)theme.getProperty("soundIcon", Style.ALL);
            videoIcon = (Icon)theme.getProperty("videoIcon", Style.ALL);

        }

    }
/*
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
*/
}
