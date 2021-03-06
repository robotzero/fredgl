/**
 *
 */
package com.robotzero.dataStructure.maze;

import com.robotzero.dataStructure.Transform;
import com.robotzero.engine.DebugDraw;
import com.robotzero.game.Prefabs;
import com.robotzero.infrastructure.constants.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class encapsulates all functionality to draw a map of the overall maze, the set of visible walls, the solution.
 * The map is drawn on the screen in such a way that the current position remains at the center of the screen.
 * The current position is visualized as a red dot with an attached arc for its current direction.
 * The solution is visualized as a yellow line from the current position towards the exit of the map.
 * Walls set are currently visible in the first person view are drawn white, all other walls are drawn in grey.
 * It is possible to zoom in and out of the map.
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 *
 */
public class MapDrawer {

  // keep local copies of values determined in Maze.java
  int view_width = 400;
  int view_height = 400;
  int map_unit = 128;
  int map_scale = 10;
  int step_size = map_unit / 4;
  public static List<Transform> stoneTransforms;
  public static List<Transform> jumpBoards;
  public static Map<Integer, List<Transform>> lineTransforms = new HashMap<>();
  Cells seencells;

  // contains all necessary information about current maze, i.e.
  // cells: location of walls
  // dists: distance to exit
  // width and height of the maze
  MazeConfiguration mazeConfig;

  MazeController controller;

  /**
   * Constructor
   * @param width of display
   * @param height of display
   * @param map_unit
   * @param step_size
   * @param seencells
   * @param map_scale
   * @param c
   */
  public MapDrawer(int width, int height, int map_unit, int step_size, Cells seencells, int map_scale, MazeController c) {
    //System.out.println("MapDrawer: using new maze config constructor") ;
    view_width = width;
    view_height = height;
    this.map_unit = map_unit;
    this.step_size = step_size;
    this.seencells = seencells;
    this.map_scale = map_scale;
    controller = c;
    mazeConfig = controller.getMazeConfiguration();
    assert mazeConfig != null : "MapDrawer: maze configuration is null in given maze object!";
    lineTransforms.put(0, new ArrayList<>());
    lineTransforms.put(1, new ArrayList<>());
    lineTransforms.put(2, new ArrayList<>());
  }
  ///////////////////// Methods to implement Viewer interface //////////////////////////////////////

  public void incrementMapScale() {
    if (null != controller) {
      if (controller.isInMapMode())
        map_scale += 1;
    }
  }

  public void decrementMapScale() {
    if (null != controller) {
      if (controller.isInMapMode()) {
        map_scale -= 1;
        if (1 > map_scale)
          map_scale = 1;
      }
    }
  }


  public void redraw(Graphics gc, int px, int py,
                     int view_dx, int view_dy, int walk_step, int view_offset, RangeSet rset, int ang) {
    //dbg("redraw") ;
    if (null != controller) {
      if (controller.isInMapMode()) {
        draw_map(gc, px, py, walk_step, view_dx, view_dy, controller.isInShowMazeMode(), controller.isInShowSolutionMode());
        draw_currentlocation(gc, view_dx, view_dy);
      }
    }
  }

