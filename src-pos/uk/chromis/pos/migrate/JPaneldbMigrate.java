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
//    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>.
//
//   
package uk.chromis.pos.migrate;

import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.JMessageDialog;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.loader.Session;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.pos.config.PanelConfig;
import uk.chromis.pos.forms.*;
import uk.chromis.pos.util.AltEncrypter;
import uk.chromis.pos.util.DirectoryEvent;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 *
 *
 */
public class JPaneldbMigrate extends JPanel implements JPanelView {

    private DirtyManager dirty = new DirtyManager();
    private Connection con;
    private String sdbmanager;
    //  private Session session;
    private Connection con2;
    private String sdbmanager2;
    private Session session2;
    private ResultSet rs;
    private ResultSet rs2;
    private Statement stmt;
    private Statement stmt2;
    private String SQL;
    private PreparedStatement pstmt;
    private String ticketsnumInvoice;
    private String ticketsnum;
    private String ticketsnumRefund;
    private String ticketsnumPayment;
    private List<PanelConfig> m_panelconfig;

    private String db_user2;
    private String db_url2;
    private char[] pass;
    private String db_password2;
    private String changelog;
    private Liquibase liquibase;

    /**
     * Creates new form JPaneldbMigrate
     *
     * @param oApp
     */
    public JPaneldbMigrate(AppView oApp) {
        this(oApp.getProperties());
    }

    /**
     *
     * @param props
     */
    public JPaneldbMigrate(AppProperties props) {

        initComponents();
        jPanel2.setPreferredSize(new java.awt.Dimension(645, 209));
        m_panelconfig = new ArrayList<>();

        jtxtDbDriverLib.getDocument().addDocumentListener(dirty);
        jtxtDbDriver.getDocument().addDocumentListener(dirty);
        jtxtDbURL.getDocument().addDocumentListener(dirty);
        jtxtDbPassword.getDocument().addDocumentListener(dirty);
        jtxtDbUser.getDocument().addDocumentListener(dirty);
        jbtnDbDriverLib.addActionListener(new DirectoryEvent(jtxtDbDriverLib));
        jNewdbType.addActionListener(dirty);

        jNewdbType.addItem("MySQL");
        jNewdbType.addItem("PostgreSQL");
        jNewdbType.addItem("Derby");

    }

    /**
     *
     * @return
     */
    @SuppressWarnings("empty-statement")
    public Boolean createMigratedb() {
        pb.setString("Creating blank database ..... ");

        if ((!"MySQL".equals(sdbmanager2)) && (!"PostgreSQL".equals(sdbmanager2)) && (!"Apache Derby".equals(sdbmanager2))) {
            return (false);
        }

        try {
            ClassLoader cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));

            changelog = "uk/chromis/pos/liquibase/migratecreate.xml";

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(DriverManager.getConnection(db_url2, db_user2, db_password2)));
            liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database);
            liquibase.update("implement");

        } catch (DatabaseException ex) {
            Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LiquibaseException ex) {
            Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (true);
    }

    /**
     *
     * @return
     */
    public Boolean addFKeys() {
        try {
            ClassLoader cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));

            changelog = "uk/chromis/pos/liquibase/migratecomplete.xml";

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(DriverManager.getConnection(db_url2, db_user2, db_password2)));
            liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database);
            liquibase.update("implement");

        } catch (DatabaseException ex) {
            Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LiquibaseException ex) {
            Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (true);
    }

    /**
     *
     * @return
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Configuration");
    }

    /**
     *
     * @return
     */
    public Boolean getSeconddbDetails() {

        db_user2 = jtxtDbUser.getText();
        db_url2 = jtxtDbURL.getText();
        pass = jtxtDbPassword.getPassword();
        db_password2 = new String(pass);

        Properties connectionProps = new Properties();
        connectionProps.put("user", db_user2);
        connectionProps.put("password", db_password2);
        try {
            Class.forName(jtxtDbDriver.getText());

            ClassLoader cloader = new URLClassLoader(new URL[]{new File(jtxtDbDriverLib.getText()).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(jtxtDbDriver.getText(), true, cloader).newInstance()));
            con2 = (Connection) DriverManager.getConnection(db_url2, db_user2, db_password2);

            session2 = new Session(db_url2, db_user2, db_password2);
            sdbmanager2 = con2.getMetaData().getDatabaseProductName();
            return (true);
        } catch (ClassNotFoundException | MalformedURLException | InstantiationException | IllegalAccessException | SQLException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, AppLocal.getIntString("database.UnableToConnect"), e));
            return (false);
        }
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {
        // connect to the database
        String db_user = (AppConfig.getInstance().getProperty("db.user"));
        String db_url = (AppConfig.getInstance().getProperty("db.URL"));
        String db_password = (AppConfig.getInstance().getProperty("db.password"));

        if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
            // the password is encrypted
            AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
            db_password = cypher.decrypt(db_password.substring(6));
        }

        try {
            Session session = AppViewConnection.createSession();
            con = DriverManager.getConnection(db_url, db_user, db_password);
            sdbmanager = con.getMetaData().getDatabaseProductName();
        } catch (BasicException | SQLException e) {
// put some error trap here  
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, AppLocal.getIntString("database.UnableToConnect"), e));
            System.exit(0);
        }

    }

    /**
     *
     * @return
     */
    @Override
    public boolean deactivate() {
        return (true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbtnMigrate = new javax.swing.JButton();
        jbtnExit = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jtxtDbDriverLib = new javax.swing.JTextField();
        jbtnDbDriverLib = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtDbDriver = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtDbURL = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxtDbUser = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtxtDbPassword = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        jNewdbType = new javax.swing.JComboBox();
        jButtonTest = new javax.swing.JButton();
        pb = new javax.swing.JProgressBar();

        setPreferredSize(new java.awt.Dimension(600, 300));

        jbtnMigrate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnMigrate.setText(AppLocal.getIntString("button.migrate")); // NOI18N
        jbtnMigrate.setMaximumSize(new java.awt.Dimension(70, 33));
        jbtnMigrate.setMinimumSize(new java.awt.Dimension(70, 33));
        jbtnMigrate.setPreferredSize(new java.awt.Dimension(70, 33));
        jbtnMigrate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnMigrateActionPerformed(evt);
            }
        });

        jbtnExit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnExit.setText(AppLocal.getIntString("Button.Exit")); // NOI18N
        jbtnExit.setMaximumSize(new java.awt.Dimension(70, 33));
        jbtnExit.setMinimumSize(new java.awt.Dimension(70, 33));
        jbtnExit.setPreferredSize(new java.awt.Dimension(70, 33));
        jbtnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExitActionPerformed(evt);
            }
        });

        jPanel1.setLayout(null);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "New Database details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText(AppLocal.getIntString("label.dbdriverlib")); // NOI18N

        jtxtDbDriverLib.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jtxtDbDriverLib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtDbDriverLibActionPerformed(evt);
            }
        });

        jbtnDbDriverLib.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/fileopen.png"))); // NOI18N
        jbtnDbDriverLib.setMaximumSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.setMinimumSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.setPreferredSize(new java.awt.Dimension(64, 32));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("Label.DbDriver")); // NOI18N

        jtxtDbDriver.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("Label.DbURL")); // NOI18N

        jtxtDbURL.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jtxtDbURL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtDbURLActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("Label.DbUser")); // NOI18N

        jtxtDbUser.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jtxtDbUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtDbUserActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("Label.DbPassword")); // NOI18N

        jtxtDbPassword.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel5.setText("New  Database ");

        jNewdbType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNewdbTypeActionPerformed(evt);
            }
        });

        jButtonTest.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButtonTest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/database.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jButtonTest.setText(bundle.getString("Button.Test")); // NOI18N
        jButtonTest.setActionCommand(bundle.getString("Button.Test")); // NOI18N
        jButtonTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestjButtonTestConnectionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtDbUser, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtDbPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addComponent(jButtonTest, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jNewdbType, 0, 394, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jtxtDbDriver, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                                    .addComponent(jtxtDbDriverLib, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtxtDbURL))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jbtnDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jNewdbType, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtDbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtDbURL, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtDbUser, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtDbPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButtonTest, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(364, Short.MAX_VALUE)
                .addComponent(jbtnMigrate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbtnExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pb, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnMigrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void performAction() {
        if (getSeconddbDetails()) {
// check if this a supported migration path         
            if (createMigratedb()) {

                try {
                    stmt = (Statement) con.createStatement();
                    stmt2 = (Statement) con2.createStatement(rs2.TYPE_SCROLL_SENSITIVE, rs2.CONCUR_UPDATABLE);


//copy applications table
                    pb.setString("Migrating Applications table");
                    SQL = "SELECT * FROM APPLICATIONS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateString("VERSION", rs.getString("VERSION"));
                        rs2.insertRow();
                    }

// copy attribute table       
                    pb.setString("Migrating Attributes table");
                    SQL = "SELECT * FROM ATTRIBUTE";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.insertRow();
                    }

//  copy attributeinstance table        
                    pb.setString("Migrating Attributeinstance table");
                    SQL = "SELECT * FROM ATTRIBUTEINSTANCE";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("ATTRIBUTESETINSTANCE_ID", rs.getString("ATTRIBUTESETINSTANCE_ID"));
                        rs2.updateString("ATTRIBUTE_ID", rs.getString("ATTRIBUTE_ID"));
                        rs2.updateString("VALUE", rs.getString("VALUE"));
                        rs2.insertRow();
                    }

// copy attributeset table       
                    pb.setString("Migrating Attributeset table");
                    SQL = "SELECT * FROM ATTRIBUTESET";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.insertRow();
                    }

// copy attributesetinstance table  
                    pb.setString("Migrating Attributesetinstance table");
                    SQL = "SELECT * FROM ATTRIBUTESETINSTANCE";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("ATTRIBUTESET_ID", rs.getString("ATTRIBUTESET_ID"));
                        rs2.updateString("DESCRIPTION", rs.getString("DESCRIPTION"));
                        rs2.insertRow();
                    }

