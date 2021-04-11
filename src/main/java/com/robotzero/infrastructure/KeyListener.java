package com.robotzero.infrastructure;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

public class KeyListener {
  private static KeyListener instance;
  private final boolean[] keyPressed = new boolean[350];
  private final int[] repeats = new int[350];

  private KeyListener() {}

  public static void keyCallback(long window, int key, int scancode, int action, int mods) {
    if (action == GLFW_REPEAT && get().keyPressed[GLFW_KEY_A] || get().keyPressed[GLFW_KEY_D] || get().keyPressed[GLFW_KEY_RIGHT] || get().keyPressed[GLFW_KEY_LEFT]) {
      get().repeats[key] = get().repeats[key] + 1;
    }
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

  public static int repeates(int keyCode) {
    return get().repeats[keyCode];
  }
}
