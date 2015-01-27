package imm.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class WebAppConfig {

	public Properties properties ;
	
	public String mindMapTemplateName ;
	
	public File immRoot;
		
	public String userResourcesDirName;
		
	public File userResourcesDir;
	
	public String resourcesDirName;
	
	public File resourcesDir;
		
	public String uploadsUrlPath;

	public WebAppConfig() {
		properties = loadProps();
		mindMapTemplateName = prop("mindMapTemplateName", "impress.ftl");
		immRoot = new File(prop("immRoot", "/home/panda/imm"));
		userResourcesDirName = prop("userResourcesDirName", "user_resources");
		userResourcesDir = new File(immRoot, userResourcesDirName);
		resourcesDirName = prop("resourcesDirName","resources");
		resourcesDir = new File(immRoot, resourcesDirName);
		uploadsUrlPath = prop("uploadsUrlPath", "uploads");
	}

	
	private Properties loadProps() {
		Properties props = new Properties();
		try {
			InputStream resourceAsStream = WebAppConfig.class.getResourceAsStream("/imm.properties");
			if (resourceAsStream != null) {
				props.load(resourceAsStream);
				System.out.println(">>> Properties loaded");
				resourceAsStream.close();
			} else {
				System.out.println(">>> Properties not loaded");
			}
		} catch (IOException e) {
			System.out.println(">>> Properties not loaded");
		}
		return props;
	}
	
	private String prop(String key, String def) {
		String prop = properties.getProperty(key);
		if (prop != null && prop.length()>0)
			return prop;
		else
			return def;
	}
	
}
