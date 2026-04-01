package fr.supdevinci.games.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import fr.supdevinci.games.logic.MovementIntent;

/**
 * Translates keyboard polling into gameplay-friendly commands.
 */
public final class GameInputController {
    /**
     * Reads the current keyboard state and converts it into a movement intent.
     *
     * @return immutable movement intent for the current frame
     */
    public MovementIntent readMovement() {
        float horizontal = 0f;
        float vertical = 0f;

        if (isPressed(Input.Keys.LEFT, Input.Keys.A)) {
            horizontal -= 1f;
        }
        if (isPressed(Input.Keys.RIGHT, Input.Keys.D)) {
            horizontal += 1f;
        }
        if (isPressed(Input.Keys.UP, Input.Keys.W)) {
            vertical += 1f;
        }
        if (isPressed(Input.Keys.DOWN, Input.Keys.S)) {
            vertical -= 1f;
        }

        return new MovementIntent(horizontal, vertical);
    }

    private boolean isPressed(int primaryKey, int secondaryKey) {
        return Gdx.input.isKeyPressed(primaryKey) || Gdx.input.isKeyPressed(secondaryKey);
    }
}