// copy attributeuse table    
                    pb.setString("Migrating Attributeuse table");
                    SQL = "SELECT * FROM ATTRIBUTEUSE";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("ATTRIBUTESET_ID", rs.getString("ATTRIBUTESET_ID"));
                        rs2.updateString("ATTRIBUTE_ID", rs.getString("ATTRIBUTE_ID"));
                        rs2.updateInt("LINENO", rs.getInt("LINENO"));
                        rs2.insertRow();
                    }

// copy attributevalue table       
                    pb.setString("Migrating Attributevalue table");
                    SQL = "SELECT * FROM ATTRIBUTEVALUE";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("ATTRIBUTE_ID", rs.getString("ATTRIBUTE_ID"));
                        rs2.updateString("VALUE", rs.getString("VALUE"));
                        rs2.insertRow();
                    }

// copy breaks table     
                    pb.setString("Migrating Breaks table");
                    SQL = "SELECT * FROM BREAKS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateString("NOTES", rs.getString("NOTES"));
                        rs2.updateBoolean("VISIBLE", rs.getBoolean("VISIBLE"));
                        rs2.insertRow();
                    }

// copy categories table    
                    pb.setString("Migrating Categories table");
                    SQL = "SELECT * FROM CATEGORIES";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateString("PARENTID", rs.getString("PARENTID"));
                        rs2.updateBytes("IMAGE", rs.getBytes("IMAGE"));
                        rs2.updateString("TEXTTIP", rs.getString("TEXTTIP"));
                        rs2.updateBoolean("CATSHOWNAME", rs.getBoolean("CATSHOWNAME"));
                        rs2.updateString("COLOUR", rs.getString("COLOUR"));
                        rs2.updateInt("CATORDER", rs.getInt("CATORDER"));
                        rs2.insertRow();
                    }

// copy closedcash  table  
                    pb.setString("Migrating closedcash table");
                    SQL = "SELECT * FROM CLOSEDCASH";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("MONEY", rs.getString("MONEY"));
                        rs2.updateString("HOST", rs.getString("HOST"));
                        rs2.updateInt("HOSTSEQUENCE", rs.getInt("HOSTSEQUENCE"));
                        rs2.updateTimestamp("DATESTART", rs.getTimestamp("DATESTART"));
                        rs2.updateTimestamp("DATEEND", rs.getTimestamp("DATEEND"));
                        rs2.updateInt("NOSALES", rs.getInt("NOSALES"));
                        rs2.insertRow();
                    }

