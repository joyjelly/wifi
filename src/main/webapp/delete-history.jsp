<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="wifi.dao.HistoryDAO" %>
<%
    int id = Integer.parseInt(request.getParameter("id"));
    HistoryDAO historyDAO = new HistoryDAO();
    historyDAO.deleteHistory(id);
    response.sendRedirect("history.jsp");
%>
