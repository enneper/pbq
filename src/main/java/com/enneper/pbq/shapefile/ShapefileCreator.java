package com.enneper.pbq.shapefile;

import com.enneper.pbq.town.Town;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.util.logging.Logging;
import org.locationtech.jts.geom.MultiPolygon;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class ShapefileCreator {
  private static final Logger LOGGER = Logging.getLogger(ShapefileCreator.class);

  private final SimpleFeatureBuilder featureBuilder;
  private final ShapefileDataStore dataStore;
  private final CoordinateReferenceSystem crs;

  public ShapefileCreator(final String filename, final CoordinateReferenceSystem crs) throws IOException {

    this.crs = crs;

    // Name output file so it is guaranteed (almost) to be unique
    File file = new File( new File(".").getAbsolutePath() + "/" + filename +
            new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".shp");
    LOGGER.info("Created output shapefile: " + file.getName());

    FileDataStoreFactorySpi factory = new ShapefileDataStoreFactory();

    Map<String, Serializable> params = new HashMap<>();
    params.put("url", file.toURI().toURL());
    params.put("create spatial index", Boolean.TRUE);
    dataStore = (ShapefileDataStore) factory.createNewDataStore(params);

    SimpleFeatureType TYPE = setShapefileStructure();
    dataStore.createSchema(TYPE);
    featureBuilder = new SimpleFeatureBuilder(TYPE);
  }

  private SimpleFeatureType setShapefileStructure() {
    SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
    builder.setName("Location");
    builder.setCRS(this.crs);

    // Define output shapefile fields.
    builder.minOccurs(1).nillable(false).add(Town.FieldNames.GEOMETRY.getName(), MultiPolygon.class);
    builder.minOccurs(1).nillable(false).add(Town.FieldNames.ID.getName(), Long.class);
    builder.minOccurs(1).nillable(false).length(80).add(Town.FieldNames.TOWN.getName(), String.class);
    builder.setDefaultGeometry(Town.FieldNames.GEOMETRY.getName());

    return builder.buildFeatureType();
  }

  public void writeRecord(final Town town) throws java.io.IOException {
    Transaction transaction = new DefaultTransaction("create");
    SimpleFeatureType TYPE = setShapefileStructure();
    DefaultFeatureCollection collection = new DefaultFeatureCollection("internal", TYPE);
    String typeName = dataStore.getTypeNames()[0];
    SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);

    if (featureSource instanceof SimpleFeatureStore) {
      SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource; // write access
      featureStore.setTransaction(transaction);
      try {
        featureBuilder.add(town.getGeometry());
        featureBuilder.add(town.getId());
        featureBuilder.add(town.getTown_name());
        collection.add(featureBuilder.buildFeature(null));
        featureStore.addFeatures(collection);
        transaction.commit();
      } catch (Exception e) {
        transaction.rollback();
      } finally {
        transaction.close();
      }
    } else {
      LOGGER.severe(typeName + "does not support read/write access.");
      System.exit(1);
    }
  }
}

