package com.robotzero.game;

import com.robotzero.dataStructure.AssetPool;
import com.robotzero.dataStructure.Tuple;
import com.robotzero.dataStructure.maze.Cells;
import com.robotzero.dataStructure.maze.Constants;
import com.robotzero.dataStructure.maze.MapDrawer;
import com.robotzero.dataStructure.maze.MazeController;
import com.robotzero.dataStructure.maze.Order;
import com.robotzero.engine.BoxBounds;
import com.robotzero.engine.DebugDraw;
import com.robotzero.engine.GameObject;
import com.robotzero.infrastructure.KeyListener;
import com.robotzero.infrastructure.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class LevelScene extends Scene {
  private boolean debug = false;
  public LevelScene(String name) {
    super.Scene(name);
  }

  @Override
  public void init() {
    initAssetPool();
    MazeController mazeController = new MazeController(Order.Builder.Kruskal);
    mazeController.init();
    final var conf = mazeController.getMazeConfiguration();
    final Cells seencells = new Cells(conf.getWidth() + 1,conf.getHeight() + 1) ;
    MapDrawer mapDrawer = new MapDrawer(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, Constants.MAP_UNIT, Constants.STEP_SIZE, seencells, Prefabs.STONEWIDTH, mazeController);
    mapDrawer.draw_map(null, 0, 0, 0, 65536, 0, true, true);

    final var startingPositionX = mazeController.getMazeConfiguration().getStartingPosition()[0];
    final var startingPositionY = mazeController.getMazeConfiguration().getStartingPosition()[1];
    final var mazeHeight = mazeController.getMazeConfiguration().getHeight();
    final var map_scale_width = Prefabs.STONEWIDTH * 2;
    final var map_scale_height = Prefabs.STONEHEIGHT * 2;
    final var offx = Prefabs.STONEWIDTH;
    final var offy = (Constants.VIEW_HEIGHT - (mazeHeight * map_scale_height)) - Prefabs.STONEHEIGHT;
    final var nx1 = startingPositionX * map_scale_width + offx;
    final var ny1 = Constants.VIEW_HEIGHT - 0 - (startingPositionY * map_scale_height + offy);
    GameObject fredGameObject = Prefabs.FRED_PREFAB();
    fredGameObject.getTransform().position.x = nx1;
    fredGameObject.getTransform().position.y = ny1;

    GameObject ghost = Prefabs.GHOST_PREFAB();
    ghost.getTransform().position.x = nx1;
    ghost.getTransform().position.y = ny1;

    List<GameObject> stoneBlocks = Prefabs.STONES_MAP_DRAWER();
//    List<GameObject> stoneBlocks = Prefabs.STONES(Optional.ofNullable(AssetPool.getMap("assets/maps/map.txt")).orElseThrow());
    stoneBlocks.forEach(stoneBlock -> {
      BoxBounds boxBounds = stoneBlock.getComponent(BoxBounds.class);
//      DebugDraw.addBox2D(
//          new Vector2f(
//              stoneBlock.getTransform().position.x + (boxBounds.getWidth() * 0.5f),
//              stoneBlock.getTransform().position.y + (boxBounds.getHeight() * 0.5f)
//          ),
//          new Vector2f(boxBounds.getWidth(), boxBounds.getHeight()),
//          0,
//          new Vector3f(1f, 0f, 0f),
//          0
//      );
      gameObjects.add(stoneBlock);
      renderer.add(stoneBlock);
      physics.addGameObject(stoneBlock);
      worldPartition.put(stoneBlock.getGridCoords(), stoneBlock);
      stoneBlock.start();
    });

//    List<GameObject> lineBlocks = Prefabs.LINES(Optional.ofNullable(AssetPool.getMap("assets/maps/map.txt")).orElseThrow());
    List<GameObject> lineBlocks = Prefabs.LINES_MAP_DRAWER();
    lineBlocks.forEach(lineBlock -> {
      BoxBounds boxBounds = lineBlock.getComponent(BoxBounds.class);
//      DebugDraw.addBox2D(
//          new Vector2f(
//              lineBlock.getTransform().position.x + (boxBounds.getWidth() * 0.5f),
//              lineBlock.getTransform().position.y + (boxBounds.getHeight() * 0.5f)
//          ),
//          new Vector2f(boxBounds.getWidth(), boxBounds.getHeight()),
//          0,
//          new Vector3f(1f, 0f, 0f),
//          0
//      );

      gameObjects.add(lineBlock);
      if (!lineBlock.getName().contains("Jump")) {
        renderer.add(lineBlock);
      }
      physics.addGameObject(lineBlock);
      worldPartition.put(lineBlock.getGridCoords(), lineBlock);
      lineBlock.start();
    });

//    List<GameObject> jumpboards = Prefabs.JUMPBOARDS_MAP_DRAWER();
////    List<GameObject> jumpboards = List.of();
//    jumpboards.forEach(jumpBoard -> {
//      BoxBounds boxBounds = jumpBoard.getComponent(BoxBounds.class);

//      DebugDraw.addBox2D(
//          new Vector2f(
//              jumpBoard.getTransform().position.x + (boxBounds.getWidth() * 0.5f),
//              jumpBoard.getTransform().position.y + (boxBounds.getHeight() * 0.5f)!
//          ),
//          new Vector2f(boxBounds.getWidth(), boxBounds.getHeight()),
//          0,
//          new Vector3f(1f, 0f, 0f),
//          0
//      );

//      gameObjects.add(jumpBoard);
//      renderer.add(jumpBoard);
//      physics.addGameObject(jumpBoard);
//      worldPartition.put(jumpBoard.getGridCoords(), jumpBoard);
//      jumpBoard.start();
//    });

    gameObjects.add(fredGameObject);
    renderer.add(fredGameObject);
    physics.addGameObject(fredGameObject);
    worldPartition.put(fredGameObject.getGridCoords(), fredGameObject);
    fredGameObject.start();

    gameObjects.add(ghost);
    renderer.add(ghost);
    physics.addGameObject(ghost);
    worldPartition.put(ghost.getGridCoords(), ghost);
    ghost.start();
    Window.getWindow().setColor(com.robotzero.infrastructure.constants.Window.COLOR_BLACK);
    //AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").play();
  }


  public void initAssetPool() {
    // Game Assets
    AssetPool.addSpritesheet("assets/spritesheets/decorationsAndBlocks.png", 16, 16, 0, 7, 81);
    AssetPool.addSpritesheet("assets/spritesheets/items.png", 16, 16, 0, 7, 34);
    AssetPool.addSpritesheet("assets/spritesheets/character_and_enemies_32.png", 16, 16, 0, 14, 26);
    AssetPool.addSpritesheet("assets/spritesheets/character_and_enemies_64.png", 16, 32, 0, 21, 21 * 2);
    AssetPool.addSpritesheet("assets/spritesheets/icons.png", 32, 32, 0, 7, 15);
    AssetPool.addSpritesheet("assets/spritesheets/turtle.png", 16, 24, 0, 4, 4);
    AssetPool.addSpritesheet("assets/spritesheets/fred_walking_sheet.png", 32, 32, 0, 4, 4);
    AssetPool.addSpritesheet("assets/spritesheets/stones_sprites_better.png", 32, 40, 0, 8, 8);
    AssetPool.addSpritesheet("assets/spritesheets/fred_jump_sheet.png", 32, 32, 0, 2, 2);
    AssetPool.addSpritesheet("assets/spritesheets/fred_climb.png", 32, 32, 0, 1, 1);
    AssetPool.addSpritesheet("assets/spritesheets/fred_idle.png", 32, 32, 0, 1, 1);
    AssetPool.addSpritesheet("assets/spritesheets/ghost_sheet.png", 23, 29, 0, 2, 2);
    AssetPool.addMap("assets/maps/map.txt");
    // Engine Assets
    AssetPool.addSpritesheet("assets/spritesheets/defaultAssets.png", 24, 21, 0, 2, 2);
    AssetPool.addSpritesheet("assets/spritesheets/line.png", 7, 40, 0, 3, 3);

    // Sounds
//    AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", true);
//    AssetPool.addSound("assets/sounds/flagpole.ogg", false);
//    AssetPool.addSound("assets/sounds/break_block.ogg", false);
//    AssetPool.addSound("assets/sounds/bump.ogg", false);
//    AssetPool.addSound("assets/sounds/coin.ogg", false);
//    AssetPool.addSound("assets/sounds/gameover.ogg", false);
//    AssetPool.addSound("assets/sounds/jump-small.ogg", false);
//    AssetPool.addSound("assets/sounds/mario_die.ogg", false);
//    AssetPool.addSound("assets/sounds/pipe.ogg", false);
//    AssetPool.addSound("assets/sounds/powerup.ogg", false);
//    AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
//    AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
//    AssetPool.addSound("assets/sounds/stomp.ogg", false);
//    AssetPool.addSound("assets/sounds/kick.ogg", false);
//    AssetPool.addSound("assets/sounds/invincible.ogg", false);
  }

  @Override
  public void update(double dt) {
    for (GameObject go : gameObjects) {
      if (go.getComponent(FredController.class) != null || go.getTransform().position.x > this.camera.position().x && go.getTransform().position.x + go.getTransform().scale.x < this.camera.position().x + com.robotzero.infrastructure.constants.Window.SCREEN_WIDTH + 128) {
        go.update(dt);
//        DebugDraw.addBox2DDynamic(
//            new Vector2f(
//                camera.position().x + (com.robotzero.infrastructure.constants.Window.SCREEN_WIDTH / 2f),
//                camera.position().y + (com.robotzero.infrastructure.constants.Window.SCREEN_HEIGHT / 2f)
//            ),
//            new Vector2f(com.robotzero.infrastructure.constants.Window.SCREEN_WIDTH - 10, com.robotzero.infrastructure.constants.Window.SCREEN_HEIGHT - 10),
//            1,
//            new Vector3f(1f, 0f, 0f)
//        );

//        DebugDraw.addBox2DDynamic(
//            new Vector2f(
//                camera.position().x + com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_X1 + 1,
//                camera.position().y + ((com.robotzero.infrastructure.constants.Window.SCREEN_HEIGHT) / 2f)
//            ),
//            new Vector2f(2, com.robotzero.infrastructure.constants.Window.SCREEN_HEIGHT),
//            1,
//            new Vector3f(1f, 0f, 0f)
//        );
//
//        DebugDraw.addBox2DDynamic(
//            new Vector2f(
//                camera.position().x + com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_X2 + 1,
//                camera.position().y + ((com.robotzero.infrastructure.constants.Window.SCREEN_HEIGHT) / 2f)
//            ),
//            new Vector2f(2, com.robotzero.infrastructure.constants.Window.SCREEN_HEIGHT),
//            1,
//            new Vector3f(1f, 0f, 0f)
//        );
//
//
//        DebugDraw.addBox2DDynamic(
//            new Vector2f(
//                (com.robotzero.infrastructure.constants.Window.SCREEN_WIDTH / 2f) + 1 + this.camera.position().x,
//                com.robotzero.infrastructure.constants.Window.SCREEN_HEIGHT / 2f
//            ),
//            new Vector2f(2, com.robotzero.infrastructure.constants.Window.SCREEN_HEIGHT),
//            1,
//            new Vector3f(0.5f, 1f, 1f)
//        );
//
//        DebugDraw.addBox2DDynamic(
//            new Vector2f(
//                (com.robotzero.infrastructure.constants.Window.SCREEN_WIDTH / 2f) + this.camera.position().x,
//                (com.robotzero.infrastructure.constants.Window.SCREEN_HEIGHT / 2f) + 1 + this.camera.position().y
//            ),
//            new Vector2f(com.robotzero.infrastructure.constants.Window.SCREEN_WIDTH, 2),
//            1,
//            new Vector3f(0.5f, 1f, 1f)
//        );
//
//        DebugDraw.addBox2DDynamic(
//            new Vector2f(
//                (com.robotzero.infrastructure.constants.Window.SCREEN_WIDTH / 2f) + this.camera.position().x,
//                com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y1 + this.camera.position().y
//            ),
//            new Vector2f(com.robotzero.infrastructure.constants.Window.SCREEN_WIDTH, 2),
//            1,
//            new Vector3f(1f, 1f, 1f)
//        );
//
//        DebugDraw.addBox2DDynamic(
//            new Vector2f(
//                (com.robotzero.infrastructure.constants.Window.SCREEN_WIDTH / 2f) + this.camera.position().x,
//                com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y2 + this.camera.position().y
//            ),
//            new Vector2f(com.robotzero.infrastructure.constants.Window.SCREEN_WIDTH, 2),
//            1,
//            new Vector3f(1f, 1f, 1f)
//        );


        Optional.ofNullable(go.getComponent(FredController.class)).ifPresent(_notUsed -> {
          BoxBounds fredBoxBounds = go.getComponent(BoxBounds.class);
//          DebugDraw.addBox2DDynamic(
//              new Vector2f(
//                  fredBoxBounds.getCenterX(),
//                  fredBoxBounds.getCenterY()
//              ),
//              new Vector2f(fredBoxBounds.getWidth(), fredBoxBounds.getHeight()),
//              1,
//              new Vector3f(1f, 0f, 0f)
//          );
        });

      } else if (go.getTransform().position.x + go.getTransform().scale.x < this.camera.position().x || go.getTransform().position.y + go.getTransform().scale.y < com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_3) {
        //deleteGameObject(go);
      }
    }

    if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
      Window.getWindow().changeScene(0);
    }

    if (KeyListener.isKeyPressed(GLFW_KEY_0)) {
      this.debug = !this.debug;

    }
    if (objsToDelete.size() > 0) {
      for (GameObject obj : objsToDelete) {
        Tuple<Integer> gridCoords = obj.getGridCoords();
        worldPartition.remove(gridCoords);
        this.gameObjects.remove(obj);
        this.renderer.deleteGameObject(obj);
        this.physics.deleteGameObject(obj);
      }
      objsToDelete.clear();
    }

    if (objsToAdd.size() > 0) {
      for (GameObject g : objsToAdd) {
        gameObjects.add(g);
        renderer.add(g);
        physics.addGameObject(g);

        if (g.getComponent(BoxBounds.class) != null && g.getComponent(BoxBounds.class).isStatic) {
          Tuple<Integer> gridPos = g.getGridCoords();
          worldPartition.put(gridPos, g);
        }
      }

      for (GameObject g : objsToAdd) {
        g.start();
      }
      objsToAdd.clear();
    }
  }
}
