package com.robotzero.engine;

import com.robotzero.dataStructure.Tuple;
import com.robotzero.infrastructure.Window;
import org.joml.Vector2f;

public class BoxBounds extends Bounds {
  private float width, height;
  private float halfWidth, halfHeight;
  private Vector2f center = new Vector2f();
  private float xBuffer = 0.0f;
  private float yBuffer = 0.0f;

  private boolean shouldCheckTop = true;
  private boolean shouldCheckBottom = true;
  private boolean shouldCheckLeft = true;
  private boolean shouldCheckRight = true;

  public BoxBounds(float width, float height, boolean isStatic, boolean isTrigger) {
    init(width, height, isStatic, isTrigger);
  }

  public void init(float width, float height, boolean isStatic, boolean isTrigger) {
    this.width = width;
    this.height = height;
    this.halfWidth = this.width / 2.0f;
    this.halfHeight = this.height / 2.0f;
    this.type = BoundsType.Box;
    this.isStatic = isStatic;
    this.isTrigger = isTrigger;
  }

  public void setHeight(float newHeight) {
    this.height = newHeight;
    this.halfHeight = newHeight / 2.0f;
    this.calculateCenter();
  }

  public void setTrigger(boolean val) {
    this.isTrigger = val;
  }

  public float getCenterX() {
    return this.center.x;
  }

  public float getCenterY() {
    return this.center.y;
  }

  public void setXBuffer(float val) {
    this.xBuffer = val;
  }

  public void setYBuffer(float val) {
    this.yBuffer = val;
  }

  @Override
  public void update(double dt) {

  }

  @Override
  public void start() {
    this.calculateCenter();

    if (isStatic) {
      // Figure out if static blocks have blocks above, below, right, or left of them
      // to avoid 'catching' on edges
      Tuple<Integer> gridCoords = gameObject.getGridCoords().copy();
      gridCoords.y += com.robotzero.infrastructure.constants.Window.TILE_HEIGHT;
      GameObject go = Window.getScene().getWorldPartition().get(gridCoords);
      if (go != null && go.getComponent(BoxBounds.class).isStatic) {
        this.shouldCheckTop = false;
      }
      gridCoords.y -= com.robotzero.infrastructure.constants.Window.TILE_HEIGHT * 2;
      go = Window.getScene().getWorldPartition().get(gridCoords);
      if (go != null && go.getComponent(BoxBounds.class).isStatic) {
        this.shouldCheckBottom = false;
      }
      gridCoords.y += com.robotzero.infrastructure.constants.Window.TILE_HEIGHT;
      gridCoords.x += com.robotzero.infrastructure.constants.Window.TILE_WIDTH;
      go = Window.getScene().getWorldPartition().get(gridCoords);
      if (go != null && go.getComponent(BoxBounds.class).isStatic) {
        this.shouldCheckRight = false;
      }
      gridCoords.x -= com.robotzero.infrastructure.constants.Window.TILE_WIDTH * 2;
      go = Window.getScene().getWorldPartition().get(gridCoords);
      if (go != null && go.getComponent(BoxBounds.class).isStatic) {
        this.shouldCheckLeft = false;
      }
    }
  }

  @Override
  public void setGameObject(GameObject gameObject) {
    this.gameObject = gameObject;
  }

  public void calculateCenter() {
    this.center.x = this.gameObject.getTransform().position.x + this.halfWidth + this.xBuffer;
    this.center.y = this.gameObject.getTransform().position.y + this.halfHeight + this.yBuffer;
  }

  public static boolean checkCollision(BoxBounds b1, BoxBounds b2) {
    b1.calculateCenter();
    b2.calculateCenter();

    float dx = b2.center.x - b1.center.x;
    float dy = b2.center.y - b1.center.y;

    float combinedHalfWidths = b1.halfWidth + b2.halfWidth;
    float combinedHalfHeights = b1.halfHeight + b2.halfHeight;

    if (Math.abs(dx) <= combinedHalfWidths) {
      return Math.abs(dy) <= combinedHalfHeights;
    }

    return false;
  }

