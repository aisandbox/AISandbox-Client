package dev.aisandbox.client.scenarios.twisty.tpmodel.shapes;

import java.awt.Polygon;

public interface CellShape {

  public Polygon getPolygon(int locationX, int locationY, int rotation, int scale);
}
