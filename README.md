# spring-boot-admin-docker
![Build Status](https://github.com/cloudflightio/spring-boot-admin-docker/actions/workflows/build.yaml/badge.svg)

Continously built container images for [spring-boot-admin](https://github.com/codecentric/spring-boot-admin).

As spring-boot-admin is only available as library without packaged releases,
this repository aims to fix that. This repository automatically builds a new
image every day and publishes it to the GitHub container registry. Published
images can [be found
here](https://github.com/cloudflightio/spring-boot-admin-docker/pkgs/container/spring-boot-admin-docker).

Images are tagged with the respective spring-boot-admin version.

## Addons

- Kubernetes service discovery is included and enabled by default

## Configuration

To configure settings for your instance, either use environment variables as
[defined in the spring
documentation](https://docs.spring.io/spring-boot/docs/1.5.6.RELEASE/reference/html/boot-features-external-config.html)
or mount a file called `application.yaml` to `/deployments/application.yaml`
when running the container.

### Security

The admin UI, its API, and the client registration endpoints are locked down by
`SecurityConfig` and require an authenticated user. Only the static assets and
the login page are reachable anonymously. Authentication is delegated to an
OAuth2 / OpenID Connect provider, so you must configure an OAuth2 client before
the application will start serving the UI.

The client registration and provider **must** be named `openid`. The provider's
`issuer-uri` is mandatory: it is used both for login and for building the
provider's logout URL on sign-out. The logout URL is constructed as
`<issuer-uri>/protocol/openid-connect/logout`, which matches
[Keycloak](https://www.keycloak.org/); other providers that do not expose that
path are not supported without code changes.

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          openid:
            client-id: <your-client-id>
            client-secret: <your-client-secret>
            scope:
              - openid
              - profile
              - email
            authorization-grant-type: authorization_code
        provider:
          openid:
            issuer-uri: <issuer-uri-with-realm>
            user-name-attribute: preferred_username
```

### Service discovery

Monitored applications are located through Spring Cloud discovery. Two options
are available.

#### Kubernetes discovery

The [Kubernetes discovery
client](https://docs.spring.io/spring-cloud-kubernetes/reference/index.html) is
on the classpath. When the application runs inside a cluster it discovers other
pods/services automatically; no additional configuration is required beyond the
usual RBAC allowing the pod to list services and endpoints.

#### Simple (static) discovery

To register targets explicitly — for local testing or for instances outside the
cluster — use the simple discovery client. Each entry needs the target's base
`uri` and the `management.context-path` metadata so SBA knows where the actuator
endpoints live:

```yaml
spring:
  cloud:
    discovery:
      client:
        simple:
          instances:
            my-application:
              - uri: http://localhost:18080
                metadata:
                  management.context-path: /actuator
```

##### Discovering more than one client

The key under `instances` is the application name, and its value is a **list**,
so you can register several instances of the same application and add further
applications as additional keys:

```yaml
spring:
  cloud:
    discovery:
      client:
        simple:
          instances:
            my-application:
              - uri: http://my-application-1:18080
                metadata:
                  management.context-path: /actuator
              - uri: http://my-application-2:18080
                metadata:
                  management.context-path: /actuator
            another-application:
              - uri: http://another-application:8080
                metadata:
                  management.context-path: /actuator
```
