package com.jtudy.git.measure;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import com.jtudy.git.measure.domain.Author;
import com.jtudy.git.measure.domain.ChangeGroup;
import com.jtudy.git.measure.domain.ChangeInfo;
import com.jtudy.git.measure.domain.CommitGroup;
import com.jtudy.git.measure.domain.CommitInfo;

public class LocPerUser implements IMeasureLog {
	
	private Map<String, ChangeGroup> fileChangeMap = new TreeMap<>();
	private TreeMap<Author, CommitGroup> commitMapPerAuthor = new TreeMap<>();	
	private TreeMap<Author, Map<String, ChangeGroup>> fileChangeMapPerAuthor = new TreeMap<>();
		
	@Override
	public void measure(RevCommit commit, Git git) {
		
		try {
			if(commit.getParentCount() == 0) {
				return;
			}
			
			Author author = new Author(commit.getAuthorIdent());
			CommitGroup commitGroup = getCommitGroup(author);
			CommitInfo commitInfo = new CommitInfo();
			commitGroup.add(commitInfo);
					
			RevCommit oldCommit = GitHelper.getCommitByRevisionId(commit.getParent(0).getId(), git.getRepository());
						
			CanonicalTreeParser oldTree = GitHelper.getCommitTree(oldCommit, git);
			CanonicalTreeParser newTree = GitHelper.getCommitTree(commit, git);

			try(DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {			
				diffFormatter.setRepository(git.getRepository());
				diffFormatter.setContext(0);
				List<DiffEntry> entries = diffFormatter.scan(oldTree, newTree);
				
				entries.forEach(entry -> {toFileEditList(diffFormatter, entry).forEach(edit -> {
						ChangeInfo changeInfo = addToCommitMap(edit, commitInfo);
						addToFilePerUserMap(entry.getNewPath(), changeInfo, author);
						addToFileChangeMap(entry.getNewPath(), changeInfo);
					});
				});
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}			
	}
	
	private void calculateFileChangePerUserCounts() {		
		fileChangeMapPerAuthor.values().stream() 	//Stream<Map<String, ChangeGroup>>
			.map(cgMap -> cgMap.values()) 			//Stream<Collection<ChangeGroup>> 
			.flatMap(cgSet -> cgSet.stream()) 		//Stream<ChangeGroup>
			.forEach(changeGroup -> changeGroup.updateLineCounts());
		;
	}
	
	private void calculateFileChangeCounts() {
		fileChangeMap.values().forEach(changeGroup -> changeGroup.updateLineCounts());
	}

	private void calculateCommitGroupCounts() {
		commitMapPerAuthor.values().forEach(commitGroup -> commitGroup.updateLineCounts());
	}

	private EditList toFileEditList(DiffFormatter diffFormatter, DiffEntry entry) {
		try {
			return diffFormatter.toFileHeader(entry).toEditList();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private CommitGroup getCommitGroup(Author author) {
		CommitGroup commitGroup = commitMapPerAuthor.get(author);
		if(commitGroup == null) {
			commitGroup = new CommitGroup();
			commitGroup.setAuthor(author);
			commitMapPerAuthor.put(author, commitGroup);
		}
		return commitGroup;
	}		

	private ChangeInfo addToCommitMap(Edit edit, CommitInfo commitInfo) {
		
		ChangeInfo changeInfo = new ChangeInfo(edit, commitInfo);
		commitInfo.addChangeInfo(changeInfo);
		return changeInfo;
	}

	private void addToFilePerUserMap(String file, ChangeInfo changeInfo, Author author) {
		
		Map<String, ChangeGroup> changeMap = fileChangeMapPerAuthor.get(author);
		ChangeGroup changeGroup = null;
		if(changeMap == null) {
			changeMap = new TreeMap<>();
			changeGroup = new ChangeGroup();
			changeMap.put(file, changeGroup);
			fileChangeMapPerAuthor.put(author, changeMap);
		} else {
			changeGroup = changeMap.get(file);	
			if(changeGroup == null) {
				changeGroup = new ChangeGroup();
				changeMap.put(file, changeGroup);
			}
		}
		changeGroup.add(changeInfo);
	}
	
	private void addToFileChangeMap(String file, ChangeInfo changeInfo) {
		
		ChangeGroup changeGroup = fileChangeMap.get(file);
		if(changeGroup == null) {
			changeGroup = new ChangeGroup();
			fileChangeMap.put(file, changeGroup);
		}
		changeGroup.add(changeInfo);
	}

	@Override
	public void calculate() {
		calculateCommitGroupCounts();
		calculateFileChangePerUserCounts();
		calculateFileChangeCounts();
	}

	@Override
	public void report() {
		
		String format = "%-50s %12s %12s%n";
		System.out.printf(format, "Author", "Change count", "Commit Count");
		for(CommitGroup commitGroup : commitMapPerAuthor.values()) {
			System.out.printf(format, commitGroup.getAuthor().getName(), String.valueOf(commitGroup.getToLineCount()), String.valueOf(commitGroup.getCommitInfoList().size()));			
		}
		System.out.println("--------------------------------------------------------------------");
		System.out.println("--------------------------------------------------------------------");
		System.out.println("--------------------------------------------------------------------");
		
		format = "%-50s %12s%n";
		System.out.printf(format, "Author", "File Count");
		for(Map.Entry<Author, Map<String, ChangeGroup>> entry : fileChangeMapPerAuthor.entrySet()) {
			Author author = entry.getKey();
			System.out.printf(format, author.getName(), entry.getValue().size());
		}
	}
}
