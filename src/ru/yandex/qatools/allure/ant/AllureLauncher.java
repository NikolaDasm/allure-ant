package ru.yandex.qatools.allure.ant;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AllureLauncher {

	public static final String MAIN = "main";
	public static final String ALLURE_OLD_PROPERTIES = "allure.properties";
	public static final String ALLURE_NEW_PROPERTIES = "report.properties";
		private static String propertiesFilePath = "report.properties";
	
	public static void main(String[] arg) throws Exception {
		String allureMain = arg[0];
		propertiesFilePath = arg[1];
		ClassLoader loader = AllureLauncher.class.getClassLoader();
		Class<?> clazz = loader.loadClass(allureMain);
		Method main = clazz.getMethod(MAIN, String[].class);
		readPropertiesFile();
		readPropertiesFileFromClasspath(ALLURE_OLD_PROPERTIES);
		readPropertiesFileFromClasspath(ALLURE_NEW_PROPERTIES);
		List<String> parameters = new ArrayList<>();
		for (int i=2; i<arg.length;i++) {
			parameters.add(arg[i]);
		}
		main.invoke(null, new Object[]{parameters.toArray(new String[parameters.size()])});
	}
	
	private static void readPropertiesFile() throws IOException {
		Path path = Paths.get(propertiesFilePath);
		if (Files.exists(path)) {
			try (InputStream is = Files.newInputStream(path)) {
				readPropertiesFromStream(is);
			}
		}
	}
	
	private static void readPropertiesFromStream(InputStream is) throws IOException {
		if (is == null) return;
		System.getProperties().load(is);
	}
	
	private static void readPropertiesFileFromClasspath(String propertiesFileName) throws IOException {
		try (InputStream is = AllureLauncher.class.getClassLoader().getResourceAsStream(propertiesFileName)) {
			readPropertiesFromStream(is);
		}
	}
}
