package com.jtudy.git.measure;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

import com.jtudy.git.measure.domain.MeasureParameters;
import com.jtudy.git.measure.domain.RepositoryInfo;

/*
 * java -jar gm.jar --since=01.07.2018 --before=17.02.2019
 */
public class GitMeasure {

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");	
	
	public static void main(String[] args) {
		
		Properties params = getParameters(args);
		
		if(params.containsKey(Params.HELP)) {
			printHelp();
		} else {
			
			try {
				String since = params.getProperty(Params.SINCE, "18.04.2005");
				Date sinceDate = since == null ? null : sdf.parse(since);				

				String before = params.getProperty(Params.BEFORE);
				Date beforeDate = before == null ? Calendar.getInstance().getTime() : sdf.parse(before);
				
				String directory = params.getProperty(Params.DIRECTORY, System.getProperty("user.dir"));
				
				RepositoryInfo repositoryInfo = RepositoryInfo.builder().rootDirectory(directory).build();
				MeasureParameters parameters = MeasureParameters.builder().fromDate(sinceDate).toDate(beforeDate).build();
				MeasureEngine.measure(repositoryInfo, parameters, LocPerUser.class);
			} catch (ParseException e) {
				System.out.println("Invalid date format. Valid format is --> dd.MM.yyyy --> 24.01.2019");
			} catch(IllegalArgumentException e) {
				if(e.getMessage().contains("setGitDir")) {
					System.out.println("The current directory must be a git directory or --dir parameter must be set to a git directory");
				} else {					
					System.out.println("Invalid call. " + e.getMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void printHelp() {
		System.out.println("Sample calls: ");
		System.out.println("gm --since=18.04.2015 --before=01.01.2019 --dir=gitRepositoryDirectory");
		System.out.println("");
		System.out.println("Default parameters when called only gm:");
		System.out.println("--since=18.04.2015");
		System.out.println("--before=sysdate");
		System.out.println("--dir=current directory");
	}

	private static Properties getParameters(String[] args) {
		Properties p  = new Properties();					
		Arrays.asList(args).forEach(arg -> {
			Optional<String> opt = Params.ALL.stream().filter(param -> arg.contains(param)).findFirst();
			if(opt.isPresent()) {
				p.setProperty(opt.get(), arg.replace(opt.get(), ""));
			}
		});
		return p;
	}
}
