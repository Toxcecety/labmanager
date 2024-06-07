package fr.utbm.ciad.labmanager.configuration.security.provider;

import org.apereo.cas.client.validation.TicketValidator;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Abstract class for CAS authentication provider.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public abstract class AbstractCasAuthenticationProvider extends CasAuthenticationProvider {

    /**
     * Create a new instance of the {@link AbstractCasAuthenticationProvider} with the given user details service, service properties, ticket validator and key.
     *
     * @param userDetailsService The user details service
     * @param serviceProperties  The service properties
     * @param ticket             The ticket validator
     * @param key                The key
     */
    public AbstractCasAuthenticationProvider(UserDetailsService userDetailsService, ServiceProperties serviceProperties, TicketValidator ticket, String key) {
        super();
        setUserDetailsService(userDetailsService);
        setAuthenticationUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsService));
        setServiceProperties(serviceProperties);
        setTicketValidator(ticket);
        setKey(key);
    }
}
