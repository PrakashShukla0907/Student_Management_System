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
<jsp:include page="navbar.jsp"></jsp:include>
<div class="container-fluid px-0">
	<div class="row g-0">
		<jsp:include page="side-panel.jsp"></jsp:include>
		<main class="col-10 p-4">
			<h2>Student complaints</h2>
			<p class="text-secondary">Review mark discrepancies, update status, and provide a response visible to the student.</p>
			<c:if test="${not empty sessionScope.complaintSuccess}">
				<div class="alert alert-success"><c:out value="${sessionScope.complaintSuccess}"/></div>
				<c:remove var="complaintSuccess" scope="session"/>
			</c:if>
			<c:if test="${not empty sessionScope.complaintError}">
				<div class="alert alert-danger"><c:out value="${sessionScope.complaintError}"/></div>
				<c:remove var="complaintError" scope="session"/>
			</c:if>
			<div class="card shadow-sm">
				<div class="table-responsive">
					<table class="table table-hover align-middle mb-0">
						<thead class="table-light"><tr><th>Student</th><th>Subject</th><th>Complaint</th><th>Submitted</th><th>Update</th></tr></thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty complaints}">
									<c:forEach var="complaint" items="${complaints}">
										<tr>
											<td><c:out value="${complaint.firstName}"/> <c:out value="${complaint.lastName}"/></td>
											<td><c:out value="${complaint.subjectName}"/></td>
											<td><c:out value="${complaint.message}"/></td>
											<td>${complaint.createdAt}</td>
											<td>
												<form action="AdminComplaints" method="post" class="d-grid gap-2">
													<input type="hidden" name="complaintId" value="${complaint.id}">
													<input type="hidden" name="csrfToken" value="${csrfToken}">
													<select name="status" class="form-select form-select-sm" required>
														<option value="OPEN" ${complaint.status == 'OPEN' ? 'selected' : ''}>Open</option>
														<option value="IN_REVIEW" ${complaint.status == 'IN_REVIEW' ? 'selected' : ''}>In review</option>
														<option value="RESOLVED" ${complaint.status == 'RESOLVED' ? 'selected' : ''}>Resolved</option>
														<option value="REJECTED" ${complaint.status == 'REJECTED' ? 'selected' : ''}>Rejected</option>
													</select>
													<textarea name="adminResponse" class="form-control form-control-sm" rows="2" maxlength="2000" placeholder="Response for student"><c:out value="${complaint.adminResponse}"/></textarea>
													<button class="btn btn-sm btn-primary" type="submit">Save response</button>
												</form>
											</td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise><tr><td colspan="5" class="text-center text-secondary py-4">No student complaints.</td></tr></c:otherwise>
							</c:choose>
						</tbody>
					</table>
				</div>
			</div>
		</main>
	</div>
</div>
</body>
</html>
