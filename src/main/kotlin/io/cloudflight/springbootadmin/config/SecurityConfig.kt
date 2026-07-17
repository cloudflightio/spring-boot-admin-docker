package io.cloudflight.springbootadmin.config

import de.codecentric.boot.admin.server.config.AdminServerProperties
import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
import org.springframework.security.web.util.matcher.DispatcherTypeRequestMatcher

/**
 * Locks down the Spring Boot Admin server so that only authenticated users
 * (via the configured OAuth2 provider) can reach the UI or the actuator data
 * it aggregates.
 *
 * The configuration deliberately:
 *  - permits only the static assets and the login/OAuth2 endpoints anonymously,
 *  - requires authentication for every other request (the admin UI, API, and
 *    the SBA instance registration endpoints),
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val adminServerProperties: AdminServerProperties,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity, clientRegistrationRepository: InMemoryClientRegistrationRepository): SecurityFilterChain {
        // Base path the SBA UI is served under (default "").
        val adminContextPath = adminServerProperties.contextPath

        // Factory for path matchers rooted at the SBA context path.
        val mvc = PathPatternRequestMatcher.withDefaults()

        // After a successful login, send the user back to the SBA UI root.
        val successHandler = SavedRequestAwareAuthenticationSuccessHandler().apply {
            setTargetUrlParameter("redirectTo")
            setDefaultTargetUrl("$adminContextPath/")
        }

        http {
            authorizeHttpRequests {
                // The Spring Boot Admin UI streams instance/event updates over SSE, which
                // Spring MVC processes asynchronously. In Spring Security 6 the
                // AuthorizationFilter runs on ALL dispatcher types by default, so it would
                // re-evaluate authorization on the ASYNC re-dispatch — when the SecurityContext
                // is no longer populated — and deny access after the response is already
                // committed. Authorization is enforced on the initial REQUEST dispatch, so
                // permit the async/forward/error re-dispatches to pass through.
                authorize(DispatcherTypeRequestMatcher(DispatcherType.ASYNC), permitAll)
                authorize(DispatcherTypeRequestMatcher(DispatcherType.FORWARD), permitAll)
                authorize(DispatcherTypeRequestMatcher(DispatcherType.ERROR), permitAll)
                // Static assets and the login page itself are public.
                authorize(mvc.matcher("$adminContextPath/login"), permitAll)
                // Everything else — UI, API, registration — requires authentication.
                authorize(anyRequest, authenticated)
            }

            // Interactive browser login through the OAuth2 provider.
            oauth2Login {
                authenticationSuccessHandler = successHandler
            }

            logout {
                logoutSuccessHandler = OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository).apply {
                    setPostLogoutRedirectUri("{baseUrl}")
                }
            }

            csrf {
                disable()
            }
        }

        return http.build()
    }

}
