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
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class FredController implements Component {
  private final List<Integer> walkingKeys = List.of(GLFW_KEY_A, GLFW_KEY_D, GLFW_KEY_RIGHT, GLFW_KEY_LEFT);
  private AnimationMachine machine = null;
  private GameObject gameObject = null;
  private RigidBody rigidBody;
  private boolean onGround = true;
  private boolean collisionWithTheLine = false;

  private Camera camera;

  private float runSpeed = 1800;
  private float jumpSpeed = 17000;
  private boolean onTheLine;
  private boolean jumping = false;

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
//      if (!onTheLine) {
        this.rigidBody.acceleration.x = runSpeed;
        if (gameObject.getTransform().scale.x < 0) {
          gameObject.getTransform().scale.x *= -1;
//        }
//      } else {
//        if (gameObject.getTransform().scale.x < 0) {
//          this.rigidBody.acceleration.x = runSpeed;
//          this.rigidBody.acceleration.y = jumpSpeed;
//          this.rigidBody.velocity.x *= 2.2f;
//          collisionWithTheLine = false;
//          onTheLine = false;
//          onGround = false;
//          machine.trigger("StartJumpOff");
//        } else {
//          gameObject.getTransform().scale.x *= -1;
//        }
      }
      if (onGround) {
        machine.trigger("StartWalking");
        this.jumping = false;
      }
    } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
//      if (!onTheLine) {
        this.rigidBody.acceleration.x = -runSpeed;
        if (gameObject.getTransform().scale.x > 0) {
          gameObject.getTransform().scale.x *= -1;
        }
//      } else {
//        if (gameObject.getTransform().scale.x > 0) {
//          this.rigidBody.acceleration.x = -runSpeed;
//          this.rigidBody.acceleration.y = jumpSpeed;
//          this.rigidBody.velocity.x *= 2.2f;
//          collisionWithTheLine = false;
//          onTheLine = false;
//          onGround = false;
//          machine.trigger("StartJumpOff");
//        } else {
//          gameObject.getTransform().scale.x *= -1;
//        }
//      }

      if (onGround) {
        machine.trigger("StartWalking");
        this.jumping = false;
      }
    } else if (onGround) {
      this.rigidBody.acceleration.x = 0;
      machine.trigger("StartIdling");
      this.jumping = false;
    }

    if (!isWalking() && KeyListener.isKeyPressed(GLFW_KEY_SPACE) && onGround) {
//      AssetPool.getSound("assets/sounds/jump-small.ogg").play();
      onGround = false;
      this.rigidBody.acceleration.x = 0;
      if (collisionWithTheLine) {
        this.rigidBody.acceleration.y = jumpSpeed;
        machine.trigger("StartClimbing");
        this.rigidBody.acceleration.x = 0;
        onTheLine = true;
        this.rigidBody.acceleration.y = 0;
        this.gameObject.getTransform().position.y =+ 48;
        this.jumping = true;
        machine.trigger("StartClimbing");
      } else {
        this.rigidBody.acceleration.y = jumpSpeed;
        machine.trigger("StartJumping");
      }
    } else if (!isWalking() && KeyListener.isKeyPressed(GLFW_KEY_W) && onTheLine && !onGround) {
      this.rigidBody.acceleration.y = runSpeed;
//      rigidBody.velocity.y = 20 + (float) Math.abs(dt * com.robotzero.infrastructure.constants.Window.GRAVITY);
//    } else if (!isWalking() && KeyListener.isKeyPressed(GLFW_KEY_S) && onTheLine && !onGround) {
////      rigidBody.velocity.y = -20 + (float) Math.abs(dt * com.robotzero.infrastructure.constants.Window.GRAVITY);
//      this.rigidBody.acceleration.y = -runSpeed;
    } else {
      this.rigidBody.acceleration.y = 0;
      this.jumping = false;
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

  public boolean isJumping() {
    return this.jumping;
  }

  public boolean isOnGround() {
    return this.onGround;
  }

  public boolean collisionWithTheLine() {
    return this.collisionWithTheLine;
  }
}
