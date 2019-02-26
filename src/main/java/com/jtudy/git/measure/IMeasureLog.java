package com.jtudy.git.measure;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;

public interface IMeasureLog {
	
	public void measure(RevCommit commit, Git git);
	public void calculate();
	public void report();
}
