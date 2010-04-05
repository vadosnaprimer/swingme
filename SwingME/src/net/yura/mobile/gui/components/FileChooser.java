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
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.celleditor.TableCellEditor;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.ButtonGroup;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.io.NativeUtil;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.ImageUtil;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JFileChooser
 */
public class FileChooser extends Frame implements Runnable, ActionListener {

    private SelectableFileRenderer thumbOptionRenderer;
    private List fileList;
    private GridList fileTable;
    private ActionListener actionListener;
    private String action;
    private String dir = NativeUtil.ROOT_PREX;
    private int filter = NativeUtil.TYPE_ALL;
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
          Logger.info(ex);
        }
    }

    public void run() {
      try {
          //#debug
          Logger.debug("FC START");
          Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

          if (files.isEmpty()) {
              requestImage.removeAllElements();
              Vector fileNames = NativeUtil.listFiles(dir, filter, showNew.isSelected());

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
              //#debug
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
                      //#debug
                      Logger.debug("FC END 2");
                      return;
                  }
              }

              String absoultePath = file.getAbsolutePath();
  //#debug debug
  //Logger.debug("start load 1: "+absoultePath);
              yield();
              Image image = NativeUtil.getThumbnailFromFile(absoultePath);
  //#debug debug
  //Logger.debug("start load 2: "+absoultePath);
              if (image == null) {
                  yield();
                  image = NativeUtil.getImageFromFile( absoultePath );
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
        Logger.error(t);
      }
    }

    /**
     * @see javax.swing.JFileChooser#getSelectedFiles() JFileChooser.getSelectedFiles
     */
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
        return tbOpt.getAbsolutePath();
    }

    public void actionPerformed(String myaction) {

        if ("cancel".equals(myaction)) {
            setVisible(false);
            lastFewImages.removeAllElements();
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
            return getValueAt(getSelectedRow(), getSelectedColumn());
        }
        /**
         * @see List#getSelectedIndex()
         */
        public int getSelectedIndex() {
            if (getSelectedRow() == -1 && getSelectedColumn() == -1) return -1;
            return convertLin(getSelectedRow(), getSelectedColumn());
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
            int cc = widthUsed / getRowHeight(0);
            return cc==0?1:cc;
        }

        public int getRowCount() {
            int c = getColumnCount();
            return (dataVector.size() + (c - 1)) / c;
        }

        public void setRowHeight(int h) {
            removeEditor(); // make sure editor is closed
            int index = getSelectedIndex();
            super.setRowHeight(h);
            setSelectedIndex(index);
        }

        private int widthUsed = -1;
        public void workoutMinimumSize() {
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
    }

    class SelectableFile {

        private WeakReference thumb;
        private int currentThumbSize;
        private boolean toggleSelected = false;
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
         * @see net.yura.mobile.util.Option#getIcon()
         */
        public Icon getIcon() {

            int type = NativeUtil.getFileType(filename);
            if (type == NativeUtil.TYPE_PICTURE) {

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
                int id = NativeUtil.getFileType( tbOption.getName() );

                if (id == NativeUtil.TYPE_FOLDER) {
                    img = folderIcon;
                }
                else if (id == NativeUtil.TYPE_PICTURE) {
                    img = imageIcon;
                }
                else if (id == NativeUtil.TYPE_AUDIO) {
                    img = soundIcon;
                }
                else if (id == NativeUtil.TYPE_VIDEO) {
                    img = videoIcon;
                }

                if (img == null ){
                    img = unknownIcon;
                }
            }

            boolean dir = false;
            String name = tbOption.getName();
            int i = name.lastIndexOf('/');
            if (i == name.length() - 1) {
                dir = true;
                name = name.substring(0, name.length() - 1);
            }

            g.setColor( getCurrentForeground() );

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
            setupState(list, isSelected, cellHasFocus);
            return getTableCellEditorComponent(null, value, isSelected, 0, 0);
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
