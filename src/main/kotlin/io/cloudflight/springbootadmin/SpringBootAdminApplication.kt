package io.cloudflight.springbootadmin

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@EnableAdminServer
@SpringBootApplication
class SpringBootAdminApplication

fun main(args: Array<String>) {
	runApplication<SpringBootAdminApplication>(*args)
}
