<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Edit Student</title>
<!-- add bs cdn links for css classes and icons -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
</head>
<body class="bg-light">
<jsp:include page="navbar.jsp"></jsp:include>

<div class="container-fluid px-0">
<div class="row">

<jsp:include page="side-panel.jsp"></jsp:include>


<div class="col-10 p-4">
<c:if test="${not empty sessionScope.studentFormError}">
<div class="alert alert-danger"><c:out value="${sessionScope.studentFormError}"/></div>
<c:remove var="studentFormError" scope="session"/>
</c:if>

<!-- heading section -->
<div class="mb-4">
<h4>Edit Student</h4>
<div class="text-secondary">Edit Student Details</div>
</div>

<!-- form section -->
<div class="card shadow">
<div class="card-body p-4">

<form action="UpdateStudent" method="post">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<div class="row g-3">

<input type="hidden" name="id" value="${s.id }">

<!-- first name -->
<div class="col-md-6">
<label for="firstName" class="form-label">First Name</label>
<input type="text" name="firstName" class="form-control" value="${s.firstName }" required>
</div>

<!-- last name -->
<div class="col-md-6">
<label for="lastName" class="form-label">Last Name</label>
<input type="text" name="lastName" class="form-control" value="${s.lastName }" required>
</div>

<!-- phone -->
<div class="col-md-6">
<label for="phone" class="form-label">Phone</label>
<input type="text" name="phone" class="form-control" value="${s.phone }" required>
</div>

<!-- department -->
<div class="col-md-6">
<label for="department" class="form-label">Department</label>
<input type="text" name="department" class="form-control" value="${s.department}" required>
</div>

<!-- email -->
<div class="col-md-6">
<label for="email" class="form-label">Email</label>
<input type="email" name="email" class="form-control" value="${s.email }" required>
</div>

<!-- city -->
<div class="col-md-6">
<label for="city" class="form-label">City</label>
<input type="text" name="city" class="form-control" value="${s.city }" required>
</div>

<div class="col-md-6">
<label for="temporaryPassword" class="form-label">Reset temporary password (optional)</label>
<input type="password" id="temporaryPassword" name="temporaryPassword" class="form-control" minlength="12" maxlength="256" autocomplete="new-password">
<div class="form-text">If set, the student must change it at next sign-in.</div>
</div>

</div>

<div class="mt-4">
<button type="submit" class="btn btn-primary">
<i class="bi bi-save me-1"></i> Update Student
</button>
</div>

</form>


</div>
</div>
</div>
</div>
</div>

</body>
</html>








