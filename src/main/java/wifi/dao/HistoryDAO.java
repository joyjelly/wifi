package wifi.dao;

import java.sql.*;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import wifi.dto.HistoryDTO;

import java.io.IOException;

public class HistoryDAO {
    public static Connection connection;
    public static PreparedStatement preparedStatement;
    public static ResultSet resultSet;

    private static final String DB_URL = "jdbc:mariadb://localhost:3306/wifi";
    private static final String DB_USER = "wifi";
    private static final String DB_PASSWORD = "wifi"; 
    static {
        try {
        	  Class.forName("org.mariadb.jdbc.Driver");
        } catch ( ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public HistoryDAO() {
    }

    public void createTable() {
        connection = null;
        preparedStatement = null;
        resultSet = null;

        String sql = """ 
            CREATE TABLE IF NOT EXISTS LOCATION_HISTORY (
                ID INT PRIMARY KEY AUTO_INCREMENT,
                LAT DOUBLE NOT NULL,
                LNT DOUBLE NOT NULL,
                SEARCH_DTTM TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """;

        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    public void insertHistory(double lat, double lnt) {
        connection = null;
        preparedStatement = null;
        resultSet = null;

        try {
            connection = getConnection();
            String sql = "INSERT INTO LOCATION_HISTORY (LAT, LNT) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, lat);
            preparedStatement.setDouble(2, lnt);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    public List<HistoryDTO> getHistoryList() {
        List<HistoryDTO> list = new ArrayList<>();
        connection = null;
        preparedStatement = null;
        resultSet = null;

        try {
            connection = getConnection();
            String sql = "SELECT * FROM LOCATION_HISTORY ORDER BY ID DESC";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                HistoryDTO historyDTO = new HistoryDTO();
                historyDTO.setId(resultSet.getInt("ID"));
                historyDTO.setLat(resultSet.getDouble("LAT"));
                historyDTO.setLnt(resultSet.getDouble("LNT"));
                historyDTO.setSearchDate(resultSet.getTimestamp("SEARCH_DATE").toString());
                list.add(historyDTO);
                System.out.println(historyDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, preparedStatement, resultSet);
        }
        return list;
    }

    public void deleteHistory(int id) {
        connection = null;
        preparedStatement = null;
        resultSet = null;

        try {
            connection = getConnection();
            String sql = "DELETE FROM LOCATION_HISTORY WHERE ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, preparedStatement, resultSet);
        }
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