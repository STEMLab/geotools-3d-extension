# export DEBIAN_FRONTEND=noninteractive

#BOOST
wget https://sourceforge.net/projects/boost/files/boost/1.60.0/boost_1_60_0.tar.gz/download -c -O boost-1.60.tar.gz
tar -xvf boost-1.60.tar.gz
cd boost_1_60_0 && ./bootstrap.sh && sudo ./b2 install && cd ..

#CGAL
wget https://github.com/CGAL/cgal/releases/download/releases%2FCGAL-4.7/CGAL-4.7.tar.gz -c -O CGAL-4.7.tar.gz
tar -xvf CGAL-4.7.tar.gz
cd CGAL-4.7 && cmake . && make && sudo make install && cd ..

#SFCGAL
wget https://github.com/Oslandia/SFCGAL/archive/v1.3.0.tar.gz -c -O sfcgal-1.3.tar.gz
tar -xvf sfcgal-1.3.tar.gz
cd SFCGAL-1.3.0 && cmake . && make && sudo make install && cd ..

cmake --version