<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="wifi.dto.HistoryDTO" %>
<%@ page import="wifi.dao.HistoryDAO" %>
<%
HistoryDAO historyDAO = new HistoryDAO();
historyDAO.createTable(); // 테이블 생성
List<HistoryDTO> historyList = historyDAO.getHistoryList();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>위치 히스토리 목록</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: center;
        }
        th {
            background-color: #04AA6D;
            color: white;
        }
    </style>
</head>
<body>
    <h1>위치 히스토리 목록</h1>
    
    <div>
        <a href="index.jsp">홈</a> | 
        <a href="history.jsp">위치 히스토리 목록</a> | 
        <a href="load-wifi.jsp">Open API 와이파이 정보 가져오기</a>
    </div>
    
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>X좌표</th>
                <th>Y좌표</th>
                <th>조회일자</th>
                <th>비고</th>
            </tr>
        </thead>
        <tbody>
        <% 
          
            
            if(historyList != null && !historyList.isEmpty()) {
                for(HistoryDTO history : historyList) {
        %>
            <tr>
                <td><%= history.getId() %></td>
                <td><%= history.getLat() %></td>
                <td><%= history.getLnt() %></td>
                <td><%= history.getSearchDate() %></td>
                <td>
                    <button onclick="deleteHistory(<%= history.getId() %>)">삭제</button>
                </td>
            </tr>
        <%
                }
            } else {
        %>
            <tr>
                <td colspan="5" style="text-align: center;">
                    히스토리가 없습니다.
                </td>
            </tr>
        <%
            }
        %>
        </tbody>
    </table>

    <script>
        function deleteHistory(id) {
            if(confirm('정말 삭제하시겠습니까?')) {
                location.href = 'delete-history.jsp?id=' + id;
            }
        }
    </script>
</body>
</html>