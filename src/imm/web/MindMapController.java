package imm.web;


import imm.impress.beans.MindMap;
import imm.impress.generator.MindMapGenerator;
import imm.xmind.adapter.WorkbookLoader;
import imm.xmind.algorithm.ImmConfiguration;
import imm.xmind.algorithm.ImmAlgorithm;
import imm.xmind.beans.XmindWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.xmind.core.CoreException;

import freemarker.template.TemplateException;

public class MindMapController {	

	private static final String MINDMAP_FILENAME = "uploaded.xmind";
	
	private static final String RESOURCES_DIR_NAME = "content";
	
	public static final String XMIND_DIR_KEY = "imm-xmind-dir";
	
	private WebAppConfig config;

	private HttpSession session;

	public MindMapController(WebAppConfig config, HttpSession session) {
		this.config = config;
		this.session =  session;
	}
	
	public boolean generateMindMap(PrintWriter printWriter) throws IOException, CoreException, TemplateException {
		File file = getMindMap();
		if (file == null) {
			return false;
		}
		XmindWorkbook workbook = new XmindWorkbook(WorkbookLoader.loadWorkbook(file));
		ImmAlgorithm simpleAlgorithm = new ImmAlgorithm(workbook, prepareAlgorithmConfig());
		MindMap mindMap = simpleAlgorithm.generate();
		MindMapGenerator generator = new MindMapGenerator(config.mindMapTemplateName, config.resourcesDir);		
		generator.generateMindMap(mindMap, printWriter);
		return true;
	}

	private ImmConfiguration prepareAlgorithmConfig() {
		String prefix = String.format("%s/%s/%s", config.uploadsUrlPath, getUploadDir().getName(), RESOURCES_DIR_NAME);
		return new ImmConfiguration(380, 130, 0, prefix);
	}
	
	public void upload(FileItem item) throws IOException {
		File oldUpload = getUploadDir();
		File uploadDir = null;
		do {
			uploadDir = new File(config.userResourcesDir, UUID.randomUUID().toString());
		} while (uploadDir.exists());
		try {
			FileUtils.forceMkdir(uploadDir);
			processUploadedFile(item, uploadDir);
			setVersion(uploadDir);
			forceDelete(oldUpload);
		} catch (Exception e) {
			forceDelete(uploadDir);
			setVersion(oldUpload);
			throw new IOException("Error during upload", e);
		}
	}

	private void forceDelete(File oldUpload) {
		try {
			if (oldUpload != null) {
				FileUtils.forceDelete(oldUpload);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void setVersion(File file ) {
		if (file == null) {
			session.setAttribute(XMIND_DIR_KEY, null);
		} else {
			session.setAttribute(XMIND_DIR_KEY, file.getName());
		}
	}

	private void processUploadedFile(FileItem item, File uploadDir) throws Exception  {
		File mindMap = new File(uploadDir, MINDMAP_FILENAME);
		File resources = new File(uploadDir, RESOURCES_DIR_NAME);
		FileUtils.forceMkdir(resources);
		item.write(mindMap);
		WorkbookLoader.loadWorkbookToDir(mindMap, resources);
	}

	private File getMindMap() {
		File uploadDir = getUploadDir();
		if (uploadDir == null) {
			return null;
		}
		return new File(uploadDir, MINDMAP_FILENAME);
	}
	
	private File getUploadDir() {
		String userDirName = (String)session.getAttribute(XMIND_DIR_KEY);
		if (userDirName == null) {
			return null;
		} else {
			return new File(config.userResourcesDir, userDirName);
		}
	}

}
