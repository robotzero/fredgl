package com.robotzero.game;

import com.robotzero.dataStructure.AssetPool;
import com.robotzero.dataStructure.Tuple;
import com.robotzero.engine.BoxBounds;
import com.robotzero.engine.DebugDraw;
import com.robotzero.engine.GameObject;
import com.robotzero.infrastructure.KeyListener;
import com.robotzero.infrastructure.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class LevelScene extends Scene {

  public LevelScene(String name) {
    super.Scene(name);
  }

  @Override
  public void init() {
    initAssetPool();
    GameObject fredGameObject = Prefabs.FRED_PREFAB();
    List<GameObject> stoneBlocks = Prefabs.STONES(Optional.ofNullable(AssetPool.getMap("assets/maps/map2.txt")).orElseThrow());
    stoneBlocks.forEach(stoneBlock -> {
      gameObjects.add(stoneBlock);
      renderer.add(stoneBlock);
      physics.addGameObject(stoneBlock);
      worldPartition.put(stoneBlock.getGridCoords(), stoneBlock);
      stoneBlock.start();
    });

    List<GameObject> lineBlocks = Prefabs.LINES(Optional.ofNullable(AssetPool.getMap("assets/maps/map2.txt")).orElseThrow());
    lineBlocks.forEach(lineBlock -> {
      gameObjects.add(lineBlock);
      renderer.add(lineBlock);
      physics.addGameObject(lineBlock);
      worldPartition.put(lineBlock.getGridCoords(), lineBlock);
      lineBlock.start();
    });

    List<GameObject> jumpBoards = Prefabs.JUMPBOARD(Optional.ofNullable(AssetPool.getMap("assets/maps/map2.txt")).orElseThrow());
    jumpBoards.forEach(jumpBoard -> {
      gameObjects.add(jumpBoard);
      renderer.add(jumpBoard);
      physics.addGameObject(jumpBoard);
      worldPartition.put(jumpBoard.getGridCoords(), jumpBoard);
      jumpBoard.start();
    });

    gameObjects.add(fredGameObject);
    renderer.add(fredGameObject);
    physics.addGameObject(fredGameObject);
    worldPartition.put(fredGameObject.getGridCoords(), fredGameObject);
    fredGameObject.start();
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
    AssetPool.addSpritesheet("assets/spritesheets/fred_walking_sheet.png", 32, 32, 0, 12, 12);
    AssetPool.addSpritesheet("assets/spritesheets/stone_sheet.png", 31, 39, 0, 3, 3);
    AssetPool.addSpritesheet("assets/spritesheets/fred_jump_sheet.png", 32, 32, 0, 2, 2);
    AssetPool.addSpritesheet("assets/spritesheets/fred_climb.png", 32, 32, 0, 1, 1);
    AssetPool.addMap("assets/maps/map.txt");
    AssetPool.addMap("assets/maps/map2.txt");
    // Engine Assets
    AssetPool.addSpritesheet("assets/spritesheets/defaultAssets.png", 24, 21, 0, 2, 2);

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
    DebugDraw.addBox2D(new Vector2f(400, 200), new Vector2f(64, 32), 0, new Vector3f(0f, 1f, 0f));
    for (GameObject go : gameObjects) {
      if (go.getComponent(FredController.class) != null || go.getTransform().position.x > this.camera.position().x && go.getTransform().position.x + go.getTransform().scale.x < this.camera.position().x + 32.0f * 40f + 128) {
        go.update(dt);
      } else if (go.getTransform().position.x + go.getTransform().scale.x < this.camera.position().x || go.getTransform().position.y + go.getTransform().scale.y < com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_2) {
        //deleteGameObject(go);
      }
    }

    if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
      Window.getWindow().changeScene(0);
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
