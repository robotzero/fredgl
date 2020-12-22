package com.robotzero.engine;

import com.robotzero.infrastructure.constants.Window;
import org.joml.Vector2f;

public class RigidBody implements Component {
  public Vector2f velocity;
  public Vector2f acceleration;
  private GameObject gameObject = null;

  public RigidBody() {
    this.velocity = new Vector2f();
    this.acceleration = new Vector2f(0, Window.GRAVITY);
  }

  @Override
  public void update(double dt) {
    this.gameObject.getTransform().position.add(this.velocity.x * (float)dt, this.velocity.y * (float)dt);

    this.velocity.add(this.acceleration.x * (float)dt, (this.acceleration.y * (float)dt) + (Window.GRAVITY * (float)dt));
//    this.velocity.add(this.acceleration.x * (float)dt, (this.acceleration.y * (float)dt));
    this.velocity.x *= 0.8f;
    this.velocity.y *= 0.99f;
  }

  @Override
  public void setGameObject(GameObject gameObject) {
    this.gameObject = gameObject;
  }

  @Override
  public Component copy() {
    return new RigidBody();
  }
}
