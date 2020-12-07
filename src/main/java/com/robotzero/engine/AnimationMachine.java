package com.robotzero.engine;

import com.robotzero.game.Scene;
import com.robotzero.infrastructure.Window;

import java.util.ArrayList;
import java.util.List;

public class AnimationMachine implements Component {
    List<Animation> animations;
    Animation current;
    String startAnimation;

    private boolean inLevelEditor = false;
    private GameObject levelEditorGo;
    private SpriteRenderer spriteRenderer;
    private GameObject gameObject;

    public AnimationMachine() {
        this.animations = new ArrayList<>();
        this.current = null;
    }

    public AnimationMachine(GameObject gameObject) {
        this.animations = new ArrayList<>();
        this.current = null;
        this.gameObject = gameObject;
    }

    public void setStartAnimation(String animation) {
        this.startAnimation = animation;
    }

    public void addAnimation(Animation animation) {
        animation.machine = this;
        this.animations.add(animation);
    }

    public Animation getAnimation(String name) {
        for (Animation anim : animations) {
            if (anim.is(name)) {
                return anim;
            }
        }

        return null;
    }

    @Override
    public void start() {
        Animation startAnim = this.getAnimation(startAnimation);
        assert startAnim != null : "Error: Start Animation was never set for: " + this.gameObject.getName() + ". Did you forget to setStartAnimation?";
        this.current = startAnim;

        Scene scene = Window.getScene();
//        if (scene instanceof LevelEditorScene) {
//            inLevelEditor = true;
//            this.levelEditorGo = new GameObject("LevelEditorCopy", gameObject.transform.copy(), gameObject.zIndex);
//            levelEditorGo.addComponent(getPreviewSprite().copy());
//        }

        this.spriteRenderer = (SpriteRenderer) gameObject.getComponent(SpriteRenderer.class);
        assert this.spriteRenderer != null : "Animation machine must be attached to GameObject with SpriteRenderer!";
    }

    @Override
    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    @Override
    public void update(double dt) {
        spriteRenderer.sprite = current.getCurrentSprite();
        current.update(dt);
    }

    public void trigger(String trigger) {
        current = current.trigger(trigger);
    }

    public Sprite getPreviewSprite() {
        return this.animations.get(0).getPreviewSprite();
    }

    @Override
    public Component copy() {
        AnimationMachine machine = new AnimationMachine(gameObject);
        for (Animation anim : animations) {
            machine.addAnimation((Animation)anim.copy());
        }
        machine.setStartAnimation(this.startAnimation);

        return machine;
    }
}
