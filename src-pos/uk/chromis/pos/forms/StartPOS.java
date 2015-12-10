//    Chromis POS  - The New Face of Open Source POS
//    Copyright (C) 2008-2013 
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
//    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>
package uk.chromis.pos.forms;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import uk.chromis.convert.Conversion;
import uk.chromis.format.Formats;
import uk.chromis.pos.instance.InstanceQuery;
import uk.chromis.pos.ticket.TicketInfo;

public class StartPOS {

    private static final Logger logger = Logger.getLogger("uk.chromis.pos.forms.StartPOS");

    /**
     * Creates a new instance of StartPOS
     */
    private StartPOS() {
    }

    /**
     *
     * @return
     */
    public static boolean registerApp() {

        // vemos si existe alguna instancia        
        InstanceQuery i = null;
        try {
            i = new InstanceQuery();
            i.getAppMessage().restoreWindow();
            return false;
        } catch (RemoteException | NotBoundException e) {
            return true;
        }
    }

    private class OpenApp {

    }

    /**
     *
     * @param args
     */
    public static void main(final String args[]) {
        File file = new File(System.getProperty("user.home"), "unicentaopos.properties");
        File chromis = new File(System.getProperty("user.home"), "chromispos.properties");
        //  File openbravo = new File(System.getProperty("user.home"), "openbravopos.properties");

        if (!chromis.exists()) {
            if (file.exists()) /*|| (openbravo.exists()))*/ {
                Thread t1 = new Thread(new Runnable() {
                    public void run() {
                        Conversion convert = new Conversion() {
                        };
                        convert.init();
                    }
                });
                t1.start();
                if (t1.isAlive()) {
                } else {
                    startApp(args);
                }
            } else {
                startApp(args);
            }
        } else {
            startApp(args);
        }
    }

    private static void startApp(final String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                if (!registerApp()) {
                    System.exit(1);
                }

                AppConfig config = AppConfig.getInstance();
                // set Locale.
                String slang = AppConfig.getInstance().getProperty("user.language");
                String scountry = AppConfig.getInstance().getProperty("user.country");
                String svariant = AppConfig.getInstance().getProperty("user.variant");
                if (slang != null && !slang.equals("") && scountry != null && svariant != null) {
                    Locale.setDefault(new Locale(slang, scountry, svariant));
                }
                
                // Set the format patterns
                Formats.setIntegerPattern(AppConfig.getInstance().getProperty("format.integer"));
                Formats.setDoublePattern(AppConfig.getInstance().getProperty("format.double"));
                Formats.setCurrencyPattern(AppConfig.getInstance().getProperty("format.currency"));
                Formats.setPercentPattern(AppConfig.getInstance().getProperty("format.percent"));
                Formats.setDatePattern(AppConfig.getInstance().getProperty("format.date"));
                Formats.setTimePattern(AppConfig.getInstance().getProperty("format.time"));
                Formats.setDateTimePattern(AppConfig.getInstance().getProperty("format.datetime"));

                // Set the look and feel.
                try {

                    Object laf = Class.forName(AppConfig.getInstance().getProperty("swing.defaultlaf")).newInstance();
                    if (laf instanceof LookAndFeel) {
                        UIManager.setLookAndFeel((LookAndFeel) laf);
                    } else if (laf instanceof SubstanceSkin) {
                        SubstanceLookAndFeel.setSkin((SubstanceSkin) laf);
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                    logger.log(Level.WARNING, "Cannot set Look and Feel", e);
                }
                String hostname = AppConfig.getInstance().getProperty("machine.hostname");
                TicketInfo.setHostname(hostname);

                String screenmode = AppConfig.getInstance().getProperty("machine.screenmode");
                if ("fullscreen".equals(screenmode)) {
                    JRootKiosk rootkiosk = new JRootKiosk();
                    rootkiosk.initFrame(config);
                } else {
                    JRootFrame rootframe = new JRootFrame();
                    rootframe.initFrame(config);
                }
            }
        });
    }
}
