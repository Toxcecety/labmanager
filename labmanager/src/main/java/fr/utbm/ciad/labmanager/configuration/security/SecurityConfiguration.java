/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.utbm.ciad.labmanager.configuration.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import fr.utbm.ciad.labmanager.configuration.security.entrypoint.CommonAuthenticationEntryPoint;
import fr.utbm.ciad.labmanager.configuration.security.entrypoint.UbCasAuthenticationEntryPoint;
import fr.utbm.ciad.labmanager.configuration.security.entrypoint.UtbmCasAuthenticationEntryPoint;
import fr.utbm.ciad.labmanager.configuration.security.filter.CommonAuthenticationFilter;
import fr.utbm.ciad.labmanager.configuration.security.filter.UbCasAuthenticationFilter;
import fr.utbm.ciad.labmanager.configuration.security.filter.UtbmCasAuthenticationFilter;
import fr.utbm.ciad.labmanager.configuration.security.provider.UbCasAuthenticationProvider;
import fr.utbm.ciad.labmanager.configuration.security.provider.UtbmCasAuthenticationProvider;
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.validation.Cas30ServiceTicketValidator;
import org.apereo.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Configuration of the security login.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    @Autowired
    private UserDetailsService userDetailsService;


    @Value("${labmanager.cas-servers.ub.base}")
    private String casServerUbBaseUrl;

    @Value("${labmanager.cas-servers.ub.login}")
    private String casServerUbLoginUrl;

    @Value("${labmanager.cas-servers.ub.service}")
    private String casServerUbServiceUrl;

    @Value("${labmanager.cas-servers.ub.key}")
    private String casServerUbKey;


    @Value("${labmanager.cas-servers.utbm.base}")
    private String casServerUtbmBaseUrl;

    @Value("${labmanager.cas-servers.utbm.login}")
    private String casServerUtbmLoginUrl;

    @Value("${labmanager.cas-servers.utbm.logout}")
    private String casServerUtbmLogoutUrl;

    @Value("${labmanager.cas-servers.utbm.service}")
    private String casServerUtbmServiceUrl;

    @Value("${labmanager.cas-servers.utbm.key}")
    private String casServerUtbmKey;

    /**
     * Configure the security.
     * Override the default configuration to permit access to the login page bypass the vaadin configure.
     *
     * @param http the http security.
     * @throws Exception if an error occurs.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                (authorize)
                        -> {
                    authorize
                            .requestMatchers(new AntPathRequestMatcher("/images/**/*")).permitAll()
                            .requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll()
                            .requestMatchers(new AntPathRequestMatcher("/VAADIN/**/")).permitAll()
                            .requestMatchers(new AntPathRequestMatcher("/themes/**")).permitAll()
                            .requestMatchers("/login").permitAll()
                            .requestMatchers("/").permitAll();
                });

        http.csrf(AbstractHttpConfigurer::disable);
    }

    /**
     * Configure the security filter chain.
     * Override the default configuration to add the CAS authentication.
     *
     * @param http the http security.
     * @return the security filter chain.
     * @throws Exception if an error occurs.
     */
    @Override
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        this.configure(http);

        http
                .addFilter(commonFilter())
                .addFilterBefore(new SingleSignOutFilter(), CasAuthenticationFilter.class)
                .authorizeHttpRequests(
                        (authorize)
                                -> authorize
                                .anyRequest().authenticated())
                .exceptionHandling(
                        (exceptions)
                                -> exceptions
                                .authenticationEntryPoint(commonAuthenticationEntryPoint()));

        http
                .logout(
                        (logout)
                                -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .addLogoutHandler(new SecurityContextLogoutHandler())
                                .logoutSuccessUrl("/").permitAll());

        return http.build();
    }

    /**
     * Create a ticket validator.
     *
     * @param url the URL of the CAS server.
     * @return the ticket validator.
     */
    private TicketValidator ticketValidator(String url) {
        return new Cas30ServiceTicketValidator(url);
    }

    /**
     * First step of the CAS authentication process.
     * Create the common authentication entry point that will redirect to the correct CAS server.
     *
     * @return the authentication entry point.
     */
    @Bean
    public CommonAuthenticationEntryPoint commonAuthenticationEntryPoint() {
        UtbmCasAuthenticationEntryPoint utbmEntryPoint = new UtbmCasAuthenticationEntryPoint(this.casServerUtbmLoginUrl, serviceProperties(this.casServerUtbmServiceUrl));
        UbCasAuthenticationEntryPoint ubEntryPoint = new UbCasAuthenticationEntryPoint(this.casServerUbLoginUrl, serviceProperties(this.casServerUbServiceUrl));
        return new CommonAuthenticationEntryPoint(
                utbmEntryPoint,
                ubEntryPoint);
    }

    /**
     * Create the common filter.
     *
     * @return the common filter.
     */
    @Bean
    public CommonAuthenticationFilter commonFilter() {
        UtbmCasAuthenticationProvider utbmProvider
                = new UtbmCasAuthenticationProvider
                (
                        this.userDetailsService,
                        serviceProperties(this.casServerUtbmServiceUrl),
                        ticketValidator(this.casServerUtbmBaseUrl),
                        casServerUtbmKey
                );
        UbCasAuthenticationProvider ubProvider
                = new UbCasAuthenticationProvider
                (
                        this.userDetailsService,
                        serviceProperties(this.casServerUbServiceUrl),
                        ticketValidator(this.casServerUbBaseUrl),
                        casServerUbKey
                );

        return new CommonAuthenticationFilter
                (
                        new UtbmCasAuthenticationFilter(new ProviderManager(utbmProvider)),
                        new UbCasAuthenticationFilter(new ProviderManager(ubProvider))
                );
    }

    public ServiceProperties serviceProperties(String url) {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(url);
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        return new SingleSignOutFilter();
    }

    @Bean
    public LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(this.casServerUtbmLogoutUrl, new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl("/logout");
        return logoutFilter;
    }
}
