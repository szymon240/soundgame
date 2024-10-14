package pl.soundgame.engine.gameobjects

import android.util.Log

/**
 * Hitbox class represents a rectangle in which GameObject should put it's sprite. Hitbox is used to determine if click action occured on given GameObject
 *
 * @constructor
 * Initializes all the parameters of rectangle
 *
 * @param pX starting x coordinate in World Space
 * @param pY starting y coordinate in World Space
 * @param pWidth distance in World Space from pX to the maximum X
 * @param pHeight distance in World Space from pY to the maximum Y
 *
 * @see GameObject
 *
 * @author Adam Czyżak
 */

class Hitbox(var x: Float, var y: Float, var width: Float, var height: Float) {

    /**
     * Checks if the given world space coordinates are within the hitbox.
     *
     * @param clickX The x-coordinate of the click in world space.
     * @param clickY The y-coordinate of the click in world space.
     * @return True if the click is inside the hitbox, false otherwise.
     */
    fun isClicked(clickX: Float, clickY: Float): Boolean {
        return (clickX >= x && clickX <= x + width) && (clickY <= y && clickY >= y - height)
    }

    fun logInfo(id: String) {
        Log.i("Hitbox Info", "GameObject $id -> Hitbox: X=$x, Y=$y, Width=$width, Height=$height")
    }

    /**
     * Updates the position of the hitbox.
     */
    fun updatePosition(newX: Float, newY: Float) {
        this.x = newX
        this.y = newY
    }

    /**
     * Updates the size of the hitbox.
     */
    fun updateSize(newWidth: Float, newHeight: Float) {
        this.width = newWidth
        this.height = newHeight
    }
}