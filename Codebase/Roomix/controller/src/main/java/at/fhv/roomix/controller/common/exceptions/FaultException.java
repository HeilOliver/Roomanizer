package at.fhv.roomix.controller.common.exceptions;

/**
 * Roomix
 * at.fhv.roomix.controller.exeption
 * FaultException
 * 27/03/2018 OliverH
 * <p>
 * Enter Description here
 */
public class FaultException extends Exception {
    public FaultException(String message, Throwable cause) {
        super(message, cause);
    }

    public FaultException(Throwable cause) {
        super(cause);
    }

    protected FaultException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FaultException() {
        super();
    }

    public FaultException(String message) {
        super(message);
    }
}
