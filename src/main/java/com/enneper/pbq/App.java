package com.enneper.pbq;

import com.enneper.pbq.parcel.VTParcel;
import com.enneper.pbq.shapefile.ShapefileCreator;
import com.enneper.pbq.town.Town;
import org.geotools.feature.FeatureCollection;
import org.geotools.util.logging.Logging;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.union.UnaryUnionOp;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.File;
import java.util.Set;
import java.util.logging.Logger;

import com.enneper.pbq.shapefile.ShapefileReader;
import com.enneper.pbq.utils.GeometryUtils;

public class App {
  private static final Logger LOGGER = Logging.getLogger(App.class);

  public static void main(String[] args)
          throws java.io.IOException, org.geotools.filter.text.cql2.CQLException {

    // Do some simple argument file checking.
    if (args.length == 0) {
      System.err.println("Input shapefile filename must be passed as argument.");
      System.out.println("USAGE: java -jar towns-1.0-SNAPSHOT-jar-with-dependencies.jar <input shapefile filename>");
      System.exit(1);
    }

    final File sourceFile = new File(args[0]);
    if (!(sourceFile.exists() && sourceFile.isFile())) {
      System.err.println("Shapefile " + args[0] + " cannot be read, please check that file exists.");
      System.exit(1);
    }
    /* Passing of Parcel information to convey input shapefile structure. A factory for each state
     * would be better if this was going to be extended to other states.
     * For now just hacking in sending in VTParcel.
     */
    ShapefileReader shapefileReader = new ShapefileReader(sourceFile, new VTParcel());
    long noOfTownsProcessed = 0;
    Set towns;
    FeatureCollection<SimpleFeatureType, SimpleFeature> collection;

    /*
     * Design is to determine the unique towns (VT has 255) and then process each town one at a time.
     * I choose this option vs reading the entire input shapefile into a collection and iterating through
     * it to keep memory usage at minimum.
     */
    towns = shapefileReader.getUniqueTowns();
    if (towns.size() == 0) {
      LOGGER.severe("Input shapefile " + args[0] + " is empty.");
      System.exit(1);
    }

    // Output shapefile will have filename "towns-<yyyyMMddHHmm>"
    ShapefileCreator shapefileCreator = new ShapefileCreator("towns-", shapefileReader.getCRS());

    for (Object town : towns) {
      LOGGER.info("Processing " + town + ": " + ++noOfTownsProcessed + " of " + towns.size());

      // Get all parcels for town
      collection = shapefileReader.getCollectionFor("TOWN = '" + town.toString() +"'");

      // Fix any invalid parcel geometries
      GeometryCollection gc = GeometryUtils.getGeometryCollectionFor(collection, true);

      // Merge all parcel geometries for town and remove any holes in each parcel polygon
      Geometry g = GeometryUtils.getOuterRingOnly(UnaryUnionOp.union(gc));

      // Write the result out to the output shapefile
      Town town_record = new Town(noOfTownsProcessed, g, town.toString());
      shapefileCreator.writeRecord(town_record);
    }
  }
}
