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

package uk.chromis.pos.scheduler;

import java.util.Date;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.SerializableRead;
import uk.chromis.format.Formats;

/**
 *
 * @author John
 */
public class ScheduledTaskInfo implements SerializableRead {
    
    private static final long serialVersionUID = 9083257352541L;

    private String m_Name;
    private Date m_Start;
    private int m_Interval;
    private String m_Script;
    private boolean m_enabled;
    private String m_id;
    
    public ScheduledTaskInfo() {
        
    }
    
    /** Creates a new instance of UserInfoBasic
    *
     * @param name
     * @param start
     * @param interval
     * @param script */
    public ScheduledTaskInfo( String id, String name, Date start, int interval, String script, boolean enabled ) {
        m_Name = name;
        m_Start = start;
        m_Interval = interval;
        m_Script = script;
        m_enabled = enabled;
        m_id = id;
    }

     /**
     *
     * @param dr
     * @throws BasicException
     */
    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_id = dr.getString(DataLogicScheduler.INDEX_ID);
        m_Name = dr.getString(DataLogicScheduler.INDEX_NAME);
        m_Start = dr.getTimestamp(DataLogicScheduler.INDEX_START);
        m_Interval = dr.getInt(DataLogicScheduler.INDEX_INTERVAL);
        m_Script = dr.getString(DataLogicScheduler.INDEX_SCRIPT);  
        m_enabled = dr.getBoolean(DataLogicScheduler.INDEX_ENABLE);

    }   
    
    public boolean isEnabled() {
        return m_enabled;
    }  
    
    public String getName() {
        return m_Name;
    }  
    
    public Date getStart() {
        return m_Start;
    }  
    
    public int getInterval() {
        return m_Interval;
    }

    public String getScript() {
        return m_Script;
    }  
    
    public String printName() {
        return getName();
    }  
    
    public String printStart() {
        return Formats.DATE.formatValue(getStart());
    }  
    
    public String printInterval() {
        return Formats.INT.formatValue(getInterval());
    }  
    
    public String printScript() {
        return getScript();
    }  
}
