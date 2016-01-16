<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login Test</title>
</head>
<body>
	<form action="${pageContext.request.contextPath}/login.do" method="post">
		<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td><span>User:</span></td>
				<td align="left"><input type="text" name="userID"/></td>
			</tr>
			<tr>
				<td><span>Password:</span></td>
				<td align="left"><input type="password" name="passWord"/></td>
			</tr>
			<tr>
				<td colspan="2"><button type="submit">login</button></td>
			</tr>
		</table>
	</form>
	<span style="color: red;">${msg}</span>
</body>
</html>