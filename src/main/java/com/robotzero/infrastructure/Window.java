package com.robotzero.infrastructure;

import com.robotzero.engine.Scene;
import com.robotzero.game.TestScene;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
  public boolean isInEditor = true;

  private static Window window = null;
  private Scene currentScene = null;
  private long glfwWindow = 0L;
  private int width, height;
  private String title;
  private float aspect;
  private Vector4f clearColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

  private long audioContext;
  private long audioDevice;

  public boolean isRunning = false;

  public static void framebufferSizeCallback(long window, int width, int height) {
    Window.getWindow().setWidth(width);
    Window.getWindow().setHeight(height);
    Window.getWindow().setAspect(width / height);
    if (Window.getScene() != null) {
      glViewport(0, 0, width, height);
      Window.getScene().camera.adjustPerspective();
    }
  }

  public Window() {
    this.width = com.robotzero.infrastructure.constants.Window.SCREEN_WIDTH;
    this.height = com.robotzero.infrastructure.constants.Window.SCREEN_HEIGHT;
    this.title = com.robotzero.infrastructure.constants.Window.SCREEN_TITLE;
    this.aspect = (float) width / (float) height;
  }

  public void run() {
    System.out.println("Hello LWJGL " + Version.getVersion() + "!");

    init();
    loop();

    // Free the window callbacks and destroy the window
    glfwFreeCallbacks(glfwWindow);
    glfwDestroyWindow(glfwWindow);

    // Terminate GLFW and free the error callback
    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }

  public void init() {
    // Setup an error callback. The default implementation
    // will print the error message in System.err.
    GLFWErrorCallback.createPrint(System.err).set();

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if ( !glfwInit() )
      throw new IllegalStateException("Unable to initialize GLFW");

    // Configure GLFW
    glfwDefaultWindowHints(); // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
    glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

    // Create the window
    glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
    if ( glfwWindow == NULL )
      throw new RuntimeException("Failed to create the GLFW window");

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(glfwWindow, (window, key, scancode, action, mods) -> {
      if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
        glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
    });

    glfwSetFramebufferSizeCallback(glfwWindow, Window::framebufferSizeCallback);
    glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

    // Make the OpenGL context current
    glfwMakeContextCurrent(glfwWindow);
    // Enable v-sync
    glfwSwapInterval(1);

    // Make the window visible
    glfwShowWindow(glfwWindow);

    // Initialize audio device
    String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
    audioDevice = alcOpenDevice(defaultDeviceName);

    int[] attributes = {0};
    audioContext = alcCreateContext(audioDevice, attributes);
    alcMakeContextCurrent(audioContext);

    ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
    ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

    if(!alCapabilities.OpenAL10) {
      assert false : "Audio Library not supported.";
    }

    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities();

    glDisable(GL_DEPTH_TEST);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    changeScene(0);
  }

  public static Scene getScene() {
    return getWindow().currentScene;
  }

  private void loop() {
    float lastFrameTime = Time.getTime();
    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    this.isRunning = true;
    while ( !glfwWindowShouldClose(glfwWindow) ) {
      float time = Time.getTime();
      float deltaTime = time - lastFrameTime;
      lastFrameTime = time;

      glfwPollEvents();

      this.update(deltaTime);

      // Set the clear color
      glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
      glEnable(GL_DEPTH);

      this.currentScene.render();

      glfwSwapBuffers(glfwWindow); // swap the color buffers
    }

    this.isRunning = false;

    alcDestroyContext(audioContext);
    alcCloseDevice(audioDevice);
  }

  public void changeScene(int scene) {
    switch (scene) {
      case 0:
//        isInEditor = true;
//        currentScene = new LevelEditorScene("Level Editor");
//        currentScene.init();
//        currentScene.start();
        break;
      case 1:
//        isInEditor = false;
//        currentScene = new LevelScene("Level");
//        currentScene.init();
//        currentScene.start();
        break;
      case 2:
        isInEditor = false;
        currentScene = new TestScene("Test");
        currentScene.init();
        currentScene.start();
        break;
      default:
        System.out.println("Do not know what this scene is.");
        currentScene = null;
        break;
    }
  }

  public void setColor(Vector4f color) {
    this.clearColor.x = color.x;
    this.clearColor.y = color.y;
    this.clearColor.z = color.z;
    this.clearColor.w = color.w;
  }

  public static Window getWindow() {
    if (Window.window == null) {
      Window.window = new Window();
    }

    return Window.window;
  }

  public void update(double dt) {
    currentScene.update(dt);
//    if (currentScene instanceof LevelScene)
//      currentScene.physics.update(dt);
//    MouseListener.endFrame();
  }

  public void setWidth(int width) {
    this.width = width;
  }
  public void setHeight(int height) {
    this.height = height;
  }
  public int getWidth() {
    return this.width;
  }
  public int getHeight() {
    return this.height;
  }
  public void setAspect(float val) {
    this.aspect = val;
  }
  public float getAsepct() {
    return this.aspect;
  }
}
