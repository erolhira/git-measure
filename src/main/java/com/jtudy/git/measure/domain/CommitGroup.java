package com.jtudy.git.measure.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CommitGroup {
	
	private Author author;
	
	private int fromLineCount;
	private int toLineCount;
	
	private List<CommitInfo> commitInfoList = new ArrayList<>();
	
	public void add(CommitInfo commitInfo) {
		commitInfoList.add(commitInfo);
	}
	
	public void updateLineCounts() {
		int froms = 0;
		int tos = 0;
		for(CommitInfo commitInfo : commitInfoList) {
			froms += commitInfo.getFromLineCount();
			tos += commitInfo.getToLineCount();
		}
		fromLineCount = froms;
		toLineCount = tos;
	}
}
