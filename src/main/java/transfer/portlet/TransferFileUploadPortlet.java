package transfer.portlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import userdocumentupload.constants.TransferFileUploadPortletKeys;

import javax.mail.internet.InternetAddress;
import javax.portlet.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author jverweij
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=TransferFileUpload",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + TransferFileUploadPortletKeys.TRANSFERFILEUPLOAD,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
/**
 * Portlet to upload documents into the D&M Library
 * Code is based on https://liferayiseasy.blogspot.com/2015/07/how-to-upload-documents-and-files-in.html
 */
public class TransferFileUploadPortlet extends MVCPortlet {
	private static String ROOT_FOLDER_NAME = "upload_folder";//PortletProps.get("fileupload.folder.name");
	private static String ROOT_FOLDER_DESCRIPTION = "defined by upload portlet";//PortletProps.get("fileupload.folder.description");
	private static long PARENT_FOLDER_ID = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	private final static String HTMLBREAK = "<br/>";

	public void uploadDocument(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException, PortletException, PortalException, SystemException
	{
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		createFolder(actionRequest, themeDisplay);
		FileEntry fileEntry = fileUpload(themeDisplay, actionRequest);
		if (fileEntry != null) {
			sendMessage(actionRequest,fileEntry);
		}
	}

	private void sendMessage(ActionRequest actionRequest, FileEntry fileEntry) {
		System.out.println("Sending mail...");

		LiferayPortletURL downloadUrl =  PortletURLFactoryUtil.create(actionRequest, PortalUtil.getPortletId(actionRequest), PortletRequest.RENDER_PHASE);
		downloadUrl.setParameter("mvcRenderCommandName","/transfer/r/download");
		downloadUrl.setParameter("uuid",fileEntry.getUuid());

		UploadPortletRequest uploadPortletRequest = PortalUtil.getUploadPortletRequest(actionRequest);

		//todo get these from config and replace variables
		String subject = "File Transfer is ready for you!";
		StringBuilder body = new StringBuilder();
		body.append(uploadPortletRequest.getParameter("message"));
		body.append(HTMLBREAK);
		body.append(fileEntry.getUuid());
		body.append(HTMLBREAK);
		body.append(fileEntry.getGroupId());
		body.append(HTMLBREAK);
		body.append(downloadUrl.toString());

		// from
		InternetAddress from = new InternetAddress();
		from.setAddress(uploadPortletRequest.getParameter("from"));

		// to
		InternetAddress to = new InternetAddress();
		to.setAddress(uploadPortletRequest.getParameter("to"));

		MailMessage mailMessage = new MailMessage(from,to,subject,body.toString(),Boolean.TRUE);

		System.out.println(_mailService.getSession().getProperties());

		_mailService.sendEmail(mailMessage);
		System.out.println("Sending mail... almost there");
	}

	public Folder createFolder(ActionRequest actionRequest, ThemeDisplay themeDisplay)
	{
		boolean folderExist = isFolderExist(themeDisplay);
		Folder folder = null;
		if (!folderExist) {
			long repositoryId = themeDisplay.getScopeGroupId();
			try {
				ServiceContext serviceContext = ServiceContextFactory.getInstance(DLFolder.class.getName(), actionRequest);
				if (!isRootFolderExist(themeDisplay)) {
					folder = DLAppServiceUtil.addFolder(repositoryId, PARENT_FOLDER_ID, ROOT_FOLDER_NAME, ROOT_FOLDER_DESCRIPTION, serviceContext);
				} else {
					folder = DLAppServiceUtil.getFolder(themeDisplay.getScopeGroupId(), PARENT_FOLDER_ID, ROOT_FOLDER_NAME);
				}
				System.out.println("Main folder: " + folder.getName() + " / " + folder.getFolderId());
				folder = DLAppServiceUtil.addFolder(repositoryId,folder.getFolderId(),Long.toString(themeDisplay.getUserId()),"user folder", serviceContext);
			} catch (PortalException e1) {
				e1.printStackTrace();
			} catch (SystemException e1) {
				e1.printStackTrace();
			}
		}
		return folder;
	}

	public boolean isRootFolderExist(ThemeDisplay themeDisplay){
		boolean folderExist = false;
		try {
			DLAppServiceUtil.getFolder(themeDisplay.getScopeGroupId(), PARENT_FOLDER_ID, ROOT_FOLDER_NAME);
			folderExist = true;
			System.out.println("Folder is already Exist");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return folderExist;
	}


	public boolean isFolderExist(ThemeDisplay themeDisplay){
		boolean folderExist = false;
		try {
			Folder folder = DLAppServiceUtil.getFolder(themeDisplay.getScopeGroupId(), PARENT_FOLDER_ID, ROOT_FOLDER_NAME);
			DLAppServiceUtil.getFolder(themeDisplay.getScopeGroupId(), folder.getFolderId(), Long.toString(themeDisplay.getUserId()));
			folderExist = true;
			System.out.println("Folder is already Exist");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return folderExist;
	}

	public Folder getFolder(ThemeDisplay themeDisplay){
		Folder folder = null;
		try {
			folder = DLAppServiceUtil.getFolder(themeDisplay.getScopeGroupId(), PARENT_FOLDER_ID, ROOT_FOLDER_NAME);
			folder = DLAppServiceUtil.getFolder(themeDisplay.getScopeGroupId(), folder.getFolderId(), Long.toString(themeDisplay.getUserId()));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return folder;
	}

	public FileEntry fileUpload(ThemeDisplay themeDisplay,ActionRequest actionRequest) throws PwdEncryptorException, JsonProcessingException {
		FileEntry f = null;
		UploadPortletRequest uploadPortletRequest = PortalUtil.getUploadPortletRequest(actionRequest);

		String fileName=uploadPortletRequest.getFileName("uploadedFile");
		File file = uploadPortletRequest.getFile("uploadedFile");
		System.out.println(file.length() / (1024 * 1024) + " mb");

		String mimeType = uploadPortletRequest.getContentType("uploadedFile");
		String title = UUID.randomUUID().toString();

		String password = uploadPortletRequest.getParameter("password").trim();
		if (!password.isEmpty()) {
			password = PasswordEncryptorUtil.encrypt(password);
		}

		ObjectMapper mapper = new ObjectMapper();
		// create a JSON object
		ObjectNode user = mapper.createObjectNode();
		user.put("expires", uploadPortletRequest.getParameter("expires"));
		user.put("password", password);
		String description = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);

		long repositoryId = themeDisplay.getScopeGroupId();
		try
		{
			Folder folder = getFolder(themeDisplay);
			ServiceContext serviceContext = ServiceContextFactory.getInstance(DLFileEntry.class.getName(), actionRequest);
			InputStream is = new FileInputStream( file );
			f = DLAppServiceUtil.addFileEntry(repositoryId, folder.getFolderId(), fileName, mimeType,
					title, description, "", is, file.length(), serviceContext);
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return f;
	}

	@Reference
	private MailService _mailService;
}