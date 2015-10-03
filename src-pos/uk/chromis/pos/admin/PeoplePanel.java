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
package uk.chromis.pos.admin;

import java.util.logging.Level;
import java.util.logging.Logger;
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
 * @author adrianromero
 */
public class PeoplePanel extends JPanelTable {

    private TableDefinition tpeople;
    private PeopleView jeditor;
    private DataLogicAdmin dlAdmin;

    /**
     * Creates a new instance of JPanelPeople
     */
    public PeoplePanel() {
    }

    /**
     *
     */
    @Override
    protected void init() {
        dlAdmin = (DataLogicAdmin) app.getBean("uk.chromis.pos.admin.DataLogicAdmin");

        tpeople = dlAdmin.getTablePeople();
        jeditor = new PeopleView(dlAdmin, dirty, app);

        
        //this is part of a quick and dirty fix until, I can code another way to achive the required results
        //A static variable is being used in AppLocal until the solution is found.
        //It meant that changes had to added to toher class for this to work.
        //The user list is not update after a logoff\logon event after the first activation, until a restart.
        //Any help with this will be appreciated John l
        try {
            AppLocal.LIST_BY_RIGHTS = "select PEOPLE.ID, PEOPLE.NAME, APPPASSWORD, ROLE, VISIBLE, CARD,"
                    + "IMAGE from PEOPLE inner join roles on people.role=roles.id "
                    + "where roles.rightslevel <= " + dlAdmin.getRightsLevelByID(app.getAppUserView().getUser().getRole());
        } catch (BasicException ex) {
            Logger.getLogger(PeoplePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        getListProvider();

    }

    /**
     *
     * @return
     */
    @Override
    public ListProvider getListProvider() {
        return new ListProviderCreator(tpeople);
    }

    /**
     *
     * @return
     */
    @Override
    public SaveProvider getSaveProvider() {
        return new SaveProvider(tpeople);
    }

    /**
     *
     * @return
     */
    @Override
    public Vectorer getVectorer() {
        return tpeople.getVectorerBasic(new int[]{1});
    }

    /**
     *
     * @return
     */
    @Override
    public ComparatorCreator getComparatorCreator() {
        return tpeople.getComparatorCreator(new int[]{1, 3});
    }

    /**
     *
     * @return
     */
    @Override
    public ListCellRenderer getListCellRenderer() {
        return new ListCellRendererBasic(tpeople.getRenderStringBasic(new int[]{1}));
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
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {

        jeditor.activate(); // primero el editor    
        super.activate(); // y luego cargamos los datos
    }
    
    /**
     *
     * @return
     */
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Users");
    }
}
