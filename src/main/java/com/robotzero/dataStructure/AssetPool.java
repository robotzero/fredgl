package com.robotzero.dataStructure;

import com.robotzero.engine.Sprite;
import com.robotzero.engine.Spritesheet;
import com.robotzero.game.Prefabs;
import com.robotzero.render.Shader;
import com.robotzero.render.Texture;
import org.joml.Vector2f;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssetPool {
    static Map<String, Sprite> sprites = new HashMap<>();
    static Map<String, Spritesheet> spritesheets = new HashMap<>();
    static Map<String, Shader> shaders = new HashMap<>();
    static Map<String, Texture> textures = new HashMap<>();
    static Map<String, MapAsset> maps = new HashMap<>();
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

    public static MapAsset getMap(String map) {
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
            Map<Integer, String> lines = new HashMap<>();
            try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file))) {
                String line = lineNumberReader.readLine();
                List<Transform> stoneTransforms = new ArrayList<>();
                List<Transform> jumpBoardTransforms = new ArrayList<>();
                Map<Integer, List<Transform>> lineTransforms = new HashMap<>();

                while (line != null) {
                    lines.put(lineNumberReader.getLineNumber(), line);
                    line = lineNumberReader.readLine();
                }
                lineTransforms.put(0, new ArrayList<>());
                lineTransforms.put(1, new ArrayList<>());
                lineTransforms.put(2, new ArrayList<>());
                Map<Integer, String> linesReverseOrder = lines.entrySet().stream()
                    .sorted(Comparator.comparingInt(value -> {
                        Map.Entry<Integer, String> entry = (Map.Entry<Integer, String>) value;
                        return entry.getKey();
                    }).reversed())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                linesReverseOrder.forEach((key, value) -> {
                    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
                    for (int i = 0; i < bytes.length; i++) {
                        if (bytes[i] == 49) {
                            stoneTransforms.add(new Transform(new Vector2f(Prefabs.STONEWIDTH * i, Math.abs(key - linesReverseOrder.size()) * Prefabs.STONEHEIGHT)));
                        }

                        int offset = (int) ((Prefabs.STONEWIDTH * 0.5f) - (Prefabs.LINEWIDTH * 0.5f));
                        if (bytes[i] == 50) {
                            lineTransforms.get(1).add(new Transform(new Vector2f((Prefabs.STONEWIDTH * i) + offset, Math.abs(key - linesReverseOrder.size()) * Prefabs.STONEHEIGHT)));
                        }

                        if (bytes[i] == 51) {
                            lineTransforms.get(2).add(new Transform(new Vector2f((Prefabs.STONEWIDTH * i) + offset, Math.abs(key - linesReverseOrder.size()) * Prefabs.STONEHEIGHT)));
                        }

                        if (bytes[i] == 52) {
                            lineTransforms.get(2).add(new Transform(new Vector2f((Prefabs.STONEWIDTH * i) + offset, Math.abs(key - linesReverseOrder.size()) * Prefabs.STONEHEIGHT)));
                            jumpBoardTransforms.add(new Transform(new Vector2f(Prefabs.STONEWIDTH * i, (Math.abs(key - linesReverseOrder.size()) * Prefabs.STONEHEIGHT))));
                        }

                        if (bytes[i] == 53) {
                            lineTransforms.get(0).add(new Transform(new Vector2f((Prefabs.STONEWIDTH * i) + offset, Math.abs(key - linesReverseOrder.size()) * Prefabs.STONEHEIGHT)));
                        }

                        if (bytes[i] == 54) {
                            lineTransforms.get(1).add(new Transform(new Vector2f((Prefabs.STONEWIDTH * i) + offset, Math.abs(key - linesReverseOrder.size()) * Prefabs.STONEHEIGHT)));
                            jumpBoardTransforms.add(new Transform(new Vector2f(Prefabs.STONEWIDTH * i, (Math.abs(key - linesReverseOrder.size()) * Prefabs.STONEHEIGHT))));
                        }
                    }
                });
                MapAsset map = new MapAsset(stoneTransforms, lineTransforms, jumpBoardTransforms);
                maps.put(file.getAbsolutePath(), map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