// copy csvimport  table  
                    pb.setString("Migrating CSVImport table");
                    SQL = "SELECT * FROM CSVIMPORT";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("ROWNUMBER", rs.getString("ROWNUMBER"));
                        rs2.updateString("CSVERROR", rs.getString("CSVERROR"));
                        rs2.updateString("REFERENCE", rs.getString("REFERENCE"));
                        rs2.updateString("CODE", rs.getString("CODE"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateDouble("PRICEBUY", rs.getDouble("PRICEBUY"));
                        rs2.updateDouble("PRICESELL", rs.getDouble("PRICESELL"));
                        rs2.updateDouble("PREVIOUSBUY", rs.getDouble("PREVIOUSBUY"));
                        rs2.updateDouble("PREVIOUSSELL", rs.getDouble("PREVIOUSSELL"));
                        rs2.updateString("CATEGORY", rs.getString("CATEGORY"));
                        rs2.insertRow();
                    }

// copy CUSTOMERS  table  
                    pb.setString("Migrating Customers table");
                    SQL = "SELECT * FROM CUSTOMERS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("SEARCHKEY", rs.getString("SEARCHKEY"));
                        rs2.updateString("TAXID", rs.getString("TAXID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateString("TAXCATEGORY", rs.getString("TAXCATEGORY"));
                        rs2.updateString("CARD", rs.getString("CARD"));
                        rs2.updateDouble("MAXDEBT", rs.getDouble("MAXDEBT"));
                        rs2.updateString("ADDRESS", rs.getString("ADDRESS"));
                        rs2.updateString("ADDRESS2", rs.getString("ADDRESS2"));
                        rs2.updateString("POSTAL", rs.getString("POSTAL"));
                        rs2.updateString("CITY", rs.getString("CITY"));
                        rs2.updateString("REGION", rs.getString("REGION"));
                        rs2.updateString("COUNTRY", rs.getString("COUNTRY"));
                        rs2.updateString("FIRSTNAME", rs.getString("FIRSTNAME"));
                        rs2.updateString("LASTNAME", rs.getString("LASTNAME"));
                        rs2.updateString("EMAIL", rs.getString("EMAIL"));
                        rs2.updateString("PHONE", rs.getString("PHONE"));
                        rs2.updateString("PHONE2", rs.getString("PHONE2"));
                        rs2.updateString("FAX", rs.getString("FAX"));
                        rs2.updateString("NOTES", rs.getString("NOTES"));
                        rs2.updateBoolean("VISIBLE", rs.getBoolean("VISIBLE"));
                        rs2.updateTimestamp("CURDATE", rs.getTimestamp("CURDATE"));
                        rs2.updateDouble("CURDEBT", rs.getDouble("CURDEBT"));
                        rs2.updateBytes("IMAGE", rs.getBytes("IMAGE"));
                        rs2.insertRow();
                    }
                                                            
                    
// copy DBPERMISSIONS table 
                    pb.setString("Migrating DBPermissions table");
                    SQL = "SELECT * FROM DBPERMISSIONS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("CLASSNAME", rs.getString("CLASSNAME"));
                        rs2.updateString("SECTION", rs.getString("SECTION"));
                        rs2.updateString("DISPLAYNAME", rs.getString("DISPLAYNAME"));
                        rs2.updateString("DESCRIPTION", rs.getString("DESCRIPTION"));
                        rs2.insertRow();
                    }

// copy DRAWEROPEN table  
                    pb.setString("Migrating Draweropened table");
                    SQL = "SELECT * FROM DRAWEROPENED";
                    rs = stmt.executeQuery(SQL);
                   /*
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateTimestamp("OPENDATE", rs.getTimestamp("OPENDATE"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateString("TICKETID", rs.getString("TICKETID"));
                        rs2.insertRow();
                    }
                    
                    */
                    while (rs.next()) {
                        SQL = "INSERT INTO DRAWEROPENED (OPENDATE, NAME, TICKETID) VALUES (?, ?, ?)";
                        pstmt = con2.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("OPENDATE"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setString(3, rs.getString("TICKETID"));
                        pstmt.executeUpdate();
                    }                    
                  
                    

// copy FLOORS table    
                    pb.setString("Migrating Floors table");
                    SQL = "SELECT * FROM FLOORS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateBytes("IMAGE", rs.getBytes("IMAGE"));
                        rs2.insertRow();
                    }

// copy HVERSIONS table    
                    pb.setString("Migrating HVersions table");
                    SQL = "SELECT * FROM HVERSIONS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("VERSION", rs.getString("VERSION"));
                        rs2.insertRow();
                    }

// copy LEAVES table  
                    pb.setString("Migrating Leaves table");
                    SQL = "SELECT * FROM LEAVES";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("PPLID", rs.getString("PPLID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateTimestamp("STARTDATE", rs.getTimestamp("STARTDATE"));
                        rs2.updateTimestamp("ENDDATE", rs.getTimestamp("ENDDATE"));
                        rs2.updateString("NOTES", rs.getString("NOTES"));
                        rs2.insertRow();
                    }

// copy LINEREMOVED table
                    pb.setString("Migrating Lineremoved table");
                    SQL = "SELECT * FROM LINEREMOVED";
                    rs = stmt.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO LINEREMOVED (REMOVEDDATE, NAME, TICKETID, PRODUCTID, PRODUCTNAME, UNITS) VALUES (?, ?, ?, ?, ?, ?)";
                        pstmt = con2.prepareStatement(SQL);
                        pstmt.setTimestamp(1, rs.getTimestamp("REMOVEDDATE"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setString(3, rs.getString("TICKETID"));
                        pstmt.setString(4, rs.getString("PRODUCTID"));
                        pstmt.setString(5, rs.getString("PRODUCTNAME"));
                        pstmt.setInt(6, rs.getInt("UNITS"));
                        pstmt.executeUpdate();
                    }

// copy LOCATIONS table 
                    pb.setString("Migrating Locations table");
                    SQL = "SELECT * FROM LOCATIONS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateString("ADDRESS", rs.getString("ADDRESS"));
                        rs2.insertRow();
                    }

// copy ORDERS table
                    pb.setString("Migrating Orders table");
                    SQL = "SELECT * FROM ORDERS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("ORDERID", rs.getString("ORDERID"));
                        rs2.updateInt("QTY", rs.getInt("QTY"));
                        rs2.updateString("DETAILS", rs.getString("DETAILS"));
                        rs2.updateString("ATTRIBUTES", rs.getString("ATTRIBUTES"));
                        rs2.updateString("NOTES", rs.getString("NOTES"));
                        rs2.updateString("TICKETID", rs.getString("TICKETID"));
                        rs2.updateTimestamp("ORDERTIME", rs.getTimestamp("ORDERTIME"));
                        rs2.updateString("DISPLAYID", rs.getString("DISPLAYID"));
                        rs2.updateInt("AUXILIARY", rs.getInt("AUXILIARY"));
                        rs2.insertRow();
                    }

