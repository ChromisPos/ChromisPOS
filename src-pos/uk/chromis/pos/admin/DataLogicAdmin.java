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

import java.util.List;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SentenceExec;
import uk.chromis.data.loader.SentenceFind;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerReadBytes;
import uk.chromis.data.loader.SerializerReadClass;
import uk.chromis.data.loader.SerializerReadString;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.data.loader.SerializerWriteString;
import uk.chromis.data.loader.Session;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.data.loader.TableDefinition;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.BeanFactoryDataSingle;

/**
 *
 * @author adrianromero
 */
public class DataLogicAdmin extends BeanFactoryDataSingle {
    
    private Session s;
    private TableDefinition m_tpeople;
    private TableDefinition m_troles;
    private TableDefinition m_tresources;    
    protected SentenceList m_sectionList; 
    protected SentenceList m_rolesList;
    protected SentenceFind m_description;
    protected SentenceList m_displayList;
    protected SentenceFind m_roleID;
    protected SentenceFind m_roleRightsLevel;
    protected SentenceFind m_roleRightsLevelByID;
    protected SentenceFind m_roleRightsLevelByUserName;
    protected SentenceExec m_insertentry;
    private SentenceFind m_rolepermissions; 
    protected SentenceExec m_rolepermissionsdelete;
    protected SentenceList m_permissionClassList;
    
    /** Creates a new instance of DataLogicAdmin */
    public DataLogicAdmin() {
    }
    
    /**
     *
     * @param s
     */
    @Override
    public void init(Session s){
        this.s = s;
        
        m_tpeople = new TableDefinition(s,
            "PEOPLE"
            , new String[] {"ID", "NAME", "APPPASSWORD", "ROLE", "VISIBLE", "CARD", "IMAGE"}
            , new String[] {"ID", AppLocal.getIntString("label.peoplename"), AppLocal.getIntString("Label.Password"), AppLocal.getIntString("label.role"), AppLocal.getIntString("label.peoplevisible"), AppLocal.getIntString("label.card"), AppLocal.getIntString("label.peopleimage")}
            , new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.BOOLEAN, Datas.STRING, Datas.IMAGE}
            , new Formats[] {Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.BOOLEAN, Formats.STRING, Formats.NULL}
            , new int[] {0}
        );   
                        
        m_troles = new TableDefinition(s,
            "ROLES"
            , new String[] {"ID", "NAME", "PERMISSIONS", "RIGHTSLEVEL"}
            , new String[] {"ID", AppLocal.getIntString("Label.Name"), "PERMISSIONS"}
            , new Datas[] {Datas.STRING, Datas.STRING, Datas.BYTES, Datas.INT}
            , new Formats[] {Formats.STRING, Formats.STRING, Formats.NULL, Formats.INT }  
            , new int[] {0}
        );  
        
        m_tresources = new TableDefinition(s,
            "RESOURCES"
            , new String[] {"ID", "NAME", "RESTYPE", "CONTENT"}
            , new String[] {"ID", AppLocal.getIntString("Label.Name"), AppLocal.getIntString("label.type"), "CONTENT"}
            , new Datas[] {Datas.STRING, Datas.STRING, Datas.INT, Datas.BYTES}
            , new Formats[] {Formats.STRING, Formats.STRING, Formats.INT, Formats.NULL}
            , new int[] {0}
        );           
  
        
        m_sectionList = new StaticSentence(s, 
                "SELECT DISTINCT SECTION FROM DBPERMISSIONS ORDER BY SECTION"
                , null
                , SerializerReadString.INSTANCE);          
             
        m_description = new StaticSentence(s
                , "SELECT DESCRIPTION FROM DBPERMISSIONS WHERE CLASSNAME = ? "
                , SerializerWriteString.INSTANCE
                , SerializerReadString.INSTANCE);  
        
        m_displayList = new StaticSentence(s
                , "SELECT DISPLAYNAME FROM DBPERMISSIONS WHERE SECTION = ? ORDER BY DISPLAYNAME"
                , SerializerWriteString.INSTANCE
                , SerializerReadString.INSTANCE);          
        
        m_roleID = new StaticSentence(s
                , "SELECT ID FROM ROLES WHERE NAME = ? "
                , SerializerWriteString.INSTANCE
                , SerializerReadString.INSTANCE);  
        
        m_roleRightsLevel = new StaticSentence(s
                , "SELECT RIGHTSLEVEL FROM ROLES WHERE NAME = ? "
                , SerializerWriteString.INSTANCE
                , SerializerReadString.INSTANCE); 

        m_roleRightsLevelByID = new StaticSentence(s
                , "SELECT RIGHTSLEVEL FROM ROLES WHERE ID = ? "
                , SerializerWriteString.INSTANCE
                , SerializerReadString.INSTANCE); 
      
