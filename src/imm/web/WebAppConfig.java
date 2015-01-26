package imm.web;

import java.io.File;

public class WebAppConfig {

	public String mindMapTemplateName = "impress.ftl";
	
	public File immRoot = new File("/home/panda/imm");
	
	
	public String userResourcesDirName = "user_resources";
		
	public File userResourcesDir = new File(immRoot, userResourcesDirName);
	
	
	public String resourcesDirName = "resources";
	
	public File resourcesDir = new File(immRoot, resourcesDirName);
		
	
	public String uploadsUrlPath = "uploads";
	
}
