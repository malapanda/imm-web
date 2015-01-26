package imm.web.servlet;

import imm.web.WebAppConfig;
import imm.web.MindMapController;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String XMIND_FILE_KEY = "imm-xmind-filename";
	
	private WebAppConfig config;
	
    public MainServlet() {
        super();
        this.config = new WebAppConfig();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		MindMapController controller = new MindMapController(config, request.getSession());
		try {
			if(!controller.generateMindMap(response.getWriter())) {
				response.sendRedirect("empty.html");
			}	
		} catch (Exception  e) {
			throw new ServletException("Error while rendering mind map", e);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		MindMapController controller = new MindMapController(config, request.getSession());
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletContext servletContext = this.getServletConfig().getServletContext();
		File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
		factory.setRepository(repository);
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List<FileItem> items = upload.parseRequest(request);
			if (items.size() > 0) {
			    FileItem item = items.get(0);
			    if (!item.isFormField()) {
			    	controller.upload(item);
			    }
			    controller.generateMindMap(response.getWriter());
			}
		} catch (Exception e) {
			throw new ServletException("Error while uploading file ", e);
		}
	}
	
}
