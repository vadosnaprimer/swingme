package net.yura.android;

import java.util.Enumeration;

import javax.microedition.lcdui.Graphics;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;

import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.BoxLayout;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.test.MainPane.Section;

public class PimTest extends Section {

    TextField tfName;
    TextField tfNumber;


//    @Override
    public void createTests() {
        add(new Label("Android Tests"));

        addTest("Add Contact", "showAddContat");
        addTest("List contacts", "listContacts");
    }

//    @Override
    public void openTest(String actionCommand) {
        System.out.println(">>>>> " + actionCommand);


        if ("showAddContat".equals(actionCommand)) {
            Panel contactPanel = new Panel(new BorderLayout());

            Panel left = new Panel(new FlowLayout(Graphics.VCENTER, 0));
            left.add(new Label("Name:"));
            left.add(new Label("Phone:"));

            Panel right = new Panel(new BoxLayout(Graphics.VCENTER));

            tfName = new TextField(TextField.INITIAL_CAPS_WORD);
            right.add(tfName);

            tfNumber =  new TextField(TextField.PHONENUMBER);
            right.add(tfNumber);

            Button btnAdd = new Button("Add");
            btnAdd.addActionListener(this);
            btnAdd.setActionCommand("addContact2PIM");

            contactPanel.add(left, Graphics.LEFT);
            contactPanel.add(right);
            contactPanel.add(btnAdd, Graphics.BOTTOM);

            addToScrollPane(contactPanel, null );
        }
        else if ("addContact2PIM".equals(actionCommand)) {
            PIM pim = PIM.getInstance();
            ContactList cl = null;
            try {
                cl = (ContactList) pim.openPIMList(PIM.CONTACT_LIST, PIM.WRITE_ONLY);
                Contact newContact = cl.createContact();

                String[] name = new String[cl.stringArraySize(Contact.NAME)];

                if (cl.isSupportedArrayElement(Contact.NAME, Contact.NAME_GIVEN)) {
                      name[Contact.NAME_GIVEN] = tfName.getText();
                }
                else if (cl.isSupportedArrayElement(Contact.NAME, Contact.NAME_OTHER)) {
                      name[Contact.NAME_OTHER] = tfName.getText();
                }

                newContact.addStringArray(Contact.NAME, PIMItem.ATTR_NONE, name);

                if (cl.isSupportedField(Contact.ORG)) {
                    newContact.addString(Contact.ORG, PIMItem.ATTR_NONE, "Acme, Inc.");
                }
                if (cl.isSupportedField(Contact.TEL)) {
                    newContact.addString(Contact.TEL, PIMItem.ATTR_NONE, tfNumber.getText());
                    newContact.addString(Contact.TEL, PIMItem.ATTR_NONE, "123456");
                }
                if (cl.isSupportedField(Contact.EMAIL)) {
                    newContact.addString(Contact.EMAIL, PIMItem.ATTR_NONE, "support@acme.com");
                }

                if (cl.isSupportedField(Contact.NOTE)) {
                    newContact.addString(Contact.NOTE, PIMItem.ATTR_NONE,
                      "You've purchased application XXX with registration number NNN.");
                }

                newContact.commit(); // commits it to the list and the native database
                OptionPane.showConfirmDialog(null, "DONE.", "Success", OptionPane.OK_OPTION);

            } catch (Throwable e) {
                e.printStackTrace();
                OptionPane.showConfirmDialog(null, "Error: " + e.getMessage(), "PIM ERROR", OptionPane.OK_OPTION);
            }
            finally {
                if (cl != null) {
                    try {
                        cl.close();
                    } catch (Throwable e2) {
                    }
                }
            }

        }
        else if ("listContacts".equals(actionCommand)) {
            Panel contactPanel = new Panel(new FlowLayout(Graphics.VCENTER));

            PIM pim = PIM.getInstance();
            ContactList cl = null;

            try {
                cl = (ContactList) pim.openPIMList(PIM.CONTACT_LIST,PIM.READ_WRITE);
                Enumeration en = cl.items();
                while(en.hasMoreElements()) {
                    Contact contact = (Contact) en.nextElement();
                    String text = "";


                    if (contact.countValues(Contact.NAME) > 0) {
                        String[] name = contact.getStringArray(Contact.NAME, PIMItem.ATTR_NONE);

                        text += "Name Given: " + name[Contact.NAME_GIVEN];
                        text += "\nName Other: " + name[Contact.NAME_OTHER];
                    }

                    if (contact.countValues(Contact.TEL) > 0) {
                        text += "\nPhone: " + contact.getString(Contact.TEL, PIMItem.ATTR_NONE);
                    }

                    if (contact.countValues(Contact.UID) > 0) {
                        text += "\nUID: " + contact.getString(Contact.UID, PIMItem.ATTR_NONE);
                    }


                    TextArea cell = new TextArea();
                    cell.setText(text);

                    contactPanel.add(cell);

                }
            } catch (Throwable e) {
                e.printStackTrace();
                OptionPane.showConfirmDialog(null, "Error: " + e.getMessage(), "PIM ERROR", OptionPane.OK_OPTION);
            }
            finally {
                if (cl != null) {
                    try {
                        cl.close();
                    } catch (Throwable e2) {
                    }
                }
            }



            addToScrollPane(contactPanel, null );
        }
    }
}
