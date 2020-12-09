package com.robotzero.dataStructure;

import com.robotzero.engine.Maps;
import com.robotzero.engine.Sprite;
import com.robotzero.engine.Spritesheet;
import com.robotzero.render.Shader;
import com.robotzero.render.Texture;
import org.joml.Vector2f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AssetPool {
    static Map<String, Sprite> sprites = new HashMap<>();
    static Map<String, Spritesheet> spritesheets = new HashMap<>();
    static Map<String, Shader> shaders = new HashMap<>();
    static Map<String, Texture> textures = new HashMap<>();
    static Map<String, Maps> maps = new HashMap<>();
//    static Map<String, Sound> sounds = new HashMap<>();

    public static boolean hasSprite(String pictureFile) {
        File tmp = new File(pictureFile);
        return AssetPool.sprites.containsKey(tmp.getAbsolutePath());
    }

    public static boolean hasSpritesheet(String pictureFile) {
        File tmp = new File(pictureFile);
        return AssetPool.spritesheets.containsKey(tmp.getAbsolutePath());
    }

    public static boolean hasShader(String shaderPath) {
        File tmp = new File(shaderPath);
        return AssetPool.shaders.containsKey(tmp.getAbsolutePath());
    }

    public static boolean hasTexture(String pictureFile) {
        File tmp = new File(pictureFile);
        return AssetPool.textures.containsKey(tmp.getAbsolutePath());
    }

    public static boolean hasMap(String mapFile) {
        File tmp = new File(mapFile);
        return AssetPool.maps.containsKey(tmp.getAbsolutePath());
    }

//    public static boolean hasSound(String soundFile) {
//        File tmp = new File(soundFile);
//        return AssetPool.sounds.containsKey(tmp.getAbsolutePath());
//    }

    public static Texture getTexture(String pictureFile) {
        File file = new File(pictureFile);
        if (hasTexture(pictureFile)) {
            return textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture(file.getAbsolutePath());
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return AssetPool.textures.get(file.getAbsolutePath());
        }
    }

//    public static Sound getSound(String soundFile) {
//        File file = new File(soundFile);
//        if (hasSound(soundFile)) {
//            return sounds.get(file.getAbsolutePath());
//        } else {
//            assert false : "Sound file not added '" + soundFile + "'.";
//        }
//        return null;
//    }

//    public static Sound addSound(String soundFile, boolean loops) {
//        File file = new File(soundFile);
//        if (hasSound(soundFile)) {
//            return sounds.get(file.getAbsolutePath());
//        } else {
//            Sound sound = new Sound(file.getAbsolutePath(), loops);
//            AssetPool.sounds.put(file.getAbsolutePath(), sound);
//            return AssetPool.sounds.get(file.getAbsolutePath());
//        }
//    }

    public static Sprite getSprite(String pictureFile) {
        File file = new File(pictureFile);
        if (AssetPool.hasSprite(file.getAbsolutePath())) {
            return AssetPool.sprites.get(file.getAbsolutePath());
        } else {
            Sprite sprite = new Sprite(pictureFile);
            AssetPool.addSprite(pictureFile, sprite);
            return AssetPool.sprites.get(file.getAbsolutePath());
        }
    }

    public static Shader getShader(String shaderPath) {
        File file = new File(shaderPath);
        if (AssetPool.hasShader(shaderPath)) {
            return AssetPool.shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(shaderPath);
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Spritesheet getSpritesheet(String pictureFile) {
        File file = new File(pictureFile);
        if (AssetPool.hasSpritesheet(file.getAbsolutePath())) {
            return AssetPool.spritesheets.get(file.getAbsolutePath());
        } else {
            assert false : "Spritesheet '" + file.getAbsolutePath() + "' does not exist.";
        }
        return null;
    }

    public static Maps getMap(String map) {
        File file = new File(map);
        if (AssetPool.hasMap(file.getAbsolutePath())) {
            return AssetPool.maps.get(file.getAbsolutePath());
        }
        assert false : "Map '" + file.getAbsolutePath() + "' does not exists.";
        return null;
    }

    public static void addSprite(String pictureFile, Sprite sprite) {
        File file = new File(pictureFile);
        if (!AssetPool.hasSprite(file.getAbsolutePath())) {
            AssetPool.sprites.put(file.getAbsolutePath(), sprite);
        } else {
            assert false : "Asset pool already has asset: " + file.getAbsolutePath();
        }
    }

    public static void addSpritesheet(String pictureFile, int tileWidth, int tileHeight,
                                      int spacing, int columns, int size) {
        File file = new File(pictureFile);
        if (!AssetPool.hasSpritesheet(file.getAbsolutePath())) {
            Spritesheet spritesheet = new Spritesheet(pictureFile, tileWidth, tileHeight,
                    spacing, columns, size);
            AssetPool.spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    public static void addMap(String mapFile) {
        File file = new File(mapFile);
        if (!AssetPool.hasMap(file.getAbsolutePath())) {
            try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file))) {
                String line = lineNumberReader.readLine();
                List<Transform> transforms = new ArrayList<>();
                while (line != null) {
                    byte [] bytes = line.getBytes(StandardCharsets.UTF_8);
                    for (int i = 0; i < bytes.length; i++) {
                        transforms.add(new Transform(new Vector2f(32 * i, lineNumberReader.getLineNumber() * 32)));
                    }
                    line = lineNumberReader.readLine();
                }
                Maps map = new Maps(transforms);
                maps.put(file.getAbsolutePath(), map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
