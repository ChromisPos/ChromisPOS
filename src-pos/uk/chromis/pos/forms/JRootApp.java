//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) 2015 
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

import uk.chromis.basic.BasicException;
import uk.chromis.beans.JFlowPanel;
import uk.chromis.beans.JPasswordDialog;
import uk.chromis.data.gui.JMessageDialog;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.loader.Session;
import uk.chromis.format.Formats;
import uk.chromis.pos.printer.DeviceTicket;
import uk.chromis.pos.printer.TicketParser;
import uk.chromis.pos.printer.TicketPrinterException;
import uk.chromis.pos.scale.DeviceScale;
import uk.chromis.pos.scanpal2.DeviceScanner;
import uk.chromis.pos.scanpal2.DeviceScannerFactory;
import uk.chromis.pos.util.AltEncrypter;
import java.awt.CardLayout;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
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
 * @author adrianromero
 */
public class JRootApp extends JPanel implements AppView {

    private AppProperties m_props;
    private Session session;
    private DataLogicSystem m_dlSystem;

    private Properties m_propsdb = null;
    private String m_sActiveCashIndex;
    private int m_iActiveCashSequence;
    private Date m_dActiveCashDateStart;
    private Date m_dActiveCashDateEnd;

    private String m_sInventoryLocation;

    private StringBuilder inputtext;

    private DeviceScale m_Scale;
    private DeviceScanner m_Scanner;
    private DeviceTicket m_TP;
    private TicketParser m_TTP;

    private Map<String, BeanFactory> m_aBeanFactories;

    private JPrincipalApp m_principalapp = null;

    private static HashMap<String, String> m_oldclasses; // This is for backwards compatibility purposes

    private String m_clock;
    private String m_date;

    private Connection con;
    private ResultSet rs;
    private Statement stmt;
    private String SQL;
    private String sJLVersion;
    private DatabaseMetaData md;
    private SimpleDateFormat formatter;
    private MessageInf msg;

    static {
        initOldClasses();
    }

    private class PrintTimeAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            m_clock = getLineTimer();
            m_date = getLineDate();
            m_jLblTitle.setText(m_dlSystem.getResourceAsText("Window.Title"));
            m_jLblTitle.repaint();
            jLabel2.setText("  " + m_date + "  " + m_clock);
        }
    }

    private String getLineTimer() {
        try {
            if ((AppConfig2.getInstance().getProperty("clock.time") == "") || (AppConfig2.getInstance().getProperty("clock.time") == null)) {
                return Formats.HOURMIN.formatValue(new Date());
            } else {
                formatter = new SimpleDateFormat(AppConfig2.getInstance().getProperty("clock.time"));
                return formatter.format(new Date());
            }
        } catch (IllegalArgumentException e) {
            return Formats.HOURMIN.formatValue(new Date());
        }
    }

    private String getLineDate() {
        try {
            if ((AppConfig2.getInstance().getProperty("clock.date") == "") || (AppConfig2.getInstance().getProperty("clock.date") == null)) {
                return Formats.SIMPLEDATE.formatValue(new Date());
            } else {
                formatter = new SimpleDateFormat(AppConfig2.getInstance().getProperty("clock.date"));
                return formatter.format(new Date());
            }
        } catch (IllegalArgumentException e) {
            return Formats.SIMPLEDATE.formatValue(new Date());
        }
    }

    /**
     * Creates new form JRootApp
     */
    public JRootApp() {

        m_aBeanFactories = new HashMap<>();

        // Inicializo los componentes visuales
        initComponents();
        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(30, 30));
    }

    /**
     *
     * @param props
     * @return
     */
    public boolean initApp(AppProperties props) {

        m_props = props;
        m_jPanelDown.setVisible(!(Boolean.valueOf(AppConfig2.getInstance().getProperty("till.hideinfo"))));

        // support for different component orientation languages.
        applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

// ******************************************************************************************************************************************
        // lets get ride of unicenta properties
        File file = new File(System.getProperty("user.home"), "unicentaopos.properties");
        if (file.exists()) {
            try {
                session = AppViewConnection.createSession(m_props);
            } catch (BasicException e) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, e.getMessage(), e));
                return false;
            }
            m_dlSystem = (DataLogicSystem) getBean("uk.chromis.pos.forms.DataLogicSystem");

            String sDBVersion = readDataBaseVersion();
            System.out.println(sDBVersion);
            try {
                con = session.getConnection();
                md = con.getMetaData();
                stmt = (Statement) con.createStatement();
                SQL = "SELECT * from APPJL";
                rs = stmt.executeQuery(SQL);
                if (rs.next()) {
                    sJLVersion = rs.getString("version");
                }
            } catch (Exception e) {
            }
            if (getDbVersion().equals("x")) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER,
                        AppLocal.getIntString("message.databasenotsupported", session.DB.getName())));
            } else {
                JOptionPane.showMessageDialog(null, "Converting your system to Chromis POS");
                String db_user = (AppConfig2.getInstance2().getProperty("db.user"));
                String db_url = (AppConfig2.getInstance2().getProperty("db.URL"));
                String db_password = (AppConfig2.getInstance2().getProperty("db.password"));
                if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
                    AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
                    db_password = cypher.decrypt(db_password.substring(6));
                }
                try {
                    ClassLoader cloader = new URLClassLoader(new URL[]{new File(AppConfig2.getInstance2().getProperty("db.driverlib")).toURI().toURL()});
                    DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig2.getInstance2().getProperty("db.driver"), true, cloader).newInstance()));
                    String changelog = "uk/chromis/pos/liquibase/jlupdatelog.xml";
                    Liquibase liquibase = null;
                    Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(DriverManager.getConnection(db_url, db_user, db_password)));
                    liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database);
                    liquibase.update("implement");
                } catch (DatabaseException ex) {
                    Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LiquibaseException ex) {
                    Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (con != null) {
                        try {
                            con.rollback();
                            con.close();
                        } catch (SQLException e) {
                        }
                    }
                }
            }

            boolean success = file.renameTo(new File(System.getProperty("user.home"), "chromispos.properties"));
            System.out.println("File renamed");
            System.exit(0);

        }