  /**
   * Helper method for redraw, called if map_mode is true, i.e. the users wants to see the overall map.
   * The map is drawn only on a small rectangle inside the maze area such that only a part of the map is actually shown.
   * Of course a part covering the current location needs to be displayed.
   * @param gc graphics handler to manipulate screen
   */
  public void draw_map(Graphics gc, int px, int py, int walk_step, int view_dx, int view_dy, boolean showMaze, boolean showSolution) {
    // dimensions of the maze
    final int mazew = mazeConfig.getWidth();
    final int mazeh = mazeConfig.getHeight();
    int step_size_y = Prefabs.STONEHEIGHT;
    int map_scale_width = Prefabs.STONEWIDTH * 2;
    int map_scale_height = Prefabs.STONEHEIGHT * 2;
    int offset = (int) ((Prefabs.STONEWIDTH * 0.5f) - (Prefabs.LINEWIDTH * 0.5f));

//		gc.setColor(Color.white);

    // determine offsets for x and y
//    int vx = px * map_unit + map_unit / 2;
//    vx += viewd_unscale(view_dx * (step_size * walk_step));
//    int vy = py * map_unit + map_unit / 2;
//    vy += viewd_unscale(view_dy * (step_size_y * walk_step));
//    int offx = -vx * map_scale / map_unit + view_width / 2;
//    int offy = -vy * map_scale / map_unit + view_height / 2;
    int offx = Prefabs.STONEWIDTH;
    int offy = (view_height - (mazeConfig.getHeight() * map_scale_height)) - Prefabs.STONEHEIGHT;

    // compute minimum for x,y
//    int xmin = -offx / map_scale_width;
//    int ymin = -offy / map_scale_height;
    int ymin = 0;
    int xmin = 0;
    if (xmin < 0) xmin = 0;
    if (ymin < 0) ymin = 0;

    // compute maximum for x,y
    int xmax = (view_width - offx) / map_scale_width + 1;
    int ymax = (view_height - offy) / map_scale_height + 1;
    if (xmax >= mazew) xmax = mazew;
    if (ymax >= mazeh) ymax = mazeh;
    stoneTransforms = new ArrayList<>();
    jumpBoards = new ArrayList<>();
    // iterate over integer grid between min and max of x,y
    for (int y = ymin; y <= ymax; y++) {
      for (int x = xmin; x <= xmax; x++) {
        int nx1 = x * map_scale_width + offx;
        int ny1 = view_height - 0 - (y * map_scale_height + offy);
        int nx2 = nx1 + map_scale_width;
        int ny2 = ny1 - map_scale_height;
        boolean theCondition1 = (x >= mazew) ? false : ((y < mazeh) ?
            mazeConfig.hasWall(x, y, CardinalDirection.North) :
            mazeConfig.hasWall(x, y - 1, CardinalDirection.South));

        if (x < mazew && y > 0 && y < mazeh && mazeConfig.hasWall(x, y - 1, CardinalDirection.North) && !mazeConfig.hasWall(x, y, CardinalDirection.North)) {
          final var t = new Transform(new Vector2f(nx1 + offset, ny1));
          if (!lineTransforms.get(2).contains(t)) {
            lineTransforms.get(2).add(t);
          }
        int y2 = y + 1;
        while (y2 < mazeh && !mazeConfig.hasWall(x, y2, CardinalDirection.North)) {
          int ny11 = view_height - 0 - (y2 * map_scale_height + offy);
          int ny12 = view_height - 0 - (y2 * map_scale_height + offy) - Prefabs.STONEHEIGHT;
          int ny13 = view_height - 0 - (y2 * map_scale_height + offy) + Prefabs.STONEHEIGHT;
          final var t2 = new Transform(new Vector2f(nx1 + offset, ny11));
          final var t3 = new Transform(new Vector2f(nx1 + offset, ny12));
          final var t4 = new Transform(new Vector2f(nx1 + offset, ny13));
          if (!lineTransforms.get(1).contains(t2)) {
            lineTransforms.get(1).add(t2);
          }
          if (!lineTransforms.get(1).contains(t3)) {
            lineTransforms.get(1).add(t3);
          }
          if (!lineTransforms.get(1).contains(t4)) {
            lineTransforms.get(1).add(t4);
          }
//          if (!mazeConfig.hasWall(x, y2, CardinalDirection.West)) {
//            final var j1 = new Transform(new Vector2f(nx1 + 3, ny11));
//						final var j2 = new Transform(new Vector2f(nx1 + 3, ny12));
//            if (!jumpBoards.contains(j1)) {
//              jumpBoards.add(j1);
//            }
//
//						if (!jumpBoards.contains(j2)) {
//							jumpBoards.add(j2);
//						}
////							DebugDraw.addLine2D(new Vector2f(nx1, ny1), new Vector2f(nx1, ny2), new Vector3f(Window.COLOR_BLUE.x, Window.COLOR_BLUE.y, Window.COLOR_BLUE.z), 0);
//          }
//
//          if (!mazeConfig.hasWall(x, y2, CardinalDirection.East)) {
//            final var j1 = new Transform(new Vector2f(nx1 + 3, ny11));
//						final var j2 = new Transform(new Vector2f(nx1 + 3, ny12));
//            if (!jumpBoards.contains(j1)) {
//              jumpBoards.add(j1);
//            }
//
//						if (!jumpBoards.contains(j2)) {
//							jumpBoards.add(j2);
//						}
//							DebugDraw.addLine2D(new Vector2f(nx1, ny1), new Vector2f(nx1, ny2), new Vector3f(Window.COLOR_BLUE.x, Window.COLOR_BLUE.y, Window.COLOR_BLUE.z), 0);
//          }
          y2 = y2 + 1;
        }
        int ny14 = view_height - 0 - (y2 * map_scale_height + offy);
        final var t4 = new Transform(new Vector2f(nx1 + offset, ny14));
        if (!lineTransforms.get(0).contains(t4)) {
          lineTransforms.get(0).add(t4);
        }

        int ny15 = view_height - 0 - (y2 * map_scale_height + offy) + Prefabs.STONEHEIGHT;
        final var t5 = new Transform(new Vector2f(nx1 + offset, ny15));
        if (!lineTransforms.get(1).contains(t5)) {
          lineTransforms.get(1).add(t5);
        }
      }
//				gc.setColor(seencells.hasWall(x,y, CardinalDirection.North) ? Color.white : Color.gray);
      if ((seencells.hasWall(x, y, CardinalDirection.North) || showMaze) && theCondition1) {
        if (!stoneTransforms.contains(new Transform(new Vector2f(nx1 - Prefabs.STONEWIDTH, ny1 - Prefabs.STONEHEIGHT)))) {
          stoneTransforms.add(new Transform(new Vector2f(nx1 - Prefabs.STONEWIDTH, ny1 - Prefabs.STONEHEIGHT)));
        }
        if (!stoneTransforms.contains(new Transform(new Vector2f(nx1, ny1 - Prefabs.STONEHEIGHT)))) {
          stoneTransforms.add(new Transform(new Vector2f(nx1, ny1 - Prefabs.STONEHEIGHT)));
        }

        if (!stoneTransforms.contains(new Transform(new Vector2f(nx2 - Prefabs.STONEWIDTH, ny1 - Prefabs.STONEHEIGHT)))) {
          stoneTransforms.add(new Transform(new Vector2f(nx2 - Prefabs.STONEWIDTH, ny1 - Prefabs.STONEHEIGHT)));
        }
//					DebugDraw.addLine2D(new Vector2f(nx1, ny1), new Vector2f(nx2, ny1), new Vector3f(Window.COLOR_RED.x, Window.COLOR_RED.y, Window.COLOR_RED.z), 0);
      }
//					gc.drawLine(nx1, ny1, nx2, ny1);

      boolean theCondition2 = (y >= mazeh) ? false : ((x < mazew) ?
          mazeConfig.hasWall(x, y, CardinalDirection.West) :
          mazeConfig.hasWall((x - 1), y, CardinalDirection.East));

//				gc.setColor(seencells.hasWall(x,y, CardinalDirection.West) ? Color.white : Color.gray);
      if ((seencells.hasWall(x, y, CardinalDirection.West) || showMaze) && theCondition2) {
        if (!stoneTransforms.contains(new Transform(new Vector2f(nx1 - Prefabs.STONEWIDTH, ny1 - Prefabs.STONEHEIGHT)))) {
          stoneTransforms.add(new Transform(new Vector2f(nx1 - Prefabs.STONEWIDTH, ny1 - Prefabs.STONEHEIGHT)));
        }
        if (!stoneTransforms.contains(new Transform(new Vector2f(nx1 - Prefabs.STONEWIDTH, ny2)))) {
          stoneTransforms.add(new Transform(new Vector2f(nx1 - Prefabs.STONEWIDTH, ny2)));
        }
        if (!stoneTransforms.contains(new Transform(new Vector2f(nx1 - Prefabs.STONEWIDTH, ny2 - Prefabs.STONEHEIGHT)))) {
          stoneTransforms.add(new Transform(new Vector2f(nx1 - Prefabs.STONEWIDTH, ny2 - Prefabs.STONEHEIGHT)));
        }
//					DebugDraw.addLine2D(new Vector2f(nx1, ny1), new Vector2f(nx1, ny2), new Vector3f(Window.COLOR_BLUE.x, Window.COLOR_BLUE.y, Window.COLOR_BLUE.z), 0);
      }
    }
  }
//		if (showSolution) {
//			draw_solution(gc, offx, offy, px, py) ;
//		}
}

