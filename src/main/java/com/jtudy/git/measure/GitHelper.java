package com.jtudy.git.measure;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.AndRevFilter;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import com.jtudy.git.measure.domain.MeasureParameters;

/*
 * committer -- the user who pushes
 * author -- the user who commits
 */
public class GitHelper {
	
	/*
	 * The caller is responsible to close the repository instance when it is no longer needed.
	 */
	public static Repository openRepository(String repositoryDirectory) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder.findGitDir(new File(repositoryDirectory)).build();
    }
	
	public static RevCommit getCommitByRevisionId(String revisionId, Repository repository) throws MissingObjectException, IncorrectObjectTypeException, IOException {
		ObjectId commitId = ObjectId.fromString(revisionId);
		return getCommitByRevisionId(commitId, repository);
	}
	
	public static RevCommit getCommitByRevisionId(ObjectId revisionId, Repository repository) throws MissingObjectException, IncorrectObjectTypeException, IOException {
		RevCommit commit;
		try (RevWalk revWalk = new RevWalk(repository)) {
			commit = revWalk.parseCommit(revisionId);
		} 
		return commit;
	}
	
	public static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
		try (RevWalk walk = new RevWalk(repository)) {
			RevCommit commit = walk.parseCommit(repository.resolve(objectId));
			RevTree tree = walk.parseTree(commit.getTree().getId());

			CanonicalTreeParser treeParser = new CanonicalTreeParser();
			try (ObjectReader reader = repository.newObjectReader()) {
				treeParser.reset(reader, tree.getId());
			}
			walk.dispose();
			return treeParser;
		}
	}
	
	public static Iterable<RevCommit> commits(Repository repository, Git git, MeasureParameters params) throws Exception {
		
		LogCommand logCommand = git.log();
		RevFilter revFilter = null;
		if(params.getFromDate() != null || params.getToDate() != null) {
			RevFilter between = CommitTimeRevFilter.between(params.getFromDate(), params.getToDate());
			revFilter = AndRevFilter.create(between, RevFilter.NO_MERGES);
		} else {
			revFilter = RevFilter.NO_MERGES;
		}
		
		Iterable<RevCommit> commits = logCommand.setRevFilter(revFilter).call();
		return commits;
	}

	public static CanonicalTreeParser getCommitTree(RevCommit commit, Git git) throws IncorrectObjectTypeException, IOException {
		CanonicalTreeParser tree = new CanonicalTreeParser();
		tree.reset(git.getRepository().newObjectReader(), commit.getTree());
		return tree;
	}
}
