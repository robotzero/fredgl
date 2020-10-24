package com.robotzero.engine;

import com.robotzero.dataStructure.AssetPool;
import com.robotzero.infrastructure.KeyListener;
import com.robotzero.infrastructure.Window;
import com.robotzero.render.Camera;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class PlayerController implements Component {

    private AnimationMachine machine = null;
//    private Rigidbody rb = null;
    private SpriteRenderer sprite = null;
    private boolean onGround = true;
//    private BoxBounds bounds = null;
    private Camera camera;
    private boolean isDead = false;
    private float maxDeathY = 0;
    private float deathYJumpHeight = 20;
    private boolean movingUp = true;

    private float immunityTime = 1f;
    private float immunityLeft = 0.0f;
    private float flashLeft = 0.2f;
    private float flashTime = 0.2f;

    private int lives = 3;
    private boolean doWinAnimation = false;
    private boolean slidingDown = true;
    private float runTime = 5f;
    private float deadAnimTime = 2f;
    private boolean triggerRunAnim = true;
    private boolean triggerSlideAnim = true;

    private float invinciblityTime = 16f;
    private float invincibilityLeft = 0f;
    private float changeColorTime = 0.3f;
    private float changeColorTimeLeft = 0f;
    private int currentColor = 0;

    private float fireballCooldownTime = 0.0f;
    private float fireballCooldown = 1f;


    private float jumpSpeed = 17000;
    private float runSpeed = 1800;
    private float fireballSpeed = 4200;
    private GameObject gameObject;

    public PlayerController(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public void setState() {
//        if (type == PlayerType.BIG) {
//            AssetPool.getSound("assets/sounds/pipe.ogg").play();
//            if (this.type == PlayerType.SMALL || this.type == PlayerType.STAR) {
//                this.type = PlayerType.BIG;
//                this.machine.trigger("StartBig");
//                this.gameObject.getTransform().scale.y = 64;
//                bounds.setHeight(63);
//            } else if (this.type == PlayerType.BIG) {
//                System.out.println("Collect 100 points!");
//            }
//        } else if (type == PlayerType.FIRE) {
//            AssetPool.getSound("assets/sounds/pipe.ogg").play();
//            if (this.type == PlayerType.BIG) {
//                this.type = PlayerType.FIRE;
//                this.machine.trigger("StartFire");
//            } else if (this.type == PlayerType.FIRE) {
//                System.out.println("Collect 100 points!");
//            }
//        } else if (type == PlayerType.SMALL) {
//            AssetPool.getSound("assets/sounds/pipe.ogg").play();
//            this.type = PlayerType.SMALL;
//            this.machine.trigger("StartBig");
//            this.machine.trigger("StartSmall");
//            this.gameObject.getTransform().scale.y = 32;
//            bounds.setHeight(31);
//            immunityLeft = immunityTime;
//        } else if (type == PlayerType.STAR) {
//            AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stop();
//            AssetPool.getSound("assets/sounds/invincible.ogg").play();
//            this.type = PlayerType.STAR;
//            invincibilityLeft = invinciblityTime;
//        }
    }

    @Override
    public Component copy() {
        return new PlayerController(this.gameObject.copy());
    }

    @Override
    public void start() {
        this.machine = (AnimationMachine) gameObject.getComponent(AnimationMachine.class);
//        this.rb = gameObject.getComponent(Rigidbody.class);
        this.sprite = (SpriteRenderer) gameObject.getComponent(SpriteRenderer.class);
//        this.bounds = gameObject.getComponent(BoxBounds.class);
        this.camera = Window.getScene().camera;
    }

    @Override
    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    @Override
    public void update(double dt) {
        if (gameObject.getTransform().position.y < com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_1 + com.robotzero.infrastructure.constants.Window.TILE_HEIGHT && gameObject.getTransform().position.y > com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_1) {
//            die();
        }

        if (doWinAnimation) {
            if (slidingDown) {
                if (triggerSlideAnim) {
                    machine.trigger("StartSlide");
                    gameObject.getTransform().scale.x = -32;
                    gameObject.getTransform().position.x += 32;
//                    rb.acceleration.x = 0;
                    triggerSlideAnim = false;
                }
//                rb.velocity.x = 0;
//                rb.velocity.y = -150;
            } else {
                if (triggerRunAnim) {
//                    AssetPool.getSound("assets/sounds/stage_clear.ogg").play();
                    machine.trigger("StartRunning");
                    triggerRunAnim = false;
                }
                gameObject.getTransform().scale.x = 32;
//                rb.acceleration.x = 1000;
                runTime -= dt;
                if (runTime < 0) {
                    Window.getWindow().changeScene(0);
                }
            }
            return;
        }

        if (invincibilityLeft > 0) {
            if (changeColorTimeLeft < 0) {
                currentColor = (currentColor + 1) % 3;
                if (currentColor == 0) {
                    sprite.color.x = 1;
                    sprite.color.y = 0.4f;
                    sprite.color.z = 0.8f;
                } else if (currentColor == 1) {
                    sprite.color.x = 0.8f;
                    sprite.color.y = 0.4f;
                    sprite.color.z = 1;
                } else if (currentColor == 2) {
                    sprite.color.x = 0.7f;
                    sprite.color.y = 0.4f;
                    sprite.color.z = 1;
                }
                changeColorTimeLeft = changeColorTime;
            } else {
                changeColorTimeLeft -= dt;
            }
//        } else if (this.type == PlayerType.STAR) {
//            this.setState(PlayerType.BIG);
//            sprite.color.x = 1;
//            sprite.color.y = 1;
//            sprite.color.z = 1;
//            AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").play();
//            AssetPool.getSound("assets/sounds/invincible.ogg").stop();
        }

        if (immunityLeft > 0 && flashLeft < 0) {
            if (sprite.color.w < 1) {
                sprite.color.w = 1;
            } else {
                sprite.color.w = 0.5f;
            }
            flashLeft = flashTime;
        } else if (sprite.color.w < 1) {
            sprite.color.w = 1;
        }

        if (isDead) {
            if (movingUp && gameObject.getTransform().position.y < maxDeathY) {
//                rb.acceleration.y = 3000;
            } else {
//                rb.acceleration.y = -100;
                movingUp = false;
                if (deadAnimTime < 0) {
                    Window.getWindow().changeScene(0);
                }
                deadAnimTime -= dt;
            }
            return;
        }

        if (this.camera.position().x < this.gameObject.getTransform().position.x - com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_X) {
            this.camera.position().x = this.gameObject.getTransform().position.x - com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_X;
        }

        if (this.gameObject.getTransform().position.y < com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_1 && camera.position().y != com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_2) {
            Window.getWindow().setColor(com.robotzero.infrastructure.constants.Window.COLOR_BLACK);
            this.camera.position().y = com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_2;
        } else if (this.gameObject.getTransform().position.y > com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_1 && camera.position().y != com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_1) {
            Window.getWindow().setColor(com.robotzero.infrastructure.constants.Window.SKY_COLOR);
            this.camera.position().y = com.robotzero.infrastructure.constants.Window.CAMERA_OFFSET_Y_1;
        }

        if ((KeyListener.isKeyPressed(GLFW_KEY_S) || KeyListener.isKeyPressed(GLFW_KEY_DOWN))) {
            if (fireballCooldownTime < 0) {
//                GameObject fireball = Prefabs.FIREBALL();
//                if (this.gameObject.getTransform().scale.x > 0) {
//                    fireball.transform.position.x = this.gameObject.getTransform().position.x + this.gameObject.getTransform().scale.x;
//                } else {
//                    fireball.transform.position.x = this.gameObject.getTransform().position.x - fireball.transform.scale.x;
//                }
//                fireball.transform.position.y = this.gameObject.getTransform().position.y + (this.gameObject.getTransform().scale.y / 2.0f);
//                fireball.getComponent(Rigidbody.class).acceleration.x = this.gameObject.getTransform().scale.x > 0 ? fireballSpeed : -fireballSpeed;
//                fireball.getComponent(Rigidbody.class).acceleration.y = -30f;
//                Window.getScene().safeAddGameObject(fireball);
//                fireballCooldownTime = fireballCooldown;
            }
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
//            rb.acceleration.x = runSpeed;
            if (gameObject.getTransform().scale.x < 0) {
                gameObject.getTransform().scale.x *= -1;
            }
            if (onGround) {
                machine.trigger("StartRunning");
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
//            rb.acceleration.x = -runSpeed;
            if (gameObject.getTransform().scale.x > 0) {
                gameObject.getTransform().scale.x *= -1;
            }
            if (onGround) {
                machine.trigger("StartRunning");
            }
        } else if (onGround) {
//            rb.acceleration.x = 0;
            machine.trigger("StartIdle");
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE) && onGround) {
//            AssetPool.getSound("assets/sounds/jump-small.ogg").play();
            onGround = false;
//            rb.acceleration.y = jumpSpeed;
            machine.trigger("StartJumping");
        } else {
//            rb.acceleration.y = 0;
        }

        immunityLeft -= dt;
        flashLeft -= dt;
        fireballCooldownTime -= dt;
        invincibilityLeft -= dt;
    }

    public void win(boolean extraLife) {
        if (doWinAnimation) return;

//        AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stop();
//        AssetPool.getSound("assets/sounds/flagpole.ogg").play();
        if (extraLife) {
            lives++;
            System.out.println("Extra life!");
        }
        doWinAnimation = true;
    }

//    @Override
//    public void collision(Collision collision) {
//        if (doWinAnimation && collision.side == Collision.CollisionSide.BOTTOM) {
//            slidingDown = false;
//            return;
//        }
//
//        if (collision.side == Collision.CollisionSide.BOTTOM) {
//            onGround = true;
//        }
//    }
//
//    @Override
//    public String serialize(int tabSize) {
//        StringBuilder builder = new StringBuilder();
//
//        builder.append(beginObjectProperty("PlayerController", tabSize));
//        builder.append(closeObjectProperty(tabSize));
//
//        return builder.toString();
//    }

//    public void damage() {
//        if (immunityLeft > 0 || invincibilityLeft > 0) return;
//
//        if (type == PlayerType.SMALL) {
//            die();
//        } else if (type == PlayerType.BIG || type == PlayerType.FIRE) {
//            setState(PlayerType.SMALL);
//        }
//    }

//    public void die() {
//        AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stop();
//        AssetPool.getSound("assets/sounds/mario_die.ogg").play();
//        setState(PlayerType.SMALL);
//        machine.trigger("Die");
//        isDead = true;
//        maxDeathY = gameObject.getTransform().position.y + deathYJumpHeight;
//        rb.acceleration.x = 0;
//        rb.velocity.x = 0;
//        gameObject.getComponent(BoxBounds.class).setTrigger(true);
//        gameObject.zIndex = 10;
//    }
//
//    public static PlayerController deserialize() {
//        Parser.consumeEndObjectProperty();
//
//        return new PlayerController();
//    }
}
