package com.jtudy.git.measure;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;

public class CommitsPerUser implements IMeasureLog {

	@Override
	public void measure(RevCommit commit, Git git) {
		//System.out.println("LogCommit: " + commit.getCommitTime() + " - " + commit.getAuthorIdent().getWhen() + " - " + commit.getAuthorIdent().getName() + " - " + commit.getShortMessage());
	}

	@Override
	public void calculate() {
		
	}

	@Override
	public void report() {
		
	}
	
	
	
}
