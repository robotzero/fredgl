package com.robotzero.game;

import com.robotzero.engine.AnimationMachine;
import com.robotzero.engine.Collision;
import com.robotzero.engine.Component;
import com.robotzero.engine.GameObject;
import com.robotzero.engine.Line;
import com.robotzero.engine.RigidBody;
import com.robotzero.engine.Trigger;
import com.robotzero.infrastructure.KeyListener;
import com.robotzero.infrastructure.Window;
import com.robotzero.render.Camera;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class FredController implements Component {
  private final List<Integer> walkingKeys = List.of(GLFW_KEY_A, GLFW_KEY_D, GLFW_KEY_RIGHT, GLFW_KEY_LEFT);
  private AnimationMachine machine = null;
  private GameObject gameObject = null;
  private RigidBody rigidBody = null;
  private boolean onGround = true;
  private boolean collisionWithTheLine = false;
  private Camera camera;

  private float runSpeed = 1000;
  private float jumpSpeed = 15000;
  private boolean onTheLine;

  @Override
  public void start() {
    this.machine = gameObject.getComponent(AnimationMachine.class);
    this.rigidBody = gameObject.getComponent(RigidBody.class);
    this.camera = Window.getScene().camera;
  }

  @Override
  public void update(double dt) {
    if (this.camera.position().x < this.gameObject.getTransform().position.x - com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_X) {
      this.camera.position().x = this.gameObject.getTransform().position.x - com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_X;
    } else {
      this.camera.position().x = this.gameObject.getTransform().position.x - com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_X;
    }

    if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
      if (!onTheLine) {
        rigidBody.acceleration.x = runSpeed;
      } else {
        rigidBody.acceleration.x = runSpeed;
        rigidBody.acceleration.y = jumpSpeed;
        rigidBody.velocity.x *= 2.2f;
        collisionWithTheLine = false;
        onTheLine = false;
        onGround = false;
        machine.trigger("StartJumpOff");
      }

      if (gameObject.getTransform().scale.x < 0) {
        gameObject.getTransform().scale.x *= -1;
      }
      if (onGround) {
        machine.trigger("StartWalking");
      }
    } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
      if (!onTheLine) {
        rigidBody.acceleration.x = -runSpeed;
      } else {
        rigidBody.acceleration.x = -runSpeed;
        rigidBody.acceleration.y = jumpSpeed;
        rigidBody.velocity.x *= 2.2f;
        collisionWithTheLine = false;
        onTheLine = false;
        onGround = false;
        machine.trigger("StartJumpOff");
      }

      if (gameObject.getTransform().scale.x > 0) {
        gameObject.getTransform().scale.x *= -1;
      }
      if (onGround) {
        machine.trigger("StartWalking");
      }
    } else if (onGround) {
      rigidBody.acceleration.x = 0;
      machine.trigger("StartIdling");
    }

    if (!isWalking() && KeyListener.isKeyPressed(GLFW_KEY_SPACE) && onGround) {
//      AssetPool.getSound("assets/sounds/jump-small.ogg").play();
      onGround = false;
      if (collisionWithTheLine) {
        rigidBody.acceleration.y = 12000;
        machine.trigger("StartClimbing");
        rigidBody.acceleration.x = 0;
        onTheLine = true;
      } else {
        rigidBody.acceleration.y = jumpSpeed;
        machine.trigger("StartJumping");
      }
    } else if (!isWalking() && KeyListener.isKeyPressed(GLFW_KEY_W) && onTheLine && !onGround) {
      rigidBody.acceleration.y = runSpeed;
      rigidBody.velocity.y = 20 + (float) Math.abs(dt * com.robotzero.infrastructure.constants.Window.GRAVITY);
    } else if (!isWalking() && KeyListener.isKeyPressed(GLFW_KEY_S) && onTheLine && !onGround) {
      rigidBody.velocity.y = -20 + (float) Math.abs(dt * com.robotzero.infrastructure.constants.Window.GRAVITY);
      rigidBody.acceleration.y = -runSpeed;
    } else {
      rigidBody.acceleration.y = 0;
    }

    if (onTheLine) {
      //rigidBody.velocity.y += (float) Math.abs(dt * com.robotzero.infrastructure.constants.Window.GRAVITY);
    }
//    immunityLeft -= dt;
//    flashLeft -= dt;
//    fireballCooldownTime -= dt;
//    invincibilityLeft -= dt;
  }

  @Override
  public void collision(Collision collision) {
//    if (doWinAnimation && collision.side == Collision.CollisionSide.BOTTOM) {
//      slidingDown = false;
//      return;
//    }
    if (collision.side == Collision.CollisionSide.BOTTOM && !collisionWithTheLine) {
      onGround = true;
    }
  }

  @Override
  public void setGameObject(GameObject gameObject) {
    this.gameObject = gameObject;
  }

  @Override
  public Component copy() {
    return null;
  }

  private boolean isWalking() {
    return walkingKeys.stream().anyMatch(KeyListener::isKeyPressed);
  }

  public void setCollisionWithTheLine(boolean collisionWithTheLine) {
    this.collisionWithTheLine = collisionWithTheLine;
  }
}
