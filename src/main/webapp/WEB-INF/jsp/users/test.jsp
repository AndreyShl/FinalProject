<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<head>
    <title>Users List</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        h1 {
            color: #333;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 10px;
            border: 1px solid #ddd;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
            font-weight: bold;
        }
        tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        tr:hover {
            background-color: #f1f1f1;
        }
        .no-users {
            margin-top: 20px;
            color: #666;
        }
    </style>
</head>
<body>
<h1>
    <c:choose>
        <c:when test="${not empty sessionScope.user}">
            Здравствуйте, ${sessionScope.user.username}!
        </c:when>
        <c:otherwise>
            Добро пожаловать!
        </c:otherwise>
    </c:choose>
</h1>


<c:if test="${empty users}">
    <div class="no-users">
        <p>No users found.</p>
    </div>
</c:if>

<c:if test="${not empty users}">
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Login</th>
            <th>Phone Number</th>
            <th>Role</th>
            <th>Balance</th>
            <th>Status</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${users}" var="user">
            <tr>
                <td>${user.id}</td>
                <td>${user.username}</td>
                <td>${user.login}</td>
                <td>${user.phoneNumber}</td>
                <td>${user.role}</td>
                <td>${user.balance}</td>
                <td>${user.status}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>
</body>
</html>
