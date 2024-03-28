package xis.xdsp.ex;

public class RecipeAltSeqException extends Exception{

    public RecipeAltSeqException() {
    }

    public RecipeAltSeqException(String message) {
        super(message);
    }

    public RecipeAltSeqException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecipeAltSeqException(Throwable cause) {
        super(cause);
    }

    public RecipeAltSeqException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
