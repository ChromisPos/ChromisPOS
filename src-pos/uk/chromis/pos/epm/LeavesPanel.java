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

package uk.chromis.pos.epm;

import javax.swing.ListCellRenderer;
import uk.chromis.basic.BasicException;
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

/**
 *
 * @author Ali Safdar & Aneeqa Baber
 */
public class LeavesPanel extends JPanelTable {

    private TableDefinition tleaves;
    private LeavesView jeditor;

    /** Creates a new instance of LeavesPanel */
    public LeavesPanel() {
    }

    /**
     *
     */
    @Override
    protected void init() {
        DataLogicPresenceManagement dlPresenceManagement  = (DataLogicPresenceManagement) app.getBean("uk.chromis.pos.epm.DataLogicPresenceManagement");
        tleaves = dlPresenceManagement.getTableLeaves();
        jeditor = new LeavesView(app, dirty);
        AppLocal.LIST_BY_RIGHTS="";        
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {
        jeditor.activate();
        super.activate();
    }

    /**
     *
     * @return
     */
    @Override
    public ListProvider getListProvider() {
        return new ListProviderCreator(tleaves);
    }

    /**
     *
     * @return
     */
    @Override
    public SaveProvider getSaveProvider() {
        return new SaveProvider(tleaves, new int[] {0, 1, 2, 3, 4, 5});
    }

    /**
     *
     * @return
     */
    @Override
    public Vectorer getVectorer() {
        return tleaves.getVectorerBasic(new int[]{2, 5});
    }

    /**
     *
     * @return
     */
    @Override
    public ComparatorCreator getComparatorCreator() {
        return tleaves.getComparatorCreator(new int[] {2, 3, 4, 5});
    }

    /**
     *
     * @return
     */
    @Override
    public ListCellRenderer getListCellRenderer() {
        return new ListCellRendererBasic(tleaves.getRenderStringBasic(new int[]{2}));
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
        return AppLocal.getIntString("Menu.Leaves");
    }
}
