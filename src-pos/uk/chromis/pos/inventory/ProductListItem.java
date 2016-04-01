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

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.IKeyed;
import uk.chromis.data.loader.SerializableRead;

/**
 *
 * @author adrianromero
 * Created on February 13, 2007, 10:13 AM
 *
 */
public class ProductListItem implements SerializableRead, IKeyed {
    
    private static final long serialVersionUID = 9032683595445L;
    private String m_sName;
    private String m_sProduct;
    private String m_sReference;
    
    /** Creates a new instance of LocationInfo */
    public ProductListItem() {
        m_sName = null;
        m_sProduct = null;
        m_sReference = null;
    }
    
    /**
     *
     * @return
     */
    public Object getKey() {
        return m_sProduct;
    }

    /**
     *
     * @param dr
     * @throws BasicException
     */
    public void readValues(DataRead dr) throws BasicException {
        m_sProduct = dr.getString(1);
        m_sReference = dr.getString(2);
        m_sName = dr.getString(3);
    }

    /**
     *
     * @param sID
     */
    public void setID(String sID) {
        m_sProduct = sID;
    }
    
    /**
     *
     * @return
     */
    public String getID() {
        return m_sProduct;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return m_sName;
    }
    
    /**
     *
     * @param sName
     */
    public void setName(String sName) {
        m_sName = sName;
    }  

    /**
     *
     * @return
     */
    public String getReference() {
        return m_sReference;
    }
    
    /**
     *
     * @param sReference
     */
    public void setReference(String sReference) {
        m_sReference = sReference;
    }  

    
    /**
     *
     * @return
     */
    public String getProduct() {
        return m_sProduct;
    }
    
    /**
     *
     * @param sName
     */
    public void setProduct(String sName) {
        m_sProduct = sName;
    }  
    
    public String toString(){
        return m_sReference + "-" + m_sName;
    }    
}
