//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) 2015 uniCenta
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
package uk.chromis.pos.admin;

import uk.chromis.data.gui.ListCellRendererBasic;
import uk.chromis.data.loader.ComparatorCreator;
import uk.chromis.data.loader.TableDefinition;
import uk.chromis.data.loader.Vectorer;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProvider;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.panels.JPanelTable;
import javax.swing.ListCellRenderer;

/**
 *
 * @author adrianromero
 */
public class RolesPanel extends JPanelTable {

    private TableDefinition troles;
    private TableDefinition trolesmenu;
    private DataLogicAdmin dlAdmin;
    private RolesViewTree jeditor;

    /**
     * Creates a new instance of RolesPanel
     */
    public RolesPanel() {
    }

    /**
     *
     */
    @Override
    protected void init() {
        dlAdmin = (DataLogicAdmin) app.getBean("uk.chromis.pos.admin.DataLogicAdmin");
        troles = dlAdmin.getTableRoles();         
        jeditor = new RolesViewTree(dlAdmin, dirty, app);
        AppLocal.LIST_BY_RIGHTS="";       
    }

    /**
     *
     * @return
     */
    @Override
    public ListProvider getListProvider() {
        return new ListProviderCreator(troles);
    }

    /**
     *
     * @return
     */
    @Override
    public SaveProvider getSaveProvider() {
        return new SaveProvider(troles);
    }

    /**
     *
     * @return
     */
    @Override
    public Vectorer getVectorer() {
        return troles.getVectorerBasic(new int[]{1});
    }

    /**
     *
     * @return
     */
    @Override
    public ComparatorCreator getComparatorCreator() {
        return troles.getComparatorCreator(new int[]{1});
    }

    /**
     *
     * @return
     */
    @Override
    public ListCellRenderer getListCellRenderer() {
        return new ListCellRendererBasic(troles.getRenderStringBasic(new int[]{1}));
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
        return AppLocal.getIntString("Menu.Roles");
    }

}
