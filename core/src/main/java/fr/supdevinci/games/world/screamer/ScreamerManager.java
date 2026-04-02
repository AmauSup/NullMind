package fr.supdevinci.games.world.screamer;

import java.util.Objects;

/**
 * Tracks the lifecycle of a screamer event (active / inactive, remaining time).
 *
 * <p>Call {@link #tryActivate()} when a grave or book is explored for the first time.
 * Call {@link #update(float)} every game frame to advance the timer.
 * The screamer deactivates automatically after the configured display duration.</p>
 *
 * <p>Rendering is intentionally kept out of this class; it only exposes
 * {@link #isActive()} so that a renderer can decide what to draw.</p>
 */
public final class ScreamerManager {

    private final ScreamerTrigger trigger;
    private final float displayDuration;

    private boolean active;
    private float remainingTime;

    /**
     * @param trigger         decides whether a screamer fires when {@link #tryActivate()} is called
     * @param displayDuration how long (in seconds) the screamer overlay stays on screen
     * @throws IllegalArgumentException if displayDuration is not positive
     */
    public ScreamerManager(ScreamerTrigger trigger, float displayDuration) {
        if (displayDuration <= 0f) {
            throw new IllegalArgumentException("displayDuration must be > 0, got: " + displayDuration);
        }
        this.trigger = Objects.requireNonNull(trigger, "trigger");
        this.displayDuration = displayDuration;
        this.active = false;
        this.remainingTime = 0f;
    }

    /**
     * Asks the trigger whether the screamer should activate.
     * Does nothing if a screamer is already active.
     */
    public void tryActivate() {
        if (canActivateNow()) {
            activate();
        }
    }

    private boolean canActivateNow() {
        return !active && trigger.shouldFire();
    }

    private void activate() {
        active = true;
        remainingTime = displayDuration;
    }

    /**
     * Advances the screamer timer by one frame.
     *
     * @param delta elapsed time in seconds since the last frame
     */
    public void update(float delta) {
        if (!active) {
            return;
        }

        remainingTime -= delta;
        if (hasExpired()) {
            deactivate();
        }
    }

    private boolean hasExpired() {
        return remainingTime <= 0f;
    }

    private void deactivate() {
        active = false;
        remainingTime = 0f;
    }

    /**
     * Returns {@code true} while the screamer overlay should be visible.
     *
     * @return whether the screamer is currently active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns the remaining display time in seconds (0 when inactive).
     *
     * @return seconds left for the current screamer, or 0 if inactive
     */
    public float getRemainingTime() {
        return remainingTime;
    }
}
