#!/bin/sh

VERSION="0.2.10"

mkdir -p target/dev/softtest/doozer/$VERSION

mvn javadoc:jar
mvn source:jar
mvn package -Ddoozer.directory=src/test/java/dev/softtest/doozer/scripts/ -Ddoozer.failOnPixelDiff=false

cp target/doozer-$VERSION.jar target/dev/softtest/doozer/$VERSION/
cp target/doozer-$VERSION-javadoc.jar target/dev/softtest/doozer/$VERSION/
cp target/doozer-$VERSION-sources.jar target/dev/softtest/doozer/$VERSION/
cp pom.xml target/dev/softtest/doozer/$VERSION/doozer-$VERSION.pom

gpg -ab --yes target/dev/softtest/doozer/$VERSION/doozer-$VERSION.jar
gpg -ab --yes target/dev/softtest/doozer/$VERSION/doozer-$VERSION.pom
gpg -ab --yes target/dev/softtest/doozer/$VERSION/doozer-$VERSION-sources.jar
gpg -ab --yes target/dev/softtest/doozer/$VERSION/doozer-$VERSION-javadoc.jar

cd target/dev/softtest/doozer/$VERSION/

md5 -q doozer-$VERSION.jar > doozer-$VERSION.jar.md5
md5 -q doozer-$VERSION.pom > doozer-$VERSION.pom.md5
md5 -q doozer-$VERSION-javadoc.jar > doozer-$VERSION-javadoc.jar.md5
md5 -q doozer-$VERSION-sources.jar > doozer-$VERSION-sources.jar.md5

shasum doozer-$VERSION.jar | awk '{print $1}' > doozer-$VERSION.jar.sha1
shasum doozer-$VERSION.pom | awk '{print $1}' > doozer-$VERSION.pom.sha1
shasum doozer-$VERSION-javadoc.jar | awk '{print $1}' > doozer-$VERSION-javadoc.jar.sha1
shasum doozer-$VERSION-sources.jar | awk '{print $1}' > doozer-$VERSION-sources.jar.sha1

cd -
cd target
zip -r doozer-$VERSION.zip dev