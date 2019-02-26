package com.jtudy.git.measure.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CommitInfo {
	
	private int fromLineCount;
	private int toLineCount;
	
	private List<ChangeInfo> changeInfoList = new ArrayList<>();
	
	public void addChangeInfo(ChangeInfo changeInfo) {
		changeInfoList.add(changeInfo);
		fromLineCount += changeInfo.getFromLineCount();
		toLineCount += changeInfo.getToLineCount();
	}
}