// copy payments table    
                    pb.setString("Migrating Payments table");
                    SQL = "SELECT * FROM PAYMENTS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("RECEIPT", rs.getString("RECEIPT"));
                        rs2.updateString("PAYMENT", rs.getString("PAYMENT"));
                        rs2.updateDouble("TOTAL", rs.getDouble("TOTAL"));
                        rs2.updateString("TRANSID", rs.getString("TRANSID"));
                        rs2.updateString("NOTES", rs.getString("NOTES"));
                        rs2.updateDouble("TENDERED", rs.getDouble("TENDERED"));
                        rs2.updateString("CARDNAME", rs.getString("CARDNAME"));
                        rs2.updateBytes("RETURNMSG", rs.getBytes("RETURNMSG"));
                        rs2.insertRow();
                    }

// copy PEOPLE table  
                    pb.setString("Migrating People table");
                    SQL = "SELECT * FROM PEOPLE";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateString("APPPASSWORD", rs.getString("APPPASSWORD"));
                        rs2.updateString("CARD", rs.getString("CARD"));
                        rs2.updateString("ROLE", rs.getString("ROLE"));
                        rs2.updateBoolean("VISIBLE", rs.getBoolean("VISIBLE"));
                        rs2.updateBytes("IMAGE", rs.getBytes("IMAGE"));
                        rs2.insertRow();
                    }

// copy Places table      
                    pb.setString("Migrating Places table");
                    SQL = "SELECT * FROM PLACES";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateInt("X", rs.getInt("X"));
                        rs2.updateInt("Y", rs.getInt("Y"));
                        rs2.updateString("FLOOR", rs.getString("FLOOR"));
                        rs2.updateString("CUSTOMER", rs.getString("CUSTOMER"));
                        rs2.updateString("WAITER", rs.getString("WAITER"));
                        rs2.updateString("TICKETID", rs.getString("TICKETID"));
                        rs2.updateBoolean("TABLEMOVED", rs.getBoolean("TABLEMOVED"));
                        rs2.insertRow();
                    }

// copy Products  table   
                    pb.setString("Migrating Products table");
                    SQL = "SELECT * FROM PRODUCTS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("REFERENCE", rs.getString("REFERENCE"));
                        rs2.updateString("CODE", rs.getString("CODE"));
                        rs2.updateString("CODETYPE", rs.getString("CODETYPE"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateDouble("PRICEBUY", rs.getDouble("PRICEBUY"));
                        rs2.updateDouble("PRICESELL", rs.getDouble("PRICESELL"));
                        rs2.updateString("CATEGORY", rs.getString("CATEGORY"));
                        rs2.updateString("TAXCAT", rs.getString("TAXCAT"));
                        rs2.updateString("ATTRIBUTESET_ID", rs.getString("ATTRIBUTESET_ID"));
                        rs2.updateDouble("STOCKCOST", rs.getDouble("STOCKCOST"));
                        rs2.updateDouble("STOCKVOLUME", rs.getDouble("STOCKVOLUME"));
                        rs2.updateBytes("IMAGE", rs.getBytes("IMAGE"));
                        rs2.updateBoolean("ISCOM", rs.getBoolean("ISCOM"));
                        rs2.updateBoolean("ISSCALE", rs.getBoolean("ISSCALE"));
                        rs2.updateBoolean("ISKITCHEN", rs.getBoolean("ISKITCHEN"));
                        rs2.updateBoolean("PRINTKB", rs.getBoolean("PRINTKB"));
                        rs2.updateBoolean("SENDSTATUS", rs.getBoolean("SENDSTATUS"));
                        rs2.updateBoolean("ISSERVICE", rs.getBoolean("ISSERVICE"));
                        rs2.updateString("DISPLAY", rs.getString("DISPLAY"));
                        rs2.updateBytes("ATTRIBUTES", rs.getBytes("ATTRIBUTES"));
                        rs2.updateBoolean("ISVPRICE", rs.getBoolean("ISVPRICE"));
                        rs2.updateBoolean("ISVERPATRIB", rs.getBoolean("ISVERPATRIB"));
                        rs2.updateString("TEXTTIP", rs.getString("TEXTTIP"));
                        rs2.updateBoolean("WARRANTY", rs.getBoolean("WARRANTY"));
                        rs2.updateDouble("STOCKUNITS", rs.getDouble("STOCKUNITS"));
                        rs2.updateString("ALIAS", rs.getString("ALIAS"));
                        rs2.updateBoolean("ALWAYSAVAILABLE", rs.getBoolean("ALWAYSAVAILABLE"));
                        rs2.updateBoolean("CANDISCOUNT", rs.getBoolean("CANDISCOUNT"));
                        rs2.updateBoolean("ISPACK", rs.getBoolean("ISPACK"));
                        rs2.updateDouble("PACKQUANTITY", rs.getDouble("PACKQUANTITY"));
                        rs2.updateString("PACKPRODUCT", rs.getString("PACKPRODUCT"));
                        rs2.updateBoolean("ISCATALOG", rs.getBoolean("ISCATALOG"));
                        rs2.updateInt("CATORDER", rs.getInt("CATORDER"));
                        rs2.updateString("PROMOTIONID", rs.getString("PROMOTIONID"));
                        rs2.updateBoolean("ALLPRODUCTS", rs.getBoolean("ALLPRODUCTS"));
                        rs2.insertRow();

                    }

// copy PRODUCTS_COM table   
                    pb.setString("Migrating Products_com table");
                    SQL = "SELECT * FROM PRODUCTS_COM";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("PRODUCT", rs.getString("PRODUCT"));
                        rs2.updateString("PRODUCT2", rs.getString("PRODUCT2"));
                        rs2.insertRow();
                    }

// copy PRODUCTS_KIT table 
                    pb.setString("Migrating Producst_kit table");
                    SQL = "SELECT * FROM PRODUCTS_KIT";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("PRODUCT", rs.getString("PRODUCT"));
                        rs2.updateString("PRODUCT_KIT", rs.getString("PRODUCT_KIT"));
                        rs2.updateDouble("QUANTITY", rs.getDouble("QUANTITY"));
                        rs2.insertRow();
                    }

// copy PROMOTIONS table 
                    pb.setString("Migrating Promotions table");
                    SQL = "SELECT * FROM PROMOTIONS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateBytes("CRITERIA", rs.getBytes("CRITERIA"));
                        rs2.updateBytes("SCRIPT", rs.getBytes("SCRIPT"));
                        rs2.updateBoolean("ALLPRODUCTS", rs.getBoolean("ALLPRODUCTS"));
                        rs2.updateBoolean("ISENABLED", rs.getBoolean("ISENABLED"));
                        rs2.insertRow();
                    }

