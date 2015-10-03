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

package uk.chromis.pos.catalog;

import java.awt.Component;
import java.awt.event.ActionListener;
import uk.chromis.basic.BasicException;

/**
 *
 * @author adrianromero
 */
public interface CatalogSelector {
    
    /**
     *
     * @throws BasicException
     */
    public void loadCatalog() throws BasicException;

    /**
     *
     * @param id
     */
    public void showCatalogPanel(String id);

    /**
     *
     * @param value
     */
    public void setComponentEnabled(boolean value);

    /**
     *
     * @return
     */
    public Component getComponent();
    
    /**
     *
     * @param l
     */
    public void addActionListener(ActionListener l);  

    /**
     *
     * @param l
     */
    public void removeActionListener(ActionListener l);    
}
