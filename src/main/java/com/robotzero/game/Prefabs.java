package com.robotzero.game;

import com.robotzero.dataStructure.AssetPool;
import com.robotzero.dataStructure.MapAsset;
import com.robotzero.dataStructure.Transform;
import com.robotzero.dataStructure.maze.MapDrawer;
import com.robotzero.engine.Animation;
import com.robotzero.engine.AnimationMachine;
import com.robotzero.engine.BoxBounds;
import com.robotzero.engine.GameObject;
import com.robotzero.engine.Ghost;
import com.robotzero.engine.Line;
import com.robotzero.engine.RigidBody;
import com.robotzero.engine.SpriteRenderer;
import com.robotzero.engine.Spritesheet;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class Prefabs {
  public static final int LINEWIDTH = 7;
  public static final int STONEWIDTH = 32;
  public static final int STONEHEIGHT = 40;
  public static final int FREDWIDTH = 32;
  public static final int FREDHEIGHT = 32;
  public static final int GHOSTWIDTH = 23;
  public static final int GHOSTHEIGHT = 29;

  public static GameObject FRED_PREFAB() {
    Spritesheet idle_spritesheet = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/fred_idle.png")).orElseThrow();
    Animation idle = new Animation("Idle", 0.1f, idle_spritesheet.sprites.subList(0, 1), false);
    Spritesheet walk_spritesheet = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/fred_walking_sheet.png")).orElseThrow();
    Animation walk = new Animation("Walk", 0.264f, walk_spritesheet.sprites, true);
    Spritesheet jump_spritesheet = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/fred_jump_sheet.png")).orElseThrow();
    Animation jump = new Animation("Jump", 0.6f, jump_spritesheet.sprites.subList(0, 1), false);
    Spritesheet climb_spritesheet = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/fred_climb.png")).orElseThrow();
    Animation climb = new Animation("Climb", 0.6f, List.of(jump_spritesheet.sprites.subList(0, 1).get(0), climb_spritesheet.sprites.get(0)), false);

    Animation jumpOnTheLine = new Animation("JumpOn", 0.6f, List.of(jump_spritesheet.sprites.subList(0, 1).get(0), climb_spritesheet.sprites.get(0)), false);
    Animation jumpOffTheLine = new Animation("JumpOff", 0.6f, List.of(jump_spritesheet.sprites.subList(0, 1).get(0), walk_spritesheet.sprites.subList(0, 1).get(0)), false);

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
    jumpOffTheLine.addStateTransfer("StartWalking", "Walk");
    jumpOnTheLine.addStateTransfer("StartJumpOff", "JumpOff");

    fredAnimation.addAnimation(idle);
    fredAnimation.addAnimation(walk);
    fredAnimation.addAnimation(jump);
    fredAnimation.addAnimation(climb);
    fredAnimation.addAnimation(jumpOffTheLine);
    fredAnimation.addAnimation(jumpOnTheLine);

    RigidBody rigidBody = new RigidBody();
    BoxBounds boxBounds = new BoxBounds(Prefabs.FREDWIDTH, Prefabs.FREDHEIGHT, false, true);
    FredController fredController = new FredController();

    //Transform transform = new Transform(new Vector2f(650, 300.0f));
    Transform transform = new Transform(new Vector2f(258f, Prefabs.STONEHEIGHT));
    transform.scale = new Vector2f(FREDWIDTH, FREDHEIGHT);
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

  public static GameObject GHOST_PREFAB() {
    Spritesheet walk_spritesheet = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/ghost_sheet.png")).orElseThrow();
    Animation walk = new Animation("Walk", 0.264f, walk_spritesheet.sprites, true);

    AnimationMachine ghostAnimation = new AnimationMachine();
    ghostAnimation.setStartAnimation("Walk");
    ghostAnimation.addAnimation(walk);

    RigidBody rigidBody = new RigidBody();
    BoxBounds boxBounds = new BoxBounds(Prefabs.GHOSTWIDTH, Prefabs.GHOSTHEIGHT, false, true);
    Ghost ghost = new Ghost();

    Transform transform = new Transform(new Vector2f(258f, Prefabs.STONEHEIGHT));
    transform.scale = new Vector2f(GHOSTWIDTH, GHOSTHEIGHT);
    GameObject gameObject = new GameObject("Ghost", transform, 0);
    walk.setGameObject(gameObject);
    SpriteRenderer spriteRenderer = new SpriteRenderer(ghostAnimation.getPreviewSprite(), gameObject);
    spriteRenderer.setGameObject(gameObject);
    rigidBody.setGameObject(gameObject);
    boxBounds.setGameObject(gameObject);
    ghost.setGameObject(gameObject);
    ghostAnimation.setGameObject(gameObject);
    gameObject.addComponent(ghost);
    gameObject.addComponent(spriteRenderer);
    gameObject.addComponent(ghostAnimation);
    gameObject.addComponent(rigidBody);
    gameObject.addComponent(boxBounds);

    return gameObject;
  }


  public static List<GameObject> STONES(MapAsset map) {
    final Random randomGen = new Random();
    Spritesheet items = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/stones_sprites_better.png")).orElseThrow();
    return map.getStoneTransforms().stream().map(transform -> {
      GameObject stone = new GameObject(String.format("Stone_Block_Prefab_%s", transform.toString()), transform, 0);
      SpriteRenderer spriteRenderer = new SpriteRenderer(items.sprites.get(randomGen.nextInt(8)), stone);
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

  public static List<GameObject> STONES_MAP_DRAWER() {
    final Random randomGen = new Random();
    Spritesheet items = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/stones_sprites_better.png")).orElseThrow();
    return MapDrawer.stoneTransforms.stream().map(transform -> {
      GameObject stone = new GameObject(String.format("Stone_Block_Prefab_%s", transform.toString()), transform, 0);
      SpriteRenderer spriteRenderer = new SpriteRenderer(items.sprites.get(randomGen.nextInt(8)), stone);
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
    Spritesheet items = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/line.png")).orElseThrow();
    return map.getLineTransforms().entrySet().stream().flatMap(entrySet -> {
      int mappedAsciCode = entrySet.getKey();
      List<Transform> transforms = entrySet.getValue();
      return transforms.stream().map(transform -> {
        GameObject line = new GameObject(String.format("Line_Block_Prefab_%s", transform.toString()), transform, 0);
        SpriteRenderer spriteRenderer = new SpriteRenderer(items.sprites.get(mappedAsciCode), line);
        Line lineComponent = new Line();
        if (mappedAsciCode == 2 || mappedAsciCode == 1) {
          BoxBounds boxBounds = new BoxBounds(6, STONEHEIGHT, false, true);
          boxBounds.setXBuffer(8);
          boxBounds.setGameObject(line);
          line.addComponent(boxBounds);
        }

        if (mappedAsciCode == 0) {
          BoxBounds boxBounds = new BoxBounds(6, STONEHEIGHT, false, true);
          boxBounds.setGameObject(line);
          line.addComponent(boxBounds);
        }

        lineComponent.setGameObject(line);
        spriteRenderer.setGameObject(line);
        line.addComponent(spriteRenderer);
        line.addComponent(lineComponent);

        line.getTransform().scale.x = LINEWIDTH;
        line.getTransform().scale.y = STONEHEIGHT;

        return line;
      });
    }).collect(Collectors.toList());
  }

  public static List<GameObject> LINES_MAP_DRAWER() {
    Spritesheet items = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/line.png")).orElseThrow();
    List<GameObject> jumpBoards = new ArrayList<>();
    List<GameObject> lines = MapDrawer.lineTransforms.entrySet().stream().flatMap(entrySet -> {
      int mappedAsciCode = entrySet.getKey();
      List<Transform> transforms = entrySet.getValue();
      return transforms.stream().map(transform -> {
        GameObject line = new GameObject(String.format("Line_Block_Prefab_%s", transform.toString()), transform, 0);
        SpriteRenderer spriteRenderer = new SpriteRenderer(items.sprites.get(mappedAsciCode), line);
        Line lineComponent = new Line();
        lineComponent.setGameObject(line);
        lineComponent.setType(mappedAsciCode);
        spriteRenderer.setGameObject(line);

        if (mappedAsciCode == 2 || mappedAsciCode == 1) {
          Vector2f jumbBoardPosition = new Vector2f(transform.position.x() - 9, transform.position.y());
          Transform transform1 = new Transform(jumbBoardPosition);
          GameObject jumpBoard = new GameObject(String.format("JumpBoard_Block_Prefab_%s", transform1), transform1, 0);
          BoxBounds jumbBoardBoxBounds = new BoxBounds(STONEWIDTH - 8, STONEHEIGHT, false, true);
          jumbBoardBoxBounds.setGameObject(jumpBoard);
          jumpBoard.addComponent(jumbBoardBoxBounds);
          jumpBoard.getTransform().scale.x = STONEWIDTH;
          jumpBoard.getTransform().scale.y = STONEHEIGHT;
          jumpBoards.add(jumpBoard);
        }

        BoxBounds boxBounds = new BoxBounds(6, STONEHEIGHT, false, true);
        boxBounds.setGameObject(line);
        line.addComponent(boxBounds);
        boxBounds.setGameObject(line);
        line.addComponent(boxBounds);
        line.addComponent(spriteRenderer);
        line.addComponent(lineComponent);

        line.getTransform().scale.x = LINEWIDTH;
        line.getTransform().scale.y = STONEHEIGHT;

        return line;
      });
    }).collect(Collectors.toList());

    lines.addAll(jumpBoards);
    return lines;
  }

  public static List<GameObject> JUMPBOARDS(MapAsset map) {
    Spritesheet items = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/stones_sprites_better.png")).orElseThrow();
    return map.getJumpBoardTransforms().stream().map(transform -> {
      GameObject jumpBoard = new GameObject(String.format("JumpBoard_Block_Prefab_%s", transform.toString()), transform, 0);
      SpriteRenderer spriteRenderer = new SpriteRenderer(items.sprites.get(0), jumpBoard);
      spriteRenderer.color.x = 1f;
      spriteRenderer.color.y = 0.6f;
      spriteRenderer.color.z = 1.0f;
      BoxBounds boxBounds = new BoxBounds(STONEWIDTH, STONEHEIGHT, false, true);
      spriteRenderer.setGameObject(jumpBoard);
      boxBounds.setGameObject(jumpBoard);
//      jumpBoard.addComponent(spriteRenderer);
      jumpBoard.addComponent(boxBounds);

      jumpBoard.getTransform().scale.x = STONEWIDTH;
      jumpBoard.getTransform().scale.y = STONEHEIGHT;

      return jumpBoard;
    }).collect(Collectors.toList());
  }

  public static List<GameObject> JUMPBOARDS_MAP_DRAWER() {
    Spritesheet items = Optional.ofNullable(AssetPool.getSpritesheet("assets/spritesheets/stones_sprites_better.png")).orElseThrow();
    return MapDrawer.jumpBoards.stream().map(transform -> {
      GameObject jumpBoard = new GameObject(String.format("JumpBoard_Block_Prefab_%s", transform.toString()), transform, 0);
      SpriteRenderer spriteRenderer = new SpriteRenderer(items.sprites.get(0), jumpBoard);
      spriteRenderer.color.x = 1f;
      spriteRenderer.color.y = 0.6f;
      spriteRenderer.color.z = 1.0f;
      BoxBounds boxBounds = new BoxBounds(STONEWIDTH - 8, STONEHEIGHT, false, true);
      spriteRenderer.setGameObject(jumpBoard);
      boxBounds.setGameObject(jumpBoard);
//      jumpBoard.addComponent(spriteRenderer);
      jumpBoard.addComponent(boxBounds);

      jumpBoard.getTransform().scale.x = STONEWIDTH;
      jumpBoard.getTransform().scale.y = STONEHEIGHT;

      return jumpBoard;
    }).collect(Collectors.toList());
  }
}
