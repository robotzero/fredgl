package com.robotzero.game;

import com.robotzero.dataStructure.AssetPool;
import com.robotzero.dataStructure.Transform;
import com.robotzero.engine.Animation;
import com.robotzero.engine.AnimationMachine;
import com.robotzero.engine.GameObject;
import com.robotzero.engine.RigidBody;
import com.robotzero.engine.Sprite;
import com.robotzero.engine.SpriteRenderer;
import com.robotzero.engine.Spritesheet;
import org.joml.Vector2f;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Prefabs {
  public static GameObject FRED_PREFAB() {
    Spritesheet spritesheet = AssetPool.getSpritesheet("assets/spritesheets/fred_walking_sheet.png");
    Animation idle = new Animation("Idle", 0.1f, spritesheet.sprites.subList(0, 1), false);
    Animation walk = new Animation("Walk", 0.1f, spritesheet.sprites.subList(1, 11), true);
    AnimationMachine fredAnimation = new AnimationMachine();
    fredAnimation.setStartAnimation("Idle");
    idle.addStateTransfer("StartWalking", "Walk");
    walk.addStateTransfer("StartIdling", "Idle");
    fredAnimation.addAnimation(idle);
    fredAnimation.addAnimation(walk);

    RigidBody rigidBody = new RigidBody();
    FredController fredController = new FredController();

    Transform transform = new Transform(new Vector2f(64.0f, 64.0f));
    transform.scale = new Vector2f(32f, 32f);
    GameObject gameObject = new GameObject("Fred", transform, 1);
    idle.setGameObject(gameObject);
    walk.setGameObject(gameObject);
    fredAnimation.setGameObject(gameObject);
    Sprite sprite = spritesheet.sprites.get(0);
    SpriteRenderer spriteRenderer = new SpriteRenderer(fredAnimation.getPreviewSprite(), gameObject);
    spriteRenderer.setGameObject(gameObject);
    sprite.setGameObject(gameObject);
    gameObject.addComponent(spriteRenderer);
    gameObject.addComponent(fredAnimation);
    gameObject.addComponent(rigidBody);
    gameObject.addComponent(fredController);

    return gameObject;
  }

  public static GameObject BRICK_BLOCK() {
    Spritesheet items = AssetPool.getSpritesheet("assets/spritesheets/items.png");

    GameObject brickBlock = new GameObject("Brick_Block_Prefab", new Transform(new Vector2f()), 0);
    brickBlock.addComponent(new SpriteRenderer(items.sprites.get(5), brickBlock));

    brickBlock.getTransform().scale.x = 32;
    brickBlock.getTransform().scale.y = 32;

    return brickBlock;
  }

  public static List<GameObject> STONES() {
    Spritesheet items = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/stone_sheet.png")).orElseThrow();
    return IntStream.rangeClosed(0, 40).mapToObj(index -> {
      GameObject stone = new GameObject(String.format("Stone_Block_Prefab%d", index), new Transform(new Vector2f(index * 31, 0)), 0);
      stone.addComponent(new SpriteRenderer(items.sprites.get(index % 3), stone));

      stone.getTransform().scale.x = 31;
      stone.getTransform().scale.y = 39;

      return stone;
    }).collect(Collectors.toList());
  }
}
