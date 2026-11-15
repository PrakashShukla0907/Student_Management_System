package com.sms.model;

import java.math.BigDecimal;

public class ParentCallBean {
	private long markId;
	private long studentId;
	private long subjectId;
	private String firstName;
	private String lastName;
	private String subjectName;
	private String parentMobile;
	private BigDecimal marks;
	private String callStatus;
	private String callSid;

	public long getMarkId() { return markId; }
	public void setMarkId(long markId) { this.markId = markId; }
	public long getStudentId() { return studentId; }
	public void setStudentId(long studentId) { this.studentId = studentId; }
	public long getSubjectId() { return subjectId; }
	public void setSubjectId(long subjectId) { this.subjectId = subjectId; }
	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	public String getSubjectName() { return subjectName; }
	public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
	public String getParentMobile() { return parentMobile; }
	public void setParentMobile(String parentMobile) { this.parentMobile = parentMobile; }
	public BigDecimal getMarks() { return marks; }
	public void setMarks(BigDecimal marks) { this.marks = marks; }
	public String getCallStatus() { return callStatus; }
	public void setCallStatus(String callStatus) { this.callStatus = callStatus; }
	public String getCallSid() { return callSid; }
	public void setCallSid(String callSid) { this.callSid = callSid; }
}
