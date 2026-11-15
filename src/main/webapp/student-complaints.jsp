<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Student Complaints</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-dark bg-dark">
	<div class="container-fluid">
		<a class="navbar-brand" href="StudentReport">My Student Portal</a>
		<div class="d-flex gap-3">
			<a class="nav-link text-white" href="StudentReport">My report</a>
			<a class="nav-link text-white" href="StudentComplaints">Complaints</a>
			<a class="nav-link text-white" href="Logout">Logout</a>
		</div>
	</div>
</nav>
<main class="container py-4">
	<h2>Parent contact &amp; complaints</h2>
	<p class="text-secondary">Your parent number is only used by administrators if you fail a subject and they explicitly request a call.</p>
	<c:if test="${not empty sessionScope.studentSuccess}">
		<div class="alert alert-success"><c:out value="${sessionScope.studentSuccess}"/></div>
		<c:remove var="studentSuccess" scope="session"/>
	</c:if>
	<c:if test="${not empty sessionScope.studentError}">
		<div class="alert alert-danger"><c:out value="${sessionScope.studentError}"/></div>
		<c:remove var="studentError" scope="session"/>
	</c:if>

	<section class="card shadow-sm mb-4">
		<div class="card-header fw-bold">Parent mobile number</div>
		<div class="card-body">
			<form action="StudentComplaints" method="post" class="row g-3">
				<input type="hidden" name="action" value="saveParentMobile">
				<input type="hidden" name="csrfToken" value="${csrfToken}">
				<div class="col-md-8">
					<label for="parentMobile" class="form-label">Mobile number (international format)</label>
					<input id="parentMobile" name="parentMobile" type="tel" class="form-control" pattern="\+[1-9][0-9]{7,14}" placeholder="+14155552671" value="<c:out value='${student.parentMobile}'/>" required>
					<div class="form-text">Use country code, for example +14155552671.</div>
				</div>
				<div class="col-md-4 d-flex align-items-end"><button class="btn btn-primary" type="submit">Save number</button></div>
			</form>
		</div>
	</section>

	<section class="card shadow-sm mb-4">
		<div class="card-header fw-bold">Raise a marks discrepancy</div>
		<div class="card-body">
			<c:choose>
				<c:when test="${not empty subjects}">
					<form action="StudentComplaints" method="post">
						<input type="hidden" name="csrfToken" value="${csrfToken}">
						<div class="mb-3">
							<label for="subjectId" class="form-label">Subject in your report</label>
							<select id="subjectId" name="subjectId" class="form-select" required>
								<option value="">Choose subject</option>
								<c:forEach var="subject" items="${subjects}">
									<option value="${subject.id}"><c:out value="${subject.name}"/></option>
								</c:forEach>
							</select>
						</div>
						<div class="mb-3">
							<label for="message" class="form-label">Describe the discrepancy</label>
							<textarea id="message" name="message" class="form-control" rows="4" minlength="10" maxlength="2000" required></textarea>
						</div>
						<button class="btn btn-warning" type="submit">Submit complaint</button>
					</form>
				</c:when>
				<c:otherwise><p class="text-secondary mb-0">You can raise a subject complaint after a mark is recorded in your report.</p></c:otherwise>
			</c:choose>
		</div>
	</section>

	<section class="card shadow-sm">
		<div class="card-header fw-bold">My complaints and admin responses</div>
		<div class="table-responsive">
			<table class="table table-hover align-middle mb-0">
				<thead class="table-light"><tr><th>Submitted</th><th>Subject</th><th>Complaint</th><th>Status</th><th>Admin response</th></tr></thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty complaints}">
							<c:forEach var="complaint" items="${complaints}">
								<tr>
									<td>${complaint.createdAt}</td>
									<td><c:out value="${complaint.subjectName}"/></td>
									<td><c:out value="${complaint.message}"/></td>
									<td><c:out value="${complaint.status}"/></td>
									<td><c:out value="${complaint.adminResponse}"/></td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise><tr><td colspan="5" class="text-center text-secondary py-4">No complaints submitted.</td></tr></c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
	</section>
</main>
</body>
</html>
