package br.com.zalf.prolog.webservice.messaging.send.task;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-01-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class FirebaseLogRequestResponseHolder {
    private final int successCount;
    private final int failureCount;
    @NotNull
    private final List<FirebaseLogRequestResponse> requestResponses;

    FirebaseLogRequestResponseHolder(final int successCount,
                                     final int failureCount,
                                     @NotNull final List<FirebaseLogRequestResponse> requestResponses) {
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.requestResponses = requestResponses;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    @NotNull
    public List<FirebaseLogRequestResponse> getRequestResponses() {
        return requestResponses;
    }
}
