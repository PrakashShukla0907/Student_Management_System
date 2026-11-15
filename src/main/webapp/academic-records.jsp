<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Subjects, Marks & Leaderboard</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
</head>
<body class="bg-light">
<jsp:include page="navbar.jsp"></jsp:include>
<div class="container-fluid px-0">
	<div class="row g-0">
		<jsp:include page="side-panel.jsp"></jsp:include>
		<main class="col-10 p-4">
			<h2 class="mb-1">Subjects, Marks &amp; Results</h2>
			<p class="text-secondary">Marks are out of 100. A score of 33 or higher passes a subject.</p>

			<c:if test="${not empty sessionScope.academicSuccess}">
				<div class="alert alert-success" role="alert">${sessionScope.academicSuccess}</div>
				<c:remove var="academicSuccess" scope="session"/>
			</c:if>
			<c:if test="${not empty sessionScope.academicError}">
				<div class="alert alert-danger" role="alert">${sessionScope.academicError}</div>
				<c:remove var="academicError" scope="session"/>
			</c:if>

			<div class="row g-4 mb-4">
				<div class="col-lg-5">
					<section class="card shadow-sm h-100">
						<div class="card-header fw-bold">Add a subject</div>
						<div class="card-body">
							<form action="AcademicRecords" method="post">
								<input type="hidden" name="action" value="addSubject">
								<input type="hidden" name="csrfToken" value="${csrfToken}">
								<label for="subjectName" class="form-label">Subject name</label>
								<div class="input-group">
									<input id="subjectName" name="subjectName" class="form-control" maxlength="255" required>
									<button class="btn btn-primary" type="submit">Add subject</button>
								</div>
							</form>
							<h6 class="mt-4">Available subjects</h6>
							<c:choose>
								<c:when test="${not empty subjects}">
									<div class="d-flex flex-wrap gap-2">
										<c:forEach var="subject" items="${subjects}">
											<span class="badge text-bg-light border"><c:out value="${subject.name}"/></span>
										</c:forEach>
									</div>
								</c:when>
								<c:otherwise><p class="text-secondary mb-0">No subjects yet. Add one above.</p></c:otherwise>
							</c:choose>
						</div>
					</section>
				</div>
				<div class="col-lg-7">
					<section class="card shadow-sm h-100">
						<div class="card-header fw-bold">Record or update marks</div>
						<div class="card-body">
							<c:choose>
								<c:when test="${empty students or empty subjects}">
									<p class="text-secondary mb-0">Add at least one student and one subject to record marks.</p>
								</c:when>
								<c:otherwise>
									<form action="AcademicRecords" method="post" class="row g-3">
										<input type="hidden" name="action" value="saveMark">
										<input type="hidden" name="csrfToken" value="${csrfToken}">
										<div class="col-md-5">
											<label for="studentId" class="form-label">Student</label>
											<select id="studentId" name="studentId" class="form-select" required>
												<option value="">Choose student</option>
												<c:forEach var="student" items="${students}">
													<option value="${student.id}"><c:out value="${student.firstName}"/> <c:out value="${student.lastName}"/></option>
												</c:forEach>
											</select>
										</div>
										<div class="col-md-4">
											<label for="subjectId" class="form-label">Subject</label>
											<select id="subjectId" name="subjectId" class="form-select" required>
												<option value="">Choose subject</option>
												<c:forEach var="subject" items="${subjects}">
													<option value="${subject.id}"><c:out value="${subject.name}"/></option>
												</c:forEach>
											</select>
										</div>
										<div class="col-md-3">
											<label for="marks" class="form-label">Marks / 100</label>
											<input id="marks" name="marks" type="number" class="form-control" min="0" max="100" step="0.01" required>
										</div>
										<div class="col-12">
											<button class="btn btn-success" type="submit"><i class="bi bi-save me-1"></i>Save marks</button>
											<span class="form-text ms-2">Saving again for the same student and subject updates the mark.</span>
										</div>
									</form>
								</c:otherwise>
							</c:choose>
						</div>
					</section>
				</div>
			</div>

			<section class="card shadow-sm mb-4">
				<div class="card-header fw-bold"><i class="bi bi-card-checklist me-2"></i>Student subjects and results</div>
				<div class="table-responsive">
					<table class="table table-hover align-middle mb-0">
						<thead class="table-light"><tr><th>Student</th><th>Subject</th><th>Marks / 100</th><th>Result</th><th>Parent call</th></tr></thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty records}">
									<c:forEach var="record" items="${records}">
										<tr>
											<td><c:out value="${record.firstName}"/> <c:out value="${record.lastName}"/></td>
											<td><c:out value="${record.subjectName}"/></td>
											<td>${record.marks}</td>
											<td><c:choose><c:when test="${record.passed}"><span class="badge text-bg-success">Passed</span></c:when><c:otherwise><span class="badge text-bg-danger">Failed</span></c:otherwise></c:choose></td>
											<td>
												<c:if test="${not record.passed}">
													<c:choose>
														<c:when test="${record.parentCallStatus == 'QUEUED'}"><span class="badge text-bg-info">Call queued</span></c:when>
														<c:when test="${record.parentCallStatus == 'REQUESTING'}"><span class="badge text-bg-secondary">Call request in progress</span></c:when>
														<c:when test="${record.parentCallStatus == 'UNKNOWN'}"><span class="badge text-bg-warning">Check Twilio console before retrying</span></c:when>
														<c:when test="${record.scheduledCallStatus == 'SCHEDULED'}">
															<div class="mb-2"><span class="badge text-bg-primary">Scheduled for <fmt:formatDate value="${record.scheduledCallAt}" pattern="dd MMM yyyy, hh:mm a" timeZone="Asia/Kolkata"/> IST</span></div>
															<form action="ScheduleParentCall" method="post">
																<input type="hidden" name="action" value="cancel">
																<input type="hidden" name="markId" value="${record.markId}">
																<input type="hidden" name="csrfToken" value="${csrfToken}">
																<button type="submit" class="btn btn-sm btn-outline-secondary">Cancel scheduled call</button>
															</form>
														</c:when>
														<c:when test="${record.scheduledCallStatus == 'PROCESSING'}"><span class="badge text-bg-secondary">Scheduled call is processing</span></c:when>
														<c:when test="${record.scheduledCallStatus == 'UNKNOWN'}"><span class="badge text-bg-warning">Check Twilio console before retrying</span></c:when>
														<c:otherwise>
															<c:choose>
																<c:when test="${empty record.parentMobile}"><span class="text-secondary">Parent number not provided</span></c:when>
																<c:otherwise>
																	<div class="d-flex flex-column gap-2">
																		<form action="CallParent" method="post" onsubmit="return confirm('Place a Twilio voice call to this student’s parent now?')">
																			<input type="hidden" name="markId" value="${record.markId}">
																			<input type="hidden" name="csrfToken" value="${csrfToken}">
																			<button type="submit" class="btn btn-sm btn-outline-danger">Call parent now</button>
																		</form>
																		<form action="ScheduleParentCall" method="post" class="border rounded p-2">
																			<input type="hidden" name="action" value="schedule">
																			<input type="hidden" name="markId" value="${record.markId}">
																			<input type="hidden" name="csrfToken" value="${csrfToken}">
																			<label for="schedule-${record.markId}" class="form-label small mb-1">Schedule call (IST)</label>
																			<div class="d-flex flex-wrap gap-2">
																				<input id="schedule-${record.markId}" name="scheduledAt" type="datetime-local" class="form-control form-control-sm" min="${minimumScheduledCallTime}" required>
																				<button type="submit" class="btn btn-sm btn-primary">Schedule</button>
																			</div>
																		</form>
																	</div>
																</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
												</c:if>
											</td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise><tr><td colspan="5" class="text-center text-secondary py-4">No marks recorded yet.</td></tr></c:otherwise>
							</c:choose>
						</tbody>
					</table>
				</div>
			</section>

			<section class="card shadow-sm">
				<div class="card-header fw-bold"><i class="bi bi-trophy me-2"></i>Leaderboard</div>
				<p class="text-secondary small px-3 pt-2 mb-0">Ranked by average marks across each student's recorded subjects. Students without marks are not ranked; ties share a rank.</p>
				<div class="table-responsive">
					<table class="table table-hover align-middle mb-0">
						<thead class="table-light"><tr><th>Rank</th><th>Student</th><th>Average / 100</th><th>Subjects recorded</th></tr></thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty leaderboard}">
									<c:forEach var="entry" items="${leaderboard}">
										<tr>
											<td><span class="badge text-bg-warning">#${entry.rank}</span></td>
											<td><c:out value="${entry.firstName}"/> <c:out value="${entry.lastName}"/></td>
											<td>${entry.averageMarks}</td>
											<td>${entry.subjectCount}</td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise><tr><td colspan="4" class="text-center text-secondary py-4">Leaderboard will appear when marks have been recorded.</td></tr></c:otherwise>
							</c:choose>
						</tbody>
					</table>
				</div>
			</section>
		</main>
	</div>
</div>
</body>
</html>
