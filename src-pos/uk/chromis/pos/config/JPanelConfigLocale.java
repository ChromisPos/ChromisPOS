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
package uk.chromis.pos.config;

import java.awt.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;

/**
 *
 * @author adrianromero
 */
public class JPanelConfigLocale extends javax.swing.JPanel implements PanelConfig {

    private DirtyManager dirty = new DirtyManager();

    private final static String DEFAULT_VALUE = "(Default)";

    /**
     * Creates new form JPanelConfigLocale
     */
    public JPanelConfigLocale() {

        initComponents();

        jcboLocale.addActionListener(dirty);
        jcboInteger.addActionListener(dirty);
        jcboDouble.addActionListener(dirty);
        jcboCurrency.addActionListener(dirty);
        jcboPercent.addActionListener(dirty);
        jcboDate.addActionListener(dirty);
        jcboTime.addActionListener(dirty);
        jcboDatetime.addActionListener(dirty);

        List<Locale> availablelocales = new ArrayList<Locale>();
        availablelocales.addAll(Arrays.asList(Locale.getAvailableLocales())); // Available java locales
        addLocale(availablelocales, new Locale("eu", "ES", "")); // Basque
        addLocale(availablelocales, new Locale("gl", "ES", "")); // Gallegan

        Collections.sort(availablelocales, new LocaleComparator());

        jcboLocale.addItem(new LocaleInfo(null));
        for (Locale l : availablelocales) {
            jcboLocale.addItem(new LocaleInfo(l));
        }

        jcboInteger.addItem(DEFAULT_VALUE);
        jcboInteger.addItem("#0");
        jcboInteger.addItem("#,##0");

        jcboDouble.addItem(DEFAULT_VALUE);
        jcboDouble.addItem("#0.0");
        jcboDouble.addItem("#,##0.#");

        jcboCurrency.addItem(DEFAULT_VALUE);
        jcboCurrency.addItem("\u00A4 #0.00");
        jcboCurrency.addItem("'$' #,##0.00");

        jcboPercent.addItem(DEFAULT_VALUE);
        jcboPercent.addItem("#,##0.##%");

        String[] dateStrings = {"dd.MM.yy", "dd.MM.yyyy", "MM.dd.yy", "MM.dd.yyyy", "EEE, MMM d, yy", "EEE, MMM d, yyyy",
            "EEE, MMMM d, yy", "EEE, MMMM d, yyyy", "EEEE, MMMM d, yy", "EEEE, MMMM d, yyyy"};
        ComboBoxValModel dateListModel;

        dateListModel = new ComboBoxValModel(new ArrayList<>(Arrays.asList(dateStrings)));
        jcboDate.setModel(dateListModel);

        String[] timeStrings = {"h:mm", "h:mm:ss", "h:mm a", "h:mm:ss a", "H:mm", "H:mm:ss", "H:mm a", "H:mm:ss a"};
        ComboBoxValModel timeListModel;

        timeListModel = new ComboBoxValModel(new ArrayList<>(Arrays.asList(timeStrings)));
        jcboTime.setModel(timeListModel);

        String[] timeDateStrings = {"dd.MM.yy, H:mm", "dd.MM.yy, H:mm", "MM.dd.yy, H:mm", "MM.dd.yy, H:mm", 
            "dd.MM.yyyy, H:mm", "dd.MM.yyyy, H:mm", "MM.dd.yyyy, H:mm", "MM.dd.yyyy, H:mm",
             "EEE, MMMM d yyyy, H:mm", "EEEE, MMMM d yyyy, H:mm"
            };
        ComboBoxValModel timeDateListModel;

        timeDateListModel = new ComboBoxValModel(new ArrayList<>(Arrays.asList(timeDateStrings)));
        jcboDatetime.setModel(timeDateListModel);


    }

    private void addLocale(List<Locale> ll, Locale l) {
        if (!ll.contains(l)) {
            ll.add(l);
        }
    }

    class MyComboBoxModel extends AbstractListModel implements ComboBoxModel {

        String[] ComputerComps = {"Monitor", "Key Board", "Mouse", "Joy Stick", "Modem", "CD ROM",
            "RAM Chip", "Diskette"};

        String selection = null;

        public Object getElementAt(int index) {
            return ComputerComps[index];
        }

