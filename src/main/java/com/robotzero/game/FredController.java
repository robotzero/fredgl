package com.robotzero.game;

import com.robotzero.engine.AnimationMachine;
import com.robotzero.engine.Collision;
import com.robotzero.engine.Component;
import com.robotzero.engine.GameObject;
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
  private RigidBody rigidBody;
  private boolean onGround = true;
  private boolean collisionWithTheLine = false;
  private float animTime = 0.4f;
  private boolean jumpingOff = false;
  private boolean jumpingOn = false;

  private Camera camera;

  private float runSpeed = 1000;
  private float jumpSpeed = 11000;
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

    if (jumpingOff && onTheLine) {
      if (animTime > 0) {
        if (gameObject.getTransform().scale.x < 0) {
          this.rigidBody.acceleration.x = -900;
        } else {
          this.rigidBody.acceleration.x = 900;
        }
        this.rigidBody.acceleration.y = 0;
        animTime -= dt;
        return;
      } else {
        animTime = 0.4f;
        jumpingOff = false;
        this.jumping = false;
        this.onGround = true;
        this.onTheLine = false;
      }
    }

    if (jumpingOn) {
      if (animTime > 0) {
        if (gameObject.getTransform().scale.x < 0) {
          this.rigidBody.acceleration.x = -900;
        } else {
          this.rigidBody.acceleration.x = 900;
        }
        this.rigidBody.acceleration.y = 0;
        animTime -= dt;
        return;
      } else {
        animTime = 0.4f;
        jumpingOn = false;
        this.onGround = false;
        this.onTheLine = true;
      }
    }

    if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
      if (gameObject.getTransform().scale.x < 0) {
        gameObject.getTransform().scale.x *= -1;
      }
      if (!onTheLine && !jumpingOn) {
        this.rigidBody.acceleration.x = runSpeed;
      } else if (onTheLine) {
//        if (gameObject.getTransform().scale.x < 0) {
          this.rigidBody.acceleration.x = -900;
          this.rigidBody.acceleration.y = 0;
          machine.trigger("StartJumpOff");
          jumpingOff = true;
          return;
//        } else {
//          gameObject.getTransform().scale.x *= -1;
//        }
      } else {
        this.rigidBody.acceleration.x = 900;
        this.rigidBody.acceleration.y = 0;
        machine.trigger("StartJumpOn");
        return;
      }
      if (onGround) {
        machine.trigger("StartWalking");
        this.jumping = false;
        this.jumpingOff = false;
        this.jumpingOn = false;
      }
    } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
      if (gameObject.getTransform().scale.x > 0) {
        gameObject.getTransform().scale.x *= -1;
      }
      if (!onTheLine && !jumpingOn) {
        this.rigidBody.acceleration.x = -runSpeed;
      } else {
//        if (gameObject.getTransform().scale.x > 0) {
          this.rigidBody.acceleration.x = 900;
          this.rigidBody.acceleration.y = 0;
          onGround = false;
          machine.trigger("StartJumpOff");
          jumpingOff = true;
          return;
//        } else {
//          gameObject.getTransform().scale.x *= -1;
//        }
      }

      if (onGround) {
        machine.trigger("StartWalking");
        this.jumping = false;
        jumpingOff = false;
      }
    } else if (onGround) {
      this.rigidBody.acceleration.x = 0;
      machine.trigger("StartIdling");
      this.jumping = false;
      this.jumpingOff = false;
    }

    if (!isWalking() && KeyListener.isKeyPressed(GLFW_KEY_SPACE) && onGround) {
//      AssetPool.getSound("assets/sounds/jump-small.ogg").play();
      onGround = false;
      this.rigidBody.acceleration.x = 0;
      if (collisionWithTheLine) {
        this.rigidBody.acceleration.y = jumpSpeed;
        this.rigidBody.acceleration.x = 0;
        this.onTheLine = true;
        this.jumping = true;
        machine.trigger("StartClimbing");
      } else {
        this.rigidBody.acceleration.y = jumpSpeed;
        machine.trigger("StartJumping");
      }
    } else if (!isWalking() && KeyListener.isKeyPressed(GLFW_KEY_W) && onTheLine && !onGround) {
      this.rigidBody.acceleration.y = runSpeed;
      this.rigidBody.acceleration.x = 0;
    } else if (!isWalking() && KeyListener.isKeyPressed(GLFW_KEY_S) && onTheLine && !onGround) {
      this.rigidBody.acceleration.y = -runSpeed;
      this.rigidBody.acceleration.x = 0;
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
  public void trigger(Trigger trigger) {
    if (trigger.gameObject.getName().contains("JumpBoard")) {
      this.jumpingOn = true;
      this.machine.trigger("StartJumpOn");
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

  public boolean isOnTheLine() {
    return this.onTheLine;
  }

  public boolean isJumpingOn() {
    return this.jumpingOn;
  }
}
