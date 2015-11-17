/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.promotion;

import javax.swing.ListCellRenderer;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.ComparatorCreator;
import uk.chromis.data.loader.Vectorer;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProvider;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.panels.JPanelTable;

/**
 *
 * @author John
 */
public class PromotionPanel extends JPanelTable {

    private PromotionEditor m_Editor;
    DataLogicPromotions m_dlPromotions;

    /**
     * Creates new form PromotionPanel
     */
    public PromotionPanel() {
    }
    
    @Override
    protected void init() {
        m_dlPromotions = new DataLogicPromotions();
        m_dlPromotions.init(app);
        m_Editor = new PromotionEditor(app, dirty); 
    }
    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {

        m_Editor.activate(); 
        super.activate(); 
    }
    
    @Override
    public EditorRecord getEditor() {
        return m_Editor;
    }

    @Override
    public ListProvider getListProvider() {
        return new ListProviderCreator( m_dlPromotions.getListSentence( ) ); 
    }

    @Override
    public SaveProvider getSaveProvider() {
        return new SaveProvider(
            m_dlPromotions.getUpdateSentence(),
            m_dlPromotions.getInsertSentence(),
            m_dlPromotions.getDeleteSentence());
        
    } 
          
    /**
     *
     * @return
     */
    @Override
    public final Vectorer getVectorer() {
        return  m_dlPromotions.getRow().getVectorer();
    }
    
    /**
     *
     * @return
     */
    @Override
    public final ComparatorCreator getComparatorCreator() {
        return  m_dlPromotions.getRow().getComparatorCreator();
    }
    
    /**
     *
     * @return
     */
    @Override
    public final ListCellRenderer getListCellRenderer() {
        return  m_dlPromotions.getRow().getListCellRenderer();
    } 
    
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Promotions");
    }
}
