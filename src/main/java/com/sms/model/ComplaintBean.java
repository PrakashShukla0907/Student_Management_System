package com.sms.model;

import java.sql.Timestamp;

public class ComplaintBean {
	private long id;
	private long studentId;
	private Long subjectId;
	private String firstName;
	private String lastName;
	private String subjectName;
	private String message;
	private String status;
	private String adminResponse;
	private Timestamp createdAt;
	private Timestamp updatedAt;

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }
	public long getStudentId() { return studentId; }
	public void setStudentId(long studentId) { this.studentId = studentId; }
	public Long getSubjectId() { return subjectId; }
	public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	public String getSubjectName() { return subjectName; }
	public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public String getAdminResponse() { return adminResponse; }
	public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }
	public Timestamp getCreatedAt() { return createdAt; }
	public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
	public Timestamp getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
