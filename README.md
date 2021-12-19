# Micronaut Management OpenAPI

![Java CI](https://github.com/GoodforGod/micronaut-management-openapi/workflows/Java%20CI/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_micronaut-management-openapi&metric=alert_status)](https://sonarcloud.io/dashboard?id=GoodforGod_micronaut-arangodb)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_micronaut-management-openapi&metric=coverage)](https://sonarcloud.io/dashboard?id=GoodforGod_micronaut-arangodb)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_micronaut-management-openapi&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=GoodforGod_micronaut-arangodb)

Library provides Micronaut *cloud-friendly* OpenAPI/Swagger-UI/Rapidoc management endpoints.

Features:
- Cloud-friendly, optimized for file streaming OpenAPI/Swagger-UI/Rapidoc endpoints.
- Merging multiple OpenAPI files into one.
- OpenAPI exposure endpoint.
- Swagger UI exposure endpoint.
- Swagger UI Dark\Light theme.
- Rapidoc exposure endpoint.

<img src="https://s10.gifyu.com/images/optimized-swagger.gif" width="800"/>

## Dependency :rocket:

Library ships for *Micronaut 3*.

**Gradle**
```groovy
dependencies {
    implementation "io.goodforgod:micronaut-management-openapi:1.0.0"
}
```

**Maven**
```xml
<dependency>
    <groupId>io.goodforgod</groupId>
    <artifactId>micronaut-management-openapi</artifactId>
    <version>1.0.0</version>
</dependency>
```

## OpenAPI Generation

Library only exposes *OpenAPI* files, library **DOESN'T** generate them, so this is your responsibility to generate OpenAPI file.

There is Micronaut OpenAPI generator, Gradle configuration:

```yaml
annotationProcessor("io.micronaut.configuration:micronaut-openapi:3.2.2")
implementation("io.swagger.core.v3:swagger-annotations:2.1.11")
```

More info about Micronaut OpenAPI generator [here in official documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/index.html).

## Endpoints

Library automatically *scan* for OpenAPI files inside JAR and expose them via OpenAPI endpoint.

If *merge* is disabled then any first OpenAPI file will be exposed (according to *exclude* and *include* configuration).
If *merge* is enabled then all suitable (according to *exclude* and *include* configuration) OpenAPI files will be merged into one.

**/openapi** - Endpoint optimized for exposing [OpenAPI](https://spec.openapis.org/oas/v3.1.0) file.

**/swagger-ui** - Endpoint optimized for exposing [Swagger UI](https://petstore.swagger.io/) file.

Swagger UI have **Light\Dark** theme switch.

**/rapidoc** - Endpoint optimized for exposing [Rapidoc](https://mrin9.github.io/RapiDoc/examples/example2.html) file.

## Configuration

Most of the settings are *cloud-friendly* by default.

Just by adding this library as dependency you are ready to go.

```yaml
openapi:
  path: /openapi                        // path for OpenAPI endpoint                          (default - /openapi)
  enabled: true                         // Enable OpenAPI exposure                            (default - true)
  merge: false                          // Enable merging OpenAPI found in default-directory  (default - false)
  default-directory: META-INF/swagger   // path inside JAR where to search OpenAPI            (default - META-INF/swagger)
  exclude:                              // OpenAPI files to exclude from exposure             (path or filename)
    - openapi-1.yml                     // Can be Path or filename
    - META-INF/swagger/openapi-2.yml    // Can be Path or filename
  include:                              // Include ONLY specified OpenAPI files for exposure  (path only)
    - META-INF/swagger/openapi-3.yml    // Path to file inside JAR
    - META-INF/swagger/openapi-4.yml    // Path to file inside JAR
  
  swagger-ui:
    path: /swagger-ui                   // path for Swagger-UI endpoint                       (default - /swagger-ui)
    enalbed: false                      // Enable Swagger-UI exposure                         (default - false)
  
  rapidoc:
    path: /rapidoc                      // path for Rapidoc endpoint                          (default - /rapidoc)
    enalbed: false                      // Enable Rapidoc exposure                            (default - false) 
```

## Security

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
