package fr.utbm.ciad.labmanager.configuration.security.entrypoint;

import org.springframework.security.cas.ServiceProperties;

/**
 * Create a new instance of the {@link AbstractCasAuthenticationEntryPoint} with the given login URL and service properties for the CAS service of UTBM.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public class UbCasAuthenticationEntryPoint extends AbstractCasAuthenticationEntryPoint {

    /**
     * Create a new instance of the {@link UbCasAuthenticationEntryPoint} with the given login URL and service properties.
     *
     * @param loginUrl          the login URL
     * @param serviceProperties the service properties
     */
    public UbCasAuthenticationEntryPoint(String loginUrl, ServiceProperties serviceProperties) {
        super(loginUrl, serviceProperties);
    }
}
