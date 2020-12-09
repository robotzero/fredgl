package com.robotzero.engine;

import com.robotzero.dataStructure.Transform;

import java.util.List;

public class Maps {
  private final List<Transform> transforms;

  public Maps(List<Transform> transforms) {
    this.transforms = transforms;
  }

  public List<Transform> getTransforms() {
    return transforms;
  }
}
