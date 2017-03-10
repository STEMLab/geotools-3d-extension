#!/bin/bash

export PATH=$PATH:.

javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/PointerVector.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFCoordinate.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFGeometry.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFPoint.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFLineString.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFSurface.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFPolygon.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFTriangle.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFPolyhedralSurface.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFTriangulatedSurface.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFSolid.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFGeometryCollection.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFMultiPoint.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFMultiLineString.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFMultiPolygon.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFMultiSolid.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFEnvelope.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFTriangulate.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFPreparedGeometry.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFCAPI.java
javac -cp javacpp.jar:../src/main/java/ ../src/main/java/org/geotools/geometry/iso/sfcgal/wrapper/SFAlgorithm.java

java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/PointerVector
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFCoordinate -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFGeometry -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFPoint -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFLineString -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFSurface -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFPolygon -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFTriangle -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFPolyhedralSurface -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFTriangulatedSurface -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFSolid -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFGeometryCollection -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFMultiPoint -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFMultiLineString -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFMultiPolygon -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFMultiSolid -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFEnvelope -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFTriangulate -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFPreparedGeometry -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFCAPI -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL
java -jar javacpp.jar -cp ../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFAlgorithm -Xcompiler -lboost_system -Xcompiler -lCGAL -Xcompiler -lCGAL_Core -Xcompiler -lSFCGAL

#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/PointerVector
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFGeometry
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFCoordinate
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFGeometry
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFPoint
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFLineString
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFSurface
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFPolygon
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFTriangle
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFPolyhedralSurface
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFTriangulatedSurface
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFSolid
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFGeometryCollection
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFMultiPoint
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFMultiLineString
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFMultiPolygon
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFMultiSolid
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFEnvelope
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFTriangulate
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFPreparedGeometry
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFCAPI
#java -cp javacpp.jar:../src/main/java org/geotools/geometry/iso/sfcgal/wrapper/SFAlgorithm


