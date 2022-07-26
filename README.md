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
