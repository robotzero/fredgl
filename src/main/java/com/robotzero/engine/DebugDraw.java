
package com.robotzero.engine;

import com.robotzero.dataStructure.AssetPool;
import com.robotzero.infrastructure.Window;
import com.robotzero.render.Shader;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
  private static int MAX_LINES = 5555;

  private static List<Line2D> lines = new ArrayList<>();
  // 6 floats per vertex, 2 vertices per line
  private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
  private static Shader shader = AssetPool.getShader("assets/shaders/debug.glsl");

  private static int vaoID;
  private static int vboID;

  private static boolean started = false;

  public static void start() {
    Arrays.fill(vertexArray, 0);
    // Generate the vao
    vaoID = glGenVertexArrays();
    glBindVertexArray(vaoID);

    // Create the vbo and buffer some memory
    vboID = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboID);
    glBufferData(GL_ARRAY_BUFFER, (long) vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

    // Enable the vertex array attributes
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
    glEnableVertexAttribArray(1);

    glLineWidth(1.0f);
  }

  public static void beginFrame() {
    if (!started) {
      start();
      started = true;
    }

    // Remove dead lines
    lines = lines.stream().filter(line -> line.isStatic()).collect(Collectors.toList());
  }


  public static void draw() {
    Arrays.fill(vertexArray, 0);
    if (lines.size() == 0) return;

    int index = 0;
    for (Line2D line : lines) {
      for (int i = 0; i < 2; i++) {
        Vector2f position = i == 0 ? line.getFrom() : line.getTo();
        Vector3f color = line.getColor();

        // Load position
        vertexArray[index] = position.x;
        vertexArray[index + 1] = position.y;
        vertexArray[index + 2] = -10.0f;

        // Load the color
        vertexArray[index + 3] = color.x;
        vertexArray[index + 4] = color.y;
        vertexArray[index + 5] = color.z;
        index += 6;
      }
    }

    glBindBuffer(GL_ARRAY_BUFFER, vboID);
    glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);
//    glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, (lines.size() * 6 * 2));

    // Use our shader
    shader.use();
    shader.uploadMat4f("uProjection", Window.getScene().camera.getProjectionMatrix());
    shader.uploadMat4f("uView", Window.getScene().camera.getViewMatrix());

    // Bind the vao
    glBindVertexArray(vaoID);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    // Draw the batch
    glDrawArrays(GL_LINES, 0, (lines.size() * 6 * 2));

    // Disable Location
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glBindVertexArray(0);

    // Unbind shader
    shader.detach();
  }

  // ==================================================
  // Add line2D methods
  // ==================================================
  public static void addLine2D(Vector2f from, Vector2f to) {
    // TODO: ADD CONSTANTS FOR COMMON COLORS
    addLine2D(from, to, new Vector3f(0, 1, 0), 1);
  }

  public static void addLine2D(Vector2f from, Vector2f to, Vector3f color) {
    addLine2D(from, to, color, 1);
  }

  public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
    if (lines.size() >= MAX_LINES) return;
    DebugDraw.lines.add(new Line2D(from, to, color, lifetime));
  }

  public static void addLine2DDynamic(Vector2f from, Vector2f to, Vector3f color) {
    if (lines.size() >= MAX_LINES) return;
    DebugDraw.lines.add(new Line2D(from, to, color, 1));
  }

  // ==================================================
  // Add Box2D methods
  // ==================================================
  public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation) {
    // TODO: ADD CONSTANTS FOR COMMON COLORS
    addBox2D(center, dimensions, rotation, new Vector3f(0, 1, 0), 1);
  }

  public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Vector3f color) {
    addBox2D(center, dimensions, rotation, color, 1);
  }

  public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation,
                              Vector3f color, int lifetime) {
    Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).mul(0.5f));
    Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).mul(0.5f));

    Vector2f[] vertices = {
        new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
        new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
    };

    addLine2D(vertices[0], vertices[1], color, lifetime);
    addLine2D(vertices[0], vertices[3], color, lifetime);
    addLine2D(vertices[1], vertices[2], color, lifetime);
    addLine2D(vertices[2], vertices[3], color, lifetime);
  }

  public static void addBox2DDynamic(Vector2f center, Vector2f dimensions, float rotation,
                                     Vector3f color) {
    Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).mul(0.5f));
    Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).mul(0.5f));

    Vector2f[] vertices = {
        new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
        new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
    };

    addLine2DDynamic(vertices[0], vertices[1], color);
    addLine2DDynamic(vertices[0], vertices[3], color);
    addLine2DDynamic(vertices[1], vertices[2], color);
    addLine2DDynamic(vertices[2], vertices[3], color);
  }

  public static void clearAll() {
    Arrays.fill(vertexArray, 0);
    lines.clear();
    shader.delete();
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glDeleteBuffers(vboID);
    glDeleteVertexArrays(vaoID);
  }
}
