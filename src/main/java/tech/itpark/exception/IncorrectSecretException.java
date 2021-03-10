package tech.itpark.exception;

public class IncorrectSecretException extends RuntimeException {
    public IncorrectSecretException() {
    }

    public IncorrectSecretException(String message) {
        super(message);
    }

    public IncorrectSecretException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectSecretException(Throwable cause) {
        super(cause);
    }

    public IncorrectSecretException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
