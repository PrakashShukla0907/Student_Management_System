<!-- main body -->
<div class="col-10 bg-light min-vh-100 p-4">

<!-- welcome section -->
<div class="mb-4">
<h2>
Welcome <span class="badge bg-success">${admin.userName}</span>
</h2>
</div>
<hr>

<!-- Dash board section -->
<div class="mb-4">
<h5>Dash board</h5>
<div class="text-secondary">Quick Actions</div>
</div>

<!-- Cards -->
<div class="row g-4">

<!-- view Students -->
<div class="col">
<div class="card shadow text-center p-4">
<i class="bi bi-people fs-1 mb-2"></i>
<h5>View Students</h5>
<a href="GetAllStudents" class="btn btn-primary">Open</a>
</div>
</div>

<!-- Add Students -->
<div class="col">
<div class="card shadow text-center p-4">
<i class="bi bi-person-plus fs-1 mb-2"></i>
<h5>Add Students</h5>
<a href="add-student.jsp" class="btn btn-success">Open</a>
</div>
</div>

<!-- Subjects and marks -->
<div class="col">
<div class="card shadow text-center p-4">
<i class="bi bi-journal-check fs-1 mb-2"></i>
<h5>Subjects &amp; Marks</h5>
<a href="AcademicRecords" class="btn btn-info">Open</a>
</div>
</div>

<!-- Admin Details -->
<div class="col">
<div class="card shadow text-center p-4">
<i class="bi bi-person-gear fs-1 mb-2"></i>
<h5>View Admin</h5>
<a href="admin.jsp" class="btn btn-warning">Open</a>
</div>
</div>

<!-- Dash board -->
<div class="col">
<div class="card shadow text-center p-4">
<i class="bi bi-speedometer2 fs-1 mb-2"></i>
<h5>Dash board</h5>
<a href="admin-panel.jsp" class="btn btn-dark">Open</a>
</div>
</div>

</div>
</div>













