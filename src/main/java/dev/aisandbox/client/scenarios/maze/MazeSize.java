package dev.aisandbox.client.scenarios.maze;

/**
 * MazeSize class.
 *
 * @author gde
 * @version $Id: $Id
 */
public enum MazeSize {
  SMALL {
    @Override
    public String toString() {
      return "Small (8x6)";
    }
  },
  MEDIUM {
    @Override
    public String toString() {
      return "Medium (20x15)";
    }
  },
  LARGE {
    @Override
    public String toString() {
      return "Large (40x30)";
    }
  }
}
