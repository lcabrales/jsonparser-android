# JSONParser-Android

Android class model generation, including SQLite Database methods, based on a raw JSON object.

# Features

This console application takes a raw formatted single JSON object as an input, and generates the following as output:

- Simple POJO with proper value types (`Integer`, `String`, `Double`, `Boolean`) and naming standards (camelCase).
- SQLite Database support, with common methods definition (`addObj()`, `getObj()`, `getList()`)
- Able to set up a custom entity ID, in order to properly generate a `getObjById()` method.
- Able to define the name filter JSON field, in order to properly generate the `getObj()`'s method `whereClause`

# Usage

[Download](https://github.com/lcabrales/jsonparser-android/blob/master/JSONParser-Android-3.0.2.jar) the compiled JAR file and run it from the console:

```bash
java -jar JSONParser-Android-3.0.2.jar
```

# Changelog

## [3.0.2] - 2018-04-15

### Fixed
- Bug where sometimes it would not generate the `whereClause` for the `getObj()` method.

## [3.0.1] - 2018-04-14

### Fixed
- SQLite method generation typo

## [3.0.0] - 2018-04-13

### Added
- Support for additional SQLite Database methods.
- Ability to set up a custom entity ID.
- Ability to define the name filter JSON field.
- Common native imports to the class definition


## [2.0.0] - 2018-01-18

### Added
- Support for `Double` values.
- SQLite Database methods.


## [1.0.0] - 2018-01-16
### Added
- Generate a POJO class from a raw formatted JSON object.
- This CHANGELOG section.
