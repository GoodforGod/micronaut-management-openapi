# Micronaut Management OpenAPI

![Java CI](https://github.com/GoodforGod/micronaut-management-openapi/workflows/Java%20CI/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_micronaut-management-openapi&metric=alert_status)](https://sonarcloud.io/dashboard?id=GoodforGod_micronaut-management-openapi)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_micronaut-management-openapi&metric=coverage)](https://sonarcloud.io/dashboard?id=GoodforGod_micronaut-management-openapi)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_micronaut-management-openapi&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=GoodforGod_micronaut-management-openapi)

Library provides Micronaut *cloud-friendly* OpenAPI/Swagger-UI/Rapidoc management endpoints.

Features:
- Cloud-friendly, *optimized for file streaming* OpenAPI/Swagger-UI/Rapidoc endpoints
- Merging multiple OpenAPI files into one
- OpenAPI exposure endpoint
- Swagger UI exposure endpoint
- *Swagger UI Dark\Light theme*
- Rapidoc exposure endpoint

<img src="https://s10.gifyu.com/images/optimized-swagger.gif" width="800"/>

## Dependency :rocket:

Library ships for *Micronaut 3*.

**Gradle**
```groovy
dependencies {
    implementation "io.goodforgod:micronaut-management-openapi:1.0.1"
}
```

**Maven**
```xml
<dependency>
    <groupId>io.goodforgod</groupId>
    <artifactId>micronaut-management-openapi</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Example

Here is [simple Micronaut HTTP application](https://github.com/GoodforGod/micronaut-java-http-template)
with configured library and OpenAPI generator, you can clone and play with it.

## OpenAPI Generation

Library only exposes *OpenAPI* files, library **DOESN'T** generate them, so this is your responsibility to generate OpenAPI file.

There is Micronaut OpenAPI generator, [Gradle config](https://github.com/GoodforGod/micronaut-java-http-template/blob/master/build.gradle#L35):

```yaml
annotationProcessor("io.micronaut.openapi:micronaut-openapi:3.2.2")
compileOnly("io.swagger.core.v3:swagger-annotations:2.1.11")
```

More info about Micronaut OpenAPI generator [in official documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/index.html).

## Endpoints

Library automatically *scan* for OpenAPI files inside JAR in *default-directory* and expose them via OpenAPI endpoint.

Endpoints:
- **/openapi** - [OpenAPI](https://spec.openapis.org/oas/v3.1.0) endpoint.
- **/swagger-ui** - [Swagger UI](https://petstore.swagger.io/) endpoint.
- **/rapidoc** - [Rapidoc](https://mrin9.github.io/RapiDoc/examples/example2.html) endpoint.

Swagger UI have **Light\Dark** theme switch!!!

## Configuration

Most of the settings are *cloud-friendly* by default.

```yaml
openapi:
  path: /openapi                        // Path for OpenAPI endpoint                          (default - /openapi)
  enabled: true                         // Enable OpenAPI exposure                            (default - true)
  merge: false                          // Enable merging OpenAPI found in default-directory  (default - false)
  default-directory: META-INF/swagger   // Path inside JAR where to search OpenAPI            (default - META-INF/swagger)
  exclude:                              // OpenAPI files to exclude from exposure             (path or filename)
    - openapi-1.yml                     // Path or filename (default-directory)
    - META-INF/swagger/openapi-2.yml    // Path or filename (default-directory)
  include:                              // Include ONLY specified OpenAPI files for exposure  (path only)
    - META-INF/swagger/openapi-3.yml    // Path to file inside JAR
    - external-swagger/openapi-4.yml    // Path to file inside JAR
  
  swagger-ui:
    path: /swagger-ui                   // Path for Swagger-UI endpoint                       (default - /swagger-ui)
    enalbed: false                      // Enable Swagger-UI exposure                         (default - false)
  
  rapidoc:
    path: /rapidoc                      // Path for Rapidoc endpoint                          (default - /rapidoc)
    enalbed: false                      // Enable Rapidoc exposure                            (default - false) 
```

### Merge

- *merge* is disabled then any first OpenAPI file will be exposed (according to *exclude* and *include* configuration).
- *merge* is enabled then all suitable (according to *exclude* and *include* configuration) OpenAPI files will be merged into one and exposed.

### Security

When you have security enabled and want to provide *non-auth* access for your OpenAPI/Swagger-UI/Rapodic endpoints here is configuration for such case:

```yaml
micronaut:
  security:
    intercept-url-map:
      -
        pattern: /openapi
        http-method: GET
        access:
          - isAnonymous()
      -
        pattern: /swagger-ui
        http-method: GET
        access:
          - isAnonymous()
```

## License

This project licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
