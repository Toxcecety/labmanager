package fr.utbm.ciad.labmanager.configuration.security.entrypoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * CommonAuthenticationEntryPoint
 * <p>
 * This class is used to redirect the user to the correct CAS server based on the organization parameter or to the login page.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see UtbmCasAuthenticationEntryPoint
 * @see UbCasAuthenticationEntryPoint
 * @see AbstractCasAuthenticationEntryPoint
 * @since 4.1
 */
public class CommonAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {

    private final UtbmCasAuthenticationEntryPoint utbmCasAuthenticationEntryPoint;

    private final UbCasAuthenticationEntryPoint ubCasAuthenticationEntryPoint;

    /**
     * Create a new instance of the {@link CommonAuthenticationEntryPoint} with the given UTBM and UB CAS authentication entry points.
     *
     * @param utbmCasAuthenticationEntryPoint The UTBM CAS authentication entry point
     * @param ubCasAuthenticationEntryPoint   The UB CAS authentication entry point
     */
    public CommonAuthenticationEntryPoint(
            UtbmCasAuthenticationEntryPoint utbmCasAuthenticationEntryPoint,
            UbCasAuthenticationEntryPoint ubCasAuthenticationEntryPoint) {
        this.utbmCasAuthenticationEntryPoint = utbmCasAuthenticationEntryPoint;
        this.ubCasAuthenticationEntryPoint = ubCasAuthenticationEntryPoint;
    }

    /**
     * Check if the properties are correctly set for both CAS authentication entry points.
     *
     * @throws Exception The exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        utbmCasAuthenticationEntryPoint.afterPropertiesSet();
        ubCasAuthenticationEntryPoint.afterPropertiesSet();
    }

    /**
     * Redirect the user to the correct CAS server based on the organization parameter or to the login page.
     *
     * @param request       The request
     * @param response      The response
     * @param authException The authentication exception
     * @throws IOException      The IO exception
     * @throws ServletException The servlet exception
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        var param = request.getParameter("organization");
        if (param == null) {
            response.sendRedirect("/LabManager/login");
        } else {
            switch (param) {
                case "UTBM": // TODO: Use constants
                    utbmCasAuthenticationEntryPoint.commence(request, response, authException);
                    break;
                case "UB":
                    ubCasAuthenticationEntryPoint.commence(request, response, authException);
                    break;
                default:
                    response.sendRedirect("/LabManager/login");
                    break;
            }
        }

    }
}