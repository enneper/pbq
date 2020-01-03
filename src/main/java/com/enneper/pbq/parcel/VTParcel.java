package com.enneper.pbq.parcel;

import org.locationtech.jts.geom.Geometry;

public class VTParcel implements Parcel {

  // Define field names to use in town output file.
  public enum FieldNames { ID ("OBJECTID"), GEOMETRY ("the_geom"), TOWN ("TOWN");
    FieldNames(String name) {
      this.name = name;
    }
    private final String name;

    private String getName() {
      return name;
    }
  }

  @Override
  public String getIDName() { return FieldNames.ID.getName();}
  @Override
  public String getGeometryName() { return FieldNames.GEOMETRY.getName();}
  @Override
  public String getTownName() { return FieldNames.TOWN.getName();}

}
