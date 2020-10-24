package com.robotzero.engine;

public interface Component {
  // public GameObject gameObject;

  void update(double dt);

  void start();

  void setGameObject(GameObject gameObject);

  Component copy();

  // public void collision(Collision coll);

//  public void trigger(Trigger trigger) {
//    return;
//  }
}
