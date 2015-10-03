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

package uk.chromis.pos.panels;

import javax.swing.ListCellRenderer;
import uk.chromis.data.loader.ComparatorCreator;
import uk.chromis.data.loader.Vectorer;
import uk.chromis.data.model.Row;
import uk.chromis.data.user.ListProvider;
import uk.chromis.data.user.SaveProvider;

/**
 *
 * @author adrianromero
 */
public abstract class JPanelTable2 extends JPanelTable {
   
    /**
     *
     */
    protected Row row;

    /**
     *
     */
    protected ListProvider lpr;

    /**
     *
     */
    protected SaveProvider spr;

    /**
     *
     * @return
     */
    @Override
    public final ListProvider getListProvider() {
        return lpr;
    }

    /**
     *
     * @return
     */
    @Override
    public final SaveProvider getSaveProvider() {
        return spr;
    }
    
    /**
     *
     * @return
     */
    @Override
    public final Vectorer getVectorer() {
        return row.getVectorer();
    }
    
    /**
     *
     * @return
     */
    @Override
    public final ComparatorCreator getComparatorCreator() {
        return row.getComparatorCreator();
    }
    
    /**
     *
     * @return
     */
    @Override
    public final ListCellRenderer getListCellRenderer() {
        return row.getListCellRenderer();
    } 
}
