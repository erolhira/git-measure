package com.jtudy.git.measure.domain;

import org.eclipse.jgit.diff.Edit;

import lombok.Data;

@Data
public class ChangeInfo {
	
	private CommitInfo commitInfo;
	private int fromLineCount;
	private int toLineCount;
	
	public ChangeInfo(Edit edit, CommitInfo commitInfo) {
		this.fromLineCount = edit.getEndA() - edit.getBeginA() + 1;
		this.toLineCount = edit.getEndB() - edit.getBeginB() + 1;
		this.commitInfo = commitInfo;
	}
}
