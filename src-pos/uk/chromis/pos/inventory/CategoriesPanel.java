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

package uk.chromis.pos.inventory;

import javax.swing.ListCellRenderer;
import uk.chromis.data.gui.ListCellRendererBasic;
import uk.chromis.data.loader.ComparatorCreator;
import uk.chromis.data.loader.TableDefinition;
import uk.chromis.data.loader.Vectorer;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProvider;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.panels.JPanelTable;

/**
 *
 * @author adrianromero
 */
public class CategoriesPanel extends JPanelTable {
    
    private TableDefinition tcategories;
    private CategoriesEditor jeditor;
    
    /** Creates a new instance of JPanelCategories */
    public CategoriesPanel() {        
    }

    /**
     *
     */
    @Override
    protected void init() {   
        DataLogicSales dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");           
        tcategories = dlSales.getTableCategories();
        jeditor = new CategoriesEditor(app, dirty);    
        AppLocal.LIST_BY_RIGHTS="";        
    }
    
    /**
     *
     * @return
     */
    @Override
    public ListProvider getListProvider() {
        return new ListProviderCreator(tcategories);
    }
    
    /**
     *
     * @return
     */
    @Override
    public SaveProvider getSaveProvider() {
        return new SaveProvider(tcategories);      
    }
    
    /**
     *
     * @return
     */
    @Override
    public Vectorer getVectorer() {
        return tcategories.getVectorerBasic(new int[]{1});
    }
    
    /**
     *
     * @return
     */
    @Override
    public ComparatorCreator getComparatorCreator() {
        return tcategories.getComparatorCreator(new int[]{1});
    }
    
    /**
     *
     * @return
     */
    @Override
    public ListCellRenderer getListCellRenderer() {
        return new ListCellRendererBasic(tcategories.getRenderStringBasic(new int[]{1}));
    }
    
    /**
     *
     * @return
     */
    @Override
    public EditorRecord getEditor() {
        return jeditor;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Categories");
    }        
}
