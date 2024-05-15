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
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.validation.Cas30ServiceTicketValidator;
import org.apereo.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Value("${labmanager.cas-server.url.base}")
    private String casServerUrlBase;

    @Value("${labmanager.cas-server.url.login}")
    private String casServerUrlLogin;

    @Value("${labmanager.cas-server.url.logout}")
    private String casServerUrlLogout;

    @Value("${labmanager.cas-server.url.service}")
    private String casServerUrlService;

    @Value("${labmanager.cas-server.key}")
    private String casServerKey;

    @Autowired
    private UserDetailsService userDetailsService;

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
                            .requestMatchers("/adaptiveLogin").permitAll()
                            .requestMatchers("/").permitAll();
        });

        http.csrf(AbstractHttpConfigurer::disable);
    }

    @Override
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        this.configure(http);

        http
                .addFilter(casAuthenticationFilter())
                .addFilterBefore(new SingleSignOutFilter(), CasAuthenticationFilter.class)
                .authorizeHttpRequests(
                        (authorize)
                                -> authorize
                                .anyRequest().authenticated())
                .exceptionHandling(
                        (exceptions)
                                -> exceptions
                                .authenticationEntryPoint(casAuthenticationEntryPoint()));

        http
                .logout(
                        (logout)
                                -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .addLogoutHandler(new SecurityContextLogoutHandler())
                                .logoutSuccessUrl("/").permitAll());

        return http.build();
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setAuthenticationUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsService));
        provider.setServiceProperties(serviceProperties());
        provider.setTicketValidator(ticketValidator());
        provider.setKey(casServerKey);
        return provider;
    }

    private TicketValidator ticketValidator() {
        return new Cas30ServiceTicketValidator(this.casServerUrlBase);
    }

    /**
     * First step of the CAS authentication process.
     *
     * @return the authentication entry point.
     */
    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLoginUrl(this.casServerUrlLogin);
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        return casAuthenticationEntryPoint;
    }

    /**
     * @return
     */
    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        CasAuthenticationProvider casAuthenticationProvider = casAuthenticationProvider();
        filter.setAuthenticationManager(new ProviderManager(casAuthenticationProvider));
        return filter;
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(casServerUrlService);
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        return new SingleSignOutFilter();
    }

    @Bean
    public LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(this.casServerUrlLogout, new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl("/logout");
        return logoutFilter;
    }
}