        public int getSize() {
            return ComputerComps.length;
        }

        public void setSelectedItem(Object anItem) {
            selection = (String) anItem; // to select and register an
        } // item from the pull-down list

        // Methods implemented from the interface ComboBoxModel
        public Object getSelectedItem() {
            return selection; // to add the selection to the combo box
        }
    }

    /**
     *
     * @return
     */
    public boolean hasChanged() {
        return dirty.isDirty();
    }

    /**
     *
     * @return
     */
    public Component getConfigComponent() {
        return this;
    }

    /**
     *
     * @param config
     */
    public void loadProperties() {

        String slang = AppConfig.getInstance().getProperty("user.language");
        String scountry = AppConfig.getInstance().getProperty("user.country");
        String svariant = AppConfig.getInstance().getProperty("user.variant");

        if (slang != null && !slang.equals("") && scountry != null && svariant != null) {
            Locale currentlocale = new Locale(slang, scountry, svariant);
            for (int i = 0; i < jcboLocale.getItemCount(); i++) {
                LocaleInfo l = (LocaleInfo) jcboLocale.getItemAt(i);
                if (currentlocale.equals(l.getLocale())) {
                    jcboLocale.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            jcboLocale.setSelectedIndex(0);
        }

        jcboInteger.setSelectedItem(writeWithDefault(AppConfig.getInstance().getProperty("format.integer")));
        jcboDouble.setSelectedItem(writeWithDefault(AppConfig.getInstance().getProperty("format.double")));
        jcboCurrency.setSelectedItem(writeWithDefault(AppConfig.getInstance().getProperty("format.currency")));
        jcboPercent.setSelectedItem(writeWithDefault(AppConfig.getInstance().getProperty("format.percent")));
        jcboDate.setSelectedItem(writeWithDefault(AppConfig.getInstance().getProperty("format.date")));
        jcboTime.setSelectedItem(writeWithDefault(AppConfig.getInstance().getProperty("format.time")));
        jcboDatetime.setSelectedItem(writeWithDefault(AppConfig.getInstance().getProperty("format.datetime")));

        dirty.setDirty(false);
    }

    /**
     *
     * @param config
     */
    public void saveProperties() {

        Locale l = ((LocaleInfo) jcboLocale.getSelectedItem()).getLocale();
        if (l == null) {
            AppConfig.getInstance().setProperty("user.language", "");
            AppConfig.getInstance().setProperty("user.country", "");
            AppConfig.getInstance().setProperty("user.variant", "");
        } else {
            AppConfig.getInstance().setProperty("user.language", l.getLanguage());
            AppConfig.getInstance().setProperty("user.country", l.getCountry());
            AppConfig.getInstance().setProperty("user.variant", l.getVariant());
        }

        AppConfig.getInstance().setProperty("format.integer", readWithDefault(jcboInteger.getSelectedItem()));
        AppConfig.getInstance().setProperty("format.double", readWithDefault(jcboDouble.getSelectedItem()));
        AppConfig.getInstance().setProperty("format.currency", readWithDefault(jcboCurrency.getSelectedItem()));
        AppConfig.getInstance().setProperty("format.percent", readWithDefault(jcboPercent.getSelectedItem()));
        AppConfig.getInstance().setProperty("format.date", readWithDefault(jcboDate.getSelectedItem()));
        AppConfig.getInstance().setProperty("format.time", readWithDefault(jcboTime.getSelectedItem()));
        AppConfig.getInstance().setProperty("format.datetime", readWithDefault(jcboDatetime.getSelectedItem()));

        dirty.setDirty(false);
    }

    private String readWithDefault(Object value) {
        if (DEFAULT_VALUE.equals(value)) {
            return "";
        } else {
            return value.toString();
        }
    }

    private Object writeWithDefault(String value) {
        if (value == null || value.equals("") || value.equals(DEFAULT_VALUE)) {
            return DEFAULT_VALUE;
        } else {
            return value.toString();
        }
    }

    private static class LocaleInfo {

        private Locale locale;

        public LocaleInfo(Locale locale) {
            this.locale = locale;
        }

        public Locale getLocale() {
            return locale;
        }

        @Override
        public String toString() {
            return locale == null
                    ? "(System default)"
                    : locale.getDisplayName();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jcboLocale = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jcboInteger = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jcboDouble = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jcboCurrency = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jcboPercent = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jcboDate = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jcboTime = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jcboDatetime = new javax.swing.JComboBox();
        jtxtDate = new javax.swing.JTextField();
        jtxtTime = new javax.swing.JTextField();
        jtxtDateTime = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(0, 0));
        setPreferredSize(new java.awt.Dimension(650, 450));

        jPanel1.setPreferredSize(new java.awt.Dimension(600, 400));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText(AppLocal.getIntString("label.locale")); // NOI18N

        jcboLocale.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("label.integer")); // NOI18N

        jcboInteger.setEditable(true);
        jcboInteger.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.double")); // NOI18N

        jcboDouble.setEditable(true);
        jcboDouble.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.currency")); // NOI18N

        jcboCurrency.setEditable(true);
        jcboCurrency.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("label.percent")); // NOI18N

        jcboPercent.setEditable(true);
        jcboPercent.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText(AppLocal.getIntString("label.date")); // NOI18N

        jcboDate.setEditable(true);
        jcboDate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboDate.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcboDateItemStateChanged(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText(AppLocal.getIntString("label.time")); // NOI18N

        jcboTime.setEditable(true);
        jcboTime.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboTime.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcboTimeItemStateChanged(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText(AppLocal.getIntString("label.datetime")); // NOI18N

        jcboDatetime.setEditable(true);
        jcboDatetime.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboDatetime.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcboDatetimeItemStateChanged(evt);
            }
        });

