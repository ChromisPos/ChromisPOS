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

package uk.chromis.pos.scheduler;

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SentenceExec;
import uk.chromis.data.loader.SentenceExecTransaction;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerRead;
import uk.chromis.data.loader.SerializerReadString;
import uk.chromis.data.loader.SerializerWriteBasicExt;
import uk.chromis.data.loader.Session;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.data.model.Field;
import uk.chromis.data.model.Row;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.BeanFactoryDataSingle;

public class DataLogicScheduler extends BeanFactoryDataSingle {
    
    protected Session m_session;
    
    protected Row m_TaskRow;
    protected Row m_ScriptRow;
    
    // These next arrays are order dependant.
    // Use the provided getIndexOf and namesToIndexes functions
    // to access the columns, do not hard code array indexes.
    // If you add a new field, all arrays need a new entry.
    protected String[] m_FieldNames = new String[] {
                "ID", "NAME", "STARTDATE", "TIMEINTERVAL", "SCRIPT", "ISENABLED"
            };          
    
    protected static int INDEX_ID = 0;
    protected static int INDEX_NAME = 1;
    protected static int INDEX_START = 2;
    protected static int INDEX_INTERVAL = 3;
    protected static int INDEX_SCRIPT = 4;
    protected static int INDEX_ENABLE = 5;
    protected static int FIELD_COUNT = 6;
    
    private final Datas[] m_FieldDataTypes = new Datas[] 
        {Datas.STRING, Datas.STRING, Datas.TIMESTAMP,
                Datas.INT, Datas.SERIALIZABLE, Datas.BOOLEAN
            };
    
    private final Formats[] m_FieldFormat = 
            new Formats[] {Formats.STRING, Formats.STRING, Formats.DATE,
                Formats.INT, Formats.STRING, Formats.BOOLEAN
            };
    
    /** Creates a new instance of DataLogicPromotions */
    public DataLogicScheduler() {  
        m_TaskRow = new Row(
                new Field(m_FieldNames[0], m_FieldDataTypes[0], m_FieldFormat[0] ), 
                new Field(m_FieldNames[1], m_FieldDataTypes[1], m_FieldFormat[1], true, true, true ), 
                new Field(m_FieldNames[2], m_FieldDataTypes[2], m_FieldFormat[2] ), 
                new Field(m_FieldNames[3], m_FieldDataTypes[3], m_FieldFormat[3] ), 
                new Field(m_FieldNames[4], m_FieldDataTypes[4], m_FieldFormat[4] ),
                new Field(m_FieldNames[5], m_FieldDataTypes[5], m_FieldFormat[5] )
                );
    }
    
    /**
     *
     * @param session
     */
    @Override
    public void init(Session session){
 
        this.m_session = session;
        
    }
 
    // Find the column number of the given field
    public int getIndexOf( CharSequence fieldname ) {
        int i = m_FieldNames.length - 1;
        while( i > 0 && !m_FieldNames[i].contentEquals(fieldname)) {
            // Still looking
            --i;
        }
        return i;
    }
    
     // Find the column numbers of the given fields
    public int[] namesToIndexes( CharSequence[] fieldnames ) {
        int[] indexes = new int[ fieldnames.length ];
        
        for( int i = 0; i < fieldnames.length; ++i ){
            indexes[i] = getIndexOf( fieldnames[i] );
        }
        return indexes;
    }
    
     // Find the Datas of the given fields
    public Datas[] namesToDatas( CharSequence[] fieldnames ) {
        Datas[] datas = new Datas[ fieldnames.length ];
        
        for( int i = 0; i < fieldnames.length; ++i ){
            datas[i] = m_FieldDataTypes[ getIndexOf( fieldnames[i] ) ];
        }
        return datas;
    }
    
    // Find the format for the given field
    public Formats getFormatOf( int fieldindex ) {
        return m_FieldFormat[fieldindex];
    }
    
        // Find the Datas for the given field
    public Datas getDatasOf( int fieldindex ) {
        return m_FieldDataTypes[fieldindex];
    }
 
     /**
     *
     * @return
     */
    public final Row getRow() {
        return m_TaskRow;
    }
    
    /**
     *
     * @return
     */
    public final int getFieldCount() {
        return m_FieldNames.length;
    }
    
    public final SentenceList getResourceScriptListSentence( ) {
        
        return new StaticSentence( m_session, 
                "SELECT NAME FROM RESOURCES WHERE NAME LIKE 'task.%' ORDER BY NAME "
                , null
                , SerializerReadString.INSTANCE);          
             
    }
    
    public final PreparedSentence getListSentence() {
 	return new PreparedSentence(m_session,
            "SELECT ID, NAME, STARTDATE, TIMEINTERVAL, SCRIPT, ISENABLED FROM SCHEDULEDTASKS",
            null,
            m_TaskRow.getSerializerRead()
        );
    }

    /**
     *
     * @return
     */
    public final SentenceExec getInsertSentence() {
        return new SentenceExecTransaction(m_session) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                String[] fields = new String[] {
                            "ID", "NAME", "STARTDATE", "TIMEINTERVAL",
                            "SCRIPT", "ISENABLED"
                        };
                
		return new PreparedSentence(m_session
                    , "INSERT INTO SCHEDULEDTASKS ( "
                    + "ID, NAME, STARTDATE, TIMEINTERVAL, SCRIPT, ISENABLED ) "
                    + "VALUES (?, ?, ?, ?, ?, ?)", 
                    new SerializerWriteBasicExt(
                            m_TaskRow.getDatas(), namesToIndexes( fields ) )
                         ).exec(params);
            }
        };
    }
    
    /**
     *
     * @return
     */
    public final SentenceExec getUpdateSentence() {
        return new SentenceExecTransaction(m_session) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                String[] fields = new String[] {
                            "NAME", "STARTDATE", "TIMEINTERVAL",
                            "SCRIPT", "ISENABLED", "ID"
                        };
                
		return new PreparedSentence(m_session
                   , "UPDATE SCHEDULEDTASKS SET "
                    + "NAME = ?, STARTDATE = ?, "
                    + "TIMEINTERVAL = ?, SCRIPT = ?, ISENABLED = ? "
                    + "WHERE ID = ? ",
                    new SerializerWriteBasicExt(
                            m_TaskRow.getDatas(), namesToIndexes( fields ) )
                         ).exec(params);
            }
        };
    }

    /**
     *
     * @return
     */
    public final SentenceExec getDeleteSentence() {
        return new SentenceExecTransaction(m_session) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                String[] fields =  new String[] { "ID" };

                return new PreparedSentence(m_session
                    , "DELETE FROM SCHEDULEDTASKS WHERE ID = ?"
                    , new SerializerWriteBasicExt(
                             m_TaskRow.getDatas(), namesToIndexes( fields ) )
                    ).exec(params);
            }
        };
    }

    /**
     *
     * @return
     */
    public static SerializerRead getScheduledTaskSerializerRead() {
        return new SerializerRead() {@Override
        public Object readValues(DataRead dr) throws BasicException {
            
            return new ScheduledTaskInfo(
                    dr.getString(INDEX_ID+1), dr.getString(INDEX_NAME+1),
                    dr.getTimestamp( INDEX_START+1 ),
                    dr.getInt( INDEX_INTERVAL+1 ),
                    Formats.STRING.formatValue( Datas.SERIALIZABLE.getValue(dr, INDEX_SCRIPT+1) ),
                    dr.getBoolean(INDEX_ENABLE+1) );
            }
        };
    }    

}

