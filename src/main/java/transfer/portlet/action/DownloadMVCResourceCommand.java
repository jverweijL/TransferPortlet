package transfer.portlet.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.*;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactoryUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import userdocumentupload.constants.TransferFileUploadPortletKeys;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

@Component(
        immediate = true,
        property = {
                "javax.portlet.name=" + TransferFileUploadPortletKeys.TRANSFERFILEUPLOAD,
                "mvc.command.name=/transfer/download"
        },
        service = MVCResourceCommand.class
)
public class DownloadMVCResourceCommand implements MVCResourceCommand {
    @Override
    public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws PortletException {

        ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
        LiferayPortletRequest portletRequest = PortalUtil.getLiferayPortletRequest(resourceRequest);


        try {
            FileEntry f = DLAppServiceUtil.getFileEntryByUuidAndGroupId(portletRequest.getParameter("uuid"),themeDisplay.getLayout().getGroupId());
            // TODO check password
            String password = portletRequest.getParameter("password").trim();
            if (!password.isEmpty()) {
                password = PasswordEncryptorUtil.encrypt(PasswordEncryptorUtil.TYPE_SHA_256,password,"");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(f.getDescription());
            Boolean isValidPassword = Boolean.FALSE;
            if (jsonNode.get("password").asText().isEmpty() || jsonNode.get("password").asText().equals(password)) {
                isValidPassword = Boolean.TRUE;
            }

            Boolean isExpired = Boolean.TRUE;
            futureDate = DateUtil.
            if (jsonNode.get("password").asText().isEmpty() || jsonNode.get("password").asText().equals(password)) {
                isExpired = Boolean.TRUE;
            }

            // TODO check expired

            if (isValidPassword && !isExpired) {
                // create the zip filename
                String zipFileName = f.getFileName() + ".zip";
                ZipWriter zipWriter = ZipWriterFactoryUtil.getZipWriter();
                zipWriter.addEntry(StringPool.SLASH + f.getFileName(), f.getContentStream());
                InputStream inputStream = new FileInputStream(zipWriter.getFile());

                // send the file back to the browser.
                PortletResponseUtil.sendFile(
                        resourceRequest, resourceResponse, zipFileName, inputStream,
                        ContentTypes.APPLICATION_ZIP);
            }

        } catch (PortalException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static final Log _log = LogFactoryUtil.getLog(DownloadMVCResourceCommand.class);

    @Reference
    private Portal _portal;
}