  /**
   * Helper method for redraw, called if map_mode is true, i.e. the users wants to see the overall map.
   * The map is drawn only on a small rectangle inside the maze area such that only a part of the map is actually shown.
   * Of course a part covering the current location needs to be displayed.
   */
  public void generateGraphicMaze(int px, int py, int walk_step, int view_dx, int view_dy, boolean showMaze, boolean showSolution) {
    // dimensions of the maze
    final int mazew = mazeConfig.getWidth();
    final int mazeh = mazeConfig.getHeight();
    final int map_scale = 1;

    // determine offsets for x and y
    int vx = px * map_unit + map_unit / 2;
    vx += viewd_unscale(view_dx * (step_size * walk_step));
    int vy = py * map_unit + map_unit / 2;
    vy += viewd_unscale(view_dy * (step_size * walk_step));
    int offx = -vx * map_scale / map_unit + view_width / 2;
    int offy = -vy * map_scale / map_unit + view_height / 2;
    offx = 0;
    offy = 0;

    // compute minimum for x,y
    int xmin = -offx / map_scale;
    int ymin = -offy / map_scale;
    if (xmin < 0) xmin = 0;
    if (ymin < 0) ymin = 0;

    // compute maximum for x,y
    int xmax = (view_width - offx) / map_scale + 1;
    int ymax = (view_height - offy) / map_scale + 1;
    if (xmax >= mazew) xmax = mazew;
    if (ymax >= mazeh) ymax = mazeh;

    // iterate over integer grid between min and max of x,y
    for (int y = ymin; y <= ymax; y++)
      for (int x = xmin; x <= xmax; x++) {
        int nx1 = x * map_scale + offx;
        int ny1 = view_height - 1 - (y * map_scale + offy);
        int nx2 = nx1 + map_scale;
        int ny2 = ny1 - map_scale;
        boolean theCondition = (x >= mazew) ? false : ((y < mazeh) ?
            mazeConfig.hasWall(x, y, CardinalDirection.North) :
            mazeConfig.hasWall(x, y - 1, CardinalDirection.South));

//				gc.setColor(seencells.hasWall(x,y, CardinalDirection.North) ? Color.white : Color.gray);
        if ((seencells.hasWall(x, y, CardinalDirection.North) || showMaze) && theCondition)
          //DebugDraw.addLine2D(new Vector2f(nx1, ny1), new Vector2f(nx2, ny1), new Vector3f(Window.COLOR_BLUE.x, Window.COLOR_BLUE.y, Window.COLOR_BLUE.z), 0);
//					gc.drawLine(nx1, ny1, nx2, ny1);

          theCondition = (y >= mazeh) ? false : ((x < mazew) ?
              mazeConfig.hasWall(x, y, CardinalDirection.West) :
              mazeConfig.hasWall((x - 1), y, CardinalDirection.East));

//				gc.setColor(seencells.hasWall(x,y, CardinalDirection.West) ? Color.white : Color.gray);
        if ((seencells.hasWall(x, y, CardinalDirection.West) || showMaze) && theCondition) {
        }
//					DebugDraw.addLine2D(new Vector2f(nx1, ny1), new Vector2f(nx1, ny2), new Vector3f(Window.COLOR_BLUE.x, Window.COLOR_BLUE.y, Window.COLOR_BLUE.z), 0);
//					gc.drawLine(nx1, ny1, nx1, ny2);
      }

//		if (showSolution) {
//			draw_solution(gc, offx, offy, px, py) ;
//		}
  }

