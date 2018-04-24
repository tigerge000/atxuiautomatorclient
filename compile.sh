#!/bin/bash
mvn -Dmaven.test.skip=true clean source:jar deploy
