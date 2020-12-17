package transfer.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import org.osgi.service.component.annotations.Component;
import userdocumentupload.constants.TransferFileUploadPortletKeys;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

@Component(
        immediate = true,
        property = {
                "javax.portlet.name=" + TransferFileUploadPortletKeys.TRANSFERFILEUPLOAD,
                "mvc.command.name=/transfer/r/download"
        },
        service = MVCRenderCommand.class
)
public class DownloadMVCRenderCommand implements MVCRenderCommand {
    @Override
    public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {
        return "/download.jsp";
    }
}
