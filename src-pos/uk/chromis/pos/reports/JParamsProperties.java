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

package uk.chromis.pos.reports;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.QBFCompareEnum;
import uk.chromis.data.loader.SerializerWrite;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.inventory.ProductsEditor;

/**
 *
 * @author adrianromero
 */
public class JParamsProperties extends javax.swing.JPanel implements ReportEditorCreator {

    private Properties m_PropertyOptions;
    DataLogicSystem m_dlSystem;
    
    /** Creates new form JParamsProperties */
    public JParamsProperties() {
        initComponents();     
    }

    /**
     *
     * @param app
     */
    @Override
    public void init(AppView app) {
        m_dlSystem = (DataLogicSystem) app.getBean("uk.chromis.pos.forms.DataLogicSystem");
    }
        
    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {

        m_PropertyOptions = new Properties();
        jPropertyValueText.setVisible( false );
        jPropertyValueCombo.setVisible( false );

        try {
            m_PropertyOptions.loadFromXML( new ByteArrayInputStream( m_dlSystem.getResourceAsXML("Product.Properties").getBytes(StandardCharsets.UTF_8)));
        } catch (IOException ex) {
            Logger.getLogger(ProductsEditor.class.getName()).log(Level.SEVERE, null, ex);
            m_PropertyOptions.put( "Product.Properties", "");
            jPropertyValueText.setVisible( true );
            jPropertyValueText.setText( "Resource load failed");
        }
        for (Map.Entry<Object, Object> e : m_PropertyOptions.entrySet()) {
          jComboProperties.addItem( (String) e.getKey() );
        }
        jComboProperties.setSelectedIndex(-1);
        jComboProperties.addActionListener( new PropertyActionListener() );

    }
    
    /**
     *
     * @return
     */
    @Override
    public SerializerWrite getSerializerWrite() {
        return new SerializerWriteBasic(new Datas[] {Datas.OBJECT, Datas.STRING});
    }

    /**
     *
     * @return
     */
    @Override
    public Component getComponent() {
        return this;
    }

    /**
     *
     * @param a
     */
    protected void addFirst(List a) {
        // do nothing
    }
    
    /**
     *
     * @param l
     */
    public void addActionListener(ActionListener l) {
        jComboProperties.addActionListener(l);
        jPropertyValueText.addActionListener(l);
        jPropertyValueCombo.addActionListener(l);
    }
    
    /**
     *
     * @param l
     */
    public void removeActionListener(ActionListener l) {
        jComboProperties.removeActionListener(l);
        jPropertyValueText.removeActionListener(l);
        jPropertyValueCombo.removeActionListener(l);
    }
    
    // Get a string suitable for searching a product properties field
    // e.g. <entry key="Age_Check">1</entry>
    //
    
    private String getSearchString() {
        String sValue = "";
        String sReturn = "";
        String sel = (String) jComboProperties.getSelectedItem();

        if( sel != null ) {
            String type = m_PropertyOptions.getProperty( sel, "" );

            int nComma = type.indexOf( ',' );
            if( nComma >0 ) {
                type = type.substring( 0, nComma ).trim();
            }

            switch( type ) {
                case "boolean" :
                    String sYes = (String) jPropertyValueCombo.getSelectedItem();
                    sValue = sYes.compareTo( "Yes" ) == 0 ? "1" : "0";
                    break;
                case "number" :
                    Double dValue;
                    try {
                        dValue = (Double) Formats.DOUBLE.parseValue(jPropertyValueText.getText());
                    } catch (BasicException ex) {
                        dValue = 0.0;
                    }
                    sValue = dValue.toString();
                    break;
                case "option" :
                    sValue = (String) jPropertyValueCombo.getSelectedItem();
                    break;
                case "text" :
                    sValue = (String) jPropertyValueText.getText();
                    break;
                default:
                    break;
            }           

            if( !sValue.isEmpty() ) {
                sReturn = "<entry key=\"" + sel + "\">" + sValue + "</entry>";
            }
        }
        
        return sReturn;
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {
        String sSearch = getSearchString();
        
        return new Object[] {
            // <entry key="Age_Check">1</entry>
            sSearch.length() == 0 ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_RE, 
            sSearch
        };
    }    
    
private class PropertyActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            
            if( jComboProperties == null ) {
                return;
            }
            String sel = (String) jComboProperties.getSelectedItem();
            if( sel == null || sel.isEmpty() ) {
                return;
            }
            
            String val = m_PropertyOptions.getProperty( sel, "" );
            
            String type = val;
            int nComma = val.indexOf( ',' );
            if( nComma >0 ) {
                type = val.substring( 0, nComma ).trim();
                val = val.substring( nComma + 1 ).trim();
            }
            
            switch( type ) {
                case "boolean" :
                    jPropertyValueText.setVisible( false );
                    jPropertyValueCombo.removeAllItems();
                    jPropertyValueCombo.addItem(AppLocal.getIntString("Button.No"));
                    jPropertyValueCombo.addItem(AppLocal.getIntString("Button.Yes"));
                    jPropertyValueCombo.setVisible( true );
                     break;
                case "number" :
                    jPropertyValueCombo.setVisible( false );
                    jPropertyValueText.setVisible( true );
                    jPropertyValueText.setText( "" );
                    break;
                case "option" :
                    jPropertyValueCombo.removeAllItems();
                    nComma = val.indexOf( ',' );
                    while( nComma > 0 ) {
                        jPropertyValueCombo.addItem( val.substring( 0, nComma ) );
                        val = val.substring( nComma + 1 ).trim();
                        nComma = val.indexOf( ',' );
                    }
                    jPropertyValueCombo.addItem(val);
                    jPropertyValueCombo.setVisible( true );
                    jPropertyValueText.setVisible( false );
                    break;
                case "text" :
                    jPropertyValueCombo.setVisible( false );
                    jPropertyValueText.setVisible( true );
                    jPropertyValueText.setText( "" );
                    break;
                default:
                    Logger.getLogger(ProductsEditor.class.getName()).log(Level.WARNING, "Unknown property type (" + type + ") in Product.Properties" );
                    break;
            }           
        }

    }

    /** This method is called from within the constructor to
     * initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboProperties = new javax.swing.JComboBox();
        jPanel7 = new javax.swing.JPanel();
        jPropertyValueCombo = new javax.swing.JComboBox<>();
        jPropertyValueText = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, AppLocal.getIntString("label.byproperty"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setMinimumSize(new java.awt.Dimension(250, 120));
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(250, 120));
        setLayout(null);

        jComboProperties.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        add(jComboProperties);
        jComboProperties.setBounds(17, 17, 193, 25);

        jPanel7.setMinimumSize(new java.awt.Dimension(0, 60));
        jPanel7.setLayout(null);

        jPanel7.add(jPropertyValueCombo);
        jPropertyValueCombo.setBounds(0, 0, 220, 24);

        jPropertyValueText.setText("jTextField1");
        jPanel7.add(jPropertyValueText);
        jPropertyValueText.setBounds(0, 0, 220, 19);

        add(jPanel7);
        jPanel7.setBounds(17, 54, 227, 40);

        getAccessibleContext().setAccessibleName("By Product Paroperty");
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboProperties;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JComboBox<String> jPropertyValueCombo;
    private javax.swing.JTextField jPropertyValueText;
    // End of variables declaration//GEN-END:variables
    
}
