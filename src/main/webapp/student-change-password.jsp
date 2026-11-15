<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Change Temporary Password</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<main class="container py-5" style="max-width: 560px">
	<div class="card shadow-sm">
		<div class="card-body p-4">
			<h2>Set a new password</h2>
			<p class="text-secondary">Your administrator provided a temporary password. Set a private password to continue.</p>
			<c:if test="${not empty sessionScope.studentError}">
				<div class="alert alert-danger"><c:out value="${sessionScope.studentError}"/></div>
				<c:remove var="studentError" scope="session"/>
			</c:if>
			<form action="ChangeStudentPassword" method="post">
				<input type="hidden" name="csrfToken" value="${csrfToken}">
				<div class="mb-3">
					<label for="password" class="form-label">New password</label>
					<input id="password" name="password" type="password" minlength="12" maxlength="256" autocomplete="new-password" class="form-control" required>
					<div class="form-text">Use at least 12 characters.</div>
				</div>
				<div class="mb-3">
					<label for="confirmPassword" class="form-label">Confirm new password</label>
					<input id="confirmPassword" name="confirmPassword" type="password" minlength="12" maxlength="256" autocomplete="new-password" class="form-control" required>
				</div>
				<button class="btn btn-primary" type="submit">Change password</button>
				<a href="Logout" class="btn btn-outline-secondary">Logout</a>
			</form>
		</div>
	</div>
</main>
</body>
</html>