// copy RECEIPTS table    
                    pb.setString("Migrating Receipts table");
                    SQL = "SELECT * FROM RECEIPTS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("MONEY", rs.getString("MONEY"));
                        rs2.updateTimestamp("DATENEW", rs.getTimestamp("DATENEW"));
                        rs2.updateBytes("ATTRIBUTES", rs.getBytes("ATTRIBUTES"));
                        rs2.updateString("PERSON", rs.getString("PERSON"));
                        rs2.insertRow();
                    }

// copy reservation_customers table 
                    pb.setString("Migrating Reservayion_customers table");
                    SQL = "SELECT * FROM RESERVATION_CUSTOMERS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("CUSTOMER", rs.getString("CUSTOMER"));
                        rs2.insertRow();
                    }

// copy reservationS table    
                    pb.setString("Migrating Reservations table");
                    SQL = "SELECT * FROM RESERVATIONS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateTimestamp("CREATED", rs.getTimestamp("CREATED"));
                        rs2.updateTimestamp("DATENEW", rs.getTimestamp("DATENEW"));
                        rs2.updateString("TITLE", rs.getString("TITLE"));
                        rs2.updateInt("CHAIRS", rs.getInt("CHAIRS"));
                        rs2.updateBoolean("ISDONE", rs.getBoolean("ISDONE"));
                        rs2.updateString("DESCRIPTION", rs.getString("DESCRIPTION"));
                        rs2.insertRow();
                    }

// copy resources table   
                    pb.setString("Migrating Resources table");
                    SQL = "SELECT * FROM RESOURCES";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateInt("RESTYPE", rs.getInt("RESTYPE"));
                        rs2.updateBytes("CONTENT", rs.getBytes("CONTENT"));
                        rs2.insertRow();
                    }

// copy ROLES table    
                    pb.setString("Migrating Roles table");
                    SQL = "SELECT * FROM ROLES";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateBytes("PERMISSIONS", rs.getBytes("PERMISSIONS"));
                        rs2.updateInt("RIGHTSLEVEL", rs.getInt("RIGHTSLEVEL"));
                        rs2.insertRow();
                    }
// copy SHAREDTICKETS table  
                    pb.setString("Migrating Sharedtickets table");
                    SQL = "SELECT * FROM SHAREDTICKETS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateBytes("CONTENT", rs.getBytes("CONTENT"));
                        rs2.updateBytes("APPUSER", rs.getBytes("APPUSER"));
                        rs2.updateInt("PICKUPID", rs.getInt("PICKUPID"));
                        rs2.insertRow();
                    }

// copy SHIFT_BREAKS table 
                    pb.setString("Migrating Shift_breaks table");
                    SQL = "SELECT * FROM SHIFT_BREAKS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("SHIFTID", rs.getString("SHIFTID"));
                        rs2.updateString("BREAKID", rs.getString("BREAKID"));
                        rs2.updateTimestamp("STARTTIME", rs.getTimestamp("STARTTIME"));
                        rs2.updateTimestamp("ENDTIME", rs.getTimestamp("ENDTIME"));
                        rs2.insertRow();
                    }

// copy SHIFTS table     
                    pb.setString("Migrating Shifts table");
                    SQL = "SELECT * FROM SHIFTS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateTimestamp("STARTSHIFT", rs.getTimestamp("STARTSHIFT"));
                        rs2.updateTimestamp("ENDSHIFT", rs.getTimestamp("ENDSHIFT"));
                        rs2.updateString("PPLID", rs.getString("PPLID"));
                        rs2.insertRow();
                    }

// copy STOCKCHANGES table  
                    pb.setString("Migrating Stockchanges table");
                    SQL = "SELECT * FROM STOCKCHANGES";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("LOCATION", rs.getString("LOCATION"));
                        rs2.updateString("USERNAME", rs.getString("USERNAME"));
                        rs2.updateTimestamp("UPLOADTIME", rs.getTimestamp("UPLOADTIME"));
                        rs2.updateString("PRODUCTID", rs.getString("PRODUCTID"));
                        rs2.updateInt("TYPE", rs.getInt("TYPE"));
                        rs2.updateString("DISPLAY", rs.getString("DISPLAY"));
                        rs2.updateString("FIELD", rs.getString("FIELD"));
                        rs2.updateString("TEXTVALUE", rs.getString("TEXTVALUE"));
                        rs2.updateBytes("BLOBVALUE", rs.getBytes("BLOBVALUE"));
                        rs2.updateInt("CHANGES_PROCESSED", rs.getInt("CHANGES_PROCESSED"));
                        rs2.insertRow();
                    }

// copy STOCKDIARY table     
                    pb.setString("Migrating Stockdiary table");
                    SQL = "SELECT * FROM STOCKDIARY";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateTimestamp("DATENEW", rs.getTimestamp("DATENEW"));
                        rs2.updateInt("REASON", rs.getInt("REASON"));
                        rs2.updateString("LOCATION", rs.getString("LOCATION"));
                        rs2.updateString("PRODUCT", rs.getString("PRODUCT"));
                        rs2.updateString("ATTRIBUTESETINSTANCE_ID", rs.getString("ATTRIBUTESETINSTANCE_ID"));
                        rs2.updateDouble("UNITS", rs.getDouble("UNITS"));
                        rs2.updateDouble("PRICE", rs.getDouble("PRICE"));
                        rs2.updateString("APPUSER", rs.getString("APPUSER"));
                        rs2.insertRow();
                    }

// copy STOCKLEVEL table  
                    pb.setString("Migrating Stocklevel table");
                    SQL = "SELECT * FROM STOCKLEVEL";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("LOCATION", rs.getString("LOCATION"));
                        rs2.updateString("PRODUCT", rs.getString("PRODUCT"));
                        rs2.updateDouble("STOCKSECURITY", rs.getDouble("STOCKSECURITY"));
                        rs2.updateDouble("STOCKMAXIMUM", rs.getDouble("STOCKMAXIMUM"));
                        rs2.insertRow();
                    }

// copy TAXCATEGORIES table   
                    pb.setString("Migrating Taxcategories table");
                    SQL = "SELECT * FROM TAXCATEGORIES";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.insertRow();
                    }

// copy TAXCUSTCATEGORIES table 
                    pb.setString("Migrating Taxcustcategories table");
                    SQL = "SELECT * FROM TAXCUSTCATEGORIES";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.insertRow();
                    }

// copy TAXES table    
                    pb.setString("Migrating Taxes table");
                    SQL = "SELECT * FROM TAXES";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateString("CATEGORY", rs.getString("CATEGORY"));
                        rs2.updateString("CUSTCATEGORY", rs.getString("CUSTCATEGORY"));
                        rs2.updateString("PARENTID", rs.getString("PARENTID"));
                        rs2.updateDouble("RATE", rs.getDouble("RATE"));
                        rs2.updateBoolean("RATECASCADE", rs.getBoolean("RATECASCADE"));
                        rs2.updateInt("RATEORDER", rs.getInt("RATEORDER"));
                        rs2.insertRow();
                    }