// ******************************************************************************************************************************************        
        // Database start
        try {
            session = AppViewConnection.createSession(m_props);
        } catch (BasicException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, e.getMessage(), e));
            return false;
        }

        m_dlSystem = (DataLogicSystem) getBean("uk.chromis.pos.forms.DataLogicSystem");

        String sDBVersion = readDataBaseVersion();

        if (!AppLocal.APP_VERSION.equals(sDBVersion)) {
            if (getDbVersion().equals("x")) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER,
                        AppLocal.getIntString("message.databasenotsupported", session.DB.getName())));
            } else {
                // Create or upgrade script exists.
                if (JOptionPane.showConfirmDialog(this, AppLocal.getIntString(sDBVersion == null ? "message.createdatabase" : "message.updatedatabase"), AppLocal.getIntString("message.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    String db_user = (AppConfig2.getInstance().getProperty("db.user"));
                    String db_url = (AppConfig2.getInstance().getProperty("db.URL"));
                    String db_password = (AppConfig2.getInstance().getProperty("db.password"));

                    if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
                        // the password is encrypted
                        AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
                        db_password = cypher.decrypt(db_password.substring(6));
                    }

                    try {
                        ClassLoader cloader = new URLClassLoader(new URL[]{new File(AppConfig2.getInstance().getProperty("db.driverlib")).toURI().toURL()});
                        DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig2.getInstance().getProperty("db.driver"), true, cloader).newInstance()));

                        String changelog = "uk/chromis/pos/liquibase/chromis.xml";
                        Liquibase liquibase = null;

                        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(DriverManager.getConnection(db_url, db_user, db_password)));
                        liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database);
                        liquibase.update("implement");

                    } catch (DatabaseException ex) {
                        Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LiquibaseException ex) {
                        MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, "Liquibase Error", ex.getCause().toString().replace("liquibase.exception.DatabaseException:", ""));
                        msg.show(this);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InstantiationException ex) {
                        Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
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
                }
            }
        }