        m_roleRightsLevelByUserName = new StaticSentence(s
                , "SELECT ROLES.RIGHTSLEVEL FROM ROLES INNER JOIN PEOPLE ON PEOPLE.ROLE=ROLES.ID WHERE PEOPLE.NAME= ? "
                , SerializerWriteString.INSTANCE
                , SerializerReadString.INSTANCE);      
         
        m_rolesList = new StaticSentence(s
                , "SELECT ID FROM ROLES "
                , null
                , SerializerReadString.INSTANCE);              
        

        m_permissionClassList = new StaticSentence(s
                , "SELECT CLASSNAME FROM DBPERMISSIONS "
                , null
                , SerializerReadString.INSTANCE);      
        
        
        m_rolepermissions = new PreparedSentence(s, "SELECT PERMISSIONS FROM ROLES WHERE ID = ?"
                , SerializerWriteString.INSTANCE
                , SerializerReadBytes.INSTANCE);      
        
               
        m_insertentry = new StaticSentence(s
                , "INSERT INTO DBPERMISSIONS (CLASSNAME, SECTION, DISPLAYNAME, DESCRIPTION) " +
                  "VALUES (?, ?, ?, ?)"
                , new SerializerWriteBasic(new Datas[] {
                    Datas.STRING, 
                    Datas.STRING, 
                    Datas.STRING, 
                    Datas.STRING,}));   
        
        
        m_rolepermissionsdelete = new StaticSentence(s, "DELETE * FROM DBPERMISSIONS WHERE CLASSNAME = ?"
                , SerializerWriteString.INSTANCE
                , null); 
        
    }
    /*
     *
     * @return
     */
    public final SentenceList getRolesList(String rightsLevel) {
        String sql = "SELECT ID, NAME FROM ROLES WHERE RIGHTSLEVEL <= " + rightsLevel + " ORDER BY NAME ";
        return new StaticSentence(s
            , sql
            , null
            , new SerializerReadClass(RoleInfo.class));
    }

    /*
     *
     * @return
     */
     public final List<DBPermissionsInfo> getAlldbPermissions() throws BasicException  {
	return new PreparedSentence(s
		, "SELECT CLASSNAME, SECTION, DISPLAYNAME, DESCRIPTION FROM DBPERMISSIONS ORDER BY DISPLAYNAME"
		, null               
		, DBPermissionsInfo.getSerializerRead()).list();
    }  
           
     /*
     *
     * @return
     */
    public final TableDefinition getTablePeople() {
        return m_tpeople;
    }    

    /**
     *
     * @return
     */
    public final TableDefinition getTableRoles() {
        return m_troles;
    }

    /*
     *
     * @return
     */
    public final TableDefinition getTableResources() {
        return m_tresources;
    }
    
    /**
     *
     * @param sRole
     * @return
     */
    public final String findRolePermissions(String sRole) {    
        try {
            return Formats.BYTEA.formatValue(m_rolepermissions.find(sRole));        
        } catch (BasicException e) {
            return null;                    
        }             
    }
    
    /**
     *
     * @return
     */
    public final SentenceList getPeopleList() {
        return new StaticSentence(s
                , "SELECT ID, NAME FROM PEOPLE ORDER BY NAME"
                , null
                , new SerializerReadClass(PeopleInfo.class));
    }
    
    public final List<String> getSectionsList() throws BasicException {
        return m_sectionList.list();    
}
      
    public final List<String> getDisplayList(String section) throws BasicException {
        return m_displayList.list(section);    
    }
     
    public final String getDescription(String className) throws BasicException {
        return m_description.find(className).toString();    
    }
  
    public final List<String> getRoles() throws BasicException {
        return m_rolesList.list();
    }        
        
    public final List<String> getClassNames() throws BasicException {
        return m_permissionClassList.list();
    }     

    public final String getRoleID(String roleName) throws BasicException {
        return m_roleID.find(roleName).toString();
    }    

    public final Integer getRightsLevel(String roleName) throws BasicException {
        return Integer.parseInt(m_roleRightsLevel.find(roleName).toString());
    } 
  
    public final String getRightsLevelByID(String roleName) throws BasicException {
        return m_roleRightsLevelByID.find(roleName).toString();
    }
    
     public final String getRightsLevelByUserName(String userName) throws BasicException {
        return m_roleRightsLevelByUserName.find(userName).toString();
    }   
    
    
    
    public final void insertEntry(Object[] entry) throws BasicException {
        m_insertentry.exec(entry);
    }
    
    public final void deleteEntry(String entry) throws BasicException {
        m_rolepermissionsdelete.exec(entry);
    }  
  
 }
