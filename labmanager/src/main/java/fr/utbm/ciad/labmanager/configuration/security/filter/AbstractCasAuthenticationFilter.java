package fr.utbm.ciad.labmanager.configuration.security.filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.web.CasAuthenticationFilter;

/**
 * Abstract class for CAS authentication filter.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public abstract class AbstractCasAuthenticationFilter extends CasAuthenticationFilter {
    public AbstractCasAuthenticationFilter(AuthenticationManager authenticationManager) {
        super();
        setFilterProcessesUrl("/login/cas");
        setAuthenticationManager(authenticationManager);
    }
}
