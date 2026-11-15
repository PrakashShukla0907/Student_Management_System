package com.sms.model;

import java.math.BigDecimal;

public class LeaderboardEntryBean {
	private int rank;
	private Long studentId;
	private String firstName;
	private String lastName;
	private BigDecimal averageMarks;
	private int subjectCount;

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

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

	public BigDecimal getAverageMarks() {
		return averageMarks;
	}

	public void setAverageMarks(BigDecimal averageMarks) {
		this.averageMarks = averageMarks;
	}

	public int getSubjectCount() {
		return subjectCount;
	}

	public void setSubjectCount(int subjectCount) {
		this.subjectCount = subjectCount;
	}
}
