package com.robotzero.game;

import com.robotzero.engine.AnimationMachine;
import com.robotzero.engine.BoxBounds;
import com.robotzero.engine.Collision;
import com.robotzero.engine.Component;
import com.robotzero.engine.GameObject;
import com.robotzero.engine.RigidBody;
import com.robotzero.engine.Trigger;
import com.robotzero.infrastructure.KeyListener;
import com.robotzero.infrastructure.Window;
import com.robotzero.render.Camera;

import java.util.List;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class FredController implements Component {
  private final List<Integer> walkingKeys = List.of(GLFW_KEY_A, GLFW_KEY_D, GLFW_KEY_RIGHT, GLFW_KEY_LEFT);
  private final static float time = 0.6f;
  private final static int jumpHeight = (int) (Prefabs.STONEHEIGHT * 0.15);
  private AnimationMachine machine = null;
  private GameObject gameObject = null;
  private RigidBody rigidBody;
  private boolean onGround = true;
  private boolean collisionWithTheLine = false;
  private float animTime = time;
  private boolean jumpingOff = false;
  private boolean jumpingOn = false;

  private Camera camera;

  private float runSpeed = 100;
  private float jumpSpeed = 15;
  private boolean onTheLine;
  private boolean jumpingUp = false;
  private boolean jumpingDown = false;
  private int currentRunSpeed = 0;

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

    if (this.camera.position().y < this.gameObject.getTransform().position.y - com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_3) {
      this.camera.position().y = this.gameObject.getTransform().position.y - com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_3;
    } else if (this.camera.position().y > com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_3 - this.gameObject.getTransform().position.y) {
      if (this.camera.position().y > com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_3 - this.gameObject.getTransform().position.y) {
        this.camera.position().y = this.gameObject.getTransform().position.y - com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_3;
      }
    }

    if (KeyListener.isKeyPressed(GLFW_KEY_P)) {
      if (!(runSpeed == 100 * 4f)) {
        runSpeed = runSpeed * 4f;
      }
    } else {
      runSpeed = this.runSpeed;
    }

    rigidBody.gravity = 0;
    if (jumpingOff && onTheLine) {
      if (animTime > 0) {
        if (gameObject.getTransform().scale.x < 0) {
          this.rigidBody.acceleration.x = -Prefabs.STONEWIDTH * 1.5f - 4;
        } else {
          this.rigidBody.acceleration.x = Prefabs.STONEWIDTH * 1.5f + 4;
        }
        animTime -= dt;
        return;
      } else {
        this.rigidBody.velocity.y = -jumpHeight * 2 * 2;
        this.rigidBody.velocity.x = 0;
        animTime = time;
        jumpingOff = false;
        this.onTheLine = false;
        this.onGround = true;
      }
    }

    if (jumpingUp) {
      if (animTime > 0) {
        this.onGround = false;
        animTime -= dt;
        return;
      } else {
        animTime = time;
        jumpingUp = false;
        this.onGround = false;
        this.rigidBody.acceleration.y = 0;
        this.rigidBody.acceleration.x = 0;
        this.jumpingDown = true;
      }
    }

    if (jumpingDown) {
      animTime = time;
      jumpingUp = false;
      this.onGround = true;
      this.rigidBody.velocity.y = -jumpHeight;
      this.jumpingDown = false;
    }

    if (jumpingOn) {
      if (animTime > 0) {
        this.onGround = false;
        if (gameObject.getTransform().scale.x < 0) {
          this.rigidBody.acceleration.x = -(Prefabs.STONEWIDTH + (int) (Prefabs.STONEWIDTH * 0.5));
        } else {
          this.rigidBody.acceleration.x = Prefabs.STONEWIDTH + (int) (Prefabs.STONEWIDTH * 0.5);
        }
        this.rigidBody.acceleration.y = jumpHeight;
        animTime -= dt;
        return;
      } else {
        animTime = time;
        jumpingOn = false;
        this.onGround = false;
        this.onTheLine = true;
        this.rigidBody.acceleration.x = 0;
        this.rigidBody.acceleration.y = 0;
      }
    }

    if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D) && !jumpingUp && !jumpingDown) {
      if (gameObject.getTransform().scale.x < 0) {
        gameObject.getTransform().scale.x *= -1;
      }
      if (!onTheLine && !jumpingOn) {
        this.rigidBody.acceleration.x = runSpeed;
      } else if (onTheLine && !jumpingOn) {
        this.rigidBody.acceleration.x = -runSpeed;
        this.rigidBody.acceleration.y = 0;
        machine.trigger("StartJumpOff");
        jumpingOff = true;
        return;
      }
      if (onGround) {
        machine.trigger("StartWalking");
        this.jumpingUp = false;
        this.jumpingOff = false;
        this.jumpingOn = false;
      }
    } else if ((KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) && !jumpingUp && !jumpingDown) {
      if (gameObject.getTransform().scale.x > 0) {
        gameObject.getTransform().scale.x *= -1;
      }
      if (!onTheLine && !jumpingOn) {
        this.rigidBody.acceleration.x = -runSpeed;
      } else if (onTheLine && !jumpingOn) {
        this.rigidBody.acceleration.x = runSpeed;
        this.rigidBody.acceleration.y = 0;
        machine.trigger("StartJumpOff");
        jumpingOff = true;
        return;
      }

      if (onGround) {
        machine.trigger("StartWalking");
        this.jumpingUp = false;
        jumpingOff = false;
        this.jumpingOn = false;
      }
    } else if (onGround) {
      this.rigidBody.acceleration.x = 0;
      machine.trigger("StartIdling");
      this.jumpingUp = false;
      this.jumpingOff = false;
      this.jumpingOn = false;
    }

    if (!isWalking() && KeyListener.isKeyPressed(GLFW_KEY_SPACE) && onGround) {
//      AssetPool.getSound("assets/sounds/jump-small.ogg").play();
      onGround = false;
      this.rigidBody.acceleration.x = 0;
      if (collisionWithTheLine) {
        this.rigidBody.acceleration.y = 0;
        this.rigidBody.velocity.y = jumpHeight;
        this.onTheLine = true;
        this.rigidBody.gravity = 0;
        machine.trigger("StartClimbing");
      } else {
        this.jumpingUp = true;
        this.rigidBody.acceleration.y = 0;
        this.rigidBody.gravity = 0;
        this.rigidBody.velocity.y = jumpHeight;
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
    if (collision.side == Collision.CollisionSide.BOTTOM && !onTheLine && !jumpingOn) {
      onGround = true;
    }
  }

  @Override
  public void trigger(Trigger trigger) {
    if (trigger.gameObject.getName().contains("JumpBoard")) {
      BoxBounds thisBounds = gameObject.getComponent(BoxBounds.class);
      BoxBounds otherBounds = trigger.gameObject.getComponent(BoxBounds.class);
      Optional.ofNullable(thisBounds.resolveCollision(otherBounds, true)).filter(collision -> {
        return collision.side == Collision.CollisionSide.LEFT || collision.side == Collision.CollisionSide.RIGHT;
      }).ifPresent(collision -> {
        if (isOnGround()) {
          if (this.gameObject.getTransform().scale.x > 0 && collision.side == Collision.CollisionSide.RIGHT) {
            this.jumpingOn = true;
            this.machine.trigger("StartJumpOn");
          } else if (this.gameObject.getTransform().scale.x < 0 && collision.side == Collision.CollisionSide.LEFT) {
            this.jumpingOn = true;
            this.machine.trigger("StartJumpOn");
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
  public Component copy() {
    return null;
  }

  private boolean isWalking() {
    return walkingKeys.stream().anyMatch(KeyListener::isKeyPressed);
  }

  public void setCollisionWithTheLine(boolean collisionWithTheLine) {
    this.collisionWithTheLine = collisionWithTheLine;
  }

  public boolean isOnGround() {
    return this.onGround;
  }
}
