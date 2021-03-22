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
        BoxBounds thisBounds = gameObject.getComponent(BoxBounds.class);
        BoxBounds otherBounds = trigger.gameObject.getComponent(BoxBounds.class);
        Optional.ofNullable(thisBounds.resolveCollision(otherBounds, true)).filter(collision -> {
          return collision.side == Collision.CollisionSide.LEFT || collision.side == Collision.CollisionSide.RIGHT;
        }).ifPresent(collision -> {
          this.trigger = trigger;
          fredController.setCollisionWithTheLine(true);
          fredController.setCollisionObjectXPosition(this.gameObject.getTransform().position.x);
        });
      });
    }
  }

  @Override
  public void unTrigger(Trigger trigger) {
    if (this.trigger != null) {
      Optional.ofNullable(trigger.gameObject.getComponent(FredController.class)).ifPresent(fredController -> {
        BoxBounds thisBounds = gameObject.getComponent(BoxBounds.class);
        BoxBounds otherBounds = trigger.gameObject.getComponent(BoxBounds.class);
        Optional.ofNullable(thisBounds.resolveCollision(otherBounds, true)).filter(collision -> {
          return collision.side == Collision.CollisionSide.LEFT || collision.side == Collision.CollisionSide.RIGHT;
        }).ifPresent(collision -> {
          this.trigger = null;
          fredController.setCollisionWithTheLine(false);
          fredController.setCollisionObjectXPosition(0);
        });
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
