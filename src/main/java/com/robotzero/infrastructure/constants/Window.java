package com.robotzero.infrastructure.constants;

import com.robotzero.game.Prefabs;
import org.joml.Vector4f;

import java.awt.*;

public class Window {
  public static final int SCREEN_WIDTH = 1280;
  public static final int SCREEN_HEIGHT = 672;
//  public static final int SCREEN_WIDTH = 1920;
//  public static final int SCREEN_HEIGHT = 1080;
  public static final String SCREEN_TITLE = "FredGL";

  public static final int TILE_WIDTH = 32;
  public static final int TILE_HEIGHT = 32;
  public static final float GRAVITY = -50;
  public static final float CAMERA_OFFSET_X1 = SCREEN_WIDTH / 2f - Prefabs.FREDWIDTH / 2f;
  public static final float CAMERA_OFFSET_X2 = SCREEN_WIDTH / 2f + Prefabs.FREDWIDTH / 2f;
  public static final float CAMERA_OFFSET_Y1 = SCREEN_HEIGHT / 2f - Prefabs.FREDHEIGHT / 2f;
  public static final float CAMERA_OFFSET_Y2 = SCREEN_HEIGHT / 2f + Prefabs.FREDHEIGHT / 2f;
  public static final int JUMP_ALLOWANCE = 10;

  public static FontMetrics FONT_METRICS;
  public static String CURRENT_LEVEL = "Default";
  public static int Z_INDEX = 0;
  //public static final int CAMERA_OFFSET_X = 32 * 12;
  //public static final int CAMERA_OFFSET_X = (32 * 32) / 2;
  public static final float CAMERA_OFFSET_X = SCREEN_WIDTH / 2f;
  public static final int CAMERA_OFFSET_Y_1 = 0;
  //public static final int CAMERA_OFFSET_Y_2 = 0 - (32 * 21);
  public static final float CAMERA_OFFSET_Y_3 = SCREEN_HEIGHT / 2f ;
  //public static final int CAMERA_OFFSET_Y_3 = (32 * 21) / 2 ;

  public static final Vector4f BG_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 0.5f);
  public static final Vector4f TITLE_BG_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
  public static final Vector4f BUTTON_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
  public static final Vector4f ACTIVE_TAB = new Vector4f(0.26f, 0.59f, 0.98f, 1.00f);
  public static final Vector4f HOT_TAB = new Vector4f(0.26f, 0.59f, 0.98f, 0.80f);
  public static final Vector4f COLOR_CLEAR = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
  public static final Vector4f COLOR_HALF_ALPHA = new Vector4f(1.0f, 1.0f, 1.0f, 0.5f);
  public static final Vector4f COLOR_WHITE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
  public static final Vector4f COLOR_BLACK = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
  public static final Vector4f COLOR_GREEN = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
  public static final Vector4f COLOR_BLUE = new Vector4f(0.0f,  0.0f, 1.0f, 1.0f);
  public static final Vector4f COLOR_RED = new Vector4f(1f, 0f, 0f, 1f);
  public static final Vector4f COLOR_CYAN = new Vector4f(0.58f, 0.929f, 1f, 1f);
  public static final Vector4f COLOR_PURPLE = new Vector4f(0.671f, 0f, 1f, 1f);
  public static final Vector4f COLOR_ORANGE = new Vector4f(1f, 0.647f, 0f, 1f);
  public static final Vector4f SKY_COLOR = new Vector4f(93f / 255f, 148f / 255f, 251f / 255f, 1.0f);
  public static final Vector4f COLOR_GRAY = new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);

}
