package com.enneper.pbq.parcel;

public interface Parcel {

  enum State {VT, NH}

  String getIDName();
  String getGeometryName();
  String getTownName();

}
