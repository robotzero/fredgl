package com.robotzero.game;

import com.robotzero.engine.AnimationMachine;
import com.robotzero.engine.Component;
import com.robotzero.engine.GameObject;
import com.robotzero.engine.RigidBody;
import com.robotzero.engine.SpriteRenderer;
import com.robotzero.infrastructure.KeyListener;
import com.robotzero.infrastructure.Window;
import com.robotzero.render.Camera;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

public class FredController implements Component {
  private AnimationMachine machine = null;
  private SpriteRenderer sprite = null;
  private GameObject gameObject = null;
  private RigidBody rigidBody = null;
  private boolean onGround = true;
  private Camera camera;

  private float runSpeed = 1800;
  private float jumpSpeed = 17000;

  @Override
  public void start() {
    this.machine = (AnimationMachine) gameObject.getComponent(AnimationMachine.class);
    this.sprite = (SpriteRenderer) gameObject.getComponent(SpriteRenderer.class);
    this.rigidBody = (RigidBody) gameObject.getComponent(RigidBody.class);
    this.camera = Window.getScene().camera;
  }

  @Override
  public void update(double dt) {
    if (this.camera.position().x < this.gameObject.getTransform().position.x - com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_X) {
      this.camera.position().x = this.gameObject.getTransform().position.x - com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_X;
    }

    if (this.gameObject.getTransform().position.y < com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_1 && camera.position().y != com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_2) {
      Window.getWindow().setColor(com.robotzero.infrastructure.constants.Window.COLOR_BLACK);
      this.camera.position().y = com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_2;
    } else if (this.gameObject.getTransform().position.y > com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_1 && camera.position().y != com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_1) {
      Window.getWindow().setColor(com.robotzero.infrastructure.constants.Window.SKY_COLOR);
      this.camera.position().y = com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_1;
    }

    if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
      rigidBody.acceleration.x = runSpeed;
      if (gameObject.getTransform().scale.x < 0) {
        gameObject.getTransform().scale.x *= -1;
      }
      if (onGround) {
        machine.trigger("StartRunning");
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

//    immunityLeft -= dt;
//    flashLeft -= dt;
//    fireballCooldownTime -= dt;
//    invincibilityLeft -= dt;
  }


  @Override
  public void setGameObject(GameObject gameObject) {
    this.gameObject = gameObject;
  }

  @Override
  public Component copy() {
    return null;
  }
}
