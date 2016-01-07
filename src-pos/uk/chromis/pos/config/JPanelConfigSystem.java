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
//    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>.
package uk.chromis.pos.config;

import java.awt.Component;
import javax.swing.SpinnerNumberModel;
import uk.chromis.data.user.DirtyManager;
import eu.hansolo.custom.*;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.util.AutoLogoff;

/**
 *
 *
 */
public class JPanelConfigSystem extends javax.swing.JPanel implements PanelConfig {

    private DirtyManager dirty = new DirtyManager();
    private String tableRetain;
    private String retain = "1";
    private Integer x;

    private SteelCheckBox jDisableDefaultProduct2;

    /**
     * Creates new form JPanelConfigDatabase
     */
    public JPanelConfigSystem() {

        initComponents();
        jAutoLogoffTime.setText("100");
        jMaxChange.setText("20");

        jAutoLogoffTime.getDocument().addDocumentListener(dirty);
        jAutoLogoffAfterKitchen.addActionListener(dirty);
        jAutoLogoffAfterPrint.addActionListener(dirty);
        jInactivityTimer.addActionListener(dirty);
        jAutologoffAfterSale.addActionListener(dirty);
        jEnableAutoLogoff.addActionListener(dirty);
        jAutoLogoffToTables.addActionListener(dirty);
        jMarineOpt.addActionListener(dirty);
        jchkTextOverlay.addActionListener(dirty);
        jchkShowCustomerDetails.addActionListener(dirty);
        jchkShowWaiterDetails.addActionListener(dirty);
        jCustomerColour.addActionListener(dirty);
        jWaiterColour.addActionListener(dirty);
        jTableNameColour.addActionListener(dirty);
        jMoveAMountBoxToTop.addActionListener(dirty);
        jCheckPrice00.addActionListener(dirty);
        jMoveAMountBoxToTop.addActionListener(dirty);
        jCloseCashbtn.addActionListener(dirty);
        jTableRetain.addChangeListener(dirty);
        jUpdatedbprice.addActionListener(dirty);
        jChangeSalesScreen.addActionListener(dirty);
        jConsolidate.addActionListener(dirty);
        jDisableDefaultProduct.addActionListener(dirty);
        jTaxIncluded.addActionListener(dirty);
        jCategoiesBynumber.addActionListener(dirty);
        jMaxChange.getDocument().addDocumentListener(dirty);
        jMaxChangeEnable.addActionListener(dirty);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasChanged() {
        return dirty.isDirty();
    }

    /**
     *
     * @return
     */
    @Override
    public Component getConfigComponent() {
        return this;
    }

    /**
     *
     * @param config
     */
    @Override
    public void loadProperties() {

//lets test for our settings       
        String timerCheck = (AppConfig.getInstance().getProperty("till.autologofftimerperiod"));
        if (timerCheck == null) {
            AppConfig.getInstance().setProperty("till.autologofftimerperiod", "100");
        }

        String changeCheck = (AppConfig.getInstance().getProperty("till.changelimit"));
        if (changeCheck == null) {
            AppConfig.getInstance().setProperty("till.changelimit", "20");
        }

        jEnableAutoLogoff.setSelected(AppConfig.getInstance().getBoolean("till.enableautologoff"));
        jInactivityTimer.setSelected(AppConfig.getInstance().getBoolean("till.autologoffinactivitytimer"));
        jAutoLogoffTime.setText(AppConfig.getInstance().getProperty("till.autologofftimerperiod"));
        jAutologoffAfterSale.setSelected(AppConfig.getInstance().getBoolean("till.autologoffaftersale"));
        jAutoLogoffToTables.setSelected(AppConfig.getInstance().getBoolean("till.autologofftotables"));
        jAutoLogoffAfterKitchen.setSelected(AppConfig.getInstance().getBoolean("till.autologoffafterkitchen"));
        jAutoLogoffAfterPrint.setSelected(AppConfig.getInstance().getBoolean("till.autologoffafterprint"));
        jMarineOpt.setSelected(AppConfig.getInstance().getBoolean("till.marineoption"));
        jchkShowCustomerDetails.setSelected(AppConfig.getInstance().getBoolean("table.showcustomerdetails"));
        jchkShowWaiterDetails.setSelected(AppConfig.getInstance().getBoolean("table.showwaiterdetails"));
        jchkTextOverlay.setSelected(AppConfig.getInstance().getBoolean("payments.textoverlay"));
        jMoveAMountBoxToTop.setSelected(AppConfig.getInstance().getBoolean("till.taxincluded"));
        jCheckPrice00.setSelected(AppConfig.getInstance().getBoolean("till.pricewith00"));
        jMoveAMountBoxToTop.setSelected(AppConfig.getInstance().getBoolean("till.amountattop"));
        jCloseCashbtn.setSelected(AppConfig.getInstance().getBoolean("screen.600800"));
        jTableButtons.setSelected(AppConfig.getInstance().getBoolean("table.transparentbuttons"));
        jUpdatedbprice.setSelected(AppConfig.getInstance().getBoolean("db.productupdate"));
        jChangeSalesScreen.setSelected(AppConfig.getInstance().getBoolean("sales.newscreen"));
        jConsolidate.setSelected(AppConfig.getInstance().getBoolean("display.consolidated"));
        jDisableDefaultProduct.setSelected(AppConfig.getInstance().getBoolean("product.hidedefaultproductedit"));
        jTaxIncluded.setSelected(AppConfig.getInstance().getBoolean("till.taxincluded"));
        jCategoiesBynumber.setSelected(AppConfig.getInstance().getBoolean("till.categoriesbynumberorder"));
        jMaxChange.setText(AppConfig.getInstance().getProperty("till.changelimit"));
        jMaxChangeEnable.setSelected(AppConfig.getInstance().getBoolean("till.enablechangelimit"));
        
        jMaxChangeEnableActionPerformed(null);

// hide some values until the code has been implmented        
        if (AppConfig.getInstance().getProperty("table.customercolour") == null) {
            jCustomerColour.setSelectedItem("blue");
        } else {
            jCustomerColour.setSelectedItem(AppConfig.getInstance().getProperty("table.customercolour"));
        }
        if (AppConfig.getInstance().getProperty("table.waitercolour") == null) {
            jWaiterColour.setSelectedItem("red");
        } else {
            jWaiterColour.setSelectedItem(AppConfig.getInstance().getProperty("table.waitercolour"));
        }
        if (AppConfig.getInstance().getProperty("table.tablecolour") == null) {
            jTableNameColour.setSelectedItem("black");
        } else {
            jTableNameColour.setSelectedItem((AppConfig.getInstance().getProperty("table.tablecolour")));
        }

        jAutoLogoffAfterPrint.setVisible(false);
        if (jEnableAutoLogoff.isSelected()) {
            jAutoLogoffToTables.setEnabled(true);
            jLabelInactiveTime.setEnabled(true);
            jLabelTimedMessage.setEnabled(true);
            jAutoLogoffTime.setEnabled(true);
            jAutoLogoffAfterKitchen.setEnabled(true);
            jAutoLogoffAfterPrint.setEnabled(true);
            jInactivityTimer.setEnabled(true);
            jAutologoffAfterSale.setEnabled(true);
        } else {
            jAutoLogoffToTables.setEnabled(false);
            jLabelInactiveTime.setEnabled(false);
            jLabelTimedMessage.setEnabled(false);
            jAutoLogoffTime.setEnabled(false);
            jAutoLogoffAfterKitchen.setEnabled(false);
            jAutoLogoffAfterPrint.setEnabled(false);
            jInactivityTimer.setEnabled(false);
            jAutologoffAfterSale.setEnabled(false);
        }
        tableRetain = (AppConfig.getInstance().getProperty("dbtable.retaindays"));
        if (tableRetain == null || "".equals(jTableRetain)) {
            jTableRetain.setModel(new SpinnerNumberModel(7, 7, 90, 1));
        } else {
            jTableRetain.setModel(new SpinnerNumberModel(Integer.parseInt(tableRetain), 7, 90, 1));
        }

        dirty.setDirty(false);
    }

    /**
     *
     * @param config
     */
    @Override
    public void saveProperties() {
        AppConfig.getInstance().setBoolean("till.enableautologoff", jEnableAutoLogoff.isSelected());
        AppConfig.getInstance().setBoolean("till.autologoffinactivitytimer", jInactivityTimer.isSelected());
        AppConfig.getInstance().setProperty("till.autologofftimerperiod", jAutoLogoffTime.getText());
        AppConfig.getInstance().setBoolean("till.autologoffaftersale", jAutologoffAfterSale.isSelected());
        AppConfig.getInstance().setBoolean("till.autologofftotables", jAutoLogoffToTables.isSelected());
        AppConfig.getInstance().setBoolean("till.autologoffafterkitchen", jAutoLogoffAfterKitchen.isSelected());
        AppConfig.getInstance().setBoolean("till.autologoffafterprint", jAutoLogoffAfterPrint.isSelected());
        AppConfig.getInstance().setBoolean("till.marineoption", jMarineOpt.isSelected());
        AppConfig.getInstance().setBoolean("table.showcustomerdetails", jchkShowCustomerDetails.isSelected());
        AppConfig.getInstance().setBoolean("table.showwaiterdetails", jchkShowWaiterDetails.isSelected());
        AppConfig.getInstance().setBoolean("payments.textoverlay", jchkTextOverlay.isSelected());
        AppConfig.getInstance().setProperty("table.customercolour", jCustomerColour.getSelectedItem().toString());
        AppConfig.getInstance().setProperty("table.waitercolour", jWaiterColour.getSelectedItem().toString());
        AppConfig.getInstance().setProperty("table.tablecolour", jTableNameColour.getSelectedItem().toString());
        AppConfig.getInstance().setBoolean("till.taxincluded", jMoveAMountBoxToTop.isSelected());
        AppConfig.getInstance().setBoolean("till.pricewith00", jCheckPrice00.isSelected());
        AppConfig.getInstance().setBoolean("till.amountattop", jMoveAMountBoxToTop.isSelected());
        AppConfig.getInstance().setBoolean("screen.600800", jCloseCashbtn.isSelected());
        AppConfig.getInstance().setBoolean("table.transparentbuttons", jTableButtons.isSelected());
        AppConfig.getInstance().setProperty("dbtable.retaindays", jTableRetain.getValue().toString());
        AppConfig.getInstance().setBoolean("db.productupdate", jUpdatedbprice.isSelected());
        AppConfig.getInstance().setBoolean("sales.newscreen", jChangeSalesScreen.isSelected());
        AppConfig.getInstance().setBoolean("display.consolidated", jConsolidate.isSelected());
        AppConfig.getInstance().setBoolean("product.hidedefaultproductedit", jDisableDefaultProduct.isSelected());
        AppConfig.getInstance().setBoolean("till.taxincluded", jTaxIncluded.isSelected());
        AppConfig.getInstance().setBoolean("till.categoriesbynumberorder", jCategoiesBynumber.isSelected());
        AppConfig.getInstance().setProperty("till.changelimit", jMaxChange.getText());        
        AppConfig.getInstance().setBoolean("till.enablechangelimit", jMaxChangeEnable.isSelected());

        dirty.setDirty(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jEnableAutoLogoff = new eu.hansolo.custom.SteelCheckBox();
        jPanel7 = new javax.swing.JPanel();
        jAutoLogoffToTables = new eu.hansolo.custom.SteelCheckBox();
        jAutologoffAfterSale = new eu.hansolo.custom.SteelCheckBox();
        jPanel8 = new javax.swing.JPanel();
        jAutoLogoffAfterKitchen = new eu.hansolo.custom.SteelCheckBox();
        jAutoLogoffAfterPrint = new eu.hansolo.custom.SteelCheckBox();
        jPanel9 = new javax.swing.JPanel();
        jAutoLogoffTime = new javax.swing.JTextField();
        jLabelTimedMessage = new javax.swing.JLabel();
        jInactivityTimer = new eu.hansolo.custom.SteelCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jLabelCustomerTextColour = new javax.swing.JLabel();
        jCustomerColour = new javax.swing.JComboBox();
        jLabelServerTextColour = new javax.swing.JLabel();
        jWaiterColour = new javax.swing.JComboBox();
        jLabelTableNameTextColour = new javax.swing.JLabel();
        jTableNameColour = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jchkShowCustomerDetails = new eu.hansolo.custom.SteelCheckBox();
        jchkShowWaiterDetails = new eu.hansolo.custom.SteelCheckBox();
        jTableButtons = new eu.hansolo.custom.SteelCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jTableRetain = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jMarineOpt = new eu.hansolo.custom.SteelCheckBox();
        jchkTextOverlay = new eu.hansolo.custom.SteelCheckBox();
        jCloseCashbtn = new eu.hansolo.custom.SteelCheckBox();
        jTaxIncluded = new eu.hansolo.custom.SteelCheckBox();
        jUpdatedbprice = new eu.hansolo.custom.SteelCheckBox();
        jConsolidate = new eu.hansolo.custom.SteelCheckBox();
        jDisableDefaultProduct = new eu.hansolo.custom.SteelCheckBox();
        jCheckPrice00 = new eu.hansolo.custom.SteelCheckBox();
        jChangeSalesScreen = new eu.hansolo.custom.SteelCheckBox();
        jMoveAMountBoxToTop = new eu.hansolo.custom.SteelCheckBox();
        jCategoiesBynumber = new eu.hansolo.custom.SteelCheckBox();
        jMaxChangeEnable = new eu.hansolo.custom.SteelCheckBox();
        jMaxChange = new javax.swing.JTextField();
        jLabelMaxChange = new javax.swing.JLabel();
        jLabelInactiveTime = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(700, 500));
        setPreferredSize(new java.awt.Dimension(700, 650));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("label.autologoffpanel"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanel2.setLayout(null);

        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jEnableAutoLogoff.setBorder(null);
        jEnableAutoLogoff.setText(bundle.getString("label.autologonoff")); // NOI18N
        jEnableAutoLogoff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEnableAutoLogoffActionPerformed(evt);
            }
        });
        jPanel6.add(jEnableAutoLogoff, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, -1));

        jPanel2.add(jPanel6);
        jPanel6.setBounds(10, 20, 540, 30);

        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jAutoLogoffToTables.setText(bundle.getString("label.autoloffrestaurant")); // NOI18N
        jPanel7.add(jAutoLogoffToTables, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 0, 260, -1));

        jAutologoffAfterSale.setText(bundle.getString("label.autologoff")); // NOI18N
        jPanel7.add(jAutologoffAfterSale, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, -1, -1));

        jPanel2.add(jPanel7);
        jPanel7.setBounds(10, 80, 580, 30);

        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jAutoLogoffAfterKitchen.setText(bundle.getString("label.logoffaftersendtokitchen")); // NOI18N
        jPanel8.add(jAutoLogoffAfterKitchen, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, 250, -1));

        jAutoLogoffAfterPrint.setText(bundle.getString("label.logoffafterprinting")); // NOI18N
        jAutoLogoffAfterPrint.setEnabled(false);
        jPanel8.add(jAutoLogoffAfterPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 0, 253, -1));

        jPanel2.add(jPanel8);
        jPanel8.setBounds(10, 110, 580, 30);

        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jAutoLogoffTime.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jAutoLogoffTime.setText("0");
        jAutoLogoffTime.setMaximumSize(new java.awt.Dimension(0, 25));
        jAutoLogoffTime.setMinimumSize(new java.awt.Dimension(0, 0));
        jAutoLogoffTime.setPreferredSize(new java.awt.Dimension(0, 25));
        jPanel9.add(jAutoLogoffTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 0, 50, 30));

        jLabelTimedMessage.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelTimedMessage.setText(bundle.getString("label.autologoffzero")); // NOI18N
        jLabelTimedMessage.setMaximumSize(new java.awt.Dimension(0, 25));
        jLabelTimedMessage.setMinimumSize(new java.awt.Dimension(0, 0));
        jLabelTimedMessage.setPreferredSize(new java.awt.Dimension(0, 25));
        jPanel9.add(jLabelTimedMessage, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 0, 190, 30));

        jInactivityTimer.setText(bundle.getString("label.inactivity")); // NOI18N
        jInactivityTimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jInactivityTimerActionPerformed(evt);
            }
        });
        jPanel9.add(jInactivityTimer, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, 230, -1));

        jPanel2.add(jPanel9);
        jPanel9.setBounds(10, 50, 580, 30);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("label.tabledisplayoptions"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanel3.setLayout(null);

        jLabelCustomerTextColour.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelCustomerTextColour.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCustomerTextColour.setText(bundle.getString("label.textcolourcustomer")); // NOI18N
        jLabelCustomerTextColour.setMaximumSize(new java.awt.Dimension(0, 25));
        jLabelCustomerTextColour.setMinimumSize(new java.awt.Dimension(0, 0));
        jLabelCustomerTextColour.setPreferredSize(new java.awt.Dimension(0, 25));
        jPanel3.add(jLabelCustomerTextColour);
        jLabelCustomerTextColour.setBounds(240, 20, 130, 25);

        jCustomerColour.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jCustomerColour.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "black", "blue", "grey", "green", "orange", "red", "white", "yellow" }));
        jCustomerColour.setMaximumSize(new java.awt.Dimension(0, 25));
        jCustomerColour.setMinimumSize(new java.awt.Dimension(0, 0));
        jCustomerColour.setPreferredSize(new java.awt.Dimension(0, 25));
        jCustomerColour.setSelectedItem("blue");
        jPanel3.add(jCustomerColour);
        jCustomerColour.setBounds(380, 20, 200, 30);

        jLabelServerTextColour.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelServerTextColour.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelServerTextColour.setText(bundle.getString("label.textcolourwaiter")); // NOI18N
        jLabelServerTextColour.setMaximumSize(new java.awt.Dimension(0, 25));
        jLabelServerTextColour.setMinimumSize(new java.awt.Dimension(0, 0));
        jLabelServerTextColour.setPreferredSize(new java.awt.Dimension(0, 25));
        jPanel3.add(jLabelServerTextColour);
        jLabelServerTextColour.setBounds(240, 60, 130, 25);

        jWaiterColour.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jWaiterColour.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "black", "blue", "grey", "green", "orange", "red", "white", "yellow" }));
        jWaiterColour.setMaximumSize(new java.awt.Dimension(0, 25));
        jWaiterColour.setMinimumSize(new java.awt.Dimension(0, 0));
        jWaiterColour.setPreferredSize(new java.awt.Dimension(0, 25));
        jPanel3.add(jWaiterColour);
        jWaiterColour.setBounds(380, 60, 200, 30);

        jLabelTableNameTextColour.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelTableNameTextColour.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTableNameTextColour.setText(bundle.getString("label.textclourtablename")); // NOI18N
        jLabelTableNameTextColour.setMaximumSize(new java.awt.Dimension(0, 25));
        jLabelTableNameTextColour.setMinimumSize(new java.awt.Dimension(0, 0));
        jLabelTableNameTextColour.setPreferredSize(new java.awt.Dimension(0, 25));
        jPanel3.add(jLabelTableNameTextColour);
        jLabelTableNameTextColour.setBounds(230, 100, 140, 30);

        jTableNameColour.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTableNameColour.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "black", "blue", "grey", "green", "orange", "red", "white", "yellow" }));
        jTableNameColour.setMaximumSize(new java.awt.Dimension(0, 25));
        jTableNameColour.setMinimumSize(new java.awt.Dimension(0, 0));
        jTableNameColour.setPreferredSize(new java.awt.Dimension(0, 25));
        jPanel3.add(jTableNameColour);
        jTableNameColour.setBounds(380, 100, 200, 30);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jchkShowCustomerDetails.setText(bundle.getString("label.tableshowcustomerdetails")); // NOI18N
        jPanel1.add(jchkShowCustomerDetails, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, -1));

        jchkShowWaiterDetails.setText(bundle.getString("label.tableshowwaiterdetails")); // NOI18N
        jPanel1.add(jchkShowWaiterDetails, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 220, -1));

        jTableButtons.setBorder(null);
        jTableButtons.setText(bundle.getString("label.tablebuttons")); // NOI18N
        jTableButtons.setUi(null);
        jTableButtons.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTableButtonsActionPerformed(evt);
            }
        });
        jPanel1.add(jTableButtons, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 220, -1));

        jPanel3.add(jPanel1);
        jPanel1.setBounds(10, 20, 220, 140);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("label.general"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTableRetain.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jTableRetain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTableRetainStateChanged(evt);
            }
        });
        jPanel4.add(jTableRetain, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 50, 40));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText(bundle.getString("label.cleardrawertable")); // NOI18N
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 170, 370, -1));

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jMarineOpt.setText(bundle.getString("label.marine")); // NOI18N
        jPanel5.add(jMarineOpt, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 190, -1));

        jchkTextOverlay.setText(bundle.getString("label.currencybutton")); // NOI18N
        jPanel5.add(jchkTextOverlay, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 190, -1));

        jCloseCashbtn.setBorder(null);
        jCloseCashbtn.setText(bundle.getString("message.systemclosecash")); // NOI18N
        jCloseCashbtn.setRequestFocusEnabled(false);
        jPanel5.add(jCloseCashbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 190, -1));

        jTaxIncluded.setBorder(null);
        jTaxIncluded.setText(bundle.getString("label.taxincluded")); // NOI18N
        jPanel5.add(jTaxIncluded, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 195, -1));

        jUpdatedbprice.setBorder(null);
        jUpdatedbprice.setText(bundle.getString("label.updatepricefromedit")); // NOI18N
        jPanel5.add(jUpdatedbprice, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 190, -1));

        jConsolidate.setText(bundle.getString("Label.ConsolidatedScreen")); // NOI18N
        jPanel5.add(jConsolidate, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, 170, -1));

        jDisableDefaultProduct.setText(bundle.getString("label.default")); // NOI18N
        jPanel5.add(jDisableDefaultProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, 160, -1));

        jCheckPrice00.setBorder(null);
        jCheckPrice00.setText(bundle.getString("label.pricewith00")); // NOI18N
        jCheckPrice00.setRequestFocusEnabled(false);
        jPanel5.add(jCheckPrice00, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, 195, -1));

        jChangeSalesScreen.setText(bundle.getString("Label.ChangesSalesScreen")); // NOI18N
        jPanel5.add(jChangeSalesScreen, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 60, 160, -1));

        jMoveAMountBoxToTop.setBorder(null);
        jMoveAMountBoxToTop.setText(bundle.getString("label.inputamount")); // NOI18N
        jPanel5.add(jMoveAMountBoxToTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 60, 199, -1));

        jCategoiesBynumber.setText(bundle.getString("label.categoryorder")); // NOI18N
        jPanel5.add(jCategoiesBynumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 90, 170, -1));

        jMaxChangeEnable.setText(bundle.getString("message.enablechange")); // NOI18N
        jMaxChangeEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMaxChangeEnableActionPerformed(evt);
            }
        });
        jPanel5.add(jMaxChangeEnable, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 90, 160, -1));

        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 20, 580, -1));

        jMaxChange.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMaxChange.setText("50.00");
        jPanel4.add(jMaxChange, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 160, 60, 30));

        jLabelMaxChange.setText(bundle.getString("label.maxchange")); // NOI18N
        jPanel4.add(jLabelMaxChange, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 160, 130, 30));

        jLabelInactiveTime.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelInactiveTime.setText(bundle.getString("label.autolofftime")); // NOI18N
        jLabelInactiveTime.setMaximumSize(new java.awt.Dimension(0, 25));
        jLabelInactiveTime.setMinimumSize(new java.awt.Dimension(0, 0));
        jLabelInactiveTime.setPreferredSize(new java.awt.Dimension(0, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabelInactiveTime, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 492, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabelInactiveTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jEnableAutoLogoffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEnableAutoLogoffActionPerformed
        if (jEnableAutoLogoff.isSelected()) {
            jAutoLogoffToTables.setEnabled(true);
            jLabelInactiveTime.setEnabled(true);
            jLabelTimedMessage.setEnabled(true);
            jAutoLogoffTime.setEnabled(true);
            jAutoLogoffAfterKitchen.setEnabled(true);
            jAutoLogoffAfterPrint.setEnabled(true);
            jInactivityTimer.setEnabled(true);
            jAutologoffAfterSale.setEnabled(true);
        } else {
            jAutoLogoffToTables.setEnabled(false);
            jLabelInactiveTime.setEnabled(false);
            jLabelTimedMessage.setEnabled(false);
            jAutoLogoffTime.setEnabled(false);
            jAutoLogoffAfterKitchen.setEnabled(false);
            jAutoLogoffAfterPrint.setEnabled(false);
            jInactivityTimer.setEnabled(false);
            jAutologoffAfterSale.setEnabled(false);
        }
    }//GEN-LAST:event_jEnableAutoLogoffActionPerformed

    private void jTableButtonsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTableButtonsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableButtonsActionPerformed

    private void jInactivityTimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jInactivityTimerActionPerformed
        Integer delay = 100;
        try {
            delay = Integer.valueOf(jAutoLogoffTime.getText());
            if (delay == 0) {
                jAutoLogoffTime.setText("100");
            }
        } catch (NumberFormatException e) {
            jAutoLogoffTime.setText(String.valueOf(delay));
        }
    }//GEN-LAST:event_jInactivityTimerActionPerformed

    private void jMaxChangeEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMaxChangeEnableActionPerformed
        if (jMaxChangeEnable.isSelected()) {
            jMaxChange.setVisible(true);
            jLabelMaxChange.setVisible(true);
        } else {
            jMaxChange.setVisible(false);
            jLabelMaxChange.setVisible(false);
        }
    }//GEN-LAST:event_jMaxChangeEnableActionPerformed

    private void jTableRetainStateChanged(javax.swing.event.ChangeEvent evt) {
        // TODO add your handling code here:
        retain = "";
        x = 1;
        while (x < (Integer) jTableRetain.getValue()) {
            retain = retain + "0";
            x++;
        }
        retain = retain + "1";

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private eu.hansolo.custom.SteelCheckBox jAutoLogoffAfterKitchen;
    private eu.hansolo.custom.SteelCheckBox jAutoLogoffAfterPrint;
    private javax.swing.JTextField jAutoLogoffTime;
    private eu.hansolo.custom.SteelCheckBox jAutoLogoffToTables;
    private eu.hansolo.custom.SteelCheckBox jAutologoffAfterSale;
    private eu.hansolo.custom.SteelCheckBox jCategoiesBynumber;
    private eu.hansolo.custom.SteelCheckBox jChangeSalesScreen;
    private eu.hansolo.custom.SteelCheckBox jCheckPrice00;
    private eu.hansolo.custom.SteelCheckBox jCloseCashbtn;
    private eu.hansolo.custom.SteelCheckBox jConsolidate;
    private javax.swing.JComboBox jCustomerColour;
    private eu.hansolo.custom.SteelCheckBox jDisableDefaultProduct;
    private eu.hansolo.custom.SteelCheckBox jEnableAutoLogoff;
    private eu.hansolo.custom.SteelCheckBox jInactivityTimer;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelCustomerTextColour;
    private javax.swing.JLabel jLabelInactiveTime;
    private javax.swing.JLabel jLabelMaxChange;
    private javax.swing.JLabel jLabelServerTextColour;
    private javax.swing.JLabel jLabelTableNameTextColour;
    private javax.swing.JLabel jLabelTimedMessage;
    private eu.hansolo.custom.SteelCheckBox jMarineOpt;
    private javax.swing.JTextField jMaxChange;
    private eu.hansolo.custom.SteelCheckBox jMaxChangeEnable;
    private eu.hansolo.custom.SteelCheckBox jMoveAMountBoxToTop;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private eu.hansolo.custom.SteelCheckBox jTableButtons;
    private javax.swing.JComboBox jTableNameColour;
    private javax.swing.JSpinner jTableRetain;
    private eu.hansolo.custom.SteelCheckBox jTaxIncluded;
    private eu.hansolo.custom.SteelCheckBox jUpdatedbprice;
    private javax.swing.JComboBox jWaiterColour;
    private eu.hansolo.custom.SteelCheckBox jchkShowCustomerDetails;
    private eu.hansolo.custom.SteelCheckBox jchkShowWaiterDetails;
    private eu.hansolo.custom.SteelCheckBox jchkTextOverlay;
    // End of variables declaration//GEN-END:variables

}
