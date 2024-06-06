package fr.utbm.ciad.labmanager.configuration.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.cas.web.CasAuthenticationFilter;

import java.io.IOException;

/**
 * This class is used to redirect the user to the correct CAS server based on the organization parameter or to the login page.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see UtbmCasAuthenticationFilter
 * @see UbCasAuthenticationFilter
 * @see AbstractCasAuthenticationFilter
 * @since 4.1
 */
public class CommonAuthenticationFilter extends CasAuthenticationFilter implements Filter {

    private final UtbmCasAuthenticationFilter utbmCasAuthenticationFilter;
    private final UbCasAuthenticationFilter ubCasAuthenticationFilter;

    public CommonAuthenticationFilter(UtbmCasAuthenticationFilter utbmCasAuthenticationFilter, UbCasAuthenticationFilter ubCasAuthenticationFilter) {
        setFilterProcessesUrl("/login/cas");
        this.utbmCasAuthenticationFilter = utbmCasAuthenticationFilter;
        this.ubCasAuthenticationFilter = ubCasAuthenticationFilter;
    }

    @Override
    public void afterPropertiesSet() {
        this.utbmCasAuthenticationFilter.afterPropertiesSet();
        this.ubCasAuthenticationFilter.afterPropertiesSet();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        this.doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        var organizationParam = request.getParameter("organization");
        boolean requiresAuthentication = super.requiresAuthentication(request, response);
        if (!requiresAuthentication) {
            chain.doFilter(request, response);
        } else {
            switch (organizationParam) {
                case "UTBM":
                    utbmCasAuthenticationFilter.doFilter(request, response, chain);
                    break;
                case "UB":
                    ubCasAuthenticationFilter.doFilter(request, response, chain);
                    break;
                default:
                    this.logger.warn("Default");
                    break;
            }
        }
    }
}