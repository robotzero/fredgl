package com.robotzero.engine;

public interface Component {
  // public GameObject gameObject;

  default void collision(Collision coll) {}

  default void start() {}

  void setGameObject(GameObject gameObject);

  void update(double dt);

  Component copy();


//  public void trigger(Trigger trigger) {
//    return;
//  }
}
