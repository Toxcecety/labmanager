package fr.utbm.ciad.labmanager.configuration.security.entrypoint;

import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;

/**
 * Abstract class for CAS authentication entry point.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public abstract class AbstractCasAuthenticationEntryPoint extends CasAuthenticationEntryPoint {

    /**
     * Create a new instance of the {@link AbstractCasAuthenticationEntryPoint} with the given login URL and service properties.
     *
     * @param loginUrl          the login URL
     * @param serviceProperties the service properties
     */
    public AbstractCasAuthenticationEntryPoint(String loginUrl, ServiceProperties serviceProperties) {
        super();
        setLoginUrl(loginUrl);
        setServiceProperties(serviceProperties);
    }
}
