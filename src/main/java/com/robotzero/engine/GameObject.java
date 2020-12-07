package com.robotzero.engine;

import com.robotzero.dataStructure.Transform;
import com.robotzero.dataStructure.Tuple;
import com.robotzero.infrastructure.constants.Window;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private final Transform transform;
    private final int zIndex;
    private final List<Component> components;
    private float lastZIndex;

    private final String name;
    private boolean isStarted = false;

    private final Tuple<Integer> gridCoords = new Tuple<>(0, 0, 0);

    public GameObject(String name, Transform transform, int zIndex) {
        this.name = name;
        this.transform = transform;
        this.components = new ArrayList<>();
        this.zIndex = zIndex;
        this.lastZIndex = this.zIndex;
    }

    public String getName() {
        return this.name;
    }

    public Transform getTransform() {
        return this.transform;
    }

    public int getzIndex() {
        return this.zIndex;
    }

    public float getLastZIndex() {
        return this.lastZIndex;
    }

    public void collision(Collision coll) {
        for (Component c : components) {
            c.collision(coll);
        }
    }
//
//    public void trigger(Trigger trigger) {
//        for (java.awt.Component c : components) {
//            c.trigger(trigger);
//        }
//    }

    public <T extends Component> T getComponent(Class<T> clazz) {
        for (Component c : components) {
            if (clazz.isAssignableFrom(c.getClass())) {
                try {
                    return clazz.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }

        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(c);
                return;
            }
        }
    }

    public List<Component> getAllComponents() {
        return this.components;
    }

    public void addComponent(Component c) {
        components.add(c);
        c.setGameObject(this);
    }

    public Tuple<Integer> getGridCoords() {
        int gridX = (int)(Math.floor(this.transform.position.x / Window.TILE_WIDTH) * Window.TILE_WIDTH);
        int gridY = (int)(Math.floor(this.transform.position.y / Window.TILE_WIDTH) * Window.TILE_HEIGHT);
        gridCoords.x = gridX;
        gridCoords.y = gridY;
        gridCoords.z = this.zIndex;

        return gridCoords;
    }

    public GameObject copy() {
        GameObject newGameObject = new GameObject(this.name, transform.copy(), this.zIndex);
        for (Component c : components) {
            Component copy = c.copy();
            if (copy != null) {
                newGameObject.addComponent(copy);
            }
        }

        newGameObject.start();

        return newGameObject;
    }


    public void update(double dt) {
        // Not really sure how game objects are getting added that haven't been started...
        if (!isStarted) {
            start();
        }

        for (Component c : components) {
            if (!(c instanceof RigidBody)) {
                c.update(dt);
            }
        }

        lastZIndex = this.zIndex;
    }

    public void start() {
        this.isStarted = true;
        for (Component c : components) {
            c.start();
        }
    }
}
