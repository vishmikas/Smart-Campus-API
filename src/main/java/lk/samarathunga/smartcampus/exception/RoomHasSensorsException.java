package lk.samarathunga.smartcampus.exception;

/**
 * Thrown when a room deletion is attempted but the room still has sensors.
 * Maps to HTTP 409 Conflict.
 *
 * Author : S.D.V. Samarathunga
 * UoW ID : 2151919w
 * IIT ID : 20241769
 */
public class RoomHasSensorsException extends RuntimeException {
    public RoomHasSensorsException(String message) {
        super(message);
    }
}
