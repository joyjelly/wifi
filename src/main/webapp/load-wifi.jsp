<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="wifi.service.WifiService" %>
<%@ page import="wifi.dto.WifiDTO" %>
<%
    int insertedCount = new WifiService().loadPublicWifiData();
%>
<!DOCTYPE html>
<html>
<head>
    <title>와이파이 정보 구하기</title>
    <meta charset="UTF-8">
</head>
<body>
    <h1><%= insertedCount %>개의 WIFI 정보를 정상적으로 저장하였습니다.</h1>
    <a href="index.jsp">홈 으로 가기</a>
</body>
</html>