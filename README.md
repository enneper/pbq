# pbq
Java JTS problem solution

Problem is describe in "Vermont ENG Code Exercise.pdf", input shapefile data can be downloaded here (https://geodata.vermont.gov/datasets/e12195734d38410185b3e4f1f17d7de1_17/data).

Repository can be cloned down to local (see green "clone" button above).  You can project within IDE or use maven, use "mvn clean package", to generate uber (with dependencies) jar named towns-1.0-SNAPSHOT-jar-with-dependencies.jar to run at command line.

Usage requires unzipped vermont parcel source shapefile (.shp file) as argument to jar (eg. java -jar towns-1.0-SNAPSHOT-jar-with-dependencies.jar <source file directory>\VT_Data_Statewide_Standardized_Parcel_Data__parcel_polygons.shp)
Program will create shapefile named "town-<timestamp>.*, an example solution is at the root of this repository.

Some notable items on source:
1. Source shapefile does contain one parcel (6181657) in town of Chester that contains no geometry, this parcel is skipped
2. There are about a 20 invalid parcel geometries (typically those that have intersecting lines) in source, these are automatically corrected to be valid.

TODO items on this code.  These are key items that would be included if this code was going to be used in on-going production and built to be extended for other (NH) parcel sources. 
1. Unit tests - ISLE OF MOTTE is good test case as it is a MultiPolygon
2. Parcel Factory - for parcel source input using Parcel interface (which is present) and concrete parcel classes for each state (eg. VTParcel which is also present)
