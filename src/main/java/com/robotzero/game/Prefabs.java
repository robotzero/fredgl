package com.robotzero.game;

import com.robotzero.dataStructure.AssetPool;
import com.robotzero.dataStructure.MapAsset;
import com.robotzero.dataStructure.Transform;
import com.robotzero.engine.Animation;
import com.robotzero.engine.AnimationMachine;
import com.robotzero.engine.BoxBounds;
import com.robotzero.engine.GameObject;
import com.robotzero.engine.Line;
import com.robotzero.engine.RigidBody;
import com.robotzero.engine.SpriteRenderer;
import com.robotzero.engine.Spritesheet;
import org.joml.Vector2f;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class Prefabs {
  public static GameObject FRED_PREFAB() {
    Spritesheet walk_spritesheet = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/fred_walking_sheet.png")).orElseThrow();
    Animation idle = new Animation("Idle", 0.1f, walk_spritesheet.sprites.subList(0, 1), false);
    Animation walk = new Animation("Walk", 0.1f, walk_spritesheet.sprites.subList(1, 11), true);
    Spritesheet jump_spritesheet = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/fred_jump_sheet.png")).orElseThrow();
    Animation jump = new Animation("Jump", 1f, jump_spritesheet.sprites.subList(0, 1), false);
    Spritesheet climb_spritesheet = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/fred_climb.png")).orElseThrow();
    Animation climb = new Animation("Climb", 0.6f, List.of(jump_spritesheet.sprites.subList(0, 1).get(0), climb_spritesheet.sprites.get(0)), false);

    Animation jumpOffTheLine = new Animation("JumpOff", 2f, List.of(jump_spritesheet.sprites.subList(0, 1).get(0), walk_spritesheet.sprites.subList(0, 1).get(0)), true);

    AnimationMachine fredAnimation = new AnimationMachine();
    fredAnimation.setStartAnimation("Idle");
    idle.addStateTransfer("StartWalking", "Walk");
    idle.addStateTransfer("StartJumping", "Jump");
    idle.addStateTransfer("StartClimbing", "Climb");

    walk.addStateTransfer("StartIdling", "Idle");
    jump.addStateTransfer("StartIdling", "Idle");

    climb.addStateTransfer("StartJumpOff", "JumpOff");

//    jumpOffTheLine.addStateTransfer("StartWalking", "Walk");
    jumpOffTheLine.addStateTransfer("StartIdling", "Idle");

    fredAnimation.addAnimation(idle);
    fredAnimation.addAnimation(walk);
    fredAnimation.addAnimation(jump);
    fredAnimation.addAnimation(climb);
    fredAnimation.addAnimation(jumpOffTheLine);

    RigidBody rigidBody = new RigidBody();
    BoxBounds boxBounds = new BoxBounds(28, 28, false, false);
    boxBounds.setXBuffer(1);
    FredController fredController = new FredController();

    Transform transform = new Transform(new Vector2f(400, 32.0f));
    transform.scale = new Vector2f(30f, 30f);
    GameObject gameObject = new GameObject("Fred", transform, 0);
    idle.setGameObject(gameObject);
    walk.setGameObject(gameObject);
    climb.setGameObject(gameObject);
    jump.setGameObject(gameObject);
    jumpOffTheLine.setGameObject(gameObject);
    fredAnimation.setGameObject(gameObject);
    SpriteRenderer spriteRenderer = new SpriteRenderer(fredAnimation.getPreviewSprite(), gameObject);
    spriteRenderer.setGameObject(gameObject);
    rigidBody.setGameObject(gameObject);
    boxBounds.setGameObject(gameObject);
    fredController.setGameObject(gameObject);
    fredAnimation.setGameObject(gameObject);
    gameObject.addComponent(spriteRenderer);
    gameObject.addComponent(fredAnimation);
    gameObject.addComponent(rigidBody);
    gameObject.addComponent(boxBounds);
    gameObject.addComponent(fredController);

    return gameObject;
  }

  public static GameObject BRICK_BLOCK() {
    Spritesheet items = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/items.png")).orElseThrow();

    GameObject brickBlock = new GameObject("Brick_Block_Prefab", new Transform(new Vector2f()), 0);
    brickBlock.addComponent(new SpriteRenderer(items.sprites.get(5), brickBlock));

    brickBlock.getTransform().scale.x = 32;
    brickBlock.getTransform().scale.y = 32;

    return brickBlock;
  }

  public static List<GameObject> STONES(MapAsset map) {
    final Random randomGen = new Random();
    Spritesheet items = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/stone_sheet.png")).orElseThrow();
    return map.getStoneTransforms().stream().map(transform -> {
      GameObject stone = new GameObject(String.format("Stone_Block_Prefab_%s", transform.toString()), transform, 0);
      SpriteRenderer spriteRenderer = new SpriteRenderer(items.sprites.get(randomGen.nextInt(3)), stone);
      BoxBounds boxBounds = new BoxBounds(32, 40, true, false);
      spriteRenderer.setGameObject(stone);
      boxBounds.setGameObject(stone);
      stone.addComponent(spriteRenderer);
      stone.addComponent(boxBounds);

      stone.getTransform().scale.x = 32;
      stone.getTransform().scale.y = 40;

      return stone;
    }).collect(Collectors.toList());
  }

  public static List<GameObject> LINES(MapAsset map) {
    Spritesheet items = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/stone_sheet.png")).orElseThrow();
    return map.getLineTransforms().stream().map(transform -> {
      GameObject line = new GameObject(String.format("Line_Block_Prefab_%s", transform.toString()), transform, 0);
      SpriteRenderer spriteRenderer = new SpriteRenderer(items.sprites.get(0), line);
      spriteRenderer.color.x = 1f;
      spriteRenderer.color.y = 0.4f;
      spriteRenderer.color.z = 0.8f;
      BoxBounds boxBounds = new BoxBounds(10, 40, false, true);
      Line lineComponent = new Line();
      lineComponent.setGameObject(line);
      spriteRenderer.setGameObject(line);
      boxBounds.setGameObject(line);
      line.addComponent(spriteRenderer);
      line.addComponent(boxBounds);
      line.addComponent(lineComponent);

      line.getTransform().scale.x = 32;
      line.getTransform().scale.y = 40;

      return line;
    }).collect(Collectors.toList());
  }
}
