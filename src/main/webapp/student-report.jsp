<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>My Student Report</title>
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
	<h2>My report</h2>
	<p class="text-secondary">Welcome, <c:out value="${student.firstName}"/>. Your marks are out of 100; 33 or higher passes.</p>
	<c:if test="${not empty sessionScope.studentSuccess}">
		<div class="alert alert-success"><c:out value="${sessionScope.studentSuccess}"/></div>
		<c:remove var="studentSuccess" scope="session"/>
	</c:if>
	<section class="card shadow-sm mb-4">
		<div class="card-header fw-bold">My subjects and results</div>
		<div class="table-responsive">
			<table class="table table-hover align-middle mb-0">
				<thead class="table-light"><tr><th>Subject</th><th>Marks / 100</th><th>Result</th></tr></thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty records}">
							<c:forEach var="record" items="${records}">
								<tr>
									<td><c:out value="${record.subjectName}"/></td>
									<td>${record.marks}</td>
									<td><c:choose><c:when test="${record.passed}"><span class="badge text-bg-success">Passed</span></c:when><c:otherwise><span class="badge text-bg-danger">Failed</span></c:otherwise></c:choose></td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise><tr><td colspan="3" class="text-center text-secondary py-4">No marks have been recorded yet.</td></tr></c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
	</section>
	<section class="card shadow-sm">
		<div class="card-header fw-bold">Student leaderboard</div>
		<p class="text-secondary small px-3 pt-2 mb-0">Ranked by average marks across recorded subjects. Tied averages share a rank.</p>
		<div class="table-responsive">
			<table class="table table-hover align-middle mb-0">
				<thead class="table-light"><tr><th>Rank</th><th>Student</th><th>Average / 100</th><th>Subjects recorded</th></tr></thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty leaderboard}">
							<c:forEach var="entry" items="${leaderboard}">
								<tr>
									<td>#${entry.rank}</td>
									<td><c:out value="${entry.firstName}"/> <c:out value="${entry.lastName}"/></td>
									<td>${entry.averageMarks}</td>
									<td>${entry.subjectCount}</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise><tr><td colspan="4" class="text-center text-secondary py-4">No leaderboard results yet.</td></tr></c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
	</section>
</main>
</body>
</html>
