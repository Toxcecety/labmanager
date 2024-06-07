package fr.utbm.ciad.labmanager.configuration.security.filter;

import org.springframework.security.authentication.AuthenticationManager;

/**
 * This class represents the CAS authentication filter for UTBM.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see CommonAuthenticationFilter
 * @see UbCasAuthenticationFilter
 * @see AbstractCasAuthenticationFilter
 * @since 4.1
 */
public class UtbmCasAuthenticationFilter extends AbstractCasAuthenticationFilter {
    public UtbmCasAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
}
