package fr.utbm.ciad.labmanager.configuration.security.provider;

import org.apereo.cas.client.validation.TicketValidator;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * This class represents the CAS authentication provider for UB.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public class UbCasAuthenticationProvider extends AbstractCasAuthenticationProvider {

    /**
     * Create a new instance of the {@link UbCasAuthenticationProvider} with the given user details service, service properties, ticket validator and key.
     *
     * @param userDetailsService The user details service
     * @param serviceProperties  The service properties
     * @param ticket             The ticket validator
     * @param key                The key
     */
    public UbCasAuthenticationProvider(UserDetailsService userDetailsService, ServiceProperties serviceProperties, TicketValidator ticket, String key) {
        super(userDetailsService, serviceProperties, ticket, key);
    }
}
