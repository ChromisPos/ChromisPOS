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

package uk.chromis.pos.imports;

import uk.chromis.pos.inventory.*;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.ListCellRenderer;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ListCellRendererBasic;
import uk.chromis.data.loader.ComparatorCreator;
import uk.chromis.data.loader.ComparatorCreatorBasic;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.SentenceExec;
import uk.chromis.data.loader.SentenceExecTransaction;
import uk.chromis.data.loader.Vectorer;
import uk.chromis.data.loader.VectorerBasic;
import uk.chromis.data.user.EditorListener;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProvider;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.forms.DataLogicStockChanges;
import uk.chromis.pos.panels.JPanelTable;
import uk.chromis.pos.reports.JParamsLocation;

public class StockChangesPanel extends JPanelTable implements EditorListener {

    private StockChangesEditor jeditor;
    private JParamsLocation m_paramslocation;    
    
    private DataLogicStockChanges m_dataLogic = null;
    private DataLogicSales m_dlSales = null;
    private SaveProvider m_spr = null;
    
    /** Creates a new instance of StockChangesPanel */
    public StockChangesPanel() {
    }
    
    /**
     *
     */
    @Override
    protected void init() {   
        m_dataLogic = (DataLogicStockChanges) app.getBean("uk.chromis.pos.forms.DataLogicStockChanges");
        m_dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");

        m_spr = new SaveProvider(
            m_dataLogic.getChangesUpdate(),
            null,
            m_dataLogic.getChangesDelete());

        m_paramslocation = new JParamsLocation();
        m_paramslocation.init(app);

         // el panel del editor
        jeditor = new StockChangesEditor( m_dataLogic, m_dlSales, dirty);       
        
        setListWidth(400);
    }
    
    /**
     *
     * @return value
     */
    @Override
    public EditorRecord getEditor() {
        return jeditor;
    }
    
    /**
     *
     * @return value
     */
    @Override
    public Component getFilter() {
        return m_paramslocation.getComponent();
    }

    /**
     *
     * @return btnScanPal
     */
    @Override
    public Component getToolbarExtras() {
        
        JButton btnScanPal = new JButton();
        btnScanPal.setText("ScanPal");
        btnScanPal.setVisible(app.getDeviceScanner() != null);
        btnScanPal.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanPalActionPerformed(evt);
            }
        });      
        
        return btnScanPal;
    }
    
    private void btnScanPalActionPerformed(java.awt.event.ActionEvent evt) {                                           
  
        JDlgUploadProducts.showMessage(this, app.getDeviceScanner(), bd);
    }

  /**
     *
     * @return
     */
    @Override
    public ListProvider getListProvider() {
        return new ListProviderCreator(m_dataLogic.getChangesListbyLocation(),
                m_paramslocation );
    }

    /**
     *
     * @return
     */
    @Override
    public SaveProvider getSaveProvider() {
        return m_spr;
        }

    /**
     *
     * @return
     */
    @Override
    public Vectorer getVectorer() {
        String[] names = new String[2];
        Formats[] formats = new Formats[2];

        names[0] = "LOCATION";
        formats[0] =  m_dataLogic.getFormatOf( m_dataLogic.getIndexOf(names[0]) );
        
        names[1] = "PRODUCTNAME";
        formats[1] =  m_dataLogic.getFormatOf( m_dataLogic.getIndexOf(names[1] ));
        
        return new VectorerBasic( names, formats, new int[] { 0,1 } );
    }

    /**
     *
     * @return
     */
    @Override
    public ComparatorCreator getComparatorCreator() {
        String[] names = new String[2];
        Datas[] datas = new Datas[2];

        names[0] = "LOCATION";
        datas[0] =  m_dataLogic.getDatasOf( m_dataLogic.getIndexOf(names[0]) );
        
        names[1] = "PRODUCTNAME";
        datas[1] =  m_dataLogic.getDatasOf( m_dataLogic.getIndexOf(names[1]) );
        
        return new ComparatorCreatorBasic(names, datas, new int[] { 0,1 } );
    }

    /**
     *
     * @return
     */
    @Override
    public ListCellRenderer getListCellRenderer() {
        return new ListCellRendererBasic(m_dataLogic.getRenderStringChange() );
    }
    
    /**
     *
     * @return value
     */
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.StockChanges");
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {
        
        jeditor.activate();
        m_paramslocation.activate();

        super.activate();
    }

    /**
     *
     * @param value
     */
    @Override
    public void updateValue(Object value) {
    }    
   
}
