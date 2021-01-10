package com.robotzero.dataStructure;

import java.util.List;
import java.util.Map;

public class MapAsset {
  private final List<Transform> stoneTransforms;
  private final Map<Integer, List<Transform>> lineTransforms;
  private final List<Transform> jumpBoardTransforms;

  public MapAsset(List<Transform> stoneTransforms, Map<Integer, List<Transform>> lineTransforms, List<Transform> jumpBoardTransforms) {
    this.stoneTransforms = stoneTransforms;
    this.lineTransforms = lineTransforms;
    this.jumpBoardTransforms = jumpBoardTransforms;
  }

  public List<Transform> getStoneTransforms() {
    return stoneTransforms;
  }

  public Map<Integer, List<Transform>> getLineTransforms() {
    return lineTransforms;
  }

  public List<Transform> getJumpBoardTransforms() {
    return jumpBoardTransforms;
  }
}
