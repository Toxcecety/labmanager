package fr.utbm.ciad.labmanager.configuration.security.provider;

import org.apereo.cas.client.validation.TicketValidator;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * This class represents the CAS authentication provider for UTBM.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public class UtbmCasAuthenticationProvider extends AbstractCasAuthenticationProvider {

    /**
     * Create a new instance of the {@link UtbmCasAuthenticationProvider} with the given user details service, service properties, ticket validator and key.
     *
     * @param userDetailsService The user details service
     * @param serviceProperties  The service properties
     * @param ticket             The ticket validator
     * @param key                The key
     */
    public UtbmCasAuthenticationProvider(
            UserDetailsService userDetailsService,
            ServiceProperties serviceProperties,
            TicketValidator ticket,
            String key) {
        super(userDetailsService, serviceProperties, ticket, key);
    }
}