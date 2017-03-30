#!/bin/bash
# This file is meant to be included by the parent cppbuild.sh script
if [[ -z "$PLATFORM" ]]; then
    pushd ..
    bash cppbuild.sh "$@" sfcgal-iso
    popd
    exit
fi

download https://sourceforge.net/projects/boost/files/boost/1.60.0/boost_1_60_0.tar.gz/download boost-1.60.tar.gz
download https://github.com/CGAL/cgal/releases/download/releases%2FCGAL-4.7/CGAL-4.7.tar.gz CGAL-4.7.tar.gz
download https://github.com/Oslandia/SFCGAL/archive/v1.3.0.tar.gz SFCGAL-1.3.0.tar.gz

mkdir -p $PLATFORM
cd $PLATFORM
tar -xzvf ../boost-1.60.tar.gz
tar -xzvf ../CGAL-4.7.tar.gz
tar -xzvf ../SFCGAL-1.3.0.tar.gz

case $PLATFORM in
    linux-x86_64)
    
    	# building boost
    	cd boost_1_60_0
        ./bootstrap.sh
        ./b2 install
        cd ../
        
        # building cgal
        cd CGAL-4.7
        CC="$OLDCC -m64" CXX="$OLDCXX -m64" $CMAKE -DCMAKE_INSTALL_PREFIX=..
        make
        make install
        cd ../
        
        # building sfcgal
        cd SFCGAL-1.3.0
        CC="$OLDCC -m64" CXX="$OLDCXX -m64" $CMAKE -DCMAKE_INSTALL_PREFIX=..
        make
        make install
        cd ../
        
        ;;
    *)
        echo "Error: Platform \"$PLATFORM\" is not supported"
        ;;
esac

cd ../..