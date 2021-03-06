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
import org.joml.Vector2f;

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
  // @TODO match the time frames ticks
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
  private boolean canJumpOff = true;
  private int lineOffsetPosition = 12;
  private int jumpBoardPositionOffset = 3;
  private float waitKeyPressTime = 0.16f;

  private Camera camera;

  private float runSpeed = Prefabs.STONEWIDTH * 100;

  private boolean onTheLine = false;
  private boolean jumpingUp = false;
  private boolean jumpingDown = false;
  private int currentRunSpeed = 0;
  private float linePositionX = 0;
  private float jumpBoardPositionX = 0;
  private boolean bottomLineCollision;
  private boolean waitKeyPress = false;
  private boolean flipdir = false;
  private boolean rightKey = false;
  private boolean leftKey = false;

  @Override
  public void start() {
    this.machine = gameObject.getComponent(AnimationMachine.class);
    this.rigidBody = gameObject.getComponent(RigidBody.class);
    this.camera = Window.getScene().camera;
  }

  @Override
  public void update(double dt) {
    final var posXmiddle = this.gameObject.getTransform().position.x + (Prefabs.FREDWIDTH / 2f);
    final var middleX = (com.robotzero.infrastructure.constants.Window.SCREEN_WIDTH / 2f) + this.camera.position().x;
    if (posXmiddle < middleX &&  posXmiddle < this.camera.position().x + com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_X1 && rigidBody.acceleration.x < 0 && this.camera.position().x > 0) {
      this.camera.position().x = posXmiddle - com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_X1;
    }

    if (posXmiddle > middleX && posXmiddle > this.camera.position().x + com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_X2 && rigidBody.acceleration.x > 0) {
      this.camera.position().x = posXmiddle - com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_X2;
    }

    final var posYmiddle = this.gameObject.getTransform().position.y + (Prefabs.FREDHEIGHT / 2f);
    final var middleY = (com.robotzero.infrastructure.constants.Window.SCREEN_HEIGHT / 2f) + this.camera.position().y;

    if (posYmiddle < middleY && posYmiddle < camera.position().y + com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y1 && rigidBody.acceleration.y < com.robotzero.infrastructure.constants.Window.GRAVITY && this.camera.position().y > 0) {
      this.camera.position().y = posYmiddle - com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y1;
    }

    if (posYmiddle > middleY && posYmiddle > this.camera.position().y + com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y2 && rigidBody.acceleration.y > com.robotzero.infrastructure.constants.Window.GRAVITY) {
      this.camera.position().y = posYmiddle - com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y2;
    }

//    if (KeyListener.isKeyPressed(GLFW_KEY_P)) {
//      if (!(runSpeed == 100 * 4f)) {
////        runSpeed = runSpeed * 4f;
//      }
//    } else {
//      runSpeed = this.runSpeed;
//    }

    rigidBody.gravity = 0;
    if (jumpingOff && onTheLine) {
      if (animTime > 0) {
        if (rightKey) {
          if (gameObject.getTransform().scale.x < 0 && flipdir) {
            gameObject.getTransform().scale.x *= -1;
            this.rigidBody.acceleration.x = Prefabs.STONEWIDTH * 1.5f;
          } else if (gameObject.getTransform().scale.x > 0 && flipdir) {
            this.rigidBody.acceleration.x = Prefabs.STONEWIDTH * 1.5f;
            flipdir = false;
          } else if (gameObject.getTransform().scale.x < 0 && !flipdir) {
            gameObject.getTransform().scale.x *= -1;
            this.rigidBody.acceleration.x = -Prefabs.STONEWIDTH * 1.5f;
          } else if (gameObject.getTransform().scale.x > 0 && !flipdir) {
            this.rigidBody.acceleration.x = Prefabs.STONEWIDTH * 1.5f;
          }
        }

        if (leftKey) {
          if (gameObject.getTransform().scale.x > 0 && flipdir) {
            gameObject.getTransform().scale.x *= -1;
            this.rigidBody.acceleration.x = -Prefabs.STONEWIDTH * 1.5f;
          } else if (gameObject.getTransform().scale.x < 0 && flipdir) {
            flipdir = false;
            this.rigidBody.acceleration.x = -Prefabs.STONEWIDTH * 1.5f;
          } else if (gameObject.getTransform().scale.x > 0 && !flipdir) {
            gameObject.getTransform().scale.x *= -1;
            this.rigidBody.acceleration.x = -Prefabs.STONEWIDTH * 1.5f;
          } else if (gameObject.getTransform().scale.x < 0 && !flipdir) {
            this.rigidBody.acceleration.x = -Prefabs.STONEWIDTH * 1.5f;
          }
        }
        animTime -= dt;
        return;
      } else {
        this.rightKey = false;
        this.leftKey = false;
        this.flipdir = false;
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
        this.gameObject.getTransform().position.x = jumpBoardPositionX - jumpBoardPositionOffset;
        this.waitKeyPress = true;
        this.waitKeyPressTime = 0.16f;
      }
    }

    if (waitKeyPress) {
      if (waitKeyPressTime > 0) {
        waitKeyPressTime -= dt;
        return;
      } else {
        waitKeyPress = false;
        waitKeyPressTime = 0.16f;
      }
    }

    if (isOnTheLine() && bottomLineCollision && this.gameObject.getTransform().position.y % Prefabs.STONEHEIGHT < 8) {
      this.gameObject.getTransform().position.y = this.gameObject.getTransform().position.y + 2;
    }

    if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D) && !jumpingUp && !jumpingDown && !KeyListener.isKeyPressed(GLFW_KEY_LEFT) && !KeyListener.isKeyPressed(GLFW_KEY_A)) {
      if (onTheLine) {
        rightKey = true;
        leftKey = false;
        if (gameObject.getTransform().scale.x > 0) {
          gameObject.getTransform().scale.x *= -1;
          flipdir = true;
          this.waitKeyPress = true;
          return;
        }
      }

      if (!onTheLine && gameObject.getTransform().scale.x < 0) {
        gameObject.getTransform().scale.x *= -1;
        this.waitKeyPress = true;
        return;
      }

      if (!onTheLine && !jumpingOn) {
        this.rigidBody.acceleration.x = (float) (dt * runSpeed);
        machine.trigger("StartWalking");
      } else if (onTheLine && !jumpingOn && this.gameObject.getTransform().position.y % Prefabs.STONEHEIGHT < com.robotzero.infrastructure.constants.Window.JUMP_ALLOWANCE) {
        this.rigidBody.acceleration.x = (float) (dt * runSpeed);
        this.rigidBody.acceleration.y = 0;
        machine.trigger("StartJumpOff");
        jumpingOff = true;
        return;
      }
      if (onGround) {
        this.jumpingUp = false;
        this.jumpingOff = false;
        this.jumpingOn = false;
      }
    } else if ((KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) && !jumpingUp && !jumpingDown && !KeyListener.isKeyPressed(GLFW_KEY_RIGHT) && !KeyListener.isKeyPressed(GLFW_KEY_D)) {
      if (onTheLine) {
        this.rightKey = false;
        this.leftKey = true;
        if (gameObject.getTransform().scale.x < 0) {
          gameObject.getTransform().scale.x *= -1;
          flipdir = true;
          this.waitKeyPress = true;
          return;
        }
      }

      if (!onTheLine && gameObject.getTransform().scale.x > 0) {
        gameObject.getTransform().scale.x *= -1;
        this.waitKeyPress = true;
        return;
      }

      if (!onTheLine && !jumpingOn) {
        this.rigidBody.acceleration.x = -(float) (dt * runSpeed);
        machine.trigger("StartWalking");
      } else if (onTheLine && !jumpingOn && canJumpOff && this.gameObject.getTransform().position.y % Prefabs.STONEHEIGHT < com.robotzero.infrastructure.constants.Window.JUMP_ALLOWANCE) {
        if (this.gameObject.getTransform().scale.x > 0) {
          this.rigidBody.acceleration.x = (float) (dt * runSpeed);
        } else {
          this.rigidBody.acceleration.x = -(float) (dt * runSpeed);
        }
        this.rigidBody.acceleration.y = 0;
        machine.trigger("StartJumpOff");
        jumpingOff = true;
        return;
      }

      if (onGround) {
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
      if (collisionWithTheLine && this.gameObject.getTransform().scale.x > 0 && new Vector2f(this.gameObject.getTransform().position.x, 0).distance(new Vector2f(linePositionX + Prefabs.LINEWIDTH / 2f, 0)) < (Prefabs.FREDHEIGHT * 0.5f) + 8) {
        this.rigidBody.acceleration.y = 0;
        this.rigidBody.velocity.y = jumpHeight;
        this.onTheLine = true;
        this.rigidBody.gravity = 0;
        this.gameObject.getTransform().position.x = linePositionX - lineOffsetPosition;
        machine.trigger("StartClimbing");
      } else if (collisionWithTheLine && this.gameObject.getTransform().scale.x < 0 && new Vector2f(this.gameObject.getTransform().position.x + Prefabs.FREDHEIGHT, 0).distance(new Vector2f(linePositionX + Prefabs.LINEWIDTH / 2f, 0)) < (Prefabs.FREDHEIGHT * 0.5f) + 8) {
        this.rigidBody.acceleration.y = 0;
        this.rigidBody.velocity.y = jumpHeight;
        this.onTheLine = true;
        this.rigidBody.gravity = 0;
        this.gameObject.getTransform().position.x = linePositionX - lineOffsetPosition;
        machine.trigger("StartClimbing");
      } else {
        this.jumpingUp = true;
        this.rigidBody.acceleration.y = 0;
        this.rigidBody.gravity = 0;
        this.rigidBody.velocity.y = jumpHeight;
        machine.trigger("StartJumping");
      }
    } else if (!isWalking() && KeyListener.isKeyPressed(GLFW_KEY_W) && onTheLine && !onGround && !jumpingOn && !jumpingDown && !jumpingOff) {
      this.rigidBody.acceleration.y = (float) (dt * runSpeed);
      this.rigidBody.acceleration.x = 0;
    } else if (!isWalking() && KeyListener.isKeyPressed(GLFW_KEY_S) && onTheLine && !onGround && !jumpingOn && !jumpingDown && !jumpingOff) {
        this.rigidBody.acceleration.y = -(float) (dt * runSpeed);
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
    if (collision.side == Collision.CollisionSide.BOTTOM && collision.gameObject.getName().contains("Stone_Block_Prefab") && !onTheLine && !jumpingOn) {
      onGround = true;
      return;
    }
//    } else if (collision.side == Collision.CollisionSide.TOP && collision.gameObject.getName().contains("Stone_Block_Prefab") && !canJumpOff) {
//      canJumpOff = true;
//    };
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
          this.jumpBoardPositionX = trigger.gameObject.getTransform().position.x;
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

  public void setCollisionObjectXPosition(float linePositionX) {
    this.linePositionX = linePositionX;
  }

  public boolean isOnTheLine() {
    return onTheLine;
  }

  public void setBottomLineCollision(boolean bottomLineCollision) {
    this.bottomLineCollision = bottomLineCollision;
  }
}
