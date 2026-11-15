<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Student Management System Login</title>

<!-- Bootstrap 5 CDN -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
	rel="stylesheet">
</head>
<body class="bg-light">

	<div class="container mt-5">
		<div class="row justify-content-center">
			<div class="col-md-5">
				<div class="card shadow p-4">
					<div class="card-body">

						<h3 class="text-center mb-2">Student Management System</h3>
						<p class="text-center text-secondary mb-4">Choose your sign-in</p>

						<form action="login" method="post" class="border rounded p-3 mb-4">
							<h5><i class="bi bi-person-gear me-2"></i>Admin</h5>

							<div class="mb-3">
								<label class="form-label">Email</label> <input type="text"
									name="email" class="form-control" autocomplete="username" required>
							</div>

							<div class="mb-3">
								<label class="form-label">Password</label> <input
									type="password" name="password" class="form-control" autocomplete="current-password" required>
							</div>

							<button type="submit" class="btn btn-primary w-100">Admin Login</button>
						</form>

						<form action="student-login" method="post" class="border rounded p-3">
							<h5><i class="bi bi-mortarboard me-2"></i>Student</h5>
							<div class="mb-3">
								<label class="form-label">Student email</label>
								<input type="email" name="email" class="form-control" autocomplete="username" required>
							</div>
							<div class="mb-3">
								<label class="form-label">Password</label>
								<input type="password" name="password" class="form-control" autocomplete="current-password" required>
							</div>
							<button type="submit" class="btn btn-success w-100">Student Login</button>
						</form>

						<c:if test="${not empty errorMsg}">
							<div class="alert alert-danger text-center mt-3">${errorMsg}</div>
						</c:if>
						<c:if test="${not empty studentErrorMsg}">
							<div class="alert alert-danger text-center mt-3">${studentErrorMsg}</div>
						</c:if>
					</div>
				</div>
			</div>


		</div>
	</div>

</body>
</html>