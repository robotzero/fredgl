package com.robotzero.engine;

import com.robotzero.dataStructure.AssetPool;
import com.robotzero.dataStructure.Transform;
import com.robotzero.infrastructure.JMath;
import com.robotzero.infrastructure.constants.Window;
import com.robotzero.render.Shader;
import com.robotzero.render.quads.Quad;
import org.joml.Vector4f;

public class SpriteRenderer implements Component {
    public Sprite sprite;
    public Vector4f color = JMath.copy(Window.COLOR_WHITE);
    public Shader shader = AssetPool.getShader("assets/shaders/default.glsl");

    private boolean dirty = false;
    private Transform lastTransform;
    private int lastSpriteIndex;
    private String lastSpritePictureFile;
    private Vector4f lastColor;
    private Quad quad;
    private GameObject gameObject;

    private boolean isMouse, isLevelEditor;

    public SpriteRenderer(Sprite sprite, GameObject gameObject) {
        this.sprite = sprite;
        this.gameObject = gameObject;
        this.lastSpriteIndex = this.sprite.index;
        this.lastSpritePictureFile = this.sprite.pictureFile;
        this.lastColor = JMath.copy(this.color);
        this.quad = new Quad(this.sprite, this.color);
        this.dirty = true;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void setClean() {
        this.dirty = false;
    }

    public void setDirty() {
        this.dirty = true;
    }

    public Quad getQuad() {
        return this.quad;
    }

    @Override
    public void start() {
        this.lastTransform = this.gameObject.getTransform().copy();
        this.dirty = true;
//        this.isMouse = this.gameObject.getComponent(LevelEditorControls.class) != null;
    }

    @Override
    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    @Override
    public void update(double dt) {
        if (!this.lastSpritePictureFile.equals(this.sprite.pictureFile)) {
            this.dirty = true;
            this.lastSpritePictureFile = this.sprite.pictureFile;
            this.quad.setSprite(sprite);
        }

        if (this.lastSpriteIndex != this.sprite.index) {
            this.dirty = true;
            this.lastSpriteIndex = this.sprite.index;
            this.quad.setSprite(sprite);
        }

        if (!this.lastTransform.equals(this.gameObject.getTransform())) {
            this.dirty = true;
            Transform.copyValues(this.gameObject.getTransform(), this.lastTransform);
        }

        if (!this.lastColor.equals(this.color)) {
            this.dirty = true;
            JMath.copyValues(this.color, this.lastColor);
            this.quad.setColor(this.color);
        }

        if (isLevelEditor) {
            if (this.gameObject.getzIndex() != Window.Z_INDEX && this.color.w != 0.5f && !this.isMouse) {
                this.quad.setColor(Window.COLOR_HALF_ALPHA);
                this.color = Window.COLOR_HALF_ALPHA;
            } else if (this.gameObject.getzIndex() == Window.Z_INDEX && this.color.w != 1.0f && !this.isMouse) {
                this.quad.setColor(Window.COLOR_WHITE);
                this.color = Window.COLOR_WHITE;
            }
        }
    }

    @Override
    public Component copy() {
        return new SpriteRenderer((Sprite)this.sprite.copy(), this.gameObject.copy());
    }

    public GameObject getGameObject() {
        return this.gameObject;
    }
}
