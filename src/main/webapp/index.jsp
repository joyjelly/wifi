<%@page import="wifi.dto.SearchDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="wifi.dao.WifiDAO"%>
<%@ page import="wifi.dao.HistoryDAO"%>
<%@ page import="wifi.dto.WifiDTO"%>
<%@ page import="java.util.*"%>

<%
    String lat = request.getParameter("lat") == null ? "0.0" : request.getParameter("lat");
    String lnt = request.getParameter("lnt") == null ? "0.0" : request.getParameter("lnt");
    List<WifiDTO> wifiList = null;

    if(!("0.0").equals(lat) && !("0.0").equals(lnt)) {
        // 위치 히스토리 저장
        HistoryDAO historyDAO = new HistoryDAO();
        historyDAO.createTable();
        historyDAO.insertHistory(Double.parseDouble(lat), Double.parseDouble(lnt));

        // 와이파이 정보 검색
        WifiDAO wifiDAO = new WifiDAO();
        wifiDAO.createTable();
        SearchDTO s = new SearchDTO();
        s.setLat(Double.parseDouble(lat));
        s.setLnt(Double.parseDouble(lnt));
        wifiList = wifiDAO.getNearbyWifi(s);
    }
%>

<!DOCTYPE html>
<html>
<head>

    <meta charset="UTF-8">
    <title>와이파이 정보 구하기</title>
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
        .input-group {
            margin: 20px 0;
        }
    </style>
</head>
<body>
    <h1>와이파이 정보 구하기</h1>
    
    <div>
        <a href="index.jsp">홈</a> | 
        <a href="history.jsp">위치 히스토리 목록</a> | 
        <a href="load-wifi.jsp">Open API 와이파이 정보 가져오기</a>
    </div>
    
   <div class="input-group">
    <form id="locationForm" method="get" action="index.jsp">
        <span>LAT:</span>
        <input type="text" id="lat" name="lat" value="<%= lat %>">
        <span>, LNT:</span>
        <input type="text" id="lnt" name="lnt" value="<%= lnt %>">
        <button type="button" onclick="getLocation()">내 위치 가져오기</button>
        <button type="submit">근처 WIFI 정보 보기</button>
    </form>
</div>

    <table>
        <thead>
            <tr>
                <th>거리(Km)</th>
                <th>관리번호</th>
                <th>자치구</th>
                <th>와이파이명</th>
                <th>도로명주소</th>
                <th>상세주소</th>
                <th>설치위치(층)</th>
                <th>설치기관</th>
                <th>설치유형</th>
                <th>서비스구분</th>
                <th>망종류</th>
                <th>설치년도</th>
                <th>실내외구분</th>
                <th>WIFI접속환경</th>
                <th>X좌표</th>
                <th>Y좌표</th>
                <th>작업일자</th>
            </tr>
        </thead>
        <tbody>
            <% 
            if (wifiList != null && !wifiList.isEmpty()) {
                for (WifiDTO wifi : wifiList) {
            %>
                <tr>
                    <td><%= String.format("%.4f", wifi.getDistance()) %></td>
                    <td><%= wifi.getX_SWIFI_MGR_NO() %></td>
                    <td><%= wifi.getX_SWIFI_WRDOFC() %></td>
                    <td>
                        <a href="detail.jsp?mgrNo=<%= wifi.getX_SWIFI_MGR_NO() %>&distance=<%= wifi.getDistance() %>">
                            <%= wifi.getX_SWIFI_MAIN_NM() %>
                        </a>
                    </td>
                    <td><%= wifi.getX_SWIFI_ADRES1() %></td>
                    <td><%= wifi.getX_SWIFI_ADRES2() %></td>
                    <td><%= wifi.getX_SWIFI_INSTL_FLOOR() %></td>
                    <td><%= wifi.getX_SWIFI_INSTL_MBY() %></td>
                    <td><%= wifi.getX_SWIFI_INSTL_TY() %></td>
                    <td><%= wifi.getX_SWIFI_SVC_SE() %></td>
                    <td><%= wifi.getX_SWIFI_CMCWR() %></td>
                    <td><%= wifi.getX_SWIFI_CNSTC_YEAR() %></td>
                    <td><%= wifi.getX_SWIFI_INOUT_DOOR() %></td>
                    <td><%= wifi.getX_SWIFI_REMARS3() %></td>
                    <td><%= wifi.getLAT() %></td>
                    <td><%= wifi.getLNT() %></td>
                    <td><%= wifi.getWORK_DTTM() %></td>
                </tr>
            <%
                }
            } else {
            %>
                <tr>
                    <td colspan="17" style="text-align: center; padding: 10px;">
                        위치 정보를 입력한 후에 조회해 주세요.
                    </td>
                </tr>
            <%
            }
            %>
        </tbody>
    </table>

    <script>
        function getLocation() {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(
                    function(position) {
                        document.getElementById('lat').value = position.coords.latitude;
                        document.getElementById('lnt').value = position.coords.longitude;
                    },
                    function(error) {
                        alert("위치 정보를 가져오는데 실패했습니다.");
                    }
                );
            } else {
                alert("이 브라우저에서는 위치 정보를 지원하지 않습니다.");
            }
        }

        function getNearbyWifi() {
            let lat = document.getElementById('lat').value;
            let lnt = document.getElementById('lnt').value;
            
            if (!lat || !lnt) {
                alert("위치 정보를 입력해주세요.");
                return;
            }
            
            window.location.href = `index.jsp?lat=${lat}&lnt=${lnt}`;
        }
    </script>
</body>
</html>