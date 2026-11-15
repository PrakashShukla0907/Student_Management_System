package com.sms.model;

import java.math.BigDecimal;
import java.util.Date;

public class AcademicRecordBean {
	private Long studentId;
	private String firstName;
	private String lastName;
	private String subjectName;
	private BigDecimal marks;
	private boolean passed;
	private long markId;
	private String parentMobile;
	private String parentCallStatus;
	private Long scheduledCallAtEpochMillis;
	private String scheduledCallStatus;

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public BigDecimal getMarks() {
		return marks;
	}

	public void setMarks(BigDecimal marks) {
		this.marks = marks;
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public long getMarkId() {
		return markId;
	}

	public void setMarkId(long markId) {
		this.markId = markId;
	}

	public String getParentMobile() {
		return parentMobile;
	}

	public void setParentMobile(String parentMobile) {
		this.parentMobile = parentMobile;
	}

	public String getParentCallStatus() {
		return parentCallStatus;
	}

	public void setParentCallStatus(String parentCallStatus) {
		this.parentCallStatus = parentCallStatus;
	}

	public Long getScheduledCallAtEpochMillis() {
		return scheduledCallAtEpochMillis;
	}

	public Date getScheduledCallAt() {
		return scheduledCallAtEpochMillis == null ? null : new Date(scheduledCallAtEpochMillis.longValue());
	}

	public void setScheduledCallAtEpochMillis(Long scheduledCallAtEpochMillis) {
		this.scheduledCallAtEpochMillis = scheduledCallAtEpochMillis;
	}

	public String getScheduledCallStatus() {
		return scheduledCallStatus;
	}

	public void setScheduledCallStatus(String scheduledCallStatus) {
		this.scheduledCallStatus = scheduledCallStatus;
	}
}
