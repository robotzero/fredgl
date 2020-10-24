package com.robotzero.render;

import com.robotzero.engine.GameObject;
import com.robotzero.engine.SpriteRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<com.robotzero.render.RenderBatch> batches;
    private Camera camera;

    public Renderer(Camera camera) {
        this.batches = new ArrayList<>();
        this.camera = camera;
    }

    public Camera camera() {
        return this.camera;
    }

    public void resetLevel() {
        batches.clear();
    }

    public void add(GameObject go) {
        SpriteRenderer spr = (SpriteRenderer) go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            add(spr);
        }
    }

    public void deleteGameObject(GameObject go) {
        SpriteRenderer spr = (SpriteRenderer) go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            spr.getQuad().shouldDelete = true;
        }
    }

    public void add(SpriteRenderer renderer) {
        boolean added = false;
        renderer.setDirty();
        for (RenderBatch batch : batches) {
            if (batch.hasRoom && batch.zIndex == renderer.getGameObject().getzIndex()) {
                if (renderer.getQuad().getTexture() == null || (batch.hasTexture(renderer.getQuad().getTexture()) || batch.hasTextureRoom())) {
                    batch.add(renderer);
                    added = true;
                    break;
                }
            }
        }
        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, this, renderer.getGameObject().getzIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.add(renderer);

            Collections.sort(batches);
        }
    }

    public void render() {
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }
}
