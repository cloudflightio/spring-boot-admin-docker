package io.cloudflight.springbootadmin.config

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest(
    @Autowired val mockMvc: MockMvc,
) {

    @ParameterizedTest
    @ValueSource(strings = ["/login"])
    fun `public endpoints are accessible without authentication`(path: String) {
        mockMvc.perform(get(path))
            .andExpect(status().is2xxSuccessful())
    }

    @ParameterizedTest
    @ValueSource(strings = ["/", "/applications"])
    fun `protected endpoints require authentication and redirect to login`(path: String) {
        mockMvc.perform(get(path))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"))
    }

    @Configuration
    class Config {

        @Bean
        fun inMemoryClientRegistrationRepository() = mock<InMemoryClientRegistrationRepository>()

    }
}