// copy TAXLINES table     
                    pb.setString("Migrating Taxlines table");
                    SQL = "SELECT * FROM TAXLINES";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("RECEIPT", rs.getString("RECEIPT"));
                        rs2.updateString("TAXID", rs.getString("TAXID"));
                        rs2.updateDouble("BASE", rs.getDouble("BASE"));
                        rs2.updateDouble("AMOUNT", rs.getDouble("AMOUNT"));
                        rs2.insertRow();
                    }

// copy THIRDPARTIES table 
                    pb.setString("Migrating Thirdparties table");
                    SQL = "SELECT * FROM THIRDPARTIES";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("CIF", rs.getString("CIF"));
                        rs2.updateString("NAME", rs.getString("NAME"));
                        rs2.updateString("ADDRESS", rs.getString("ADDRESS"));
                        rs2.updateString("CONTACTCOMM", rs.getString("CONTACTCOMM"));
                        rs2.updateString("CONTACTFACT", rs.getString("CONTACTFACT"));
                        rs2.updateString("PAYRULE", rs.getString("PAYRULE"));
                        rs2.updateString("FAXNUMBER", rs.getString("FAXNUMBER"));
                        rs2.updateString("PHONENUMBER", rs.getString("PHONENUMBER"));
                        rs2.updateString("MOBILENUMBER", rs.getString("MOBILENUMBER"));
                        rs2.updateString("EMAIL", rs.getString("EMAIL"));
                        rs2.updateString("WEBPAGE", rs.getString("WEBPAGE"));
                        rs2.updateString("NOTES", rs.getString("NOTES"));
                        rs2.insertRow();
                    }

// copy TICKETLINES table    
                    pb.setString("Migrating Ticketlines table");
                    SQL = "SELECT * FROM TICKETLINES";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("TICKET", rs.getString("TICKET"));
                        rs2.updateInt("LINE", rs.getInt("LINE"));
                        rs2.updateString("PRODUCT", rs.getString("PRODUCT"));
                        rs2.updateString("ATTRIBUTESETINSTANCE_ID", rs.getString("ATTRIBUTESETINSTANCE_ID"));
                        rs2.updateDouble("UNITS", rs.getDouble("UNITS"));
                        rs2.updateDouble("PRICE", rs.getDouble("PRICE"));
                        rs2.updateString("TAXID", rs.getString("TAXID"));
                        rs2.updateBytes("ATTRIBUTES", rs.getBytes("ATTRIBUTES"));
                        rs2.updateDouble("REFUNDQTY", rs.getDouble("REFUNDQTY"));
                        rs2.insertRow();
                    }

// copy TICKETS table   
                    pb.setString("Migrating Tickets table");
                    SQL = "SELECT * FROM TICKETS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateInt("TICKETTYPE", rs.getInt("TICKETTYPE"));
                        rs2.updateInt("TICKETID", rs.getInt("TICKETID"));
                        rs2.updateString("PERSON", rs.getString("PERSON"));
                        rs2.updateString("CUSTOMER", rs.getString("CUSTOMER"));
                        rs2.updateInt("STATUS", rs.getInt("STATUS"));
                        rs2.insertRow();
                    }

// copy TICKETS table      
                    pb.setString("Migrating Vouchers table");
                    SQL = "SELECT * FROM VOUCHERS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("VOUCHER", rs.getString("VOUCHER"));
                        rs2.updateTimestamp("SOLDDATE", rs.getTimestamp("SOLDDATE"));
                        rs2.updateTimestamp("REDEEMDATE", rs.getTimestamp("REDDEMDATE"));
                        rs2.updateString("SOLDTICKETID", rs.getString("SOLDTICKETID"));
                        rs2.updateString("REDEEMTICKETID", rs.getString("REDEEMTICKETID"));
                        rs2.insertRow();
                    }

// copy STOCKCURRENT table   
                    pb.setString("Migrating Stockcurrent table");
                    /*
                    SQL = "SELECT * FROM STOCKCURRENT";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("LOCATION", rs.getString("LOCATION"));
                        rs2.updateString("PRODUCT", rs.getString("PRODUCT"));
                        rs2.updateString("ATTRIBUTESETINSTANCE_ID", rs.getString("ATTRIBUTESETINSTANCE_ID"));
                        rs2.updateDouble("UNITS", rs.getDouble("UNITS"));
                        rs2.insertRow();
                    }*/
                    SQL = "SELECT * FROM STOCKCURRENT";
                    rs = stmt.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO STOCKCURRENT(LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS ) VALUES (?, ?, ?, ?)";
                        pstmt = con2.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("LOCATION"));
                        pstmt.setString(2, rs.getString("PRODUCT"));
                        pstmt.setString(3, rs.getString("ATTRIBUTESETINSTANCE_ID"));
                        pstmt.setDouble(4, rs.getDouble("UNITS"));
                        pstmt.executeUpdate();
                    }

// copy MENUENTRIES TABLE   
                    pb.setString("Migrating MenuEntries table");
                    SQL = "SELECT * FROM MENUENTRIES";
                    rs = stmt.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO MENUENTRIES (ENTRY, FOLLOWS, GRAPHIC, TITLE) VALUES (?, ?, ?, ?)";
                        pstmt = con2.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ENTRY"));
                        pstmt.setString(2, rs.getString("FOLLOWS"));
                        pstmt.setString(3, rs.getString("GRAPHIC"));
                        pstmt.setString(4, rs.getString("TITLE"));
                        pstmt.executeUpdate();
                    }
                    /*
                    SQL = "SELECT * FROM MENUENTRIES";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ADDRESS", rs.getString("ADDRESS"));
                        rs2.updateString("FOLLOWS", rs.getString("FOLLOWS"));
                        rs2.updateString("GRAPHIC", rs.getString("GRAPHIC"));
                        rs2.updateString("TITLE", rs.getString("TITLE"));
                        rs2.insertRow();
                    }*/

