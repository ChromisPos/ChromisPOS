/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.admin;

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.IKeyed;
import uk.chromis.data.loader.SerializerRead;

/**
 *
 * @author john
 */
public class DBPermissionsInfo implements IKeyed {

    private static final long serialVersionUID  = 1L;
    private String m_displayName;
    private String m_className;
    private String m_section;
    private String m_description;

    public DBPermissionsInfo(String className, String section, String displayName, String description) {
        m_className = className;
        m_section = section;
        m_displayName = displayName;
        m_description = description;
    }

    @Override
    public Object getKey() {
        return m_className;
    }

    public String getClassName() {
        return m_className;
    }

    public void setClassName(String m_className) {
        this.m_className = m_className;
    }

    public String getSection() {
        return m_section;
    }

    public void setSection(String section) {
        this.m_section = section;
    }

    public String getDisplayName() {
        return m_displayName;
    }

    public void setDisplayName(String displayName) {
        this.m_displayName = displayName;
    }

    public String getDescription() {
        return m_description;
    }

    public void setDescription(String description) {
        this.m_description = description;
    }
        
    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new DBPermissionsInfo(dr.getString(1), dr.getString(2), dr.getString(3), dr.getString(4));
            }
        };
    }

}