// Clear the cash drawer table as required, by setting 
        try {
            if (getDbVersion() == "d") {
                SQL = "DELETE FROM DRAWEROPENED WHERE OPENDATE < {fn TIMESTAMPADD(SQL_TSI_DAY ,-" + AppConfig2.getInstance().getProperty("dbtable.retaindays") + ", CURRENT_TIMESTAMP)}";
            } else {
                SQL = "DELETE FROM DRAWEROPENED WHERE OPENDATE < (NOW() - INTERVAL '" + AppConfig2.getInstance().getProperty("dbtable.retaindays") + "' DAY)";
            }
            stmt.execute(SQL);
        } catch (Exception e) {
        }

        m_propsdb = m_dlSystem.getResourceAsProperties(AppConfig2.getInstance().getHost() + "/properties");
     
        try {
            String sActiveCashIndex = m_propsdb.getProperty("activecash");
            Object[] valcash = sActiveCashIndex == null
                    ? null
                    : m_dlSystem.findActiveCash(sActiveCashIndex);
            if (valcash == null || !AppConfig2.getInstance().getHost().equals(valcash[0])) {
                // no la encuentro o no es de mi host por tanto creo una...
                setActiveCash(UUID.randomUUID().toString(), m_dlSystem.getSequenceCash(AppConfig2.getInstance().getHost()) + 1, new Date(), null);

                // creamos la caja activa      
                m_dlSystem.execInsertCash(
                        new Object[]{getActiveCashIndex(), AppConfig2.getInstance().getHost(), getActiveCashSequence(), getActiveCashDateStart(), getActiveCashDateEnd()});
            } else {
                setActiveCash(sActiveCashIndex, (Integer) valcash[1], (Date) valcash[2], (Date) valcash[3]);
            }
        } catch (BasicException e) {
            // Casco. Sin caja no hay pos
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
            msg.show(this);
            session.close();
            return false;
        }

        // Leo la localizacion de la caja (Almacen).
        m_sInventoryLocation = m_propsdb.getProperty("location");
        if (m_sInventoryLocation == null) {
            m_sInventoryLocation = "0";
            m_propsdb.setProperty("location", m_sInventoryLocation);
            m_dlSystem.setResourceAsProperties(AppConfig2.getInstance().getHost() + "/properties", m_propsdb);
        }

        // Inicializo la impresora...
        m_TP = new DeviceTicket(this, m_props);

        // Inicializamos 
        m_TTP = new TicketParser(getDeviceTicket(), m_dlSystem);
        printerStart();

        // Inicializamos la bascula
        m_Scale = new DeviceScale(this, m_props);

        // Inicializamos la scanpal
        m_Scanner = DeviceScannerFactory.createInstance(m_props);

        /**
         * JG Added - Start timer for title bar clock
         */
        new javax.swing.Timer(250, new PrintTimeAction()).start();

        String sWareHouse;
        try {
            sWareHouse = m_dlSystem.findLocationName(m_sInventoryLocation);
        } catch (BasicException e) {
            sWareHouse = null; // no he encontrado el almacen principal
        }

        // Show Hostname, Warehouse and URL in taskbar
        String url;
        try {
            url = session.getURL();
        } catch (SQLException e) {
            url = "";
        }
        m_jHost.setText("<html>" + AppConfig2.getInstance().getHost() + " - " + sWareHouse + "<br>" + url);

        // display the new logo if set
        String newLogo = AppConfig2.getInstance().getProperty("start.logo");
        if (newLogo != null) {
            if ("".equals(newLogo)) {
                jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/chromis.png")));
            } else {
                jLabel1.setIcon(new javax.swing.ImageIcon(newLogo));
            }
        }

        // change text under logo
        String newText = AppConfig2.getInstance().getProperty("start.text");
        if (newText != null) {
            if (newText.equals("")) {
                jLabel1.setText("<html><center>Chromis POS - The New Face of Open Source POS<br>"
                        + "Copyright \u00A9 2015 Chromis <br>"
                        + "<br>"
                        + "http://www.chromis.co.uk/<br>"
                        + "<br>"
                        + " Chromis POS is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.<br>"
                        + "<br>"
                        + " Chromis POS is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.<br>"
                        + "<br>"
                        + "You should have received a copy of the GNU General Public License along with Chromis POS.  If not, see http://www.gnu.org/licenses/<br>"
                        + "</center>");
            } else {
                try {
                    String newTextCode = new Scanner(new File(newText), "UTF-8").useDelimiter("\\A").next();
                    jLabel1.setText(newTextCode);
                } catch (Exception e) {
                }

                jLabel1.setAlignmentX(0.5F);
                jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                jLabel1.setMaximumSize(new java.awt.Dimension(800, 1024));
                jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            }
        }

        showLogin();

        return true;
    }

    private String readDataBaseVersion() {
        try {
            return m_dlSystem.findVersion();
        } catch (BasicException ed) {
            return null;
        }
    }

    public String getDbVersion() {
        String sdbmanager = m_dlSystem.getDBVersion();
        if (("HSQL Database Engine".equals(sdbmanager)) | ("HSQLDB".equals(sdbmanager))) {
            return ("h");
        } else if ("MySQL".equals(sdbmanager)) {
            return ("m");
        } else if ("PostgreSQL".equals(sdbmanager)) {
            return ("p");
        } else if ("Oracle".equals(sdbmanager)) {
            return ("o");
        } else if ("Apache Derby".equals(sdbmanager)) {
            return ("d");
        } else if ("Derby".equals(sdbmanager)) {
            return ("d");
        } else {
            return ("x");
        }
    }

    /**
     *
     */
    public void tryToClose() {

        if (closeAppView()) {

            // success. continue with the shut down
            // apago el visor
            m_TP.getDeviceDisplay().clearVisor();
            // me desconecto de la base de datos.
            session.close();

            // Download Root form
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }

    // Interfaz de aplicacion
    /**
     *
     * @return
     */
    @Override
    public DeviceTicket getDeviceTicket() {
        return m_TP;
    }

    /**
     *
     * @return
     */
    @Override
    public DeviceScale getDeviceScale() {
        return m_Scale;
    }

    /**
     *
     * @return
     */
    @Override
    public DeviceScanner getDeviceScanner() {
        return m_Scanner;
    }

    /**
     *
     * @return
     */
    @Override
    public Session getSession() {
        return session;
    }

    /**
     *
     * @return
     */
    @Override
    public String getInventoryLocation() {
        return m_sInventoryLocation;
    }

    /**
     *
     * @return
     */
    @Override
    public String getActiveCashIndex() {
        return m_sActiveCashIndex;
    }

    /**
     *
     * @return
     */
    @Override
    public int getActiveCashSequence() {
        return m_iActiveCashSequence;
    }

    /**
     *
     * @return
     */
    @Override
    public Date getActiveCashDateStart() {
        return m_dActiveCashDateStart;
    }

    /**
     *
     * @return
     */
    @Override
    public Date getActiveCashDateEnd() {
        return m_dActiveCashDateEnd;
    }

    /**
     *
     * @param sIndex
     * @param iSeq
     * @param dStart
     * @param dEnd
     */
    @Override
    public void setActiveCash(String sIndex, int iSeq, Date dStart, Date dEnd) {
        m_sActiveCashIndex = sIndex;
        m_iActiveCashSequence = iSeq;
        m_dActiveCashDateStart = dStart;
        m_dActiveCashDateEnd = dEnd;

        m_propsdb.setProperty("activecash", m_sActiveCashIndex);
        m_dlSystem.setResourceAsProperties(AppConfig2.getInstance().getHost() + "/properties", m_propsdb);
    }

    /**
     *
     * @return
     */
    @Override
    public AppProperties getProperties() {
        return m_props;
    }

    /**
     *
     * @param beanfactory
     * @return
     * @throws BeanFactoryException
     */
    @Override
    public Object getBean(String beanfactory) throws BeanFactoryException {

        // For backwards compatibility
        beanfactory = mapNewClass(beanfactory);

        BeanFactory bf = m_aBeanFactories.get(beanfactory);
        if (bf == null) {

            // testing sripts
            if (beanfactory.startsWith("/")) {
                bf = new BeanFactoryScript(beanfactory);
            } else {
                // Class BeanFactory
                try {
                    Class bfclass = Class.forName(beanfactory);

                    if (BeanFactory.class.isAssignableFrom(bfclass)) {
                        bf = (BeanFactory) bfclass.newInstance();
                    } else {
                        // the old construction for beans...
                        Constructor constMyView = bfclass.getConstructor(new Class[]{AppView.class});
                        Object bean = constMyView.newInstance(new Object[]{this});

                        bf = new BeanFactoryObj(bean);
                    }

                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
                    // ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
                    throw new BeanFactoryException(e);
                }
            }

            // cache the factory
            m_aBeanFactories.put(beanfactory, bf);

            // Initialize if it is a BeanFactoryApp
            if (bf instanceof BeanFactoryApp) {
                ((BeanFactoryApp) bf).init(this);
            }
        }
        return bf.getBean();
    }

    private static String mapNewClass(String classname) {
        String newclass = m_oldclasses.get(classname);
        return newclass == null
                ? classname
                : newclass;
    }

    private static void initOldClasses() {
// JG 16 May 2013 use diamond inference
        m_oldclasses = new HashMap<>();

        // update bean names from 2.00 to 2.20    
        m_oldclasses.put("uk.chromis.pos.reports.JReportCustomers", "/uk/chromis/reports/customers.bs");
        m_oldclasses.put("uk.chromis.pos.reports.JReportCustomersB", "/uk/chromis/reports/customersb.bs");
        m_oldclasses.put("uk.chromis.pos.reports.JReportClosedPos", "/uk/chromis/reports/closedpos.bs");
        m_oldclasses.put("uk.chromis.pos.reports.JReportClosedProducts", "/uk/chromis/reports/closedproducts.bs");
        m_oldclasses.put("uk.chromis.pos.reports.JChartSales", "/uk/chromis/reports/chartsales.bs");
        m_oldclasses.put("uk.chromis.pos.reports.JReportInventory", "/uk/chromis/reports/inventory.bs");
        m_oldclasses.put("uk.chromis.pos.reports.JReportInventory2", "/uk/chromis/reports/inventoryb.bs");
        m_oldclasses.put("uk.chromis.pos.reports.JReportInventoryBroken", "/uk/chromis/reports/inventorybroken.bs");
        m_oldclasses.put("uk.chromis.pos.reports.JReportInventoryDiff", "/uk/chromis/reports/inventorydiff.bs");
        m_oldclasses.put("uk.chromis.pos.reports.JReportPeople", "/uk/chromis/reports/people.bs");
        m_oldclasses.put("uk.chromis.pos.reports.JReportTaxes", "/uk/chromis/reports/taxes.bs");
        m_oldclasses.put("uk.chromis.pos.reports.JReportUserSales", "/uk/chromis/reports/usersales.bs");
        m_oldclasses.put("uk.chromis.pos.reports.JReportProducts", "/uk/chromis/reports/products.bs");
        m_oldclasses.put("uk.chromis.pos.reports.JReportCatalog", "/uk/chromis/reports/productscatalog.bs");

        // update bean names from 2.10 to 2.20
        m_oldclasses.put("uk.chromis.pos.panels.JPanelTax", "uk.chromis.pos.inventory.TaxPanel");

    }

    /**
     *
     */
    @Override
    public void waitCursorBegin() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    /**
     *
     */
    @Override
    public void waitCursorEnd() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     *
     * @return
     */
    @Override
    public AppUserView getAppUserView() {
        return m_principalapp;
    }

    private void printerStart() {

        String sresource = m_dlSystem.getResourceAsXML("Printer.Start");
        if (sresource == null) {
            m_TP.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
        } else {
            try {
                m_TTP.printTicket(sresource);
            } catch (TicketPrinterException eTP) {
                m_TP.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
            }
        }
    }

    private void listPeople() {

        try {

            jScrollPane1.getViewport().setView(null);

            JFlowPanel jPeople = new JFlowPanel();
            jPeople.applyComponentOrientation(getComponentOrientation());

            java.util.List people = m_dlSystem.listPeopleVisible();

            for (int i = 0; i < people.size(); i++) {

                AppUser user = (AppUser) people.get(i);

                JButton btn = new JButton(new AppUserAction(user));
                btn.applyComponentOrientation(getComponentOrientation());
                btn.setFocusPainted(false);
                btn.setFocusable(false);
                btn.setRequestFocusEnabled(false);
                btn.setMaximumSize(new Dimension(110, 60));
                btn.setPreferredSize(new Dimension(110, 60));
                btn.setMinimumSize(new Dimension(110, 60));
                btn.setHorizontalAlignment(SwingConstants.CENTER);
                btn.setHorizontalTextPosition(AbstractButton.CENTER);
                btn.setVerticalTextPosition(AbstractButton.BOTTOM);

                jPeople.add(btn);
            }
            jScrollPane1.getViewport().setView(jPeople);

        } catch (BasicException ee) {
        }
    }

    // La accion del selector
    private class AppUserAction extends AbstractAction {

        private final AppUser m_actionuser;

        public AppUserAction(AppUser user) {
            m_actionuser = user;
            putValue(Action.SMALL_ICON, m_actionuser.getIcon());
            putValue(Action.NAME, m_actionuser.getName());
        }

        public AppUser getUser() {
            return m_actionuser;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            // String sPassword = m_actionuser.getPassword();
            if (m_actionuser.authenticate()) {
                // p'adentro directo, no tiene password        
                openAppView(m_actionuser);
            } else {
                // comprobemos la clave antes de entrar...
                String sPassword = JPasswordDialog.showEditPassword(JRootApp.this,
                        AppLocal.getIntString("Label.Password"),
                        m_actionuser.getName(),
                        m_actionuser.getIcon());
                if (sPassword != null) {
                    if (m_actionuser.authenticate(sPassword)) {
                        openAppView(m_actionuser);
                    } else {
                        MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.BadPassword"));
                        msg.show(JRootApp.this);
                    }
                }
            }
        }
    }

    private void showView(String view) {
        CardLayout cl = (CardLayout) (m_jPanelContainer.getLayout());
        cl.show(m_jPanelContainer, view);
    }

    private void openAppView(AppUser user) {

        if (closeAppView()) {

            m_principalapp = new JPrincipalApp(this, user);

            // The user status notificator
            jPanel3.add(m_principalapp.getNotificator());
            jPanel3.revalidate();

            // The main panel
            m_jPanelContainer.add(m_principalapp, "_" + m_principalapp.getUser().getId());
            showView("_" + m_principalapp.getUser().getId());

            m_principalapp.activate();
        }
    }

    /**
     *
     */
    public void exitToLogin() {
        closeAppView();
        showLogin();
    }

    /**
     *
     * @return
     */
    public boolean closeAppView() {

        if (m_principalapp == null) {
            return true;
        } else if (!m_principalapp.deactivate()) {
            return false;
        } else {
            // the status label
            jPanel3.remove(m_principalapp.getNotificator());
            jPanel3.revalidate();
            jPanel3.repaint();

            // remove the card
            m_jPanelContainer.remove(m_principalapp);
            m_principalapp = null;

            showLogin();

            return true;
        }
    }

    private void showLogin() {

        // Show Login
        listPeople();
        showView("login");

        // show welcome message
        printerStart();

        // keyboard listener activation
        inputtext = new StringBuilder();
        m_txtKeys.setText(null);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_txtKeys.requestFocus();
            }
        });
    }

    private void processKey(char c) {

        if ((c == '\n') || (c == '?')) {

            AppUser user = null;
            try {
                user = m_dlSystem.findPeopleByCard(inputtext.toString());
            } catch (BasicException e) {
            }

            if (user == null) {
                // user not found
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.nocard"));
                msg.show(this);
            } else {
                openAppView(user);
            }

            inputtext = new StringBuilder();

        } else {
            inputtext.append(c);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jPanelTitle = new javax.swing.JPanel();
        m_jLblTitle = new javax.swing.JLabel();
        poweredby = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        m_jPanelContainer = new javax.swing.JPanel();
        m_jPanelLogin = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 0));
        jPanel5 = new javax.swing.JPanel();
        m_jLogonName = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        m_txtKeys = new javax.swing.JTextField();
        m_jClose = new javax.swing.JButton();
        m_jPanelDown = new javax.swing.JPanel();
        panelTask = new javax.swing.JPanel();
        m_jHost = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();

        setEnabled(false);
        setPreferredSize(new java.awt.Dimension(1024, 768));
        setLayout(new java.awt.BorderLayout());

        m_jPanelTitle.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jPanelTitle.setLayout(new java.awt.BorderLayout());

        m_jLblTitle.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        m_jLblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jLblTitle.setText("Window.Title");
        m_jPanelTitle.add(m_jLblTitle, java.awt.BorderLayout.CENTER);

        poweredby.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        poweredby.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/poweredby.png"))); // NOI18N
        poweredby.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        poweredby.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        poweredby.setMaximumSize(new java.awt.Dimension(222, 34));
        poweredby.setPreferredSize(new java.awt.Dimension(180, 34));
        poweredby.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                poweredbyMouseClicked(evt);
            }
        });
        m_jPanelTitle.add(poweredby, java.awt.BorderLayout.LINE_END);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setPreferredSize(new java.awt.Dimension(180, 34));
        m_jPanelTitle.add(jLabel2, java.awt.BorderLayout.LINE_START);

        add(m_jPanelTitle, java.awt.BorderLayout.NORTH);

        m_jPanelContainer.setLayout(new java.awt.CardLayout());

        m_jPanelLogin.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/chromis.png"))); // NOI18N
        jLabel1.setText("<html><center>Chromis POS - The future of open source POS<br>" +
            "Copyright \u00A9 2009-2014 uniCenta <br>" +
            "http://www.chromis.co.uk<br>" +
            "<br>" +
            "Chromis POS is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.<br>" +
            "<br>" +
            "Chromis POS is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.<br>" +
            "<br>" +
            "You should have received a copy of the GNU General Public License along with uniCenta oPOS.  If not, see http://www.gnu.org/licenses/<br>" +
            "</center>");
        jLabel1.setAlignmentX(0.5F);
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setMaximumSize(new java.awt.Dimension(800, 1024));
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel4.add(jLabel1);
        jPanel4.add(filler2);

        m_jPanelLogin.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel5.setPreferredSize(new java.awt.Dimension(300, 400));

        m_jLogonName.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        m_jLogonName.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 100));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel8.setLayout(new java.awt.GridLayout(0, 1, 5, 5));
        jPanel2.add(jPanel8, java.awt.BorderLayout.NORTH);

        m_jLogonName.add(jPanel2, java.awt.BorderLayout.LINE_END);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        m_txtKeys.setPreferredSize(new java.awt.Dimension(0, 0));
        m_txtKeys.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_txtKeysKeyTyped(evt);
            }
        });

        m_jClose.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/exit.png"))); // NOI18N
        m_jClose.setText(AppLocal.getIntString("Button.Close")); // NOI18N
        m_jClose.setFocusPainted(false);
        m_jClose.setFocusable(false);
        m_jClose.setPreferredSize(new java.awt.Dimension(100, 50));
        m_jClose.setRequestFocusEnabled(false);
        m_jClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCloseActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(m_txtKeys, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(0, 0, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(m_jClose, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(m_txtKeys, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(m_jClose, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jScrollPane1))
                .add(104, 104, 104)
                .add(m_jLogonName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(15, 15, 15)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(m_jLogonName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(434, 434, 434))
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(jScrollPane1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        m_jPanelLogin.add(jPanel5, java.awt.BorderLayout.EAST);

        m_jPanelContainer.add(m_jPanelLogin, "login");

        add(m_jPanelContainer, java.awt.BorderLayout.CENTER);

        m_jPanelDown.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jPanelDown.setLayout(new java.awt.BorderLayout());

        m_jHost.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        m_jHost.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/display.png"))); // NOI18N
        m_jHost.setText("*Hostname");
        panelTask.add(m_jHost);

        m_jPanelDown.add(panelTask, java.awt.BorderLayout.LINE_START);
        m_jPanelDown.add(jPanel3, java.awt.BorderLayout.LINE_END);

        add(m_jPanelDown, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents


    private void m_jCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCloseActionPerformed

        tryToClose();

    }//GEN-LAST:event_m_jCloseActionPerformed

    private void m_txtKeysKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_txtKeysKeyTyped

        m_txtKeys.setText("0");

        processKey(evt.getKeyChar());

    }//GEN-LAST:event_m_txtKeysKeyTyped

    private void poweredbyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_poweredbyMouseClicked
        if (SwingUtilities.isRightMouseButton(evt)) {
            System.out.println("create system info box here");
        }
    }//GEN-LAST:event_poweredbyMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton m_jClose;
    private javax.swing.JLabel m_jHost;
    private javax.swing.JLabel m_jLblTitle;
    private javax.swing.JPanel m_jLogonName;
    private javax.swing.JPanel m_jPanelContainer;
    private javax.swing.JPanel m_jPanelDown;
    private javax.swing.JPanel m_jPanelLogin;
    private javax.swing.JPanel m_jPanelTitle;
    private javax.swing.JTextField m_txtKeys;
    private javax.swing.JPanel panelTask;
    private javax.swing.JLabel poweredby;
    // End of variables declaration//GEN-END:variables
}
