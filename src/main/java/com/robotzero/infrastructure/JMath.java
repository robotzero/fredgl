package com.robotzero.infrastructure;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class JMath {
  private static final int FLOAT_SIZE = 4;
  private static final int INT_SIZE = 4;
  private static final int BOOL_SIZE = 4;

  public static int sizeof(DataType type) {
    switch (type) {
      case FLOAT:
        return FLOAT_SIZE;
      case INT:
        return INT_SIZE;
      case BOOL:
        return BOOL_SIZE;
      default:
        System.out.println("Size of this data type unknown in JMath");
        return 0;
    }
  }

  public static void copyValues(Vector4f from, Vector4f to) {
    to.x = from.x;
    to.y = from.y;
    to.z = from.z;
    to.w = from.w;
  }

  public static Vector2f copy(Vector2f vec) {
    return new Vector2f(vec.x, vec.y);
  }

  public static Vector3f copy(Vector3f vec) {
    return new Vector3f(vec.x, vec.y, vec.z);
  }

  public static Vector4f copy(Vector4f vec) {
    return new Vector4f(vec.x, vec.y, vec.z, vec.w);
  }
}
