package wifi.dao;

import wifi.dto.WifiDTO;
import wifi.dto.SearchDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.util.Properties;



import java.io.IOException;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class WifiDAO {
	  private static final String DB_URL = "jdbc:mariadb://localhost:3306/wifi";
	    private static final String DB_USER = "wifi";
	    private static final String DB_PASSWORD = "wifi"; 

    static {
        try {
        	  Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS WIFI_INFO (
                X_SWIFI_MGR_NO VARCHAR(20) PRIMARY KEY,
                X_SWIFI_WRDOFC VARCHAR(20),
                X_SWIFI_MAIN_NM VARCHAR(100),
                X_SWIFI_ADRES1 VARCHAR(200),
                X_SWIFI_ADRES2 VARCHAR(200),
                X_SWIFI_INSTL_FLOOR VARCHAR(20),
                X_SWIFI_INSTL_TY VARCHAR(50),
                X_SWIFI_INSTL_MBY VARCHAR(50),
                X_SWIFI_SVC_SE VARCHAR(20),
                X_SWIFI_CMCWR VARCHAR(50),
                X_SWIFI_CNSTC_YEAR INT,
                X_SWIFI_INOUT_DOOR VARCHAR(20),
                X_SWIFI_REMARS3 VARCHAR(200),
                LAT DOUBLE,
                LNT DOUBLE,
                WORK_DTTM DATETIME
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertWifiData(JsonArray wifiList) {
        int insertedCount = 0;
        String sql = """
            INSERT INTO WIFI_INFO (
                X_SWIFI_MGR_NO, X_SWIFI_WRDOFC, X_SWIFI_MAIN_NM, 
                X_SWIFI_ADRES1, X_SWIFI_ADRES2, X_SWIFI_INSTL_FLOOR,
                X_SWIFI_INSTL_TY, X_SWIFI_INSTL_MBY, X_SWIFI_SVC_SE,
                X_SWIFI_CMCWR, X_SWIFI_CNSTC_YEAR, X_SWIFI_INOUT_DOOR,
                X_SWIFI_REMARS3, LAT, LNT, WORK_DTTM
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE 
                X_SWIFI_WRDOFC = VALUES(X_SWIFI_WRDOFC),
                X_SWIFI_MAIN_NM = VALUES(X_SWIFI_MAIN_NM),
                X_SWIFI_ADRES1 = VALUES(X_SWIFI_ADRES1),
                X_SWIFI_ADRES2 = VALUES(X_SWIFI_ADRES2),
                X_SWIFI_INSTL_FLOOR = VALUES(X_SWIFI_INSTL_FLOOR),
                X_SWIFI_INSTL_TY = VALUES(X_SWIFI_INSTL_TY),
                X_SWIFI_INSTL_MBY = VALUES(X_SWIFI_INSTL_MBY),
                X_SWIFI_SVC_SE = VALUES(X_SWIFI_SVC_SE),
                X_SWIFI_CMCWR = VALUES(X_SWIFI_CMCWR),
                X_SWIFI_CNSTC_YEAR = VALUES(X_SWIFI_CNSTC_YEAR),
                X_SWIFI_INOUT_DOOR = VALUES(X_SWIFI_INOUT_DOOR),
                X_SWIFI_REMARS3 = VALUES(X_SWIFI_REMARS3),
                LAT = VALUES(LAT),
                LNT = VALUES(LNT),
                WORK_DTTM = VALUES(WORK_DTTM)
        """;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (JsonElement element : wifiList) {
                    JsonObject wifi = element.getAsJsonObject();
                    
                    pstmt.setString(1, wifi.get("X_SWIFI_MGR_NO").getAsString());
                    pstmt.setString(2, wifi.get("X_SWIFI_WRDOFC").getAsString());
                    pstmt.setString(3, wifi.get("X_SWIFI_MAIN_NM").getAsString());
                    pstmt.setString(4, wifi.get("X_SWIFI_ADRES1").getAsString());
                    pstmt.setString(5, wifi.get("X_SWIFI_ADRES2").getAsString());
                    pstmt.setString(6, wifi.get("X_SWIFI_INSTL_FLOOR").getAsString());
                    pstmt.setString(7, wifi.get("X_SWIFI_INSTL_TY").getAsString());
                    pstmt.setString(8, wifi.get("X_SWIFI_INSTL_MBY").getAsString());
                    pstmt.setString(9, wifi.get("X_SWIFI_SVC_SE").getAsString());
                    pstmt.setString(10, wifi.get("X_SWIFI_CMCWR").getAsString());
                    pstmt.setString(11, wifi.get("X_SWIFI_CNSTC_YEAR").getAsString());
                    pstmt.setString(12, wifi.get("X_SWIFI_INOUT_DOOR").getAsString());
                    pstmt.setString(13, wifi.get("X_SWIFI_REMARS3").getAsString());
                    pstmt.setString(14, wifi.get("LAT").getAsString());
                    pstmt.setString(15, wifi.get("LNT").getAsString());
                    pstmt.setString(16, wifi.get("WORK_DTTM").getAsString());
                    
                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        insertedCount++;
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return insertedCount;
    }
    public List<WifiDTO> getNearbyWifi(SearchDTO searchDTO) {
        List<WifiDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = """
                SELECT 
                    *,
                    (6371 * acos(cos(radians(?)) * cos(radians(LAT)) * cos(radians(LNT) - radians(?)) + 
                    sin(radians(?)) * sin(radians(LAT)))) AS distance
                FROM WIFI_INFO
                HAVING distance <= 2
                ORDER BY distance
                LIMIT 20
            """;

            pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, searchDTO.getLat());
            pstmt.setDouble(2,searchDTO.getLnt());
            pstmt.setDouble(3, searchDTO.getLat());

            rs = pstmt.executeQuery();

            while (rs.next()) {
                WifiDTO wifiDTO = new WifiDTO();
                wifiDTO.setDistance(rs.getDouble("distance"));
                wifiDTO.setX_SWIFI_MGR_NO(rs.getString("X_SWIFI_MGR_NO"));
                wifiDTO.setX_SWIFI_WRDOFC(rs.getString("X_SWIFI_WRDOFC"));
                wifiDTO.setX_SWIFI_MAIN_NM(rs.getString("X_SWIFI_MAIN_NM"));
                wifiDTO.setX_SWIFI_ADRES1(rs.getString("X_SWIFI_ADRES1"));
                wifiDTO.setX_SWIFI_ADRES2(rs.getString("X_SWIFI_ADRES2"));
                wifiDTO.setX_SWIFI_INSTL_FLOOR(rs.getString("X_SWIFI_INSTL_FLOOR"));
                wifiDTO.setX_SWIFI_INSTL_TY(rs.getString("X_SWIFI_INSTL_TY"));
                wifiDTO.setX_SWIFI_INSTL_MBY(rs.getString("X_SWIFI_INSTL_MBY"));
                wifiDTO.setX_SWIFI_SVC_SE(rs.getString("X_SWIFI_SVC_SE"));
                wifiDTO.setX_SWIFI_CMCWR(rs.getString("X_SWIFI_CMCWR"));
                wifiDTO.setX_SWIFI_CNSTC_YEAR(rs.getInt("X_SWIFI_CNSTC_YEAR"));
                wifiDTO.setX_SWIFI_INOUT_DOOR(rs.getString("X_SWIFI_INOUT_DOOR"));
                wifiDTO.setX_SWIFI_REMARS3(rs.getString("X_SWIFI_REMARS3"));
                wifiDTO.setLAT(rs.getDouble("LAT"));
                wifiDTO.setLNT(rs.getDouble("LNT"));
                wifiDTO.setWORK_DTTM(rs.getString("WORK_DTTM"));
                
                list.add(wifiDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	close(conn, pstmt, rs);
        }

        return list;
    }
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}