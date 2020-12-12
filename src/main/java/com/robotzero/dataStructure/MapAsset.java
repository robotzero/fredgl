package com.robotzero.dataStructure;

import java.util.List;

public class MapAsset {
  private final List<Transform> stoneTransforms;
  private final List<Transform> lineTransforms;

  public MapAsset(List<Transform> stoneTransforms, List<Transform> lineTransforms) {
    this.stoneTransforms = stoneTransforms;
    this.lineTransforms = lineTransforms;
  }

  public List<Transform> getStoneTransforms() {
    return stoneTransforms;
  }

  public List<Transform> getLineTransforms() {
    return lineTransforms;
  }
}
