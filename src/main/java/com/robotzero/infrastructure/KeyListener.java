package com.robotzero.infrastructure;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
  private static KeyListener instance;
  private final boolean[] keyPressed = new boolean[350];
  private final int[] repeats = new int[350];

  private KeyListener() {}

  public static void keyCallback(long window, int key, int scancode, int action, int mods) {
    if (action == GLFW_PRESS) {
      get().keyPressed[key] = true;
    } else if (action == GLFW_RELEASE) {
      get().keyPressed[key] = false;
      get().repeats[key] = 0;
    }
  }

  public static KeyListener get() {
    if (instance == null) {
      instance = new KeyListener();
    }

    return instance;
  }

  public static boolean isKeyPressed(int keyCode) {
    return get().keyPressed[keyCode];
  }
}
