<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="kr">
<head>
    <meta charset="UTF-8">
    <title>로그인 페이지</title>
</head>
<body>
<h1>로그인</h1>
<hr>

<form action="/user/email" method="post">
    <%-- <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> --%>
    <input type="text" name="email" placeholder="이메일 입력해주세요">

    <button type="submit">로그인</button>
</form>
</body>
</html>