## Supporting 3D spatial operations
In order to support 3D spatial operations such as intersects, this module uses [Simple Feature CGAL(SFCGAL)](http://sfcgal.org), which is a C++ wrapper library around [CGAL](http://www.cgal.org) with the aim of supporting ISO 19107:2013 and OGC Simple Features Access 1.2 for 3D operations. Since SFCGAL is C++ library, this module employs [JavaCPP](https://github.com/bytedeco/javacpp) as a bridge between C++ and Java. Due to dependency on SFCGAL, there exist limitations such as supported platforms. If you need 3D operations in gt-geometry, you have to install [SFCGAL](http://sfcgal.org) manually.

## Prerequisites
* CMake
* SFCGAL
 * CGAL
 * Boost
 * GMP
 * MPFR

## License of third party libraries
SFCGAL is distributed under the terms of the [LGPL](http://www.gnu.org/licenses/old-licenses/lgpl-2.0.html)(v2+). Starting from release 4.0. CGAL is available under the [GPL](http://www.gnu.org/copyleft/gpl.html)(v3+)/[LGPL](http://www.gnu.org/copyleft/lesser.html)(v3+). JavaCPP is available under Apache License(v2.0)/[GPL](http://www.gnu.org/copyleft/gpl.html)(v2+) with "Classpath" exception.

## Installation on Windows 7 (64 bit)
To use this extension, some open source libraries and tools([Mingw-w64](http://sourceforge.net/projects/mingw-w64/),[CMake](http://cmake.org),[Boost](http://www.boost.org),[CGAL](http://cgal.org),[SFCGAL](http://sfcgal.org)) are required.

1. MinGW-w64

 * Downald the MinGW-w64 installer, and excute.
 * Choose the Version(5.2.0), Architecture(x86_64), Thread(win32).
 * Click the Next, Next and Finish.
 * Add MinGW\bin to the system PATH. (e.g., C:\dev\mingw-w64\x86_64-5.2.0-win32-seh-rt_v4-rev1\mingw64\bin)

2. Boost (1.55 version recommended)

 * Download the boost archive, unpack it to a target directory of your choice (e.g., C:\dev\boost_1_55_0).
 * Open a command-line terminal, such as cmd.
 * Run:
   
   ```
   cd C:\dev\boost_1_55_0
   ./bootstrap.bat mingw
   b2 clean
   b2 -j4 -a --toolset=gcc variant=debug,release link=static threading=multi address-model=64 stage
   ```
   
 * Make sure the subdirectory stage/lib/ has been populated with the compiled libraries
 * Add BOOST_ROOT, BOOST_INCLUDEDIR and BOOST_LIBRARYDIR variables to the environment variables.
   
   Variable Name | Description
   ------------- | ------------
   BOOST_ROOT | Root directory of your Boost installation 
   BOOST_INCLUDEDIR | Directory containing the boost/version.hpp file 
   BOOST_LIBRARYDIR | Directory containing the compiled Boost libraries
 * Add Boost_ROOT\stage\lib to the system PATH. (e.g., C:\dev\boost_1_55_0\stage\lib).

3. CMake (3.4.0 version recommended)

 * Agree to the license.
 * Check "Add CMake to the system PATH for all users".
 * Check "create desktop icon".
 * Click the Next, Next, Next.
 * Finish.

4. CGAL (4.7 version recommended)

 * CGAL installation will need to connect to the internet for GMP and MPFR.
 * Agree to the license.
 * Just choose the default: with GMP and MPFR, and with examples and demos.
 * Choose "64-bit".
 * In the "Setting Environment Variables" screen, choose all users and make sure that CGAL_DIR is checked.
 * Install.
 * Add CGAL_DIR and other variables to the environment variables.
 
   Variable Name | Description
   ------------- | ------------
   CGAL_DIR | Root directory of your CGAL installation 
   GMP_DIR | Directory of Gmp default installation (CGAL_DIR\auxiliary\gmp)
   GMP_INC_DIR | Directory containing the gmp.h file 
   GMP_LIB_DIR | Directory containing the compiled Gmp library
   MPFR_DIR | Directory of Mpfr default installation (CGAL_DIR\auxiliary\gmp)
   MPFR_INC_DIR | Directory containing the mpfr.h file
   MPFR_LIB_DIR | Directory containing the compiled Mpfr library
 * Add CGAL_DIR\auxiliary\gmp\lib the to system PATH. (e.g., C:\dev\CGAL-4.7\auxiliary\gmp\lib)

   Now you need to compile CGAL
 * Open CMake - cmake-gui (on the Desktop) -if you are using win7 make sure you open the program in administrator mode - right click on the icon and click on "run as administrator".
 * For both "Where is the source code" and "Where to build the binaries" specify the CGAL installation folder (C:\dev\CGAL-4.7)
 * Click Configure.
 * Choose "MinGW Makefiles" and click "Finish".
 * Check the CGAL_Boost_USE_STATIC_LIBS option (ON)
 * Check the WITH_CGAL_QT3(or QT5) option (OFF)
 * Set CMAKE_BUILD_TYPE option to Release.
 * Click Generate.
 * A solution named CGAL was created in the directory.
 * Open a command-line terminal, and change directory to target directory:
 * Run:
 
   ```
   cd C:\dev\CGAL4.7
   mingw32-make
   ```

5. SFCGAL (1.2.2 recommended)

 * Download the sfcgal archive, unpack it to a target directory of your choice (e.g., C:\dev\SFCGAL-1.2.2)
 * Open CMake - cmake-gui.
 * For both "Where is the source code" and "Where to build the binaries" specify the SFCGAL installation folder (C:\dev\SFCGAL-1.2.2)
 * Click Configure.
 * Choose "Mingw Makefiles" and click "Finish".
 * Check the Boost_USE_STATIC_LIBS option (ON).
 * Set CMAKE_BUILD_TYPE option to Release,
 * Click Generate.
 * A solution named SFCGAL was created in the directory.
 * Run:
 
   ```
   cd C:\dev\SFCGAL-1.2.2
   mingw32-make
   ```
 * Add SFCGAL_DIR variables to the environment variables.
  
   Variable Name | Description
   ------------- | -----------
   SFCGAL_DIR | Root directory of your SFCGAL installation.

6. Generate DLL files
 * Run the build.bat file in the getools\modules\unsupported\geometry\lib.
 * Make sure the subdirectory org\geotools\geometry\iso\sfcgal\wrapper\windows-x86_64 has been generated with the dll files.
