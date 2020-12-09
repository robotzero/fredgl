package com.robotzero.game;

import com.robotzero.engine.AnimationMachine;
import com.robotzero.engine.Collision;
import com.robotzero.engine.Component;
import com.robotzero.engine.GameObject;
import com.robotzero.engine.RigidBody;
import com.robotzero.infrastructure.KeyListener;
import com.robotzero.infrastructure.Window;
import com.robotzero.render.Camera;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class FredController implements Component {
  private final List<Integer> walkingKeys = List.of(GLFW_KEY_A, GLFW_KEY_D, GLFW_KEY_RIGHT, GLFW_KEY_LEFT);
  private AnimationMachine machine = null;
  private GameObject gameObject = null;
  private RigidBody rigidBody = null;
  private boolean onGround = true;
  private Camera camera;

  private float runSpeed = 1000;
  private float jumpSpeed = 15000;

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
      rigidBody.acceleration.x = runSpeed;
      if (gameObject.getTransform().scale.x < 0) {
        gameObject.getTransform().scale.x *= -1;
      }
      if (onGround) {
        machine.trigger("StartWalking");
      }
    } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
      rigidBody.acceleration.x = -runSpeed;
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
      rigidBody.acceleration.y = jumpSpeed;
      machine.trigger("StartJumping");
    } else {
      rigidBody.acceleration.y = 0;
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

    if (collision.side == Collision.CollisionSide.BOTTOM) {
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
}