  /**
   * Draws an oval red shape with and arrow for the current position and direction on the maze.
   * It always reside on the center of the screen. The map drawing moves if the user changes location.
   * @param gc to draw on
   */
  public void draw_currentlocation(Graphics gc, int view_dx, int view_dy) {
    gc.setColor(Color.red);
    // draw oval of appropriate size at the center of the screen
    int ctrx = view_width / 2; // center x
    int ctry = view_height / 2; // center y
    int cirsiz = map_scale / 2; // circle size
    gc.fillOval(ctrx - cirsiz / 2, ctry - cirsiz / 2, cirsiz, cirsiz);
    // draw a red arrow with the oval to indicate direction
    int arrlen = 7 * map_scale / 16; // arrow length
    int aptx = ctrx + ((arrlen * view_dx) >> 16);
    int apty = ctry - ((arrlen * view_dy) >> 16);
    int arrlen2 = map_scale / 4;
    int aptx2 = ctrx + ((arrlen2 * view_dx) >> 16);
    int apty2 = ctry - ((arrlen2 * view_dy) >> 16);
    //int ptoflen = map_scale/8; // unused
    int ptofx = -(arrlen2 * view_dy) >> 16;
    int ptofy = -(arrlen2 * view_dx) >> 16;
    // now the drawing
    gc.drawLine(ctrx, ctry, aptx, apty);
    gc.drawLine(aptx, apty, aptx2 + ptofx, apty2 + ptofy);
    gc.drawLine(aptx, apty, aptx2 - ptofx, apty2 - ptofy);
  }

