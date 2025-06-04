package com.chungang.capstone.openstep.domain.Task.entity;

public enum TaskStatus {
	FORKED(5),
	PROGRESS(10),
	PR(30),
	REVIEW(15),
	MERGED(100),
	REJECTED(5),
	NOT_STARTED(0);

	private final int xp;

	TaskStatus(int xp) {
		this.xp = xp;
	}

	public int getXp() {
		return xp;
	}
}
