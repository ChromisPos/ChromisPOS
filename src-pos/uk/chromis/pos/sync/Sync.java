//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) 2016 - John Lewis
//    http://www.chromis.co.uk
//
//    This file is part of Chromis POS
//
//    Chromis POS is free software: you can redistribute it and/or modify
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
//    
package uk.chromis.pos.sync;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.apache.commons.lang.SerializationUtils;
import uk.chromis.basic.BasicException;

/**
 * @author John Lewis
 */
public class Sync {

    private String SQL2;
    private Statement stmt;
    private ResultSet rs;
    private ResultSet rs2;
    private PreparedStatement pstmt;
    private ObjectInputStream ois;
    private ArrayList<Object> list;

    public void Sync() {

    }

    public void processSyncData(Connection connection, String localGUID, String remoteGUID) {
        try {
            stmt = (Statement) connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery("SELECT * FROM SYNCDATA ORDER BY CHANGEDATE");
            while (rs.next()) {
                switch (rs.getString("ACTION")) {
                    case "DELETE":
                        if (deleteSQLRow(connection, localGUID, remoteGUID, rs)) {
                            rs.updateInt("PROCESSED", 1);
                            rs.updateRow();
                            break;
                        }
                    case "INSERT":
                        if (insertSQLRow(connection, localGUID, remoteGUID, rs)) {
                            rs.updateInt("PROCESSED", 1);
                            rs.updateRow();
                            break;
                        }
                    case "UPDATE":
                        if (updateSQLRow(connection, localGUID, remoteGUID, rs)) {
                            rs.updateInt("PROCESSED", 1);
                            rs.updateRow();
                            break;
                        }
                }
            }

            PreparedStatement pSQL;
            pSQL = connection.prepareStatement("DELETE FROM SYNCDATA WHERE PROCESSED = true");
            pSQL.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Boolean insertSQLRow(Connection connection, String localGUID, String remoteGUID, ResultSet rs) {
        StringBuilder newSQL = new StringBuilder();
        StringBuilder sql = new StringBuilder();
        ArrayList<Object> values = new ArrayList();
        try {
            sql.append("SELECT * FROM ");
            sql.append(rs.getString("CHANGEDTABLE"));
            sql.append(" WHERE ");
            sql.append(rs.getString("IDCOLUMNNAME"));
            sql.append(" = '");
            sql.append(rs.getString("IDKEY"));
            if (rs.getString("IDKEY2") != null) {
                sql.append("' AND ");
                sql.append(rs.getString("ID2COLUMNNAME"));
                sql.append(" = '");
                sql.append(rs.getString("IDKEY2"));
            }

            if (rs.getString("IDKEY3") != null) {
                sql.append(" AND ");
                sql.append(rs.getString("ID3COLUMNNAME"));
                sql.append(" = ?");
                values.add(rs.getString("IDKEY3"));
            }
            sql.append("' AND SITEGUID = '");
            sql.append(rs.getString("SITEGUID"));
            sql.append("'");

            newSQL.append("INSERT INTO ");
            newSQL.append(rs.getString("CHANGEDTABLE"));
            newSQL.append(" (");

            stmt = (Statement) connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs2 = stmt.executeQuery(sql.toString());
            while (rs2.next()) {
                ResultSetMetaData rsmd = rs2.getMetaData();
                int cols = rs2.getMetaData().getColumnCount();
                for (int i = 0; i < cols; i++) {
                    if (i > 0) {
                        newSQL.append(", ");
                    }
                    newSQL.append(rsmd.getColumnName(i + 1));
                    values.add(rs2.getObject(i + 1));
                }
                newSQL.append(") VALUES ( ");
                for (int i = 0; i < cols; i++) {
                    if (i > 0) {
                        newSQL.append(", ");
                    }
                    newSQL.append("?");
                }
                newSQL.append(")");
            }

            return changedObjectInsert(connection, rs.getInt("ID"), localGUID, remoteGUID, newSQL.toString(), SerializationUtils.serialize(values));
        } catch (SQLException | BasicException ex) {
            Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    private Boolean updateSQLRow(Connection connection, String localGUID, String remoteGUID, ResultSet rs) {
        StringBuilder newSQL = new StringBuilder();
        StringBuilder sql = new StringBuilder();
        ArrayList<Object> values = new ArrayList();
        try {
            sql.append("SELECT * FROM ");
            sql.append(rs.getString("CHANGEDTABLE"));
            sql.append(" WHERE ");
            sql.append(rs.getString("IDCOLUMNNAME"));
            sql.append(" = '");
            sql.append(rs.getString("IDKEY"));

            if (rs.getString("IDKEY2") != null) {
                sql.append("' AND ");
                sql.append(rs.getString("ID2COLUMNNAME"));
                sql.append(" = '");
                sql.append(rs.getString("IDKEY2"));
            }

            if (rs.getString("IDKEY3") != null) {
                sql.append(" AND ");
                sql.append(rs.getString("ID3COLUMNNAME"));
                sql.append(" = ?");
                values.add(rs.getString("IDKEY3"));
            }

            sql.append("' AND SITEGUID = '");
            sql.append(rs.getString("SITEGUID"));
            sql.append("'");

            newSQL.append("UPDATE ");
            newSQL.append(rs.getString("CHANGEDTABLE"));
            newSQL.append(" SET ");

            stmt = (Statement) connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs2 = stmt.executeQuery(sql.toString());
            while (rs2.next()) {
                ResultSetMetaData rsmd = rs2.getMetaData();
                int cols = rs2.getMetaData().getColumnCount();
                for (int i = 0; i < cols; i++) {
                    if ((!rsmd.getColumnName(i + 1).equalsIgnoreCase(rs.getString("IDCOLUMNNAME"))) && (!rsmd.getColumnName(i + 1).equalsIgnoreCase("SITEGUID"))) {
                        if (i > 1) {
                            newSQL.append(", ");
                        }
                        newSQL.append(rsmd.getColumnName(i + 1));
                        newSQL.append(" = ?");
                        values.add(rs2.getObject(i + 1));
                    }
                }
            }
            newSQL.append(" WHERE ");
            newSQL.append(rs.getString("IDCOLUMNNAME"));
            newSQL.append(" = '");
            newSQL.append(rs.getString("IDKEY"));
            newSQL.append("' AND SITEGUID = '");
            newSQL.append(rs.getString("SITEGUID"));
            newSQL.append("'");
            return changedObjectInsert(connection, rs.getInt("ID"), localGUID, remoteGUID, newSQL.toString(), SerializationUtils.serialize(values));
        } catch (SQLException | BasicException ex) {
            Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private Boolean deleteSQLRow(Connection connection, String localGUID, String remoteGUID, ResultSet rs) {
        StringBuilder sql = new StringBuilder();
        ArrayList<Object> values = new ArrayList();
        
        try {
            values.add(rs.getString("IDKEY"));
            values.add(rs.getString("SITEGUID"));

            sql.append("DELETE FROM ");
            sql.append(rs.getString("CHANGEDTABLE"));
            sql.append(" WHERE ");
            sql.append(rs.getString("IDCOLUMNNAME"));
            sql.append(" = ? ");
            if (rs.getString("IDKEY2") != null) {
                sql.append(" AND ");
                sql.append(rs.getString("ID2COLUMNNAME"));
                sql.append(" = ?");
                values.add(rs.getString("IDKEY2"));
            }

            if (rs.getString("IDKEY3") != null) {
                sql.append(" AND ");
                sql.append(rs.getString("ID3COLUMNNAME"));
                sql.append(" = ?");
                values.add(rs.getString("IDKEY3"));
            }

            sql.append(" AND SITEGUID = ?");

            return changedObjectInsert(connection, rs.getInt("ID"), localGUID, remoteGUID, sql.toString(), SerializationUtils.serialize(values));
        } catch (BasicException | SQLException ex) {
            Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void processChangedObjects(Connection connection, String localGUID) {

        try {           
            pstmt = connection.prepareStatement("SELECT * FROM CHANGEDOBJECT WHERE TARGETSITE = ? AND CHANGESTATUS = FALSE ORDER BY EVENTORDER");
            pstmt.setString(1, localGUID);
            //stmt = (Statement) connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                try {
                    ois = new ObjectInputStream(new ByteArrayInputStream(rs.getBytes("CHANGEDATA")));
                    SQL2 = rs.getString("SQLCMD");
                    list = (ArrayList<Object>) ois.readObject();
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        ois.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                pstmt = connection.prepareStatement(SQL2);
                int i;
                for (i = 0; i < list.size(); i++) {
                    if (list.get(i) != null) {
                        //   System.out.println("Class : " + list.get(i).getClass().toString());
                        switch (list.get(i).getClass().toString()) {
                            case "class java.lang.String":
                                pstmt.setString(i + 1, (String) list.get(i));
                                break;
                            case "class java.lang.Boolean":
                                pstmt.setBoolean(i + 1, (Boolean) list.get(i));
                                break;
                            case "class java.lang.Integer":
                                pstmt.setInt(i + 1, (Integer) list.get(i));
                                break;
                            case "class java.lang.Double":
                                pstmt.setDouble(i + 1, (Double) list.get(i));
                                break;
                            case "class java.lang.Byte":
                            case "class [B":
                                pstmt.setBytes(i + 1, (byte[]) list.get(i));
                                break;
                            case "class java.sql.Timestamp":
                                pstmt.setTimestamp(i + 1, (Timestamp) list.get(i));
                                break;
                            case "class javax.swing.ImageIcon":
                                ImageIcon image = new ImageIcon();
                                image = (ImageIcon) list.get(i);
                                BufferedImage bi = new BufferedImage(
                                        image.getIconWidth(),
                                        image.getIconHeight(),
                                        BufferedImage.TYPE_INT_ARGB);
                                Graphics g = bi.createGraphics();
                                image.paintIcon(null, g, 0, 0);
                                g.dispose();
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                ImageIO.write(bi, "png", out);
                                byte[] buf = out.toByteArray();
                                pstmt.setBytes(i + 1, buf);
                                break;
                            default:

                                break;
                        }
                    } else {
                        pstmt.setString(i + 1, null);
                    }
                }

                // disable the triggers
                PreparedStatement pstmt2 = connection.prepareStatement("SET @DISABLE_TRIGGER = true;");
                pstmt2.executeUpdate();

                //execute the sqlcmd
                pstmt.executeUpdate();

                // enable the triggers
                pstmt2 = connection.prepareStatement("SET @DISABLE_TRIGGER = null;");
                pstmt2.executeUpdate();

                pstmt = connection.prepareStatement("UPDATE CHANGEDOBJECT SET CHANGESTATUS = true WHERE ID='" + rs.getString("ID") + "'");
                pstmt.executeUpdate();
            }
        } catch (SQLException | IOException ex) {
            //ensure that the triggers are enabled even after error
            PreparedStatement pstmt2;
            try {
                pstmt2 = connection.prepareStatement("SET @DISABLE_TRIGGER = null");
                pstmt2.executeUpdate();
            } catch (SQLException ex1) {
                Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void syncSites(Connection localConnection, Connection remoteConnection, String targetSite, String localGUID) {
        // Transfer any data form changed object table to remote server                
        PreparedStatement pstmt;
        PreparedStatement pstmt2;
        ResultSet rs2;
        try {
            pstmt = localConnection.prepareStatement("SELECT * FROM CHANGEDOBJECT WHERE TARGETSITE = ? AND TRANSFERSTATUS = false ORDER BY EVENTORDER ");
            pstmt.setString(1, targetSite);
            rs2 = pstmt.executeQuery();

            while (rs2.next()) {
                if (changedObjectSync(remoteConnection, rs2)) {
                    pstmt2 = localConnection.prepareStatement("UPDATE CHANGEDOBJECT SET TRANSFERSTATUS = TRUE WHERE ID = ?");
                    pstmt2.setString(1, rs2.getString("ID"));
                    pstmt2.executeUpdate();
                }
            }
        } catch (SQLException | BasicException ex) {
            Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
        }

        // check if there are any records for this location on remote server          
        try {
            pstmt = remoteConnection.prepareStatement("SELECT * FROM CHANGEDOBJECT WHERE TARGETSITE = ? AND TRANSFERSTATUS = false ORDER BY EVENTORDER ");
            pstmt.setString(1, localGUID);
            rs2 = pstmt.executeQuery();

            while (rs2.next()) {
                if (changedObjectSync(localConnection, rs2)) {
                    pstmt2 = remoteConnection.prepareStatement("UPDATE CHANGEDOBJECT SET TRANSFERSTATUS = TRUE WHERE ID = ?");
                    pstmt2.setString(1, rs2.getString("ID"));
                    pstmt2.executeUpdate();
                }
            }
        } catch (SQLException | BasicException ex) {
            Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Boolean changedObjectInsert(Connection connection, Integer eventOrder, String localGUID, String remoteGUID, String sql, byte[] object) throws BasicException {
        // Check if site is enabled for database sync, if so carry out the transaction
        // Get this site number
        // Get central site details        
        try {
            pstmt = connection.prepareStatement("INSERT INTO CHANGEDOBJECT (ID, SOURCESITE, TARGETSITE, SQLCMD, CHANGEDATA, CHANGESTATUS, TRANSFERSTATUS, EVENTORDER) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, localGUID);
            pstmt.setString(3, remoteGUID);
            pstmt.setString(4, sql);
            pstmt.setBytes(5, object);
            pstmt.setBoolean(6, false);
            pstmt.setBoolean(7, false);
            pstmt.setInt(8, eventOrder);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private Boolean changedObjectSync(Connection connection, ResultSet rsSet) throws BasicException {
        try {
            pstmt = connection.prepareStatement("INSERT INTO CHANGEDOBJECT (ID, SOURCESITE, TARGETSITE, SQLCMD, CHANGEDATE, CHANGEDATA, CHANGESTATUS, TRANSFERSTATUS, EVENTORDER) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, rsSet.getString("ID"));
            pstmt.setString(2, rsSet.getString("SOURCESITE"));
            pstmt.setString(3, rsSet.getString("TARGETSITE"));
            pstmt.setString(4, rsSet.getString("SQLCMD"));
            pstmt.setTimestamp(5, rsSet.getTimestamp("CHANGEDATE"));
            pstmt.setBytes(6, rsSet.getBytes("CHANGEDATA"));
            pstmt.setBoolean(7, rsSet.getBoolean("CHANGESTATUS"));
            pstmt.setBoolean(8, true);
            pstmt.setInt(9, rsSet.getInt("EVENTORDER"));
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("UPDATE CHANGEDOBJECT SET TRANSFERSTATUS = true WHERE ID='" + rsSet.getString("ID") + "'");
            pstmt.executeUpdate();
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
