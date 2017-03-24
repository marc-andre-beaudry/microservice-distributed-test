#!/bin/bash

echo "Building docker files"
cd commander
mvn package docker:build
cd ../payment
mvn package docker:build
cd ../provisioning
mvn package docker:build
cd ../ordercart
mvn package docker:build
