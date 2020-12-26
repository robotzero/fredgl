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
  public static final int LINEWIDTH = 4;
  public static final int STONEWIDTH = 32;
  public static final int STONEHEIGHT = 40;

  public static GameObject FRED_PREFAB() {
    Spritesheet walk_spritesheet = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/fred_walking_sheet.png")).orElseThrow();
    Animation idle = new Animation("Idle", 0.1f, walk_spritesheet.sprites.subList(0, 1), false);
    Animation walk = new Animation("Walk", 0.1f, walk_spritesheet.sprites.subList(1, 11), true);
    Spritesheet jump_spritesheet = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/fred_jump_sheet.png")).orElseThrow();
    Animation jump = new Animation("Jump", 1f, jump_spritesheet.sprites.subList(0, 1), false);
    Spritesheet climb_spritesheet = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/fred_climb.png")).orElseThrow();
    Animation climb = new Animation("Climb", 1f, List.of(jump_spritesheet.sprites.subList(0, 1).get(0), climb_spritesheet.sprites.get(0)), false);

    Animation jumpOnTheLine = new Animation("JumpOn", 1f, List.of(jump_spritesheet.sprites.subList(0, 1).get(0), climb_spritesheet.sprites.get(0)), false);
    Animation jumpOffTheLine = new Animation("JumpOff", 1f, List.of(jump_spritesheet.sprites.subList(0, 1).get(0), walk_spritesheet.sprites.subList(0, 1).get(0)), false);

    AnimationMachine fredAnimation = new AnimationMachine();
    fredAnimation.setStartAnimation("Idle");
    idle.addStateTransfer("StartWalking", "Walk");
    idle.addStateTransfer("StartJumping", "Jump");
    idle.addStateTransfer("StartClimbing", "Climb");
    idle.addStateTransfer("StartJumpOn", "JumpOn");

    walk.addStateTransfer("StartIdling", "Idle");
    walk.addStateTransfer("StartJumpOn", "JumpOn");
    jump.addStateTransfer("StartIdling", "Idle");

    climb.addStateTransfer("StartJumpOff", "JumpOff");

    jumpOffTheLine.addStateTransfer("StartIdling", "Idle");
    jumpOnTheLine.addStateTransfer("StartJumpOff", "JumpOff");

    fredAnimation.addAnimation(idle);
    fredAnimation.addAnimation(walk);
    fredAnimation.addAnimation(jump);
    fredAnimation.addAnimation(climb);
    fredAnimation.addAnimation(jumpOffTheLine);
    fredAnimation.addAnimation(jumpOnTheLine);

    RigidBody rigidBody = new RigidBody();
    BoxBounds boxBounds = new BoxBounds(28, 28, false, true);
    //boxBounds.setXBuffer(1);
    FredController fredController = new FredController();

    Transform transform = new Transform(new Vector2f(650, 300.0f));
    //Transform transform = new Transform(new Vector2f(130f, 32f));
    transform.scale = new Vector2f(28f, 28f);
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
      BoxBounds boxBounds = new BoxBounds(STONEWIDTH, STONEHEIGHT, true, false);
      spriteRenderer.setGameObject(stone);
      boxBounds.setGameObject(stone);
      stone.addComponent(spriteRenderer);
      stone.addComponent(boxBounds);

      stone.getTransform().scale.x = STONEWIDTH;
      stone.getTransform().scale.y = STONEHEIGHT;

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
      BoxBounds boxBounds = new BoxBounds(LINEWIDTH, 40, false, true);
      Line lineComponent = new Line();
      lineComponent.setGameObject(line);
      spriteRenderer.setGameObject(line);
      boxBounds.setGameObject(line);
      line.addComponent(spriteRenderer);
      line.addComponent(boxBounds);
      line.addComponent(lineComponent);

      line.getTransform().scale.x = LINEWIDTH;
      line.getTransform().scale.y = STONEHEIGHT;

      return line;
    }).collect(Collectors.toList());
  }

  public static List<GameObject> JUMPBOARDS(MapAsset map) {
    Spritesheet items = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/stone_sheet.png")).orElseThrow();
    return map.getJumpBoardTransforms().stream().map(transform -> {
      GameObject jumpBoard = new GameObject(String.format("JumpBoard_Block_Prefab_%s", transform.toString()), transform, 0);
      SpriteRenderer spriteRenderer = new SpriteRenderer(items.sprites.get(0), jumpBoard);
      spriteRenderer.color.x = 1f;
      spriteRenderer.color.y = 0.6f;
      spriteRenderer.color.z = 1.0f;
      BoxBounds boxBounds = new BoxBounds(STONEWIDTH, STONEHEIGHT, false, true);
      boxBounds.setXBuffer(5);
      spriteRenderer.setGameObject(jumpBoard);
      boxBounds.setGameObject(jumpBoard);
      jumpBoard.addComponent(spriteRenderer);
      jumpBoard.addComponent(boxBounds);

      jumpBoard.getTransform().scale.x = STONEWIDTH;
      jumpBoard.getTransform().scale.y = STONEHEIGHT;

      return jumpBoard;
    }).collect(Collectors.toList());
  }
}
