package com.robotzero.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Animation implements Component {
    private List<Sprite> sprites;
    float timeLeft;
    private int currentSprite;
    private float width, height;
    private boolean loops;

    private Map<String, String> stateTransfers;
    private List<Float> waitTimes;
    private String animationName;
    public AnimationMachine machine;

    public Animation(String name, float speed, List<Sprite> sprites, boolean loops) {
        List<Float> speeds = new ArrayList<>();
        for (int i=0; i < sprites.size(); i++) {
            speeds.add(speed);
        }
        init(name, speeds, sprites, loops);
    }

    public Animation(String name, List<Float> speed, List<Sprite> sprites, boolean loops) {
        init(name, speed, sprites, loops);
    }

    public void init(String name, List<Float> speeds, List<Sprite> sprites, boolean loops) {
        this.animationName = name;
        this.sprites = new ArrayList<>();
        this.waitTimes = new ArrayList<>();
        this.waitTimes.addAll(speeds);
        for (Sprite sprite : sprites) {
            Sprite copy = (Sprite)sprite.copy();
            this.sprites.add(copy);
        }
        this.timeLeft = waitTimes.get(0);
        this.width = this.sprites.get(0).width;
        this.height = this.sprites.get(0).height;
        this.stateTransfers = new HashMap<>();
        this.loops = loops;
    }

    public Sprite getCurrentSprite() {
        return this.sprites.get(this.currentSprite);
    }

    @Override
    public void update(double dt) {
        this.timeLeft -= dt;

        if (this.timeLeft <= 0.0f) {
            if (loops) {
                this.currentSprite = (this.currentSprite + 1) % this.sprites.size();
            } else {
                this.currentSprite = Math.min(this.currentSprite + 1, this.sprites.size() - 1);
            }

            this.timeLeft = this.waitTimes.get(this.currentSprite);
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void setGameObject(GameObject gameObject) {

    }

    public Animation trigger(String trigger) {
        if (machine.getAnimation(stateTransfers.get(trigger)) != null)
            return machine.getAnimation(stateTransfers.get(trigger));
        return this;
    }

    public void addStateTransfer(String trigger, String animationName) {
        stateTransfers.putIfAbsent(trigger, animationName);
    }

    public boolean is(String name) {
        return this.animationName.equals(name);
    }

    public Sprite getPreviewSprite() {
        return sprites.get(0);
    }

    public Component copy() {
        List<Sprite> spriteCopies = new ArrayList<>();
        for (Sprite sprite : this.sprites) {
            spriteCopies.add((Sprite) sprite.copy());
        }
        Animation animation = new Animation(this.animationName, this.waitTimes, spriteCopies, this.loops);
        for (String key : stateTransfers.keySet()) {
            animation.addStateTransfer(key, stateTransfers.get(key));
        }

        return animation;
    }
}
