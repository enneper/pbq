package com.enneper.pbq.shapefile;

import com.enneper.pbq.parcel.Parcel;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.visitor.UniqueVisitor;
import org.geotools.filter.text.cql2.CQL;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class ShapefileReader {

  private final FeatureSource<SimpleFeatureType, SimpleFeature> source;
  private final DataStore dataStore;
  private final Parcel parcel;

  public ShapefileReader(final File sourceFile, Parcel parcel) throws java.io.IOException {

    Map<String, Object> map = new HashMap<>();
    map.put("url", sourceFile.toURI().toURL());
    map.put("charset", "UTF8");

    this.dataStore = DataStoreFinder.getDataStore(map);
    String typeName = dataStore.getTypeNames()[0];
    this.source = dataStore.getFeatureSource(typeName);
    this.parcel = parcel;
  }

  // Read in all the information about the parcels in a specific town.
  public FeatureCollection<SimpleFeatureType, SimpleFeature> getCollectionFor(final String filterString)
    throws java.io.IOException, org.geotools.filter.text.cql2.CQLException {

    Filter filter = CQL.toFilter(filterString);
    //String name = this.source.getSchema().getGeometryDescriptor().getLocalName();  // gets geometry field name
    Query query = new Query(dataStore.getTypeNames()[0],filter,
            new String[] {this.parcel.getGeometryName(), this.parcel.getIDName()});
    return source.getFeatures(query);
  }

  // Use the same CRS for the new shapefile
  public CoordinateReferenceSystem getCRS() {
    return this.source.getSchema().getCoordinateReferenceSystem();
  }

  // Get all the unique town names in the source shapefile.
  public Set getUniqueTowns() throws java.io.IOException {
    Query query = new Query(dataStore.getTypeNames()[0], Filter.INCLUDE, new String[] { this.parcel.getTownName() });
    UniqueVisitor visitor = new UniqueVisitor(this.parcel.getTownName());

    FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(query);
    collection.accepts(visitor, null);
    return visitor.getUnique();
  }
}
