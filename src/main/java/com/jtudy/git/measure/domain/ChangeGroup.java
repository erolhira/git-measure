package com.jtudy.git.measure.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ChangeGroup {

	private int fromLineCount;
	private int toLineCount;
	
	private List<ChangeInfo> changeInfoList = new ArrayList<>();
	
	public void add(ChangeInfo changeInfo) {
		changeInfoList.add(changeInfo);
	}
	
	public void updateLineCounts() {
		int froms = 0;
		int tos = 0;
		for(ChangeInfo changeInfo : changeInfoList) {
			froms += changeInfo.getFromLineCount();
			tos += changeInfo.getToLineCount();
		}
		fromLineCount = froms;
		toLineCount = tos;
	}
}