  public Collision resolveCollision(BoxBounds otherBounds) {
    float dx = this.center.x - otherBounds.center.x;
    float dy = this.center.y - otherBounds.center.y;

    float combinedHalfWidths = otherBounds.halfWidth + this.halfWidth;
    float combinedHalfHeights = otherBounds.halfHeight + this.halfHeight;

    float overlapX = combinedHalfWidths - Math.abs(dx);
    float overlapY = combinedHalfHeights - Math.abs(dy);

    if (overlapX >= overlapY) {
      if (dy >= 0) {
        if (!otherBounds.shouldCheckTop || !this.shouldCheckBottom) {
          return null;
        }

        // Collision on the bottom of this
        this.gameObject.getTransform().position.y = otherBounds.gameObject.getTransform().position.y + otherBounds.getHeight();
        if (this.gameObject.getComponent(RigidBody.class) != null && this.gameObject.getComponent(RigidBody.class).velocity.y < 0)
          this.gameObject.getComponent(RigidBody.class).velocity.y = 0;

        // Top of other bounds
        Vector2f contactPoint = new Vector2f(otherBounds.center.x, otherBounds.gameObject.getTransform().position.y + otherBounds.getHeight());
        return new Collision(otherBounds.gameObject, Collision.CollisionSide.BOTTOM, contactPoint, this);
      } else {
        if (!otherBounds.shouldCheckBottom || !this.shouldCheckTop){
          return null;
        }

        // Collision on the top of this
        this.gameObject.getTransform().position.y = otherBounds.gameObject.getTransform().position.y - this.getHeight();
        if (this.gameObject.getComponent(RigidBody.class) != null && this.gameObject.getComponent(RigidBody.class).velocity.y > 0)
          this.gameObject.getComponent(RigidBody.class).velocity.y = 0;

        // Bottom of other bounds
        Vector2f contactPoint = new Vector2f(otherBounds.center.x, otherBounds.gameObject.getTransform().position.y);
        return new Collision(otherBounds.gameObject, Collision.CollisionSide.TOP, contactPoint, this);
      }
    } else {
      if (dx < 0) {
        if (!otherBounds.shouldCheckLeft || !this.shouldCheckRight) {
          return null;
        }

        // Collision on the right of this
        this.gameObject.getTransform().position.x = otherBounds.gameObject.getTransform().position.x - this.getWidth();

        // Left of other bounds
        Vector2f contactPoint = new Vector2f(otherBounds.gameObject.getTransform().position.x, otherBounds.center.y);
        return new Collision(otherBounds.gameObject, Collision.CollisionSide.RIGHT, contactPoint, this);
      } else {
        if (!otherBounds.shouldCheckRight || !this.shouldCheckRight) {
          return null;
        }

        // Collision on the left of this
        this.gameObject.getTransform().position.x = otherBounds.gameObject.getTransform().position.x + otherBounds.getWidth();

        // Right of other bounds
        Vector2f contactPoint = new Vector2f(otherBounds.gameObject.getTransform().position.x + otherBounds.getWidth(), otherBounds.center.y);
        return new Collision(otherBounds.gameObject, Collision.CollisionSide.LEFT, contactPoint, this);
      }
    }
  }

  @Override
  public Component copy() {
    BoxBounds bounds = new BoxBounds(width, height, isStatic, isTrigger);
    bounds.xBuffer = xBuffer;
    bounds.yBuffer = yBuffer;
    return bounds;
  }

//  @Override
//  public String serialize(int tabSize) {
//    StringBuilder builder = new StringBuilder();
//
//    builder.append(beginObjectProperty("BoxBounds", tabSize));
//    builder.append(addFloatProperty("Width", this.width, tabSize + 1, true, true));
//    builder.append(addFloatProperty("Height", this.height, tabSize + 1, true, true));
//    builder.append(addFloatProperty("xBuffer", this.xBuffer, tabSize + 1, true, true));
//    builder.append(addFloatProperty("yBuffer", this.yBuffer, tabSize + 1, true, true));
//    builder.append(addBooleanProperty("isTrigger", this.isTrigger, tabSize + 1, true, true));
//    builder.append(addBooleanProperty("isStatic", this.isStatic, tabSize + 1, true, false));
//    builder.append(closeObjectProperty(tabSize));
//
//    return builder.toString();
//  }
//
//  public static BoxBounds deserialize() {
//    float width = Parser.consumeFloatProperty("Width");
//    Parser.consume(',');
//    float height = Parser.consumeFloatProperty("Height");
//    Parser.consume(',');
//    float xBuffer = Parser.consumeFloatProperty("xBuffer");
//    Parser.consume(',');
//    float yBuffer = Parser.consumeFloatProperty("yBuffer");
//    Parser.consume(',');
//    boolean isTrigger = Parser.consumeBooleanProperty("isTrigger");
//    Parser.consume(',');
//    boolean isStatic = Parser.consumeBooleanProperty("isStatic");
//    Parser.consumeEndObjectProperty();
//
//    BoxBounds bounds =  new BoxBounds(width, height, isStatic, isTrigger);
//    bounds.xBuffer = xBuffer;
//    bounds.yBuffer = yBuffer;
//    return bounds;
//  }

  @Override
  public float getWidth() {
    return this.width;
  }

  @Override
  public float getHeight() {
    return this.height;
  }

  @Override
  public boolean raycast(Vector2f position) {
    return false;
  }
}
