/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.promotion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.gui.JMessageDialog;
import uk.chromis.data.gui.ListValModel;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.loader.Datas;
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
public class PromotionEditor extends javax.swing.JPanel 
    implements EditorRecord, ListSelectionListener  {

    private final DirtyManager m_Dirty;
    DataLogicPromotions m_dlPromotions;
    DataLogicAdmin m_dlAdmin;
    DataLogicSystem m_dlSystem;

    private final AppView m_App;
    private String m_ID;
    private String m_criteria;
    
    private final int m_IndexID;
    private final int m_IndexName;
    private final int m_IndexCriteria;
    private final int m_IndexScript;
    private final int m_IndexEnabled;
    
    private SentenceList m_SentenceProducts;
    private ListValModel m_ModelProducts;
    
    private SentenceList m_SentenceResource;
    private ComboBoxValModel m_ModelResource;

    
    class ProductsListCellRenderer extends JComponent
        implements ListCellRenderer {
         Color listForeground, listBackground,  
             listSelectionForeground,  
             listSelectionBackground;  
         
        DefaultListCellRenderer m_defaultRenderer;  
        JCheckBox m_checkbox;
        JLabel m_Label;

        public ProductsListCellRenderer() {  
            setLayout (new BorderLayout());  
            m_defaultRenderer = new DefaultListCellRenderer();  
            m_checkbox = new JCheckBox();  
            add (m_checkbox, BorderLayout.WEST);  
            add (m_defaultRenderer, BorderLayout.CENTER);  
            
             UIDefaults uid = UIManager.getLookAndFeel().getDefaults();  
             listForeground =  uid.getColor ("List.foreground");  
             listBackground =  uid.getColor ("List.background");  
             listSelectionForeground =  uid.getColor ("List.selectionForeground");  
             listSelectionBackground =  uid.getColor ("List.selectionBackground"); 
        }  
        
        public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
            Object [] avalues = (Object []) value;
            String name = (String) avalues[DataLogicPromotions.INDEX_PROMOTEDPRODUCT_NAME];
            String ref = (String) avalues[DataLogicPromotions.INDEX_PROMOTEDPRODUCT_REFERENCE];
            String promotion = (String) avalues[DataLogicPromotions.INDEX_PROMOTEDPRODUCT_PROMOTIONID];
            String product = ref + "-" + name;

            m_defaultRenderer.getListCellRendererComponent(list, product, index,
            isSelected, cellHasFocus);

            m_checkbox.setSelected (isSelected);
            
            Component[] comps = getComponents();  
            for (int i=0; i<comps.length; i++) {  
               comps[i].setForeground (listForeground);  
               comps[i].setBackground (listBackground);  
            }  
        
            return this;  
        }
    }
    
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
        m_criteria = null;
        
        m_IndexID = m_dlPromotions.getIndexOf("ID");
        m_IndexName = m_dlPromotions.getIndexOf("NAME");
        m_IndexCriteria = m_dlPromotions.getIndexOf("CRITERIA");
        m_IndexScript = m_dlPromotions.getIndexOf("SCRIPT");
        m_IndexEnabled = m_dlPromotions.getIndexOf("ISENABLED");       

        m_ModelResource = new ComboBoxValModel();
        jComboBoxResources.setModel(m_ModelResource);

        ListCellRenderer renderer = new ProductsListCellRenderer();
        jListProducts.setCellRenderer(renderer);
        jListProducts.addListSelectionListener( this );
        jListProducts.setSelectionModel(new DefaultListSelectionModel() {
            private static final long serialVersionUID = 1L;

            boolean gestureStarted = false;

            @Override
            public void setSelectionInterval(int index0, int index1) {
                if(!gestureStarted){
                    if (isSelectedIndex(index0)) {
                        super.removeSelectionInterval(index0, index1);
                    } else {
                        super.addSelectionInterval(index0, index1);
                    }
                }
                gestureStarted = true;
            }

            @Override
            public void setValueIsAdjusting(boolean isAdjusting) {
                if (isAdjusting == false) {
                    gestureStarted = false;
                }
            }

        });

        m_ModelProducts = new ListValModel();
        jListProducts.setModel(m_ModelProducts);
        
        m_jName.getDocument().addDocumentListener(dirty);
        m_jTextCriteria.getDocument().addDocumentListener(dirty);
        m_jTextScript.getDocument().addDocumentListener(dirty);
        jCheckBoxEnabled.addActionListener(dirty);
    }

     /**
     *
     * @throws BasicException
     */
    public void activate() throws BasicException {
        writeValueEOF();
        
        m_SentenceResource = m_dlPromotions.getResourceScriptListSentence();
        m_ModelResource = new ComboBoxValModel( m_SentenceResource.list());
        jComboBoxResources.setModel(m_ModelResource);
    }

    public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting() == false) {
            m_Dirty.setDirty(true);
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
        m_criteria = null;
        showProducts();
        
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
        m_criteria = null;
        showProducts();
        
        m_jName.setText(null);
        m_jTextCriteria.setText(null);
        m_jTextScript.setText(null);
        jCheckBoxEnabled.setSelected(false);
        m_ModelResource.setSelectedKey(null);

        enableAll( true );
       jButtonScript.setEnabled(false);
    }

    private void showProducts() {
          
        m_SentenceProducts = m_dlPromotions.getPromotedProductsSentence(m_ID, m_criteria );
        try {
            m_ModelProducts = new ListValModel(m_SentenceProducts.list());
        } catch (BasicException ex) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"), ex));
        }
        jListProducts.setModel(m_ModelProducts);
        
        if( m_ID != null ) {
            int count = m_ModelProducts.getSize();
            List<Integer> aIndexes = new ArrayList<Integer>();
            
            for( int i = 0; i < count; ++i ) {
                Object [] avalues = (Object [] ) m_ModelProducts.getElementAt(i);
                String pid = (String) avalues[DataLogicPromotions.INDEX_PROMOTEDPRODUCT_PROMOTIONID];
                if( pid != null && pid.contentEquals( m_ID )) {
                    aIndexes.add(i);
                }
            }
            
            int list[] = new int[aIndexes.size()];
            for( int i = 0; i < aIndexes.size(); ++i )
                list[i] = aIndexes.get(i);

            jListProducts.setSelectedIndices( list );
        }
    }
    
    private void valuesToControls( Object[] attrset ) {
        
        m_ID =  m_dlPromotions.getFormatOf( m_IndexID ).formatValue(attrset[ m_IndexID ]);
        m_jName.setText( m_dlPromotions.getFormatOf( m_IndexName ).formatValue(attrset[ m_IndexName ]));
        m_jTextCriteria.setText( m_dlPromotions.getFormatOf( m_IndexCriteria ).formatValue(attrset[ m_IndexCriteria ]));
        m_jTextCriteria.setCaretPosition(0);
        m_criteria = m_jTextCriteria.getText();
        
        m_jTextScript.setText( m_dlPromotions.getFormatOf( m_IndexScript ).formatValue(attrset[ m_IndexScript ]));
        m_jTextScript.setCaretPosition(0);
        jCheckBoxEnabled.setSelected( (Boolean) (attrset[ m_IndexEnabled ]) );
        m_ModelResource.setSelectedKey(null);

        showProducts();
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
        Object[] attrset = new Object[ m_dlPromotions.getFieldCount()];

        attrset[m_IndexID] = m_ID;
        attrset[m_IndexName] = m_jName.getText();
        attrset[m_IndexCriteria] = m_dlPromotions.getFormatOf( m_IndexCriteria ).parseValue(m_jTextCriteria.getText());
        attrset[m_IndexScript] =  m_dlPromotions.getFormatOf( m_IndexScript ).parseValue(m_jTextScript.getText());
        attrset[m_IndexEnabled] = jCheckBoxEnabled.isSelected();
        
        // Set Promotionid in the selected products
        List<Object> selected = jListProducts.getSelectedValuesList();
        List<String> aProducts = new ArrayList<String>();
        
        for(Object item : selected) {
            Object [] values = (Object []) item;
            aProducts.add( Datas.STRING.toString( values[ DataLogicPromotions.INDEX_PROMOTEDPRODUCT_ID ] ) );
        }
        
        m_dlPromotions.resetPromotionID( m_ID, aProducts );
        
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jListProducts = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("Label.Name")); // NOI18N

        m_jName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        m_jTextScript.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jTextScript.setLineWrap(true);
        m_jTextScript.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_jTextScriptKeyTyped(evt);
            }
        });

        m_jTextCriteria.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jTextCriteria.setLineWrap(true);

        jLabel1.setText("Criteria");

        jLabel3.setText("Script");

        jButtonHelp.setText("Help");
        jButtonHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHelpActionPerformed(evt);
            }
        });

        jButtonScript.setText("Use");
        jButtonScript.setName(""); // NOI18N
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

        jLabel4.setText("Script resources:");

        jButtonTest.setText("--->");
        jButtonTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestActionPerformed(evt);
            }
        });

        jListProducts.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jListProducts);

        jLabel5.setText("Products in Promotion");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jTextScript)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBoxResources, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonScript))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(jButtonHelp)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(m_jTextCriteria, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTest)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBoxEnabled, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxEnabled))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jButtonHelp))
                        .addGap(5, 5, 5)
                        .addComponent(m_jTextCriteria, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(4, 4, 4)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jButtonTest)
                                .addGap(37, 37, 37)))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jComboBoxResources, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonScript)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jTextScript, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
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
            m_dlSystem.getResourceAsText( "help.promotion" ) );
    }//GEN-LAST:event_jButtonHelpActionPerformed

    private void jButtonTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestActionPerformed
        m_criteria = m_jTextCriteria.getText();
        showProducts();
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JList jListProducts;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextArea m_jTextCriteria;
    private javax.swing.JTextArea m_jTextScript;
    // End of variables declaration//GEN-END:variables
}
