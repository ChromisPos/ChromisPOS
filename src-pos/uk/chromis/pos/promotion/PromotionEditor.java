/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.promotion;

import java.awt.Component;
import java.util.UUID;
import javax.swing.JOptionPane;
import uk.chromis.basic.BasicException;
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
public class PromotionEditor extends javax.swing.JPanel implements EditorRecord {

    private DirtyManager m_Dirty;
    DataLogicPromotions m_dlPromotions;
    DataLogicAdmin m_dlAdmin;
    DataLogicSystem m_dlSystem;
    
    private AppView m_App;
    private Object m_ID;
    
    private int m_IndexID;
    private int m_IndexName;
    private int m_IndexCriteria;
    private int m_IndexScript;
    private int m_IndexEnabled;
    private SentenceList m_SentenceResource;
    private ComboBoxValModel m_ModelResource;
   
    /** Creates new form
     * @param app
     * @param dirty */
    public PromotionEditor(AppView app, DirtyManager dirty) {
        initComponents();
        
        m_dlPromotions = (DataLogicPromotions) app.getBean("uk.chromis.pos.promotion.DataLogicPromotions");
        m_dlAdmin = (DataLogicAdmin) app.getBean("uk.chromis.pos.admin.DataLogicAdmin");
        m_dlSystem = (DataLogicSystem) app.getBean("uk.chromis.pos.forms.DataLogicSystem");
        
        m_App = app;
        m_Dirty = dirty;
    
        m_IndexID = m_dlPromotions.getIndexOf("ID");
        m_IndexName = m_dlPromotions.getIndexOf("NAME");
        m_IndexCriteria = m_dlPromotions.getIndexOf("CRITERIA");
        m_IndexScript = m_dlPromotions.getIndexOf("SCRIPT");
        m_IndexEnabled = m_dlPromotions.getIndexOf("ISENABLED");       

        m_ModelResource = new ComboBoxValModel();
        jComboBoxResources.setModel(m_ModelResource);

        m_jName.getDocument().addDocumentListener(dirty);
        m_jTextCriteria.getDocument().addDocumentListener(dirty);
        m_jTextScript.getDocument().addDocumentListener(dirty);
        jCheckBoxEnabled.addActionListener(dirty);
            
        writeValueEOF();
    }

     /**
     *
     * @throws BasicException
     */
    public void activate() throws BasicException {
        m_SentenceResource = m_dlPromotions.getResourceScriptListSentence();

        m_ModelResource = new ComboBoxValModel( m_SentenceResource.list());
        jComboBoxResources.setModel(m_ModelResource);
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
        m_jTextCriteria.setEnabled(b);
        m_jTextScript.setEnabled(b);
        jComboBoxResources.setEnabled(b);

    }

    /**
     *
     */
    @Override
    public void writeValueEOF() {
        m_ID = null;

        m_jName.setText(null);
        m_jTextCriteria.setText(null);
        m_jTextScript.setText(null);
        jCheckBoxEnabled.setSelected(false);
        m_ModelResource.setSelectedKey(null);

        enableAll( false );
        jButtonScript.setEnabled(false);
    }
    
