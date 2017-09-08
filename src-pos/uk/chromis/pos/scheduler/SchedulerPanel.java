/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.scheduler;

import uk.chromis.pos.promotion.*;
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
public class SchedulerPanel extends JPanelTable {

    private ScheduleEditor m_Editor;
    DataLogicScheduler m_dl;

    /**
     * Creates new form PromotionPanel
     */
    public SchedulerPanel() {
    }
    
    @Override
    protected void init() {
        m_dl = new DataLogicScheduler();
        m_dl.init(app);
        m_Editor = new ScheduleEditor(app, dirty); 
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
        return new ListProviderCreator( m_dl.getListSentence( ) ); 
    }

    @Override
    public SaveProvider getSaveProvider() {
        return new SaveProvider(
            m_dl.getUpdateSentence(),
            m_dl.getInsertSentence(),
            m_dl.getDeleteSentence());
        
    } 
          
    /**
     *
     * @return
     */
    @Override
    public final Vectorer getVectorer() {
        return  m_dl.getRow().getVectorer();
    }
    
    /**
     *
     * @return
     */
    @Override
    public final ComparatorCreator getComparatorCreator() {
        return  m_dl.getRow().getComparatorCreator();
    }
    
    /**
     *
     * @return
     */
    @Override
    public final ListCellRenderer getListCellRenderer() {
        return  m_dl.getRow().getListCellRenderer();
    } 
    
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Scheduler");
    }
}
