/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TextReplace.java
 *
 * Created on 29-Jun-2011, 15:22:03
 */
package net.yura.tools.translation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

/**
 *
 * @author Administrator
 */
public class TextReplace extends javax.swing.JFrame {

    Properties replace;
    
    /** Creates new form TextReplace */
    public TextReplace() {
        
        try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
                e.printStackTrace();
        }
        
        initComponents();
    }

    
    void go(File file,boolean test) {
        
        if (file.isDirectory() && ".svn".equalsIgnoreCase( file.getName() )) {
        
            System.out.println("skipping svn dir "+file);
            
        }
        else if (file.isDirectory() && "CVS".equalsIgnoreCase( file.getName() )) {
        
            System.out.println("skipping CVS dir "+file);
            
        }
        else if (file.isDirectory() && ".git".equalsIgnoreCase( file.getName() )) {
        
            System.out.println("skipping git dir "+file);
            
        }
        else if (!file.isDirectory() && (
                
                file.getName().toLowerCase().endsWith(".jpg") ||
                file.getName().toLowerCase().endsWith(".jpeg") ||
                file.getName().toLowerCase().endsWith(".png") ||
                file.getName().toLowerCase().endsWith(".gif") ||
                file.getName().toLowerCase().endsWith(".bmp") ||
                file.getName().toLowerCase().endsWith(".jar") ||
                file.getName().toLowerCase().endsWith(".zip") ||
                file.getName().toLowerCase().endsWith(".exe")

                )) {
        
            System.out.println("skipping binary file "+file);
            
        }
        else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f:files) {
                go(f,test);
            }
        }
        else {
            
            byte[] bytes = fileToArray(file);
            String text = new String(bytes, 0, bytes.length);
            bytes = null;
            
            boolean changed = false;

            for (Map.Entry<String,String> en: (Set<Map.Entry<String,String>>)(Object)replace.entrySet() ) {
                
                String key = en.getKey();
                String value = en.getValue();
                
                if (text.indexOf(preOld.getText()+key+postOld.getText()) >= 0 ) {

                    System.out.println("Found "+key+" in "+file);

                    changed = true;
                    text = text.replace(preOld.getText()+key+postOld.getText(), preOld.getText()+value+postOld.getText()); 
                }
            }
            
            if (changed && !test) {
                arrayToFile(text.getBytes(), file);
            }
            
        }
    }
    
    
public static byte[] fileToArray(File f) {
        byte[] result = null;
        DataInputStream in = null;
 
        try {
            result = new byte[(int) f.length()];
            in = new DataInputStream(new FileInputStream(f));             
            in.readFully(result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            }
            catch (Exception e) { /* ignore it */ }
        }
 
        return result;
    }
    
    public static void arrayToFile(byte[] bytes, File file) {
        
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

    }
    
    
    
    
    
    
    
    	public static void main2(String[] args) throws Exception {

		BufferedReader bufferin = new BufferedReader(new FileReader(new File(args[0])));
		BufferedWriter bufferout = new BufferedWriter( new FileWriter(new File(args[0]+".output")) );

		String input = bufferin.readLine();
		String output;

		while(input != null) {

			output = process(input);

			if (output!=null) {
				bufferout.write( output );
				bufferout.newLine();
			}

			input = bufferin.readLine();

		}

		bufferin.close();
		bufferout.close();
	}

    private static String process(String input) {
        return input;
    }
    
    
    
    
    
    
    
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        scanDir = new javax.swing.JTextField();
        pickScan = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        oldToNewFile = new javax.swing.JTextField();
        pickOldToNew = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        preOld = new javax.swing.JTextField();
        postOld = new javax.swing.JTextField();
        runButton = new javax.swing.JButton();
        testButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Yura's Mass Replace");

        jLabel1.setText("Scan");

        scanDir.setText("I:\\6_Yura\\Work\\java\\badoo\\repo\\migw");

        pickScan.setText("Select");
        pickScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pickScanActionPerformed(evt);
            }
        });

        jLabel2.setText("old=new");

        oldToNewFile.setText("I:\\6_Yura\\Work\\java\\badoo\\repo\\migw\\todo.txt");

        pickOldToNew.setText("Select");
        pickOldToNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pickOldToNewActionPerformed(evt);
            }
        });

        jLabel3.setText("text");

        preOld.setText("\"");

        postOld.setText("\"");

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        testButton.setText("Test");
        testButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(oldToNewFile, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                            .addComponent(scanDir, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pickScan)
                            .addComponent(pickOldToNew)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(preOld, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(postOld, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                        .addComponent(testButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(runButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(scanDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pickScan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(oldToNewFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(pickOldToNew)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(preOld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(postOld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(runButton)
                        .addComponent(testButton)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pickOldToNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pickOldToNewActionPerformed

        JFileChooser chooser = new JFileChooser();
        
        int result = chooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
        
            oldToNewFile.setText( chooser.getSelectedFile().toString() );
            
        }
    }//GEN-LAST:event_pickOldToNewActionPerformed

    private void pickScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pickScanActionPerformed
        
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int result = chooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
        
            scanDir.setText( chooser.getSelectedFile().toString() );
            
        }
    }//GEN-LAST:event_pickScanActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        
        File scanDirFile = new File( scanDir.getText() );
        
        File oldToNewFileFile = new File( oldToNewFile.getText() );
        
        replace = new Properties();
        try {
            replace.load( new FileReader(oldToNewFileFile) );
                       
            go(scanDirFile,false);

        }
        catch (Exception ex) {
            Logger.getLogger(TextReplace.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }//GEN-LAST:event_runButtonActionPerformed

    private void testButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testButtonActionPerformed
        File scanDirFile = new File( scanDir.getText() );
        
        File oldToNewFileFile = new File( oldToNewFile.getText() );
        
        replace = new Properties();
        try {
            replace.load( new FileReader(oldToNewFileFile) );
                       
            go(scanDirFile,true);

        }
        catch (Exception ex) {
            Logger.getLogger(TextReplace.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_testButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TextReplace().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField oldToNewFile;
    private javax.swing.JButton pickOldToNew;
    private javax.swing.JButton pickScan;
    private javax.swing.JTextField postOld;
    private javax.swing.JTextField preOld;
    private javax.swing.JButton runButton;
    private javax.swing.JTextField scanDir;
    private javax.swing.JButton testButton;
    // End of variables declaration//GEN-END:variables
}
