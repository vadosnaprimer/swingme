/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badoo.model;

import com.badoo.mobile.model.PersonStatus;
import com.badoo.mobile.ui.UIManager;

/**
 *
 * @author Orens
 */
public class PersonStatusStringGeneratorTest {

    UIManager ui;

    public PersonStatusStringGeneratorTest(UIManager ui) {
        this.ui = ui;
        generatePersonStatus();
    }

    private void generatePersonStatus() {
        for(int distance = -1; distance < 100000; distance=distance+30000){
            for(int lastActive = -10; lastActive < 100000; lastActive = lastActive + 30000)
                for (int online = 1; online < 4; online++) {
                    boolean onSite = online == 1;
                    System.out.println("");
                    PersonStatus ps = new PersonStatus();
                    ps.setDistance(distance);
                    ps.setLastActive(lastActive);
                    ps.setOnline(online);
                    ps.setOnSite(onSite);
                    ps.setUid(""+distance+lastActive+online);

                    System.out.println("distance=" + distance + " lastActive=" + lastActive + " online=" + online + " onSite=" + onSite);
                    System.out.println(ui.getLongDistanceString(ps, false));

                }
        }
    }
}
