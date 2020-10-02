# Micronaut Swagger API

![Java CI](https://github.com/GoodforGod/micronaut-swagger-api/workflows/Java%20CI/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_micronaut-swagger-api&metric=alert_status)](https://sonarcloud.io/dashboard?id=GoodforGod_micronaut-arangodb)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_micronaut-swagger-api&metric=coverage)](https://sonarcloud.io/dashboard?id=GoodforGod_micronaut-arangodb)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_micronaut-swagger-api&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=GoodforGod_micronaut-arangodb)

This project provides out-of-box *user-friendly* integration for *merging Swaggers* (OpenAPI also), exposing *Swagger & Swagger-UI HTTP* endpoints.

![](https://media.giphy.com/media/HNOzdIugRSx8FnDKWR/giphy.gif)

## Dependency :rocket:

Library ships ready for *Micronaut 2*.

**Gradle**
```groovy
dependencies {
    compile 'com.github.goodforgod:micronaut-swagger-api:1.0.1'
}
```

**Maven**
```xml
<dependency>
    <groupId>com.github.goodforgod</groupId>
    <artifactId>micronaut-swagger-api</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Endpoints

Library provide exposure for statis resources:
- **/swagger** - Endpoint for exposing merged [Swagger](https://swagger.io/docs/specification/basic-structure/) (if there are any to merge) as static resource.
- **/swagger-ui** - Endpoint for exposing [Swagger UI](https://petstore.swagger.io/) page resource.
- **/rapidoc** - Endpoint for exposing [Rapidoc](https://mrin9.github.io/RapiDoc/examples/example2.html) page resource.

## Swagger

Library only exposes *Swagger* files, not generating them, so you need to generate 
Swagger for your service first and library will help with its exposure.

By adding to your Gradle dependencies (example):

```yaml
annotationProcessor("io.micronaut.configuration:micronaut-openapi:2.1.0")
implementation("io.swagger.core.v3:swagger-annotations")
```

Check [Micronaut official documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/index.html) for more information.

## Configuration

Library provides out-of-box */swagger (merge enabled), /swagger-ui, /rapidoc* endpoints.

There is ability to easily merge multiple Swagger files via simple configuration property.

Other settings are available to configure for service, 
however most of them are default for *user-friendly* bootstrap just by adding library as dependency.

```yaml
swagger:
  enabled: true       // enalbed Swagger YAML exposing via HTTP endpoint  (default - true)
  merge: true         // enabled merging swaggers under META-INF/swagger  (default - true)
  exclude:            // Exclude from merge swaggers under META-INF/swagger
  - swagger-1.yml
  - swagger-2.yml
  path: /swagger      // path for Swagger HTTP endpoint                   (default - /swagger)
  ui:
    path: /swagger-ui // path for Swagger-UI HTTP endpoint                (default - /swagger-ui)
    enalbed: true     // enalbed Swagger-UI exposing via HTTP endpoint    (default - true)
  rapidoc:
    path: /rapidoc    // path for Rapidoc HTTP endpoint                   (default - /rapidoc)
    enalbed: true     // enalbed Rapidoc exposing via HTTP endpoint       (default - false) 

```

## Version History

**1.0.1** - UTF-8 charset for swagger correct output, internal changes.

**1.0.0** - Initial version, with */swagger, /swagger-ui, /rapidoc* HTTP endpoint support.

## License

This project licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