    /**
     *
     */
    @Override
    public void writeValueInsert() {
        m_ID = UUID.randomUUID().toString();
        m_jName.setText(null);
        m_jTextCriteria.setText(null);
        m_jTextScript.setText(null);
        jCheckBoxEnabled.setSelected(false);
        m_ModelResource.setSelectedKey(null);

        enableAll( true );
       jButtonScript.setEnabled(false);
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {
        Object[] attrset = (Object[]) value;
        m_ID = attrset[ m_IndexID ];
        m_jName.setText( m_dlPromotions.getFormatOf( m_IndexName ).formatValue(attrset[ m_IndexName ]));
        m_jTextCriteria.setText( m_dlPromotions.getFormatOf( m_IndexCriteria ).formatValue(attrset[ m_IndexCriteria ]));
        m_jTextCriteria.setCaretPosition(0);
        m_jTextScript.setText( m_dlPromotions.getFormatOf( m_IndexScript ).formatValue(attrset[ m_IndexScript ]));
        m_jTextScript.setCaretPosition(0);
        jCheckBoxEnabled.setSelected( (Boolean) (attrset[ m_IndexEnabled ]) );
        m_ModelResource.setSelectedKey(null);

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
        
        m_ID = attrset[ m_IndexID ];
        m_jName.setText( m_dlPromotions.getFormatOf( m_IndexName ).formatValue(attrset[ m_IndexName ]));
        m_jTextCriteria.setText( m_dlPromotions.getFormatOf( m_IndexCriteria ).formatValue(attrset[ m_IndexCriteria ]));
        m_jTextCriteria.setCaretPosition(0);
        m_jTextScript.setText( m_dlPromotions.getFormatOf( m_IndexScript ).formatValue(attrset[ m_IndexScript ]));
        m_jTextScript.setCaretPosition(0);
        jCheckBoxEnabled.setSelected( (Boolean) (attrset[ m_IndexEnabled ]) );
        m_ModelResource.setSelectedKey(null);

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
        Object[] attrset = new Object[ m_dlPromotions.getFieldCount()];

        attrset[m_IndexID] = m_ID;
        attrset[m_IndexName] = m_jName.getText();
        attrset[m_IndexCriteria] = m_dlPromotions.getFormatOf( m_IndexCriteria ).parseValue(m_jTextCriteria.getText());
        attrset[m_IndexScript] =  m_dlPromotions.getFormatOf( m_IndexScript ).parseValue(m_jTextScript.getText());
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
                LocalRes.getIntString("Menu.Promotions"),
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
        m_jTextScript = new javax.swing.JTextArea();
        m_jTextCriteria = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButtonHelp = new javax.swing.JButton();
        jButtonScript = new javax.swing.JButton();
        jCheckBoxEnabled = new javax.swing.JCheckBox();
        jComboBoxResources = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jButtonTest = new javax.swing.JButton();

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("Label.Name")); // NOI18N

        m_jName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        m_jTextScript.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jTextScript.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_jTextScriptKeyTyped(evt);
            }
        });

        m_jTextCriteria.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel1.setText("Criteria");

        jLabel3.setText("Script");

        jButtonHelp.setText("Help");
        jButtonHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHelpActionPerformed(evt);
            }
        });

        jButtonScript.setText("Copy");
        jButtonScript.setMaximumSize(null);
        jButtonScript.setMinimumSize(null);
        jButtonScript.setName(""); // NOI18N
        jButtonScript.setPreferredSize(null);
        jButtonScript.setRolloverEnabled(false);
        jButtonScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonScriptActionPerformed(evt);
            }
        });

        jCheckBoxEnabled.setText("Enabled");

        jComboBoxResources.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxResourcesActionPerformed(evt);
            }
        });

        jLabel4.setText("Promotion resources:");

        jButtonTest.setText("Test");
        jButtonTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBoxResources, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonScript, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButtonHelp, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jButtonTest)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(m_jTextScript)
                            .addComponent(m_jTextCriteria))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                                .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1))
                        .addGap(6, 6, 6)
                        .addComponent(jCheckBoxEnabled, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jCheckBoxEnabled))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jTextCriteria, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jComboBoxResources, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonScript, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonHelp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTest)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jTextScript, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
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
                       m_jName.setText(scriptname);
                   }
               }
        }
    }//GEN-LAST:event_jButtonScriptActionPerformed

    private void m_jTextScriptKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_jTextScriptKeyTyped
        m_ModelResource.setSelectedKey(null);
    }//GEN-LAST:event_m_jTextScriptKeyTyped

    private void jButtonHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHelpActionPerformed
        JOptionPane.showMessageDialog( this,
            m_dlSystem.getResourceAsText( "help.promotions" ) );
    }//GEN-LAST:event_jButtonHelpActionPerformed

    private void jButtonTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonTestActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonHelp;
    private javax.swing.JButton jButtonScript;
    private javax.swing.JButton jButtonTest;
    private javax.swing.JCheckBox jCheckBoxEnabled;
    private javax.swing.JComboBox jComboBoxResources;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextArea m_jTextCriteria;
    private javax.swing.JTextArea m_jTextScript;
    // End of variables declaration//GEN-END:variables
}
