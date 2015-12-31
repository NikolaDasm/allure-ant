package ru.yandex.qatools.allure.ant;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.Reference;

public class AllureTask extends Task {
	
	private String launcher = "ru.yandex.qatools.allure.ant.AllureLauncher";
	
	private String resultsDirectory = "build/allure-results/";
	private String reportDirectory = "build/allure-report/";
	private String allureMain = "ru.yandex.qatools.allure.AllureMain";
	private String propertiesFilePath = "report.properties";
	private Reference allureClassPathRef;
	
	public void setSrc(String dir) {
		resultsDirectory = dir;
	}
	
	public void setDest(String dir) {
		reportDirectory = dir;
	}
	
	public void setMain(String clazz) {
		allureMain = clazz;
	}
	
	public void setPfile(String filePath) {
		propertiesFilePath = filePath;
	}
	
	public void setClasspathRef(Reference refid) {
		allureClassPathRef = refid;
	}
	
	public void execute() {
		log("Generate Allure report to " + reportDirectory);
		if (resultsDirectory.isEmpty())
			log("Allure report was skipped because there is no results directories found.");
		List<String> parameters = new ArrayList<>();
		Path resultsDirectory = Paths.get(this.resultsDirectory);
		Path reportDirectory = Paths.get(this.reportDirectory);
		parameters.add(allureMain);
		parameters.add(propertiesFilePath);
		parameters.add(resultsDirectory.toAbsolutePath().toString());
		parameters.add(reportDirectory.toAbsolutePath().toString());
		try {
			StringBuilder sb = new StringBuilder();
			for (String parameter : parameters) {
				sb.append(parameter).append(" ");
			}
			Java javaTask = new Java();
			javaTask.setNewenvironment(true);
			javaTask.setTaskName("allure-report");
			javaTask.setProject(getProject());
			javaTask.setFork(true);
			javaTask.setFailonerror(true);
			javaTask.setClassname(launcher);
			Argument jvmArgs = javaTask.createJvmarg();
			jvmArgs.setLine("-Xms128m");
			Argument taskArgs = javaTask.createArg();
			taskArgs.setLine(sb.toString());
			javaTask.setClasspathRef(allureClassPathRef);			
			javaTask.init();
			javaTask.executeJava();
		} catch (Exception e) {
			throw new RuntimeException("Could not generate the report ", e);
		}
	}
}
