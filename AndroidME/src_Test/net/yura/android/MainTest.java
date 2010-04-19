package net.yura.android;

import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.test.MainPane;
import net.yura.mobile.test.MainPane.Section;

public class MainTest extends Section {

    public MainTest(MainPane mainPane) {
        super(mainPane);
    }

//    @Override
    public void createTests() {
        add(new Label("Android Tests"));

        addSection("PIM", new PimTest());
    }

//    @Override
    public void openTest(String actionCommand) {
        if ("mainmenu".equals(actionCommand)) {
            addToScrollPane(this, null, makeButton("Exit", "exit"));
        }
        else if ("exit".equals(actionCommand)) {
            Midlet.exit();
        }
        else if ("pim".equals(actionCommand)) {
            //TODO:
        }
    }
}
