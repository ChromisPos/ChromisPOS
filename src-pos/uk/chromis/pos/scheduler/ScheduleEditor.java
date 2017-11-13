/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.scheduler;

import java.awt.Component;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.commons.lang.StringUtils;
import uk.chromis.basic.BasicException;
import uk.chromis.beans.JCalendarDialog;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.LocalRes;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.format.Formats;
import uk.chromis.pos.admin.DataLogicAdmin;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSystem;

/**
 *
 * @author John
 */
public class ScheduleEditor extends javax.swing.JPanel 
    implements EditorRecord, ListSelectionListener  {

    private final DirtyManager m_Dirty;
    DataLogicScheduler m_dlScheduler;
    DataLogicAdmin m_dlAdmin;
    DataLogicSystem m_dlSystem;

    private final AppView m_App;
    private String m_ID;
    
    private final int m_IndexID;
    private final int m_IndexName;
    private final int m_IndexStart;
    private final int m_IndexInterval;
    private final int m_IndexScript;
    private final int m_IndexEnabled;
    
    private SentenceList m_SentenceResource;
    private ComboBoxValModel m_ModelResource;
    
    /** Creates new form
     * @param app
     * @param dirty */
    public ScheduleEditor(AppView app, DirtyManager dirty) {
        initComponents();
        
        m_dlScheduler = (DataLogicScheduler) app.getBean("uk.chromis.pos.scheduler.DataLogicScheduler");
        m_dlAdmin = (DataLogicAdmin) app.getBean("uk.chromis.pos.admin.DataLogicAdmin");
        m_dlSystem = (DataLogicSystem) app.getBean("uk.chromis.pos.forms.DataLogicSystem");
        
        m_App = app;
        m_Dirty = dirty;
        m_ID = null;
        
        m_IndexID = m_dlScheduler.getIndexOf("ID");
        m_IndexName = m_dlScheduler.getIndexOf("NAME");
        m_IndexStart = m_dlScheduler.getIndexOf("STARTDATE");
        m_IndexInterval = m_dlScheduler.getIndexOf("TIMEINTERVAL");
        m_IndexScript = m_dlScheduler.getIndexOf("SCRIPT");
        m_IndexEnabled = m_dlScheduler.getIndexOf("ISENABLED");       
        
        m_ModelResource = new ComboBoxValModel();
        jComboBoxResources.setModel(m_ModelResource);

        
        m_jName.getDocument().addDocumentListener(m_Dirty);
        m_jTextScript.getDocument().addDocumentListener(m_Dirty);
        jCheckBoxEnabled.addActionListener(m_Dirty);
        jTextInterval.getDocument().addDocumentListener(m_Dirty);
        jTxtStartDate.getDocument().addDocumentListener(m_Dirty);
    }

     /**
     *
     * @throws BasicException
     */
    public void activate() throws BasicException {
        
        m_SentenceResource = m_dlScheduler.getResourceScriptListSentence();
        m_ModelResource = new ComboBoxValModel( m_SentenceResource.list());
        jComboBoxResources.setModel(m_ModelResource);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting() == false) {
            if( m_ID != null ) {
                m_Dirty.setDirty(true);
            }
        }
    }
    
    /**
     *
     */
    @Override
    public void refresh() {
    }
    
    private void enableAll( boolean b ) {
        m_jName.setEnabled(b);
        jCheckBoxEnabled.setEnabled(b);
        m_jTextScript.setEnabled(b);
        jComboBoxResources.setEnabled(b);
        jTextInterval.setEnabled(b);
        jTxtStartDate.setEnabled(b);
        btnDateStart.setEnabled(b);
        jButtonRunNow.setEnabled(b);
    }

    /**
     *
     */
    @Override
    public void writeValueEOF() {
        m_ID = null;
        
        m_jName.setText(null);
        m_jTextScript.setText(null);
        jCheckBoxEnabled.setSelected(false);
        m_ModelResource.setSelectedKey(null);
        jTextInterval.setText(null);
        jTxtStartDate.setText(null);
        enableAll( false );
        jButtonScript.setEnabled(false);
        jButtonRunNow.setEnabled(false);
    }
    
    /**
     *
     */
    @Override
    public void writeValueInsert() {
        m_ID = UUID.randomUUID().toString();
        
        m_jName.setText(null);
        m_jTextScript.setText(null);
        jCheckBoxEnabled.setSelected(true);
        m_ModelResource.setSelectedKey(null);
        jTextInterval.setText("24");
        jTxtStartDate.setText(Formats.TIMESTAMP.formatValue( new Date() ));

        enableAll( true );
        jButtonScript.setEnabled(false);
        jButtonRunNow.setEnabled(false);
    }

    private void valuesToControls( Object[] attrset ) {
        
        m_ModelResource.setSelectedKey(null);

        m_ID =  m_dlScheduler.getFormatOf( m_IndexID ).formatValue(attrset[ m_IndexID ]);
        m_jName.setText(Formats.STRING.formatValue(attrset[m_IndexName]));
        
        jTxtStartDate.setText(Formats.TIMESTAMP.formatValue(attrset[m_IndexStart]));
        jTextInterval.setText(Formats.INT.formatValue(attrset[m_IndexInterval]));
        
        m_jTextScript.setText(Formats.STRING.formatValue(attrset[m_IndexScript]));
        m_jTextScript.setCaretPosition(0);
        jCheckBoxEnabled.setSelected( (Boolean) (attrset[ m_IndexEnabled ]) );
        
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {
        Object[] attrset = (Object[]) value;

        valuesToControls( attrset );

        enableAll( false );
        jButtonScript.setEnabled(false);

    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {
        Object[] attrset = (Object[]) value;
        
        valuesToControls( attrset );

        enableAll( true );
        jButtonScript.setEnabled(false);
        
    }

    /**
     *
     * @return
     * @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {
        Object[] attrset = new Object[ m_dlScheduler.getFieldCount()];

        attrset[m_IndexID] = m_ID;
        attrset[m_IndexName] = m_jName.getText();
        attrset[m_IndexInterval] =  m_dlScheduler.getFormatOf( m_IndexInterval ).parseValue(jTextInterval.getText());
        attrset[m_IndexStart] =  m_dlScheduler.getFormatOf( m_IndexStart ).parseValue(jTxtStartDate.getText());
        attrset[m_IndexScript] =  m_dlScheduler.getFormatOf( m_IndexScript ).parseValue(m_jTextScript.getText());
        attrset[m_IndexEnabled] = jCheckBoxEnabled.isSelected();
        return attrset;
    }

    /**
     *
     * @return
     */
    @Override
    public Component getComponent() {
        return this;
    }
   
    private boolean confirmOverWrite() {
        boolean result = false;
        if (JOptionPane.showConfirmDialog(this,
                LocalRes.getIntString("message.changeslost"),
                LocalRes.getIntString("Menu.Scheduler"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {          
                           result = true;
                }
        return result;
    }           
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jPanelScript = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jComboBoxResources = new javax.swing.JComboBox();
        jScrollPaneScript = new javax.swing.JScrollPane();
        m_jTextScript = new javax.swing.JTextArea();
        jButtonScript = new javax.swing.JButton();
        jButtonRunNow = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jCheckBoxEnabled = new eu.hansolo.custom.SteelCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jTxtStartDate = new javax.swing.JTextField();
        btnDateStart = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jTextInterval = new javax.swing.JTextField();

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("Label.Name")); // NOI18N

        m_jName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel3.setText("Script");

        jLabel4.setText("Available script resources:");

        jComboBoxResources.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxResourcesActionPerformed(evt);
            }
        });

        m_jTextScript.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jTextScript.setLineWrap(true);
        m_jTextScript.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_jTextScriptKeyTyped(evt);
            }
        });
        jScrollPaneScript.setViewportView(m_jTextScript);
        m_jTextScript.getAccessibleContext().setAccessibleParent(jScrollPaneScript);

        jButtonScript.setText("Copy");
        jButtonScript.setName(""); // NOI18N
        jButtonScript.setRolloverEnabled(false);
        jButtonScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonScriptActionPerformed(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jButtonRunNow.setText(bundle.getString("Button.Test")); // NOI18N
        jButtonRunNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunNowActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelScriptLayout = new javax.swing.GroupLayout(jPanelScript);
        jPanelScript.setLayout(jPanelScriptLayout);
        jPanelScriptLayout.setHorizontalGroup(
            jPanelScriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelScriptLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelScriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneScript)
                    .addGroup(jPanelScriptLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonRunNow)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxResources, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonScript)))
                .addContainerGap())
        );
        jPanelScriptLayout.setVerticalGroup(
            jPanelScriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelScriptLayout.createSequentialGroup()
                .addGroup(jPanelScriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelScriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonScript)
                        .addComponent(jComboBoxResources, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(jLabel3))
                    .addComponent(jButtonRunNow, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneScript, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
        );

        jPanel1.setPreferredSize(new java.awt.Dimension(143, 32));

        jCheckBoxEnabled.setSelected(true);
        jCheckBoxEnabled.setText("Enabled");

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("Label.StartDate")); // NOI18N

        jTxtStartDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTxtStartDate.setPreferredSize(new java.awt.Dimension(150, 25));

        btnDateStart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/date.png"))); // NOI18N
        btnDateStart.setToolTipText(bundle.getString("tiptext.opencalendar")); // NOI18N
        btnDateStart.setMaximumSize(new java.awt.Dimension(40, 33));
        btnDateStart.setMinimumSize(new java.awt.Dimension(40, 33));
        btnDateStart.setPreferredSize(new java.awt.Dimension(40, 33));
        btnDateStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDateStartActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText(AppLocal.getIntString("Label.Interval")); // NOI18N

        jTextInterval.setText("jTextField1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTxtStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDateStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBoxEnabled, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxEnabled, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnDateStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTxtStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 817, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jPanelScript, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelScript, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxResourcesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxResourcesActionPerformed
        String name = m_ModelResource.getSelectedText();
        if( name != null && name.length() > 0 ) {
            jButtonScript.setEnabled(true);
        } else {
            jButtonScript.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxResourcesActionPerformed

    private void jButtonScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonScriptActionPerformed
        String scriptname = m_ModelResource.getSelectedText();
        if( scriptname != null && scriptname.length() > 0 ) {
               String script = m_dlSystem.getResourceAsText( scriptname );
               
               if(  script != null && script.length() > 0 ) {
                   boolean replace = true;

                   if( m_jTextScript.getText().length() > 0  ) {
                       replace = confirmOverWrite();
                   }
                   if( replace ) {
                       m_jTextScript.setText( script );
                       if( StringUtils.isEmpty(m_jName.getText() ) ) {
                           m_jName.setText(scriptname);
                       }
                   }
               }
        }
    }//GEN-LAST:event_jButtonScriptActionPerformed

    private void m_jTextScriptKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_jTextScriptKeyTyped
    }//GEN-LAST:event_m_jTextScriptKeyTyped

    private void btnDateStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDateStartActionPerformed

        Date date;
        try {
            date = (Date) Formats.TIMESTAMP.parseValue(jTxtStartDate.getText());
        } catch (BasicException e) {
            date = null;
        }
        date = JCalendarDialog.showCalendarTimeHours(this, date);
        if (date != null) {
            jTxtStartDate.setText(Formats.TIMESTAMP.formatValue(date));
        }
    }//GEN-LAST:event_btnDateStartActionPerformed

    private void jButtonRunNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunNowActionPerformed
        try {
            ScheduledTaskInfo info = new ScheduledTaskInfo(
                    m_ID, m_jName.getText(),
                    (Date) Formats.TIMESTAMP.parseValue(jTxtStartDate.getText()),
                    (Integer) Formats.INT.parseValue(jTextInterval.getText()),
                    m_jTextScript.getText(),
                    jCheckBoxEnabled.isSelected() );
            
            ScheduledTask task = new ScheduledTask(m_App, info);
            
            task.RunOnceNow();
            
        } catch (BasicException ex) {
            Logger.getLogger(ScheduleEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonRunNowActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDateStart;
    private javax.swing.JButton jButtonRunNow;
    private javax.swing.JButton jButtonScript;
    private eu.hansolo.custom.SteelCheckBox jCheckBoxEnabled;
    private javax.swing.JComboBox jComboBoxResources;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelScript;
    private javax.swing.JScrollPane jScrollPaneScript;
    private javax.swing.JTextField jTextInterval;
    private javax.swing.JTextField jTxtStartDate;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextArea m_jTextScript;
    // End of variables declaration//GEN-END:variables
}


