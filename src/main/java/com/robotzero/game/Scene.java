package com.robotzero.game;

import com.robotzero.dataStructure.Tuple;
import com.robotzero.engine.Component;
import com.robotzero.engine.DebugDraw;
import com.robotzero.engine.GameObject;
import com.robotzero.engine.Physics;
import com.robotzero.infrastructure.constants.Window;
import com.robotzero.render.Camera;
import com.robotzero.render.Renderer;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Scene {
  String name;
  public Camera camera;
  public Physics physics;
  List<GameObject> gameObjects;
  Map<Tuple<Integer>, GameObject> worldPartition;
  List<GameObject> objsToDelete;
  List<GameObject> objsToAdd;
  Renderer renderer;

  public void Scene(String name) {
    this.name = name;
    this.camera = new Camera(new Vector2f(0f, 0f));
    this.gameObjects = new ArrayList<>();
    this.renderer = new Renderer(this.camera);
    this.physics = new Physics();

    this.worldPartition = new HashMap<>();
    this.objsToDelete = new ArrayList<>();
    this.objsToAdd = new ArrayList<>();
  }

  public void init() {

  }

  public void start() {
    for (GameObject g : gameObjects) {
      for (Component c : g.getAllComponents()) {
        c.start();
      }
    }
  }

  public void moveGameObject(GameObject g, Vector2f direction) {
    Tuple<Integer> oldCoords = g.getGridCoords();
    Tuple<Integer> newCoords = new Tuple<>(oldCoords.x + (int)(direction.x * Window.TILE_WIDTH),
        oldCoords.y + (int)(direction.y * Window.TILE_HEIGHT), oldCoords.z);

    if (!worldPartition.containsKey(newCoords)) {
      worldPartition.remove(oldCoords);
      g.getTransform().position.x = newCoords.x;
      g.getTransform().position.y = newCoords.y;
      worldPartition.put(newCoords, g);
    }
  }

  public GameObject findObjectWithComponent(Component c) {
    for (GameObject g : gameObjects) {
      if (g.getComponent(c.getClass()) != null) {
        return g;
      }
    }
    return null;
  }

//  public <T extends Component> List<GameObject> getGameObjectsWithComponent(Class<T> c) {
//    List<GameObject> result = new ArrayList<>();
//    for (GameObject g : gameObjects) {
//      if (g.getComponent(c) != null) {
//        result.add(g);
//      }
//    }
//    return result;
//  }

  public void addGameObject(GameObject g) {
    gameObjects.add(g);
    renderer.add(g);
    Tuple<Integer> gridPos = g.getGridCoords();
    worldPartition.put(gridPos, g);
  }

  public void safeAddGameObject(GameObject g) {
    objsToAdd.add(g);
  }

  public void deleteGameObject(GameObject g) {
    objsToDelete.add(g);
  }

  public Map<Tuple<Integer>, GameObject> getWorldPartition() {
    return this.worldPartition;
  }

  public abstract void update(double dt);

  public void render() {
    renderer.render();
  }

  public void cleanUp() {
    renderer.cleanUp();
  }

//  public void importLevel(String filename) {
//    if (gameObjects.size() > 0) {
//      objsToAdd.clear();
//      objsToDelete.clear();
//      gameObjects.clear();
//      renderer.resetLevel();
//      worldPartition.clear();
//    }
//
//    Parser.openLevelFile(filename);
//    GameObject go = Parser.parseGameObject();
//    while (go != null) {
//      addGameObject(go);
//      go = Parser.parseGameObject();
//    }
//  }
}
