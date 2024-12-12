#!/bin/bash

OUTPUT_NAME="todolist-1.0.0.jar"

./gradlew build

cp app/build/libs/app.jar ./$OUTPUT_NAME
