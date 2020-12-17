package transfer.configuration;

import aQute.bnd.annotation.metatype.Meta;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

@ExtendedObjectClassDefinition(
        category = "liferay-custom",
        scope = ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE
)
@Meta.OCD(
        id = "transfer.configuration.TransferPortletConfiguration"
)
public interface TransferPortletConfiguration {
    @Meta.AD(deflt = "", required = false)
    String basedir();

    @Meta.AD(deflt = "", required = false)
    String maxfilesize();

    @Meta.AD(deflt = "", required = false)
    String emailtemplate();
}
