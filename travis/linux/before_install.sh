export DEBIAN_FRONTEND=noninteractive

#BOOST
sudo add-apt-repository ppa:apokluda/boost1.53 --yes
sudo add-apt-repository ppa:kalakris/cmake --yes # CMAKE 2.8.11
sudo apt-get update -qq
sudo apt-get install --force-yes \
    cmake libboost-chrono1.53-dev libboost-program-options1.53-dev libboost-filesystem1.53-dev libboost-timer1.53-dev \
    libboost-test1.53-dev libboost-date-time1.53-dev libboost-thread1.53-dev \
    libboost-system1.53-dev libboost-serialization1.53-dev \
    libmpfr-dev libgmp-dev \
    cmake

#CGAL
wget https://github.com/CGAL/cgal/releases/download/releases%2FCGAL-4.7/CGAL-4.7.tar.gz
tar xzf CGAL-4.7.tar.gz
cd CGAL-4.7 && cmake . && make && sudo make install && cd ..

#SFCGAL
wget https://github.com/Oslandia/SFCGAL/archive/v1.3.0.tar.gz -c -O sfcgal-1.3.tar.gz
tar -xvf sfcgal-1.3.tar.gz
cd SFCGAL-1.3.0 && cmake . && make && sudo make install && cd ..

cmake --version