  /**
   * Draws a yellow line to show the solution on the overall map.
   * Method is only called if in STATE_PLAY and map_mode and showSolution are true.
   * Since the current position is fixed at the center of the screen, all lines on the map are drawn with some offset.
   * @param gc to draw lines on
   * @param offx
   * @param offy
   */
  public void draw_solution(Graphics gc, int offx, int offy, int px, int py) {

    if (!mazeConfig.isValidPosition(px, py)) {
      dbg(" Parameter error: position out of bounds: (" + px + "," + py + ") for maze of size " + mazeConfig.getWidth() + "," + mazeConfig.getHeight());
      return;
    }
    // current position on the solution path (sx,sy)
    int sx = px;
    int sy = py;
    int distance = mazeConfig.getDistanceToExit(sx, sy);

    gc.setColor(Color.yellow);

    // while we are more than 1 step away from the final position
    while (distance > 1) {
      // find neighbor closer to exit (with no wall in between)
      int[] neighbor = mazeConfig.getNeighborCloserToExit(sx, sy);
      if (null == neighbor)
        return; // error
      // scale coordinates, original calculation:
      // x-coordinates
      // nx1     == sx*map_scale + offx + map_scale/2;
      // nx1+ndx == sx*map_scale + offx + map_scale/2 + dx*map_scale == (sx+dx)*map_scale + offx + map_scale/2;
      // y-coordinates
      // ny1     == view_height-1-(sy*map_scale + offy) - map_scale/2;
      // ny1+ndy == view_height-1-(sy*map_scale + offy) - map_scale/2 + -dy * map_scale == view_height-1 -((sy+dy)*map_scale + offy) - map_scale/2
      // current position coordinates
      int nx1 = sx * map_scale + offx + map_scale / 2;
      int ny1 = view_height - 1 - (sy * map_scale + offy) - map_scale / 2;
      // neighbor position coordinates
      int nx2 = neighbor[0] * map_scale + offx + map_scale / 2;
      int ny2 = view_height - 1 - (neighbor[1] * map_scale + offy) - map_scale / 2;
      gc.drawLine(nx1, ny1, nx2, ny2);

      // update loop variables for current position (sx,sy) and distance d for next iteration
      sx = neighbor[0];
      sy = neighbor[1];
      distance = mazeConfig.getDistanceToExit(sx, sy);

    }
  }

  /**
   * Unscale value
   * @param x
   * @return
   */
  final int viewd_unscale(int x) {
    return x >> 16;
  }

  /**
   * Debug output
   * @param str
   */
  private void dbg(String str) {
    // TODO: change this to a logger
    System.out.println("MapDrawer:" + str);
  }
}
