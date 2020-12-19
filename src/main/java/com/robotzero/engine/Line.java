package com.robotzero.engine;

import com.robotzero.game.FredController;

import java.util.Optional;

public class Line implements Component {
  private GameObject gameObject;
  private Trigger trigger = null;

  @Override
  public void trigger(Trigger trigger) {
    if (this.trigger == null) {
      Optional.ofNullable(trigger.gameObject.getComponent(FredController.class)).ifPresent(fredController -> {
        this.trigger = trigger;
        fredController.setCollisionWithTheLine(true);
      });
    }
  }

  @Override
  public void unTrigger(Trigger trigger) {
    if (this.trigger != null) {
      Optional.ofNullable(trigger.gameObject.getComponent(FredController.class)).ifPresent(fredController -> {
        this.trigger = null;
        fredController.setCollisionWithTheLine(false);
      });
    }
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
