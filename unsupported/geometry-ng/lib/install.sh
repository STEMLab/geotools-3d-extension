#!/bin/bash

export PATH=$PATH:.

#### SEARCH CMAKE ####
searchCMAKE=$(dpkg --get-selections | grep camke)
if [ ! "$searchCMAKE" ];
then
	echo "Cannot find CMAKE"
	#### CMAKE INSTALL ####
	echo "---- Downloading CMAKE-3.4.3.tar.gz ----"
	wget https://cmake.org/files/v3.4/cmake-3.4.3.tar.gz -O cmake-3.4.3.tar.gz
	tar -xvf cmake-3.4.3.tar.gz
	cd cmake-3.4.3
	echo "---- Installing CMAKE ----"
	./bootstrap
	sudo make install
	cd ..
	rm -r cmake-3.4.3
else
	echo "CMAKE is found"
fi

#### SEARCH BOOST LIBRARY ####
searchBOOST=$(ldconfig -p | grep libboost_system.so)
if [ ! "$searchBOOST" ];
then
	echo "Cannot find BOOST Library"
	#### BOOST INSTALL ####
	echo "---- Downloading Boost-1.60.0.tar.gz ----"
	wget https://sourceforge.net/projects/boost/files/boost/1.60.0/boost_1_60_0.tar.gz/download -c -O boost-1.60.tar.gz
	tar -xvf boost-1.60.tar.gz
	cd boost_1_60_0
	echo "---- Installing Boost ----"
	./bootstrap.sh
	sudo ./b2 install --prefix=/usr/local/
	cd ..
	rm -r boost_1_60_0
else
	echo "BOOST Library is found"	
fi

#### SEARCH CGAL LIBRARY ####
searchCGAL=$(ldconfig -p | grep libCGAL.so)
if [ ! "$searchCGAL" ];
then
	echo "Cannot find CGAL Library"
	#### CGAL INSTALL ####
	echo "---- Downloading CGAL-4.7.tar.gz ----"
	wget https://github.com/CGAL/cgal/releases/download/releases%2FCGAL-4.7/CGAL-4.7.tar.gz -c -O CGAL-4.7.tar.gz
	tar -xvf CGAL-4.7.tar.gz
	cd CGAL-4.7
	echo "---- Installing CGAL ----"
	cmake . -DWITH_examples=ON -DWITH_demos=OFF -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX=/usr/local/
	sudo make install
	cd ..
	rm -r CGAL-4.7
else
	echo "CGAL Library is found"
fi

#### SEARCH SFCGAL LIBRARY ####
searchSFCGAL=$(ldconfig -p | grep libSFCGAL.so)
if [ ! "$searchCGAL" ];
then
	echo "Cannot find SFCGAL Library"
	#### SFCGAL INSTALL ####
	wget https://github.com/Oslandia/SFCGAL/archive/v1.3.0.tar.gz -c -O sfcgal-1.3.tar.gz
	tar -xvf sfcgal-1.3.tar.gz
	cd SFCGAL-1.3.0
	cmake . -DCMAKE_INSTALL_PREFIX=/usr/local/ -DCMAKE_BUILD_TYPE=Release -DSFCGAL_CHECK_VALIDITY=ON -DSFCGAL_BUILD_EXAMPLES=ON
	sudo make install
	cd ..
	rm -r SFCGAL-1.3.0
else
	echo "SFCGAL Library is found"
fi

#for line in $(ldconfig -p | grep libSFCGAL.so)
#do
	#echo $line
#done

#for line in "${searchBOOST[@]}";
#do
	#echo $line
#done
