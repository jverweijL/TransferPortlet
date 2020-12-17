package transfer.configuration.definition;

import transfer.configuration.TransferPortletConfiguration;
import com.liferay.portal.kernel.settings.definition.ConfigurationBeanDeclaration;

public class TransferPortletConfigurationBeanDeclaration implements ConfigurationBeanDeclaration {
    @Override
    public Class<?> getConfigurationBeanClass()
    {
        return TransferPortletConfiguration.class;
    }
}