        jtxtDate.setEditable(false);
        jtxtDate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDate.setPreferredSize(new java.awt.Dimension(6, 25));

        jtxtTime.setEditable(false);
        jtxtTime.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtTime.setMinimumSize(new java.awt.Dimension(6, 25));
        jtxtTime.setPreferredSize(new java.awt.Dimension(6, 25));

        jtxtDateTime.setEditable(false);
        jtxtDateTime.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDateTime.setMinimumSize(new java.awt.Dimension(6, 25));
        jtxtDateTime.setPreferredSize(new java.awt.Dimension(59, 25));

        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setText("eg");
        jLabel9.setMinimumSize(new java.awt.Dimension(34, 25));
        jLabel9.setPreferredSize(new java.awt.Dimension(34, 25));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText("eg");
        jLabel10.setMinimumSize(new java.awt.Dimension(34, 25));
        jLabel10.setPreferredSize(new java.awt.Dimension(34, 25));

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("eg");
        jLabel11.setMinimumSize(new java.awt.Dimension(34, 25));
        jLabel11.setPreferredSize(new java.awt.Dimension(34, 25));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboTime, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcboLocale, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcboInteger, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcboDouble, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcboPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboDate, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDateTime, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcboLocale, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboInteger, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboDouble, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboTime, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(147, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jcboDateItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcboDateItemStateChanged
        Formats.setDatePattern(jcboDate.getSelectedItem().toString());
        jtxtDate.setText(Formats.DATE.formatValue(new Date()));
    }//GEN-LAST:event_jcboDateItemStateChanged

    private void jcboTimeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcboTimeItemStateChanged
        Formats.setTimePattern(jcboTime.getSelectedItem().toString());
        jtxtTime.setText(Formats.TIME.formatValue(new Date()));
    }//GEN-LAST:event_jcboTimeItemStateChanged

    private void jcboDatetimeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcboDatetimeItemStateChanged
        Formats.setDateTimePattern(jcboDatetime.getSelectedItem().toString());       
        jtxtDateTime.setText(Formats.TIMESTAMP.formatValue(new Date()));
    }//GEN-LAST:event_jcboDatetimeItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox jcboCurrency;
    private javax.swing.JComboBox jcboDate;
    private javax.swing.JComboBox jcboDatetime;
    private javax.swing.JComboBox jcboDouble;
    private javax.swing.JComboBox jcboInteger;
    private javax.swing.JComboBox jcboLocale;
    private javax.swing.JComboBox jcboPercent;
    private javax.swing.JComboBox jcboTime;
    private javax.swing.JTextField jtxtDate;
    private javax.swing.JTextField jtxtDateTime;
    private javax.swing.JTextField jtxtTime;
    // End of variables declaration//GEN-END:variables

}
