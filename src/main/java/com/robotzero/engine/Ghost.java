package com.robotzero.engine;

import com.robotzero.game.Prefabs;

import java.util.Random;

public class Ghost implements Component {
  private GameObject gameObject;
  private float speed = Prefabs.STONEWIDTH * 100;
  private RigidBody rigidBody;
  private GHOST_DIRECTON ghostDirecton;
  private final Random random = new Random();

  @Override
  public void start() {
    this.rigidBody = gameObject.getComponent(RigidBody.class);
    GHOST_DIRECTON[] dirValues = GHOST_DIRECTON.values();
    ghostDirecton = dirValues[random.nextInt(3)];
  }

  @Override
  public void setGameObject(GameObject gameObject) {
    this.gameObject = gameObject;
  }

  @Override
  public void update(double dt) {
    rigidBody.gravity = 0;
    if (this.ghostDirecton.equals(GHOST_DIRECTON.LEFT)) {
      this.rigidBody.acceleration.x = (float) (dt * speed);
      this.rigidBody.acceleration.y = 0;
    }

    if (this.ghostDirecton.equals(GHOST_DIRECTON.RIGHT)) {
      this.rigidBody.acceleration.x = -(float) (dt * speed);
      this.rigidBody.acceleration.y = 0;
    }

    if (this.ghostDirecton.equals(GHOST_DIRECTON.UP)) {
      this.rigidBody.acceleration.x = 0;
      this.rigidBody.acceleration.y = (float) (dt * speed);
    }

    if (this.ghostDirecton.equals(GHOST_DIRECTON.DOWN)) {
      this.rigidBody.acceleration.x = 0;
      this.rigidBody.acceleration.y = -(float) (dt * speed);
    }
  }

  @Override
  public void collision(Collision collision) {
    if (collision.gameObject.getName().contains("Stone")) {
      if (collision.side == Collision.CollisionSide.LEFT) {
        while (!ghostDirecton.equals(GHOST_DIRECTON.LEFT)) {
          GHOST_DIRECTON[] dirValues = GHOST_DIRECTON.values();
          ghostDirecton = dirValues[random.nextInt() % 3];
        }
      }

      if (collision.side == Collision.CollisionSide.RIGHT) {
        while (!ghostDirecton.equals(GHOST_DIRECTON.RIGHT)) {
          GHOST_DIRECTON[] dirValues = GHOST_DIRECTON.values();
          ghostDirecton = dirValues[random.nextInt() % 3];
        }
      }

      if (collision.side == Collision.CollisionSide.TOP) {
        while (!ghostDirecton.equals(GHOST_DIRECTON.UP)) {
          GHOST_DIRECTON[] dirValues = GHOST_DIRECTON.values();
          ghostDirecton = dirValues[random.nextInt() % 3];
        }
      }

      if (collision.side == Collision.CollisionSide.BOTTOM) {
        while (!ghostDirecton.equals(GHOST_DIRECTON.DOWN)) {
          GHOST_DIRECTON[] dirValues = GHOST_DIRECTON.values();
          ghostDirecton = dirValues[random.nextInt() % 3];
        }
      }
    }
  }

  @Override
  public Component copy() {
    return null;
  }
}

enum GHOST_DIRECTON {
  RIGHT(0), LEFT(1), UP(2), DOWN(3);
  private final int dir;

  GHOST_DIRECTON(int dir) {
    this.dir = dir;
  }
}

