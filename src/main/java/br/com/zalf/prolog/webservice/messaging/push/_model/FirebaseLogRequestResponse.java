package br.com.zalf.prolog.webservice.messaging.push._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-01-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FirebaseLogRequestResponse {
    @NotNull
    private final String tokenDestinationFirebase;
    @NotNull
    private final String userIdAssociatedWithTokenDestination;

    private final boolean success;
    @Nullable
    private final String messageId;
    @Nullable
    private final String stackTraceRequestException;

    public FirebaseLogRequestResponse(@NotNull final String tokenDestinationFirebase,
                               @NotNull final String userIdAssociatedWithTokenDestination,
                               final boolean success,
                               @Nullable final String messageId,
                               @Nullable final String stackTraceRequestException) {
        this.tokenDestinationFirebase = tokenDestinationFirebase;
        this.userIdAssociatedWithTokenDestination = userIdAssociatedWithTokenDestination;
        this.success = success;
        this.messageId = messageId;
        this.stackTraceRequestException = stackTraceRequestException;
    }

    @NotNull
    public String getTokenDestinationFirebase() {
        return tokenDestinationFirebase;
    }

    @NotNull
    public String getUserIdAssociatedWithTokenDestination() {
        return userIdAssociatedWithTokenDestination;
    }

    public boolean isSuccess() {
        return success;
    }

    @Nullable
    public String getMessageId() {
        return messageId;
    }

    @Nullable
    public String getStackTraceRequestException() {
        return stackTraceRequestException;
    }
}
