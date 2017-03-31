#!/bin/bash
# This file is meant to be included by the parent cppbuild.sh script
#if [[ -z "$PLATFORM" ]]; then
#    pushd ..
#    bash cppbuild.sh "$@" sfcgal-iso
#    popd
#    exit
#fi
set -eu

which cmake3 &> /dev/null && CMAKE3="cmake3" || CMAKE3="cmake"
[[ -z ${CMAKE:-} ]] && CMAKE=$CMAKE3
[[ -z ${MAKEJ:-} ]] && MAKEJ=4
[[ -z ${OLDCC:-} ]] && OLDCC="gcc"
[[ -z ${OLDCXX:-} ]] && OLDCXX="g++"
[[ -z ${OLDFC:-} ]] && OLDFC="gfortran"

KERNEL=(`uname -s | tr [A-Z] [a-z]`)
ARCH=(`uname -m | tr [A-Z] [a-z]`)
case $KERNEL in
    darwin)
        OS=macosx
        ;;
    mingw32*)
        OS=windows
        KERNEL=windows
        ARCH=x86
        ;;
    mingw64*)
        OS=windows
        KERNEL=windows
        ARCH=x86_64
        ;;
    *)
        OS=$KERNEL
        ;;
esac
case $ARCH in
    arm*)
        ARCH=arm
        ;;
    i386|i486|i586|i686)
        ARCH=x86
        ;;
    amd64|x86-64)
        ARCH=x86_64
        ;;
esac
PLATFORM=$OS-$ARCH
echo "Detected platform \"$PLATFORM\""

cd cppbuild
TOP_PATH=`pwd`

function download {
    mkdir -p "$TOP_PATH/downloads"
    if [[ ! -e "$TOP_PATH/downloads/$2" ]]; then
        echo "Downloading $1"
        curl -L "$1" -o "$TOP_PATH/downloads/$2" --fail
        DOWNLOADSTATUS=$?
        if [ "$DOWNLOADSTATUS" -eq 28 ]
        then
		echo "Download timed out, waiting 5 minutes then trying again"
		rm "$TOP_PATH/downloads/$2"
		sleep 600
        	curl -L "$1" -o "$TOP_PATH/downloads/$2" --fail
        	if [ $? -ne 0 ]
        	then
			echo "File still could not be downloaded!"
			rm "$TOP_PATH/downloads/$2"
			exit 1
    		fi
        elif [ "$DOWNLOADSTATUS" -ne 0 ]
        then
		echo "File could not be downloaded!"
		rm "$TOP_PATH/downloads/$2"
		exit 1
        fi
    fi
    ln -sf "$TOP_PATH/downloads/$2" "$2"
}

download https://sourceforge.net/projects/boost/files/boost/1.60.0/boost_1_60_0.tar.gz/download boost-1.60.tar.gz
download https://github.com/CGAL/cgal/releases/download/releases%2FCGAL-4.7/CGAL-4.7.tar.gz CGAL-4.7.tar.gz
download https://github.com/Oslandia/SFCGAL/archive/v1.3.0.tar.gz SFCGAL-1.3.0.tar.gz

mkdir -p $PLATFORM
cd $PLATFORM
INSTALL_PATH=`pwd`
mkdir -p include lib bin

tar -xzf ../boost-1.60.tar.gz
tar -xzf ../CGAL-4.7.tar.gz
tar -xzf ../SFCGAL-1.3.0.tar.gz

case $PLATFORM in
    linux-x86_64)

    	# building boost
    	  cd boost_1_60_0
        ./bootstrap.sh "--prefix=../" "--with-libraries=filesystem,system,thread,date_time,serialization"
        ./b2 install "--prefix=../" link=static cflags=-fPIC
        cd ../

        # building cgal
        cd CGAL-4.7
        cmake "-DCMAKE_INSTALL_PREFIX=.."
        make
        make install
        cd ../

        # building sfcgal
        cd SFCGAL-1.3.0
        cmake "-DCMAKE_INSTALL_PREFIX=.."
        make
        make install
        cd ../

        ;;
    *)
        echo "Error: Platform \"$PLATFORM\" is not supported"
        ;;
esac

cd ../..
