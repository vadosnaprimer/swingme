package net.yura.android;


import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;

import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.test.MainPane.Section;

public class MultimediaTest extends Section {


//    @Override
    public void createTests() {
        add(new Label("Android Tests"));


        addTest("Properties", "showProperties");
        addTest("Protocols", "showProtocols");
        addTest("Content Types", "showContentTypes");
    }

//    @Override
    public void openTest(String actionCommand) {

        String text = "";
        if ("showProperties".equals(actionCommand)) {
            String[] props = {
                    "microedition.media.version",
                    "supports.mixing",
                    "supports.audio.capture",
                    "supports.video.capture",
                    "supports.recording",
                    "audio.encodings",
                    "video.encodings",
                    "video.snapshot.encodings",
                    "streamable.contents",
            };

            text = "--- Properties ---";
            for (int i = 0; i < props.length; i++) {
                text += "\n" + props[i] + ": " + System.getProperty(props[i]);
            }
        }
        else if ("showProtocols".equals(actionCommand)) {

            String[] protos = Manager.getSupportedProtocols(null);

            text = "--- Protocols ---";
            for (int i = 0; i < protos.length; i++) {
                text += "\n" + protos[i];
            }

            for (int i = 0; i < protos.length; i++) {
                text += "\n\n-- Content for " + protos[i] + " --";

                String[] content = Manager.getSupportedContentTypes(protos[i]);
                for (int j = 0; j < content.length; j++) {
                    text += "\n" + content[j];
                }
            }
        }
        else if ("showContentTypes".equals(actionCommand)) {
            String[] contents = Manager.getSupportedContentTypes(null);

            text = "--- Content Types ---";
            for (int i = 0; i < contents.length; i++) {
                text += "\n" + contents[i];
            }

            for (int i = 0; i < contents.length; i++) {
                text += "\n\n-- Protocols for " + contents[i] + " --";

                String[] content = Manager.getSupportedProtocols(contents[i]);
                for (int j = 0; j < content.length; j++) {
                    text += "\n" + content[j];
                }
            }
        }

        Panel mainPanel = new Panel(new FlowLayout(Graphics.VCENTER));
        TextArea cell = new TextArea(text);
        cell.setFocusable(false);
        mainPanel.add(cell);

        addToScrollPane(mainPanel, null );
    }
}
