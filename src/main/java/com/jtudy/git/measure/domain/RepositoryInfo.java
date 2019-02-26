package com.jtudy.git.measure.domain;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class RepositoryInfo {
	
	private String rootDirectory;
	private String currentBranch;
}
