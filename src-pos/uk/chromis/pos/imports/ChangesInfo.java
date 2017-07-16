//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) (c) 2015-2017
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

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.SerializableRead;

/**
 *
 * @author John
 */
public class ChangesInfo implements SerializableRead {
    
    private static final long serialVersionUID = 9083257536541L;

    private String m_ProductName;
    private String m_Field;
    private String m_Change;
    private String m_Value;

    public ChangesInfo() {
        
    }
    
    /** Creates a new instance of UserInfoBasic
    * */
    public ChangesInfo( String productName, String field, String change, String value ) {
        m_ProductName = productName;
        m_Field = field;
        m_Change = change;
        m_Value = value;
    }

     /**
     *
     * @param dr
     * @throws BasicException
     */
    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_ProductName = dr.getString(1);
        m_Field = dr.getString(2);
        m_Change = dr.getString(3);
        m_Value = dr.getString(4);  

    }   
    
    public String printProductName() {
        return m_ProductName;
    }  
    
    public String printField() {
        return m_Field;
    }  
    
    public String printChange() {
        return m_Change;
    }  
    
    public String printValue() {
        return m_Value;
    }  
}
