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

package uk.chromis.data.loader;

import uk.chromis.basic.BasicException;
import java.util.List;

/**
 *
 *   
 */
public interface SentenceList {
    
    /**
     *
     * @return
     * @throws BasicException
     */
    public List list() throws BasicException;

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    public List list(Object... params) throws BasicException;

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    public List list(Object params) throws BasicException;
    
    /**
     *
     * @param offset
     * @param length
     * @return
     * @throws BasicException
     */
    public List listPage(int offset, int length) throws BasicException;

    /**
     *
     * @param params
     * @param offset
     * @param length
     * @return
     * @throws BasicException
     */
    public List listPage(Object params, int offset, int length) throws BasicException;    
}
