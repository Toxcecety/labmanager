package fr.utbm.ciad.labmanager.configuration.security.filter;

import org.springframework.security.authentication.AuthenticationManager;

/**
 * This class represents the CAS authentication filter for UB.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see UtbmCasAuthenticationFilter
 * @see CommonAuthenticationFilter
 * @see AbstractCasAuthenticationFilter
 * @since 4.1
 */
public class UbCasAuthenticationFilter extends AbstractCasAuthenticationFilter {
    public UbCasAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
}
