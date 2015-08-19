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

package net.proteanit.sql;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 *   
 */
public class DbUtils {

    /**
     *
     * @param rs
     * @return
     */
    public static TableModel resultSetToTableModel(ResultSet rs) {
         try {
             ResultSetMetaData metaData = rs.getMetaData();
             int numberOfColumns = metaData.getColumnCount();
             Vector columnNames = new Vector();
 
            // Get the column names
             for (int column = 0; column < numberOfColumns; column++) {
                 columnNames.addElement(metaData.getColumnLabel(column + 1));
             }

             // Get all rows.
             Vector rows = new Vector();
 
            while (rs.next()) {
                 Vector newRow = new Vector();
 
                for (int i = 1; i <= numberOfColumns; i++) {
                     newRow.addElement(rs.getObject(i));
                 }

                 rows.addElement(newRow);
             }

             return new DefaultTableModel(rows, columnNames);
         } catch (Exception e) {
             e.printStackTrace();

             return null;
         }
     }    
 }

