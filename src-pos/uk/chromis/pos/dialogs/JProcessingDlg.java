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
//    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>
//
package uk.chromis.pos.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import net.miginfocom.swing.MigLayout;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.DriverWrapper;
import uk.chromis.pos.forms.JRootApp;
import uk.chromis.pos.util.AltEncrypter;

/**
 *
 * @author John
 */
public class JProcessingDlg extends JDialog {

    static final Dimension SCREEN_DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int YES = 0;
    public static final int NO = -1;
    public static int CHOICE = NO;
    public static  Boolean DBFAILED =true;
    private Connection con;
    private JProgressBar pb;

    public JProcessingDlg(String message, Boolean create, String changeLog) {

        JButton btnNo = new JButton("No");
        btnNo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CHOICE = NO;
                dispose();
            }
        });

        JButton btnYes = new JButton("Yes");
        btnYes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Thread workThread = new Thread() {
                    public void run() {
                        btnYes.setEnabled(false);
                        btnNo.setEnabled(false);
                        performAction(changeLog);
                        CHOICE = YES;
                        dispose();
                    }
                };
                workThread.start();

                pb.setIndeterminate(true);
                pb.setStringPainted(true);
                if (create) {
                    pb.setString("Creating Database ..... ");
                } else {
                    pb.setString("Upgrading Database ..... ");
                }
            }
        });

        MigLayout layout = new MigLayout("", "[fill]");
        JPanel mainPanel = new JPanel(layout);
        JPanel pbPanel = new JPanel();
        JPanel dialogPanel = new JPanel();
        JPanel btnPanel = new JPanel();

        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/chromis_main.png")));
        mainPanel.add(logoLabel, "wrap");

        dialogPanel.setBackground(Color.white);

        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setColumns(38);
        messageArea.setRows(4);
        messageArea.setText(message);
        messageArea.setLineWrap(true);
        
        Font font = new Font("Arial", Font.BOLD, 12);
        messageArea.setFont(font);
        messageArea.setDisabledTextColor(new java.awt.Color(0, 0, 255));
        messageArea.setEnabled(false);
        messageArea.setFocusable(false);
        messageArea.setOpaque(false);
        messageArea.setRequestFocusEnabled(false);
        dialogPanel.add(messageArea);
        dialogPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
        mainPanel.add(dialogPanel);

        pb = new JProgressBar(0, 100);
        pb.setIndeterminate(false);
        pb.setStringPainted(true);
        pb.setString(" ");
        pb.setSize(480, 20);
        mainPanel.add(pb, "cell 0 2, growy, wrap");

        JButton btnImport = new JButton("Import");
        btnPanel.add(btnYes, "split,right, width 100!");
        btnPanel.add(btnNo, " width 100!");
        mainPanel.add(btnPanel, "right, wrap");
        mainPanel.add(new JLabel(), "wrap");
        mainPanel.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
        getContentPane().add(mainPanel);

        int dialogWidth = SCREEN_DIMENSION.width / 4;
        int dialogHeight = SCREEN_DIMENSION.height / 4;
        int dialogX = SCREEN_DIMENSION.width / 2 - dialogWidth / 2;
        int dialogY = SCREEN_DIMENSION.height / 2 - dialogHeight / 2;

        if (create) {
            setBounds(dialogX, dialogY, 450, 250);
        } else {
            setBounds(dialogX, dialogY, 450, 300);
        }
        setUndecorated(true);
        CHOICE = YES;
    }

    private void performAction(String changelog) {
        String db_user = (AppConfig.getInstance().getProperty("db.user"));
        String db_url = (AppConfig.getInstance().getProperty("db.URL"));
        String db_password = (AppConfig.getInstance().getProperty("db.password"));

        if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
            db_password = cypher.decrypt(db_password.substring(6));
        }
        try {
            Connection con = DriverManager.getConnection(db_url, db_user, db_password);
            ClassLoader cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));

            Liquibase liquibase = null;
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(DriverManager.getConnection(db_url, db_user, db_password)));
            liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database);

            liquibase.update("implement");

            DBFAILED = false;
        } catch (DatabaseException | MalformedURLException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LiquibaseException ex) {
            String txt = ex.getMessage();
            if (ex.getCause() != null) {
                txt = ex.getCause().toString().replace("liquibase.exception.DatabaseException:", "");
            }
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, "Liquibase Error", txt);
            msg.show(this);
        } finally {
            if (con != null) {
                try {
                    con.rollback();
                    con.close();
                } catch (SQLException e) {
                    //nothing to do
                }
            }
        }
        insertMenuEntry(db_user, db_url, db_password); //insert in to menu.root
    }

    private void insertMenuEntry(String db_user, String db_url, String db_password) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_user, db_password);
            String decodedData = "";
            Statement stmt = (Statement) con.createStatement();
            // get the menu from the resources table
            String SQL = "SELECT * FROM RESOURCES WHERE NAME='Menu.Root'";
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                byte[] bytesData = rs.getBytes("CONTENT");
                decodedData = new String(bytesData);
            }
            // get the date from the menu entries table
            SQL = "SELECT * FROM MENUENTRIES ";
            rs = stmt.executeQuery(SQL);
            // while we have some entries lets process them
            while (rs.next()) {
                // lets check if the entry is in the menu
                int index1 = decodedData.indexOf(rs.getString("ENTRY"));
                if (index1 == -1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(rs.getString("FOLLOWS"));
                    sb.append("\");\n        submenu.addPanel(\"");
                    sb.append(rs.getString("GRAPHIC"));
                    sb.append("\", \"");
                    sb.append(rs.getString("TITLE"));
                    sb.append("\", \"");
                    sb.append(rs.getString("ENTRY"));
                    decodedData = decodedData.replaceAll(rs.getString("FOLLOWS"), sb.toString());
                    byte[] bytesData = decodedData.getBytes();
                    String SQL2 = "UPDATE RESOURCES SET CONTENT = ? WHERE NAME = 'Menu.Root' ";
                    PreparedStatement stmt2 = con.prepareStatement(SQL2);
                    stmt2.setBytes(1, bytesData);
                    stmt2.executeUpdate();
                }
            }

            SQL = "DELETE FROM MENUENTRIES ";
            PreparedStatement stmt2 = con.prepareStatement(SQL);
            stmt2.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(JProcessingDlg.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }
    }

}
