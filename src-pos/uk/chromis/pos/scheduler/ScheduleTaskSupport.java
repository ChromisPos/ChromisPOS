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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import uk.chromis.data.loader.Session;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSales;

public class ScheduleTaskSupport {

    private Logger m_logger;
    private final AppView m_App;
    DataLogicScheduler  m_dlScheduler = null;
    DataLogicSales m_dlSales = null;
    
    public ScheduleTaskSupport( AppView app ) {
        m_App = app;
        
        try {
            FileHandler logHandler = new FileHandler("scheduledtask.log", true );
            m_logger = Logger.getLogger("uk.chromis.pos.scheduler");
            m_logger.addHandler(logHandler);
        } catch (IOException ex) {
            Logger.getLogger(ScheduleTaskSupport.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ScheduleTaskSupport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Logger getLogger() {
        return m_logger;
    }

    // Script support functions
    
    public Session getSession() {
        return m_App.getSession();
    }

    public DataLogicSales getDLSales() {
        if( m_dlSales == null ) {
            m_dlSales = new DataLogicSales();
            m_dlSales.init( m_App );
        }
        return m_dlSales;
    }
    
    public DataLogicScheduler getDLSscheduler() {
        if( m_dlSales == null ) {
            m_dlScheduler = new DataLogicScheduler();
            m_dlScheduler.init( m_App );
        }
        return m_dlScheduler;
    }
        
    public int runCommand( String sCommand ) {
        int retValue = -1;
        
        if( sCommand != null && sCommand.length() > 0 ) {

            try {
                ProcessBuilder pb = new ProcessBuilder(sCommand);
                pb.redirectErrorStream(true);
                Process process;
                process = pb.start();

                //Check result
                retValue = process.waitFor();
                
            } catch (IOException ex) {
                getLogger().log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                getLogger().log(Level.SEVERE, null, ex);
            }
        }
        return retValue;
    }
    
    // Display a message as a popup dialog
    // ONLY USE THIS FOR DEBUGGING
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

    public boolean saveImagePng( String filePath, String fileName, BufferedImage imageData ) {
        
        try {
            File outputfile = new File( filePath + "/" + fileName );
            ImageIO.write( imageData, "png", outputfile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

