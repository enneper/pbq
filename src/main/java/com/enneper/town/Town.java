package com.enneper.town;

import org.locationtech.jts.geom.Geometry;

// Container class for output Town shapefile
public class Town {

   // Define field names to use in town output file.
   public enum FieldNames { ID ("ID"), GEOMETRY ("the_geom"), TOWN ("TOWN_NAME");
     FieldNames(String name) {
       this.name = name;
     }
     private final String name;
     public String getName() {
       return name;
     }
   }

   private final Long id;
   private final Geometry geometry;
   private final String town_name;

   public Town (final Long id, final Geometry geometry, final String town_name) {
     this.id = id;
     this.geometry = geometry;
     this.town_name = town_name;
   }

  public Long getId() {
    return id;
  }

  public Geometry getGeometry() {
    return geometry;
  }

  public String getTown_name() {
    return town_name;
  }
}