// copy DATABASECHANGELOG table
                    pb.setString("Migrating DatabaseChangeLog table");
                    PreparedStatement pstmt2 = con2.prepareStatement("DELETE FROM DATABASECHANGELOG");
                    pstmt2.executeUpdate();
                    /*
                    SQL = "SELECT * FROM DATABASECHANGELOG";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("ID", rs.getString("ID"));
                        rs2.updateString("AUTHOR", rs.getString("AUTHOR"));
                        rs2.updateString("FILENAME", rs.getString("FILENAME"));
                        rs2.updateTimestamp("DATEEXECUTED", rs.getTimestamp("DATEEXECUTED"));
                        rs2.updateInt("ORDEREXECUTED", rs.getInt("ORDEREXECUTED"));
                        rs2.updateString("EXECTYPE", rs.getString("EXECTYPE"));
                        rs2.updateString("MD5SUM", rs.getString("MD5SUM"));
                        rs2.updateString("DESCRIPTION", rs.getString("DESCRIPTION"));
                        rs2.updateString("COMMENTS", rs.getString("COMMENTS"));
                        rs2.updateString("TAG", rs.getString("TAG"));
                        rs2.updateString("LIQUIBASE", rs.getString("LIQUIBASE"));
                        rs2.insertRow();
                    }  */
                    
// copy DATABASECHANGELOG table
                    SQL = "SELECT * FROM DATABASECHANGELOG";
                    rs = stmt.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, EXECTYPE, MD5SUM, DESCRIPTION, COMMENTS, TAG, LIQUIBASE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
                        pstmt = con2.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("AUTHOR"));
                        pstmt.setString(3, rs.getString("FILENAME"));
                        pstmt.setTimestamp(4, rs.getTimestamp("DATEEXECUTED"));
                        pstmt.setInt(5, rs.getInt("ORDEREXECUTED"));
                        pstmt.setString(6, rs.getString("EXECTYPE"));
                        pstmt.setString(7, rs.getString("MD5SUM"));
                        pstmt.setString(8, rs.getString("DESCRIPTION"));
                        pstmt.setString(9, rs.getString("COMMENTS"));
                        pstmt.setString(10, rs.getString("TAG"));
                        pstmt.setString(11, rs.getString("LIQUIBASE"));
                        pstmt.executeUpdate();
                    }
                    
                    
// copy MOORERS TABLE    
                    pb.setString("Migrating Moorers table");
                    SQL = "SELECT * FROM MOORERS";
                    rs = stmt.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO MOORERS (VESSELNAME, SIZE, DAYS, POWER) VALUES (?, ?, ?, ?)";
                        pstmt = con2.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("VESSELNAME"));
                        pstmt.setInt(2, rs.getInt("SIZE"));
                        pstmt.setInt(3, rs.getInt("DAYS"));
                        pstmt.setBoolean(4, rs.getBoolean("POWER"));
                        pstmt.executeUpdate();
                    }
                    /* SQL = "SELECT * FROM MOORERS";
                    rs = stmt.executeQuery(SQL);
                    rs2 = stmt2.executeQuery(SQL);
                    rs2.moveToInsertRow();
                    while (rs.next()) {
                        rs2.updateString("VESSELNAME", rs.getString("VESSELNAME"));
                        rs2.updateInt("SIZE", rs.getInt("SIZE"));
                        rs2.updateInt("DAYS", rs.getInt("DAYS"));
                        rs2.updateBoolean("POWER", rs.getBoolean("POWER"));
                        rs2.insertRow();
                    }*/
                    
// GET THE SEQUENCE NUMBERS
                    pb.setString("Migrating sequences tables ");
                    if (("Apache Derby".equals(sdbmanager)) || ("MySQL".equals(sdbmanager))) {
                        SQL = "SELECT * FROM TICKETSNUM";
                        rs = stmt.executeQuery(SQL);
                        while (rs.next()) {
                            ticketsnum = rs.getString("ID");
                        }
                        SQL = "SELECT * FROM TICKETSNUM_PAYMENT";
                        rs = stmt.executeQuery(SQL);
                        while (rs.next()) {
                            ticketsnumPayment = rs.getString("ID");
                        }
                        SQL = "SELECT * FROM TICKETSNUM_REFUND";
                        rs = stmt.executeQuery(SQL);
                        while (rs.next()) {
                            ticketsnumRefund = rs.getString("ID");
                        }
                        SQL = "SELECT * FROM TICKETSNUM_INVOICE";
                        rs = stmt.executeQuery(SQL);
                        while (rs.next()) {
                            ticketsnumInvoice = rs.getString("ID");
                        }
                    } else {
                        SQL = "SELECT * FROM TICKETSNUM";
                        rs = stmt.executeQuery(SQL);
                        while (rs.next()) {
                            ticketsnum = rs.getString("LAST_VALUE");
                        }
                        SQL = "SELECT * FROM TICKETSNUM_PAYMENT";
                        rs = stmt.executeQuery(SQL);
                        while (rs.next()) {
                            ticketsnumPayment = rs.getString("LAST_VALUE");
                        }
                        SQL = "SELECT * FROM TICKETSNUM_REFUND";
                        rs = stmt.executeQuery(SQL);
                        while (rs.next()) {
                            ticketsnumRefund = rs.getString("LAST_VALUE");
                        }
                        SQL = "SELECT * FROM TICKETSNUM_INVOICE";
                        rs = stmt.executeQuery(SQL);
                        while (rs.next()) {
                            ticketsnumInvoice = rs.getString("LAST_VALUE");
                        }
                    }

// WRITE SEQUENCE NUMBER
                    if (("MySQL".equals(sdbmanager2))) {
                        SQL = "UPDATE TICKETSNUM SET ID=" + ticketsnum;
                        stmt2.executeUpdate(SQL);
                        SQL = "UPDATE TICKETSNUM_PAYMENT SET ID=" + ticketsnumPayment;
                        stmt2.executeUpdate(SQL);
                        SQL = "UPDATE TICKETSNUM_REFUND SET ID=" + ticketsnumRefund;
                        stmt2.executeUpdate(SQL);
                        SQL = "UPDATE TICKETSNUM_INVOICE SET ID=" + ticketsnumInvoice;
                        stmt2.executeUpdate(SQL);
                    } else if (("Apache Derby".equals(sdbmanager))) {
                        SQL = "CREATE TABLE TICKETSNUM (ID INTEGER GENERATED ALWAYS AS IDENTITY (START WITH " + ticketsnum + "))";
                        stmt2.executeUpdate(SQL);
                        SQL = "CREATE TABLE TICKETSNUM_PAYMENT (ID INTEGER GENERATED ALWAYS AS IDENTITY (START WITH " + ticketsnumPayment + "))";
                        stmt2.executeUpdate(SQL);
                        SQL = "CREATE TABLE TICKETSNUM_REFUND (ID INTEGER GENERATED ALWAYS AS IDENTITY (START WITH " + ticketsnumRefund + "))";
                        stmt2.executeUpdate(SQL);
                        SQL = "CREATE TABLE TICKETSNUM_INVOICE (ID INTEGER GENERATED ALWAYS AS IDENTITY (START WITH " + ticketsnumInvoice + "))";
                        stmt2.executeUpdate(SQL);
                    } else {
                        SQL = "ALTER SEQUENCE TICKETSNUM RESTART WITH " + ticketsnum;
                        stmt2.executeUpdate(SQL);
                        SQL = "ALTER SEQUENCE TICKETSNUM_PAYMENT RESTART WITH " + ticketsnumPayment;
                        stmt2.executeUpdate(SQL);
                        SQL = "ALTER SEQUENCE TICKETSNUM_REFUND RESTART WITH " + ticketsnumRefund;
                        stmt2.executeUpdate(SQL);
                        SQL = "ALTER SEQUENCE TICKETSNUM_INVOICE RESTART WITH " + ticketsnumInvoice;
                        stmt2.executeUpdate(SQL);
                    }

