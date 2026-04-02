package fr.supdevinci.games.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import fr.supdevinci.games.logic.MovementIntent;

/**
 * Translates keyboard polling into gameplay-friendly commands.
 *
 * <p>Movement keys: arrow keys or WASD. Interaction key: E.</p>
 */
public final class GameInputController {
    /**
     * Reads the current keyboard state and converts it into a movement intent.
     *
     * @return immutable movement intent for the current frame
     */
    public MovementIntent readMovement() {
        return new MovementIntent(
            readHorizontalAxis(),
            readVerticalAxis()
        );
    }

    /**
     * Computes horizontal movement from keyboard state.
     *
     * @return horizontal axis value in [-1, 1]
     */
    private float readHorizontalAxis() {
        return axisValue(
            isPressed(Input.Keys.LEFT, Input.Keys.A),
            isPressed(Input.Keys.RIGHT, Input.Keys.D)
        );
    }

    /**
     * Computes vertical movement from keyboard state.
     *
     * @return vertical axis value in [-1, 1]
     */
    private float readVerticalAxis() {
        return axisValue(
            isPressed(Input.Keys.DOWN, Input.Keys.S),
            isPressed(Input.Keys.UP, Input.Keys.W)
        );
    }

    /**
     * Converts pressed-state booleans to a signed axis value.
     *
     * @param negativePressed whether the negative direction key is pressed
     * @param positivePressed whether the positive direction key is pressed
     * @return computed axis value in [-1, 1]
     */
    private float axisValue(boolean negativePressed, boolean positivePressed) {
        return (positivePressed ? 1f : 0f) - (negativePressed ? 1f : 0f);
    }

    /**
     * Returns {@code true} on the frame the player presses E.
     *
     * @return whether the interact key was just pressed
     */
    public boolean isInteractPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.E);
    }

    /**
     * Checks whether either key of a binding is currently pressed.
     *
     * @param primaryKey primary key code
     * @param secondaryKey secondary key code
     * @return {@code true} if at least one key is pressed
     */
    private boolean isPressed(int primaryKey, int secondaryKey) {
        return Gdx.input.isKeyPressed(primaryKey) || Gdx.input.isKeyPressed(secondaryKey);
    }
}
