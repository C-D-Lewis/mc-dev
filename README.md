# mc-dev

Minecraft mod/plugin development.

## Setup

Using Gradle via sdkman:

```
curl -s "https://get.sdkman.io" | bash
```
```
sdk install gradle
```

* [Gradle docs](https://docs.gradle.org/current/userguide/partr1_gradle_init.html#part1_begin)
* [Paper docs](https://docs.papermc.io/paper/dev/project-setup#plugin-remapping)

## Using a project

Build with `./gradlew build`.

Output is in `app/build/libs`.

## Projects

* `test-plugin` - Test plugin to learn the basics.
