#!/bin/bash

# Creates a binary distribution by combining the current KeyBox build with
# the Jetty distribution.  The results will be in the 'dist' subdirectory.

cd ${0%/*}

JETTY_VERSION=9.2.11.v20150529

JETTY_BASENAME=jetty-distribution-$JETTY_VERSION
#JETTY_URL=http://download.eclipse.org/jetty/$JETTY_VERSION/dist/$JETTY_BASENAME.tar.gz
JETTY_URL=https://olex-secure.openlogic.com/content/private/5e6a6f0815e830bba705e79e4a0470fbee8a5880//olex-secure.openlogic.com/jetty-distribution-$JETTY_VERSION.tar.gz

TARNAME=DmsConsole-jetty

package_dir=$PWD
dist_dir=$package_dir/dist
build_dir=$package_dir/dbuild
dmsconsole_src=$(cd ..; echo $PWD)
dmsbox_src=$(cd ../console; echo $PWD)
dmsbox_target=$dmsbox_src/target
# Copy the source tree into the build directory and create the package locally
rm -rf $build_dir; mkdir $build_dir

# Create the Maven package
cd $dmsconsole_src
mvn clean
mvn package

# Find the snapshot directory and extract the version number of Keybox
snapdir=`find $dmsbox_target -type d -name 'dmsbox-*-SNAPSHOT'`
[ "$snapdir" == "" ] && (echo "Error: Couldn't find snapshot directory."; exit 1)
dmsbox_version=$(echo $snapdir | sed 's^.*/dmsbox-\([0-9]*\.[0-9]*\)\.\([0-9]*\)-SNAPSHOT^\1_\2^')

cd $package_dir

if [ ! -f ../jetty-distribution-$JETTY_VERSION.tar.gz ];then
  wget --no-check-certificate $JETTY_URL
fi

cd $build_dir


# Now, create the final tar directory by copying in relevant files, with the starter kit as the basis

rm -rf $TARNAME; mkdir $TARNAME
mkdir $TARNAME/jetty

# Untar Jetty itself and put it in place
tar xzf $package_dir/$JETTY_BASENAME.tar.gz 

mv $JETTY_BASENAME/* $TARNAME/jetty

# Remove jetty components not needed
rm -rf $TARNAME/jetty/demo-base

# Copy keybox into the jetty directory
mkdir -p $TARNAME/jetty/dmsbox
cp -a $snapdir/. $TARNAME/jetty/dmsbox

# Add template files
cp -a ../$TARNAME/. $TARNAME/.

# Add additional files from the distro
cp $dmsbox_src/README.md $dmsbox_src/LICENSE.md $TARNAME
cp $dmsbox_src/src/test/resources/keystore $TARNAME/jetty/etc

# And create the distribution archive
mkdir -p $dist_dir
tarfile=$dist_dir/dmsbox-jetty-v$dmsbox_version.tar.gz
tar czf $tarfile $TARNAME
echo created $tarfile

# Remove build directory
cd $package_dir
rm -rf $build_dir
