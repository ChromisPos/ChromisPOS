//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) (c) 2015-2016
//    http://www.chromis.co.uk
//
//    This file is part of Chromis POS
//
//     Chromis POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Chromis POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    
package uk.chromis.pos.scheduler;

import java.awt.Dialog;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class ScheduleTaskSupport {

    public ScheduleTaskSupport() {
    }

    // Script support functions
    // Display a message as a popup dialog
    public void ShowMessage(String title, String message) {

        // Get details of the original font before we change it otherwise all dialogboxes will use new settings
        JOptionPane pane = new JOptionPane();
        Font originalFont = pane.getFont();

        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("ARIAL", Font.PLAIN, 20)));
        JLabel FontText = new JLabel(message);

        JOptionPane newpane = new JOptionPane();
        newpane.setMessage(FontText);

        Dialog dlg = newpane.createDialog(title);
        dlg.setVisible(true);

        // Return to default settings
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font(originalFont.getName(), originalFont.getStyle(), originalFont.getSize())));
    }

}