// Add ALL keys and indices back
                    pb.setString("Creating Indexes and Foreign Keys ");
                    addFKeys();

                    pb.setIndeterminate(false);
                    pb.setStringPainted(false);
                    pb.setString("");

// Write new database settings to properties file
                    if ("MySQL".equals(sdbmanager2)) {
                        AppConfig.getInstance().setProperty("db.engine", "MySQL");
                    } else {
                        AppConfig.getInstance().setProperty("db.engine", "PostgreSQL");
                    }
                    AppConfig.getInstance().setProperty("db.driverlib", jtxtDbDriverLib.getText());
                    AppConfig.getInstance().setProperty("db.driver", jtxtDbDriver.getText());
                    AppConfig.getInstance().setProperty("db.URL", jtxtDbURL.getText());
                    AppConfig.getInstance().setProperty("db.user", jtxtDbUser.getText());
                    AltEncrypter cypher = new AltEncrypter("cypherkey" + jtxtDbUser.getText());
                    AppConfig.getInstance().setProperty("db.password", "crypt:" + cypher.encrypt(new String(jtxtDbPassword.getPassword())));
                    dirty.setDirty(false);

                    for (PanelConfig c : m_panelconfig) {
                        c.saveProperties();
                    }

                    try {
                        AppConfig.getInstance().save();
                        JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.restartchanges"), AppLocal.getIntString("message.title"), JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException e) {
                        JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotsaveconfig"), e));
                    }

                    JOptionPane.showMessageDialog(this, "Migration complete.");
                    jbtnMigrate.setEnabled(false);

                } catch (SQLException | HeadlessException e) {
                    JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, SQL, e));
                }
            } else {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame, AppLocal.getIntString("message.migratenotsupported"), AppLocal.getIntString("message.nigratemessage"), JOptionPane.WARNING_MESSAGE);

            }
        }
    }


    private void jbtnMigrateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnMigrateActionPerformed
        Thread workThread = new Thread() {
            public void run() {
                performAction();
            }
        };
        workThread.start();

        pb.setIndeterminate(true);
        pb.setStringPainted(true);
        pb.setString("Migrating Database ..... Processing ");


    }//GEN-LAST:event_jbtnMigrateActionPerformed

    private void jbtnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExitActionPerformed
        deactivate();
        System.exit(0);
    }//GEN-LAST:event_jbtnExitActionPerformed

    private void jtxtDbURLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtDbURLActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtDbURLActionPerformed

    private void jtxtDbDriverLibActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtDbDriverLibActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtDbDriverLibActionPerformed

    private void jNewdbTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNewdbTypeActionPerformed
        if ("MySQL".equals(jNewdbType.getSelectedItem())) {
            jtxtDbDriverLib.setText(System.getProperty("user.dir") + "/lib/mysql-connector-java-5.1.26-bin.jar");
            jtxtDbDriver.setText("com.mysql.jdbc.Driver");
            jtxtDbURL.setText("jdbc:mysql://localhost:3306/chromispos");
        } else if ("PostgreSQL".equals(jNewdbType.getSelectedItem())) {
            jtxtDbDriverLib.setText(System.getProperty("user.dir") + "/lib/postgresql-9.2-1003.jdbc4.jar");
            jtxtDbDriver.setText("org.postgresql.Driver");
            jtxtDbURL.setText("jdbc:postgresql://localhost:5432/chromispos");
        } else if ("Derby".equals(jNewdbType.getSelectedItem())) {
            jtxtDbDriverLib.setText(System.getProperty("user.dir") + "/lib/derby-10.10.2.0.jar");
            jtxtDbDriver.setText("org.apache.derby.jdbc.EmbeddedDriver");
            jtxtDbURL.setText("jdbc:derby:" + new File(new File(System.getProperty("user.home")), AppLocal.APP_ID + "-database").getAbsolutePath() + ";create=true");
            jtxtDbUser.setText("");
            jtxtDbPassword.setText("");
        }
    }//GEN-LAST:event_jNewdbTypeActionPerformed

    private void jtxtDbUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtDbUserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtDbUserActionPerformed

    private void jButtonTestjButtonTestConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestjButtonTestConnectionActionPerformed
        try {
            String driverlib = jtxtDbDriverLib.getText();
            String driver = jtxtDbDriver.getText();
            String url = jtxtDbURL.getText();
            String user = jtxtDbUser.getText();
            String password = new String(jtxtDbPassword.getPassword());

            ClassLoader cloader = new URLClassLoader(new URL[]{new File(driverlib).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(driver, true, cloader).newInstance()));

            Session session = new Session(url, user, password);
            Connection connection = session.getConnection();
            boolean isValid = (connection == null) ? false : connection.isValid(1000);

            if (isValid) {
                JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.databaseconnectsuccess"), "Connection Test", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, "Connection Error"));
            }
        } catch (InstantiationException | IllegalAccessException | MalformedURLException | ClassNotFoundException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.databasedrivererror"), e));
        } catch (SQLException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.databaseconnectionerror"), e));
        } catch (HeadlessException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, "Unknown exception", e));
        }
    }//GEN-LAST:event_jButtonTestjButtonTestConnectionActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonTest;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JComboBox jNewdbType;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton jbtnDbDriverLib;
    private javax.swing.JButton jbtnExit;
    private javax.swing.JButton jbtnMigrate;
    private javax.swing.JTextField jtxtDbDriver;
    private javax.swing.JTextField jtxtDbDriverLib;
    private javax.swing.JPasswordField jtxtDbPassword;
    private javax.swing.JTextField jtxtDbURL;
    private javax.swing.JTextField jtxtDbUser;
    private javax.swing.JProgressBar pb;
    // End of variables declaration//GEN-END:variables
}
