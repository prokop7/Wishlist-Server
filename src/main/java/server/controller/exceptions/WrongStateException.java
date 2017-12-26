package server.controller.exceptions;

public class WrongStateException extends RuntimeException {

    public WrongStateException(long stateId) {
        super("State '" + stateId + "' does not exist.");
    }
}
