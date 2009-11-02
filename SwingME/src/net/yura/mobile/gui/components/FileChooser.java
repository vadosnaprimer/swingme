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

/**
 * @author Yura Mamyrin
 */
public class FileChooser extends Frame implements Runnable,ActionListener {

    private SelectableFileRenderer thumbOptionRenderer;
    private List fileList;
    private FileGrid fileTabel;
    
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
    
    private RadioButton showAll,showNew,listView,gridView;
    private Label addressBar;
    
    public FileChooser() {

        //setActionListener(this);
        
        setName("Dialog");
        listTitle = new Label();
        //VistoPane.addTitleToWindow(this, listTitle);
        setMaximum(true);
        
        addressBar = new Label(dir);
        scroll = new ScrollPane();
        Panel mainPanel = new Panel(new BorderLayout());
        mainPanel.add(scroll);
        mainPanel.add(addressBar,Graphics.TOP);

        add(mainPanel);
        
        Menu popupMenu = new Menu( "Menu" );
        popupMenu.addActionListener(this);
        
        doneButton = new Button();
        doneButton.setActionCommand("done");
        doneButton.addActionListener(popupMenu);
        popupMenu.add(doneButton);
        
        Menu showMenu = new Menu("Show");
        showMenu.addActionListener(this);
        ButtonGroup group1 = new ButtonGroup();
        showAll = addMenuCheckBox(showMenu,group1,"All","show",true);
        showNew = addMenuCheckBox(showMenu,group1,"New","show",false);
        popupMenu.add(showMenu);
        
        Menu viewMenu = new Menu("View");
        viewMenu.addActionListener(this);
        ButtonGroup group2 = new ButtonGroup();
        listView = addMenuCheckBox(viewMenu,group2,"List","view",true);
        gridView = addMenuCheckBox(viewMenu,group2,"Grid","view",false);
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

    public void setCurrentDirectory(String string) {
        
        // does not accept .. in the path
        int index = string.indexOf("/../");
        while (index!=-1) {
            string = string.substring(0, string.lastIndexOf('/',index-1) ) + string.substring(index+3);
            index = string.indexOf("/../");
        }
        dir = string;
        addressBar.setText(dir);
    }

    private RadioButton addMenuCheckBox(Menu m,ButtonGroup g,String l,String a,boolean sel) {
        RadioButton rb = new RadioButton(l);
        if (sel) {
            rb.setSelected(true);
        }
        rb.setActionCommand(a);
        g.add(rb);
        m.add(rb);
        rb.addActionListener(m);
        return rb;
    }
    
    public void setMultiSelect(boolean multiSelect) {
            this.multiSelect = multiSelect;
    }

    public void setShowRecent(boolean recent) {
            if(recent) {
                showNew.setSelected(true);
            }
            else {
                showAll.setSelected(true);
            }
    }
    public void setGridView(boolean g) {
        if (g) {
            gridView.setSelected(true);
        }
        else {
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
        setVisible(true);
        
        new Thread(this).start();
    }
    
    private void setUpView() {
        scroll.removeAll();

        if (gridView.isSelected()) {
            if (fileTabel==null) {
                fileTabel = new FileGrid( DesktopPane.getDesktopPane().getWidth() / thumbSize);

                SelectableFileRenderer editor = new SelectableFileRenderer();
                editor.addActionListener(FileChooser.this);
                editor.setActionCommand("tableClick");
                fileTabel.setDefaultEditor(SelectableFile.class, editor);
                fileTabel.setDefaultRenderer(SelectableFile.class, thumbOptionRenderer);


            }
            
            int w = DesktopPane.getDesktopPane().getWidth();
            int h = DesktopPane.getDesktopPane().getHeight();
            thumbSize = (h>w)?w/3:h/3;
            
            fileTabel.setRowHeight(thumbSize);
            
            fileTabel.setListData(files);
            scroll.add(fileTabel);
        }
        else {
            if (fileList==null) {
                fileList = new List();
                fileList.setCellRenderer( thumbOptionRenderer);
                fileList.setActionCommand("listSelect");
                fileList.addActionListener(this);
            }
            
            thumbSize = thumbOptionRenderer.getIcon().getIconHeight();
            
            fileList.setListData(files);
            scroll.add(fileList);
        }
        revalidate();
        repaint();
    }

    public void run() {
        this.files.removeAllElements();

        Vector fileNames = NativeUtil.listFiles(dir,filter,showNew.isSelected());

        for (int c=0;c<fileNames.size();c++) {

            String name = (String)fileNames.elementAt(c);
            SelectableFile tbo = new SelectableFile(name, getImage( NativeUtil.getFileType(name) ) );
            files.addElement(tbo);

        }

        if (gridView.isSelected()) {
            fileTabel.setListData(files);
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
                        dataVector = fileTabel.getItems();
                    }
                    else {
                        dataVector = fileList.getItems();
                    }
                    
                    for (Enumeration en = dataVector.elements(); en.hasMoreElements();) {
                            SelectableFile tbOpt = (SelectableFile) en.nextElement();
                            if (tbOpt.isSelected()) {
                                    rs.addElement( tbOpt );
                            }
                    }
                    return rs;
    }
    
    public String getSelectedFile() {
        SelectableFile tbOpt;
        if (gridView.isSelected()) {
            tbOpt = (SelectableFile)fileTabel.getSelectedValue();
        }
        else {
            tbOpt = (SelectableFile)fileList.getSelectedValue();
        }
        return tbOpt.getAbsolutePath();
    }
    
    public void actionPerformed(String myaction) {

        if ("cancel".equals(myaction)) {
            setVisible(false);
        }
        else if ("mainMenu".equals(myaction)) {
            if (fileTabel!=null) {
                fileTabel.removeEditor();
            }
            doneButton.setFocusable( (multiSelect && getSelectedFiles().size()>0) || (!multiSelect && getSelectedFile()!=null && (filter==NativeUtil.TYPE_FOLDER || !NativeUtil.isFileType(getSelectedFile(), NativeUtil.TYPE_FOLDER) )) );
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
            SelectableFile to = (SelectableFile)fileList.getSelectedValue();
            if ( NativeUtil.isFileType(to.getName(), NativeUtil.TYPE_FOLDER) ) {
                // drill down into another dir!
                gotoDir(to);
            }
            else {
                if (multiSelect) {
                    to.setSelected(!to.isSelected());
                    fileList.repaint();
                }
                else {
                    actionPerformed("done");
                }
            }
        }
        else if ("tableClick".equals(myaction)) {
            SelectableFile to = (SelectableFile)fileTabel.getSelectedValue();
            if ( NativeUtil.isFileType(to.getName(), NativeUtil.TYPE_FOLDER) ) {
                // drill down into another dir!
                gotoDir(to);
            }
            else {
                if (multiSelect) {
                    // the editor does this
                }
                else {
                    actionPerformed("done");
                }
            }
        }
        else {
            //#debug
            System.out.println("unknown action in file browser: "+myaction);
        }
        
    }
    
    private void gotoDir(SelectableFile to) {
            if (fileTabel!=null) {
                fileTabel.removeEditor();
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

            private int colCount;

            public FileGrid(int cw) {
                colCount = cw;
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
                return (rowIndex* getColumnCount() ) + columnIndex;
            }

            public Class getColumnClass(int columnIndex) {
                return super.getColumnClass(0);
            }

            public Object getValueAt(int rowIndex, int columnIndex) {

                int i = convertLin(rowIndex, columnIndex);
                if (i>=dataVector.size()) return null;

                return dataVector.elementAt( i );
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                int index = convertLin(rowIndex, columnIndex);
                return index < dataVector.size(); // true by default
            }

            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                dataVector.setElementAt(aValue, convertLin(rowIndex,columnIndex) );
            }

            public int getColumnCount() {
                return colCount;
            }

            public int getRowCount() {
                int c =getColumnCount();
                return (dataVector.size()+(c-1)) / c;
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
                return dir+filename;
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
                    
                    Image img = (currentThumbSize==thumbSize && thumb!=null)?(Image)thumb.get():null;

                    if (img == null) {
                        img = NativeUtil.getThumbnailFromFile( getAbsolutePath() );
                        currentThumbSize = thumbSize;
                        thumb = new WeakReference(img);
                    }

                    if (!lastFewImages.contains(img)) { lastFewImages.addElement(img); }
                    if (lastFewImages.size()>15) { // KEEP TRACK OF LAST 15 thumbs used!
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
    class SelectableFileRenderer extends CheckBox implements ListCellRenderer,TableCellEditor {

            private SelectableFile tbOption;
            private int state;

            public SelectableFileRenderer() {
                setVerticalAlignment(Graphics.BOTTOM);
                setHorizontalAlignment(Graphics.RIGHT);
            }

            public void paintComponent(Graphics2D g) {

                Image img = tbOption.getIcon();
                
                boolean dir=false;
                String name = tbOption.getName();
                int i = name.lastIndexOf('/');
                if (i==name.length()-1) {
                    dir = true;
                    name = name.substring(0,name.length()-1);
                }
                
                g.setColor(foreground);
                    
                if (gridView.isSelected()) {
                    g.drawImage(img, (width-img.getWidth())/2, (height-img.getHeight())/2, Graphics.TOP | Graphics.LEFT);
                    if (dir) {
                        g.setFont(font);
                        g.drawString( name, (width-font.getWidth(name))/2, height-font.getHeight() );
                    }
                }
                else {
                    g.drawImage(img, (thumbSize-img.getWidth())/2, (height-img.getHeight())/2, Graphics.TOP | Graphics.LEFT);
                    g.setFont(font);
                    g.drawString(name, thumbSize, (height-font.getHeight())/2 );
                }
                if (multiSelect && !dir) {
                    // render the checkbox
                    super.paintComponent(g);
                }
            }

            public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                state=Style.ALL;
                if ( list!=null ) {
                    if (list.isFocusable()) {
                        //state |= Style.ENABLED;
                    }
                    else {
                        state |= Style.DISABLED;
                    }
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

                if (tbOption!=null) {
                    setSelected( tbOption.isSelected() );
                }
                else {
                    return null;
                }

                return this;
            }

            public Object getCellEditorValue() {
                tbOption.setSelected(isSelected());
                return tbOption;
            }

    }


    private static Image imgDir,imgPic,imgAudio,imgVid,imgUnknown;
    public static Image getImage(int ftype) {
        if (ftype == NativeUtil.TYPE_FOLDER) {
            if (imgDir==null) {
                try {
                    imgDir = Image.createImage("/directory.gif");
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return imgDir;
        }
        else if (ftype == NativeUtil.TYPE_PICTURE) {
            if (imgPic==null) {
                try {
                    imgPic = Image.createImage("/image.gif");
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return imgPic;
        }
        else if (ftype == NativeUtil.TYPE_AUDIO) {
            if (imgAudio==null) {
                try {
                    imgAudio = Image.createImage("/sound.gif");
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return imgAudio;
        }
        else if (ftype == NativeUtil.TYPE_VIDEO) {
            if (imgVid==null) {
                try {
                    imgVid = Image.createImage("/movie.gif");
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return imgVid;
        }
        else {
            if (imgUnknown==null) {
                try {
                    imgUnknown = Image.createImage("/unknown.gif");
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return imgUnknown;
        }
    }

}
