<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<html>
	<head>
	    <meta charset="utf-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	    <title>新規登録</title>
	    <link href="<c:url value="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.0/css/bootstrap.min.css" />" rel="stylesheet">
	    <link href="<c:url value="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css" />" rel="stylesheet">
	    <link href="<c:url value="/resources/css/regi_styles.min.css" />" rel="stylesheet">
	    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
	    <script src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.0/js/bootstrap.bundle.min.js"></script>
	</head>


	<body>
	    <div class="login-dark">
	        <form method="post" name="f" action="${pageContext.request.contextPath}/register">
	            <h2 class="sr-only">登録</h2>
	            <div class="illustration"><i class="ion-happy-outline"></i></div>
	            <div class="form-group"><input class="form-control" type="text" name="username" placeholder="username"></div>
	            <div class="form-group"><input class="form-control" type="password" name="password" placeholder="Password"></div>
	            <div class="form-group"><input type="radio" name="sex" value="male" checked="checked">男性 <input type="radio" name="sex" value="female">女性</div>
	            <div class="form-group"><input class="form-control" type="text" name="profile" placeholder="profile"></div>
	            <div class="form-group"><button class="btn btn-primary btn-block" type="submit" name="register" >登録</button></div>
	            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	            <a href="toLogin" class="forgot">ログイン画面に戻る</a>
	        </form>
	    </div>
	</body>
</html>
