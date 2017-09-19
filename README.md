# account-service
Example Spring Boot app for "Accounts"

Push it to Cloud Foundry!
package com.esrx.services.bootconversion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class MuleToSpringBootConverstion {

	String orgName = "eTech";
	String servicename = "SessionManagementService";
	String serviceVersion = "1.0.0";
	String groupId = "com.esrx.services";
	String jaxRsImplArtifactId = "sessionmanagement";
	String jaxRsImplArtifactVersion = "4.1.5";
	String servicePackage = "com.esrx.services.sessionmanagement";
	String muleDir = "/Users/e45588/Work/Projects/etech-sessionmanagementservice/SessionManagementMule";
	String springBootbDir = "/Users/e45588/Work/Projects/etech-sessionmanagementservice/test/SessionManagementBoot1";

	
	String javaDir = "src/main/java";
	String resourceDir = "src/main/resources";
	
	String muleResource = muleDir + "/" + resourceDir;


	String servicePackageDir = springBootbDir + "/" + javaDir + "/" + servicePackage.replace(".", "/");
	String serviceResourceDir = springBootbDir + "/" + resourceDir;
	String serviceResourceContextDir = serviceResourceDir + "/context";
	String serviceResourcePropertiesDir = serviceResourceDir + "/properties";
	String serviceResourcePbdDir = serviceResourceDir + "/pbd";
	String serviceLaunchDir = springBootbDir + "/launch";

	public static void main(String[] args) {

		try {

			MuleToSpringBootConverstion muleToSpringBootConverstion = new MuleToSpringBootConverstion();

			muleToSpringBootConverstion.copyBootClasses();

			muleToSpringBootConverstion.copyPomFile();

			muleToSpringBootConverstion.copyCerts();

			muleToSpringBootConverstion.moveContextFiles();

			muleToSpringBootConverstion.movePropertiesFiles();

			muleToSpringBootConverstion.movePbdFiles();

			muleToSpringBootConverstion.copyLaunchFile();
			
			muleToSpringBootConverstion.copyBuildFiles();

			System.out.println("Done");

		} catch (Exception sae) {
			sae.printStackTrace();
		}

	}

	public void copyBuildFiles() throws IOException {		
		// Copy Build Files
		Resource resource = new ClassPathResource("boot/build");
		FileUtils.copyDirectory(resource.getFile(), new File(springBootbDir).getParentFile());
		
		replaceTextInFile(new File(springBootbDir).getParentFile() + "/Jenkinsfile" , "APP_NAME",	servicename);
		replaceTextInFile(new File(springBootbDir).getParentFile() + "/Jenkinsfile" , "ORG_NAME",	orgName);
		replaceTextInFile(new File(springBootbDir).getParentFile() + "/manifest.yml" , "APP_NAME",	servicename.toLowerCase());
		replaceTextInFile(new File(springBootbDir).getParentFile() + "/pom.xml" , "APP_NAME",	servicename);
	}

	public void copyLaunchFile() throws IOException {
		Resource resource = new ClassPathResource("boot/launch/spring-boot.launch");
		FileUtils.copyFile(resource.getFile(),
				new File(serviceLaunchDir + "/" + servicename + "-" + resource.getFile().getName()));

		replaceTextInFile(serviceLaunchDir + "/" + servicename + "-" + resource.getFile().getName(), "SERVICE_NAME",
				servicename.toLowerCase());
		replaceTextInFile(serviceLaunchDir + "/" + servicename + "-" + resource.getFile().getName(),
				"SERVICE_PACKAGE_DIR", servicePackage.replace(".", "/"));
		
		replaceTextInFile(serviceLaunchDir + "/" + servicename + "-" + resource.getFile().getName(),
				"SERVICE_PACKAGE", servicePackage);
	}

	public void movePropertiesFiles() throws IOException {
		
		// Copy properties Files from Mule
		FileUtils.copyDirectory(new File(muleResource + "/properties/"), new File(serviceResourcePropertiesDir));
		
		// Copy Spring Boot Properties
		Resource log4jResource = new ClassPathResource("boot/properties/log4j.properties");
		FileUtils.copyFile(log4jResource.getFile(), new File(serviceResourcePropertiesDir+"/common/log4j.properties"));
		
		Resource bootstrapResource = new ClassPathResource("boot/properties/bootstrap.properties");
		FileUtils.copyFile(bootstrapResource.getFile(), new File(serviceResourcePropertiesDir+"/bootstrap.properties"));
		replaceTextInFile(serviceResourcePropertiesDir+"/bootstrap.properties", "APP_NAME",servicename);

		Resource resource = new ClassPathResource("boot/properties/application.properties");
		List<String> springBootProperties = FileUtils.readLines(resource.getFile());

		Collection<File> propertiesFiles = FileUtils.listFiles(new File(serviceResourceDir + "/properties"), new String[] { "properties" }, true);
		for (File propertiesFile : propertiesFiles) {
			if (propertiesFile.getName().equalsIgnoreCase("application.properties")) {
				List<String> applicationPropertiesLines = FileUtils.readLines(propertiesFile);

				applicationPropertiesLines.addAll(springBootProperties);
				FileUtils.writeLines(propertiesFile, applicationPropertiesLines);
				replaceTextInFile(propertiesFile, "APP_NAME",servicename);
			}
		}
		
		removeMuleProperties();
	}
	
	private void removeMuleProperties() throws IOException {
		
		List<String> linesToRemove = FileUtils.readLines(new ClassPathResource("boot/properties/entriestoremove.properties").getFile());		
		
		Collection<File> propertiesFiles = FileUtils.listFiles(new File(serviceResourceDir + "/properties"),	new String[] { "properties" }, true);

		for (File propertiesFile : propertiesFiles) {			
			List<String> properties = FileUtils.readLines(propertiesFile);
			List<String> lineToRemove = new ArrayList<String>();
			for (String property : properties) {
				for(String removeProperty: linesToRemove){
					if (property.contains(removeProperty)) {
						lineToRemove.add(property);
					}
				}				
			}
			properties.removeAll(lineToRemove);
			FileUtils.writeLines(propertiesFile, properties);
		}
	}

	public void movePbdFiles() throws IOException {
		// Copy pbd Files from Mule
		FileUtils.copyDirectory(new File(muleDir + "/wily"), new File(serviceResourcePbdDir));

	}

	public void moveContextFiles() throws IOException {
		// Copy context Files from Mule
		FileUtils.copyDirectory(new File(muleResource + "/context"), new File(serviceResourceContextDir));
		Resource resource = new ClassPathResource("boot/context");
		FileUtils.copyDirectory(resource.getFile(), new File(serviceResourceContextDir));
		FileUtils.copyFileToDirectory(new File(muleResource + "/application-config.xml"), new File(serviceResourceDir));

		addAdditionalDependencesInConfig();
		
		removeMuleDependencesInConfig();

		removeMuleDependencesInContext();

		addCertsChangesToContextFiles();
		
	}

	private void addCertsChangesToContextFiles() throws IOException {
		
		Collection<File> contextFiles = FileUtils.listFiles(new File(serviceResourceDir + "/context"),
				new String[] { "xml" }, true);

		for (File contextFile : contextFiles) {
			replaceTextInFile(contextFile, "$ENV{HOST_KEYSTORE}", "${com.esrx.app.security.ssl.keyStore}");
			replaceTextInFile(contextFile, "$ENV{HOST_KEYSTORE_PWD}", "${com.esrx.app.security.ssl.keyStorePassword}");
			replaceTextInFile(contextFile, "$ENV{HOST_TRUSTSTORE}", "${com.esrx.app.security.ssl.trustStore}");
			replaceTextInFile(contextFile, "$ENV{HOST_TRUSTSTORE_PWD}", "${com.esrx.app.security.ssl.trustStorePassword}");
			replaceTextInFile(contextFile, "exceptionClassName", "exceptionClass");
		}
	}

	private void addAdditionalDependencesInConfig() throws IOException {
		
		String applicationConfigPath = serviceResourceDir + "/application-config.xml";
		
		//Remove Comments
		String contextFileString = FileUtils.readFileToString(new File(applicationConfigPath));
		contextFileString = contextFileString.replaceAll("(?s)<!--.*?-->", "");
		FileUtils.writeStringToFile(new File(applicationConfigPath), contextFileString);		
		
		//Add addtional-context.xml
		List<String> contextLines = FileUtils.readLines(new File(applicationConfigPath));
		List<String> newContextLines = new ArrayList<String>();
		boolean isNotAddtionalContextAdded = true;
		for (String contextLine : contextLines) {

			if (contextLine.contains("context") && isNotAddtionalContextAdded) {
				newContextLines.add("<import resource=\"classpath:context/addtional-context.xml\" />");
				isNotAddtionalContextAdded = false;
			}

			newContextLines.add(contextLine);
		}

		FileUtils.writeLines(new File(applicationConfigPath), newContextLines);

	}

	private void removeMuleDependencesInContext() throws IOException {
		Collection<File> contextFiles = FileUtils.listFiles(new File(serviceResourceDir + "/context"),
				new String[] { "xml" }, true);

		for (File contextFile : contextFiles) {
			String contextFileString = FileUtils.readFileToString(contextFile);
			contextFileString = contextFileString.replaceAll("(?s)<!--.*?-->", "");
			FileUtils.writeStringToFile(contextFile, contextFileString);

		}

		contextFiles = FileUtils.listFiles(new File(serviceResourceDir + "/context"), new String[] { "xml" }, true);
		for (File contextFile : contextFiles) {
			
			List<String> contextLines = FileUtils.readLines(contextFile);
			List<String> lineToRemove = new ArrayList<String>();
			for (String contextLine : contextLines) {
				if (contextLine.contains("mule/")) {
					lineToRemove.add(contextLine);
				}
			}
			contextLines.removeAll(lineToRemove);
			FileUtils.writeLines(contextFile, contextLines);
			replaceTextInFile(contextFile,
					"com.express_scripts.inf.mule.rs.InfoResource", servicePackage + ".DummyInfoResource");
			replaceTextInFile(contextFile,
					"com.express_scripts.inf.jersey.deptracking.DependencyTrackingClientFilter", servicePackage + ".DependencyTrackingClientFilter");
		}

	}

	private void removeMuleDependencesInConfig() throws IOException {
		List<String> applicationContextLines = FileUtils
				.readLines(new File(serviceResourceDir + "/application-config.xml"));
		List<String> lineToRemove = new ArrayList<String>();
		for (String contextLine : applicationContextLines) {
			if (contextLine.contains("mule/") || contextLine.contains("SpringAopBaseXtraceContext.xml")) {
				lineToRemove.add(contextLine);
			}
		}
		applicationContextLines.removeAll(lineToRemove);
		FileUtils.writeLines(new File(serviceResourceDir + "/application-config.xml"), applicationContextLines);
	}

	public void copyPomFile() throws IOException, XmlPullParserException {
		// copy pom
		Resource resource = new ClassPathResource("boot/pom");
		FileUtils.copyDirectory(resource.getFile(), new File(springBootbDir));

		replaceTextInFile(springBootbDir + "/pom.xml", "SERVICE_NAME", servicename.toLowerCase());

		replaceTextInFile(springBootbDir + "/pom.xml", "SERVICE_VERSION", serviceVersion);

		replaceTextInFile(springBootbDir + "/pom.xml", "GROUP_ID", groupId);

		addMavenDependencies();
	}

	private void addMavenDependencies() throws FileNotFoundException, IOException, XmlPullParserException {
		Dependency tobeAdded = new Dependency();
		tobeAdded.setGroupId(groupId);
		tobeAdded.setArtifactId(jaxRsImplArtifactId);
		tobeAdded.setVersion(jaxRsImplArtifactVersion);

		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = reader.read(new FileReader(springBootbDir + "/pom.xml"));
		model.addDependency(tobeAdded);
		MavenXpp3Writer writer = new MavenXpp3Writer();
		OutputStream pomOutputSteam = new FileOutputStream(new File(springBootbDir + "/pom.xml"));
		writer.write(pomOutputSteam, model);
	}

	public void copyBootClasses() throws IOException {
		// Copy Boot Files
		Resource resource = new ClassPathResource("boot/classfiles");
		FileUtils.copyDirectory(resource.getFile(), new File(servicePackageDir));

		Collection<File> javaFiles = FileUtils.listFiles(new File(servicePackageDir), new String[] { "java" }, true);
		for (File javaFile : javaFiles) {
			replaceTextInFile(javaFile, "SERVICE_PACKAGE", servicePackage);
		}
	}

	private void replaceTextInFile(String filePath, String fromString, String toString) throws IOException {
		replaceTextInFile(new File(filePath), fromString, toString);
	}
	
	private void replaceTextInFile(File file, String fromString, String toString) throws IOException {
		String webSecConfigFile = FileUtils.readFileToString(file);
		webSecConfigFile = webSecConfigFile.replace(fromString, toString);
		FileUtils.writeStringToFile(file, webSecConfigFile);

	}

	public void copyCerts() throws IOException {
		// Copy Boot Files
		Resource resource = new ClassPathResource("boot/certs");
		FileUtils.copyDirectory(resource.getFile(), new File(serviceResourceDir));
	}
}
