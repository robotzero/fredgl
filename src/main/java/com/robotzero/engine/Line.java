package com.robotzero.engine;

import com.robotzero.game.FredController;

import java.util.Optional;

public class Line implements Component {
  private GameObject gameObject;

  @Override
  public void trigger(Trigger trigger) {
    Optional.ofNullable(trigger.gameObject.getComponent(FredController.class)).ifPresent(FredController::setOnTheLine);
  }

  @Override
  public void setGameObject(GameObject gameObject) {
    this.gameObject = gameObject;
  }

  @Override
  public void update(double dt) {

  }

  @Override
  public Component copy() {
    return null;
  }
}
