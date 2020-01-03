package com.enneper.pbq.utils;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.util.logging.Logging;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GeometryUtils {
  static final Logger LOGGER = Logging.getLogger(GeometryUtils.class);

  public static GeometryCollection getGeometryCollectionFor
          (final FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection,
           final boolean attemptFixOnInvalidGeometries) {
    GeometryFactory gf = new GeometryFactory();
    ArrayList<Geometry> geometries = new ArrayList<>();
    FeatureIterator<SimpleFeature> features = featureCollection.features();

    while (features.hasNext()) {
      SimpleFeature simpleFeature = features.next();
      Geometry geometry = (Geometry) simpleFeature.getDefaultGeometry();

      // Town of Chester has a parcel with null geometry - skip this parcel
      if (geometry == null) {
        LOGGER.warning("Geometry for objectid: " + simpleFeature.getAttribute("OBJECTID") + " is null.");
        continue;
      }

      if ((geometry != null) && (!geometry.isValid()) && (attemptFixOnInvalidGeometries)) {
        geometry = attemptFixOnInvalidGeometry(geometry);
        if (!geometry.isValid()) {
          LOGGER.warning("Parcel geometry " + simpleFeature.getAttribute("OBJECTID") +
                  "is invalid and cannot be valid.  Parcel will be skipped.");
          continue;
        }
      }
      geometries.add(geometry);
    }
    features.close();
    return gf.createGeometryCollection(geometries.toArray(new Geometry[] {}));
  }

  // Invalid geometries are polygons that have lines that touch (or almost touch).
  private static Geometry attemptFixOnInvalidGeometry(final Geometry geometry) {
    return geometry.buffer(0);
  }

  // Remove any interior holes in the town polygons
  public static Geometry getOuterRingOnly(final Geometry geometry) {
    Geometry g;
    List<Polygon> polygons = new ArrayList<>();
    List<Polygon> noHolePolygons = new ArrayList<>();
    LineString lineString;

    // check each polygon in multipolygon
    if (geometry instanceof MultiPolygon) {
      for (int i = 0; i < geometry.getNumGeometries(); i++) {
        g = geometry.getGeometryN(i);
        if (g instanceof Polygon) {
          polygons.add((Polygon) g);
        }
      }
    } else if (geometry instanceof Polygon)  {
      polygons.add((Polygon) geometry);
    }
    else {
      LOGGER.warning("Can not calculate exterior ring as source is not polygon or multipolygon.");
      return null;
    }

    // Remove holes from each polygon
    for (int i = 0; i < polygons.size(); i++) {
      lineString = polygons.get(i).getExteriorRing();
      noHolePolygons.add(new GeometryFactory().createPolygon(lineString.getCoordinates()));
    }

    // Need to put all polygons back together in multipolygon
    if (noHolePolygons.size() == 1) {
      g = noHolePolygons.get(0);
    } else {
      g = new GeometryFactory().createMultiPolygon(noHolePolygons.toArray(new Polygon[noHolePolygons.size()]));
    }
    return g;
  }
}
