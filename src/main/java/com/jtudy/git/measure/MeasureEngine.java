package com.jtudy.git.measure;

import java.util.function.Consumer;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.jtudy.git.measure.domain.MeasureParameters;
import com.jtudy.git.measure.domain.RepositoryInfo;

public class MeasureEngine {

	public static void measure(RepositoryInfo repositoryInfo, MeasureParameters params, Class<? extends IMeasureLog> measureLogClaz) throws Exception {
		
		IMeasureLog measureLog = measureLogClaz.newInstance();
		try (Repository repository = GitHelper.openRepository(repositoryInfo.getRootDirectory())) {
			try (Git git = new Git(repository)) {
				Consumer<RevCommit> measure = t -> measureLog.measure(t, git);
				GitHelper.commits(repository, git, params).forEach(measure);
				measureLog.calculate();
				measureLog.report();
			}
		}
	}
}
