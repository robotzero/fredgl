package com.robotzero.engine;

import com.robotzero.game.FredController;
import com.robotzero.game.Prefabs;

import java.util.Optional;

public class Line implements Component {
  private GameObject gameObject;
  private Trigger trigger = null;
  private Trigger bottomTrigger = null;
  private int type = 0;

  @Override
  public void trigger(Trigger trigger) {
    if (this.trigger == null || this.bottomTrigger == null) {
      Optional.ofNullable(trigger.gameObject.getComponent(FredController.class)).ifPresent(fredController -> {
        BoxBounds thisBounds = gameObject.getComponent(BoxBounds.class);
        BoxBounds otherBounds = trigger.gameObject.getComponent(BoxBounds.class);
        Optional.ofNullable(thisBounds.resolveCollision(otherBounds, true)).ifPresent(collision -> {
          if (this.trigger == null && (collision.side == Collision.CollisionSide.LEFT || collision.side == Collision.CollisionSide.RIGHT)) {
            this.trigger = trigger;
            fredController.setCollisionWithTheLine(true);
            fredController.setCollisionObjectXPosition(this.gameObject.getTransform().position.x);
          } else if (this.bottomTrigger == null) {
            if (fredController.isOnTheLine() && this.type == 1) {
              System.out.println(trigger.gameObject.getComponent(RigidBody.class).acceleration.y);
              fredController.setBottomLineCollision(true);
              this.bottomTrigger = trigger;
            }
          }
        });
      });
    }
  }

  @Override
  public void unTrigger(Trigger trigger) {
    if (bottomTrigger != null && this.type == 1) {
      Optional.ofNullable(trigger.gameObject.getComponent(FredController.class)).filter(FredController::isOnTheLine).ifPresent(fredController -> {
        if (gameObject.getTransform().position.distance(trigger.gameObject.getTransform().position) > Prefabs.FREDHEIGHT / 2f) {
          fredController.setBottomLineCollision(false);
          bottomTrigger = null;
        }
      });
    }
    if (this.trigger != null) {
      Optional.ofNullable(trigger.gameObject.getComponent(FredController.class)).filter(FredController::isOnGround).ifPresent(fredController -> {
        // Fred facing left
        if (trigger.gameObject.getTransform().scale.x < 0) {
          if (gameObject.getTransform().position.distance(trigger.gameObject.getTransform().position) > Prefabs.FREDHEIGHT) {
            this.trigger = null;
            fredController.setCollisionWithTheLine(false);
            fredController.setCollisionObjectXPosition(0);
          }
        }

        // Fred facing right
        if (trigger.gameObject.getTransform().scale.x > 0) {
          if (gameObject.getTransform().position.distance(trigger.gameObject.getTransform().position) > Prefabs.LINEWIDTH) {
            this.trigger = null;
            fredController.setCollisionWithTheLine(false);
            fredController.setCollisionObjectXPosition(0);
          }
        }
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

  public void setType(int type) {
    this.type = type;
  }
}
