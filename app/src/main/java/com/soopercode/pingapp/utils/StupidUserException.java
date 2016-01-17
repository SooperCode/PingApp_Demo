package com.soopercode.pingapp.utils;

/**
 * Signals that the user's input does not constitute a valid URL.
 *
 * @author Ria
 */
public class StupidUserException extends Exception {

    private final String msg;

    /**
     * Constructs a {@code StupidUserException} with the specified detail message.
     * This message will typically be the user's invalid input.
     *
     * @param msg The detail message
     */
    public StupidUserException(final String msg) {
        super(msg);
        this.msg = msg;
    }

    /**
     * Constructs a {@code StupidUserException} with the specified
     * detail message and cause.
     * The cause might typically be a {@code MalformedURLException} caused
     * by the invalidity of the user input specified in the detail message.
     *
     * @param msg   The detail message
     * @param cause The cause
     */
    public StupidUserException(final String msg, final Throwable cause) {
        super(msg, cause);
        this.msg = msg;
    }

    /**
     * Provides info about this exception and why it was caused.
     *
     * @return A useful message about this exception
     */
    public String toString() {
        return String.format("StupidUserException: '%s' is not a valid URL.", msg);
    }

}
