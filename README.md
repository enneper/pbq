# pbq
Java JTS problem solution

Problem is describe in "Vermont ENG Code Exercise.pdf", input shapefile data can be downloaded here (https://geodata.vermont.gov/datasets/e12195734d38410185b3e4f1f17d7de1_17/data).
Solution produces shapefile named "town-<timestamp>.*, an example solution is at the root of this repository.

Repository can be cloned down to local by via this link.
Use "mvn clean package" to generate uber (with dependencies) jar named towns-1.0-SNAPSHOT-jar-with-dependencies.jar.

Usage requires unzipped source shapefile downloaded from link above as argument to jar.

Some notable items:
1. Source shapefile does contain one parcel in town of Chester that contains no geometry, this parcel is skipped
2. There are about a 20 invalid parcel geometries (typically those that have intersecting lines) is source, these are automatically corrected to be valid.
