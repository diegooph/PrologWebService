package br.com.zalf.prolog.webservice.messaging.email._model;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.response.SendEmailsResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class EmailRequestResponseHolder {
    @Nullable
    private final SendEmailsRequest request;
    @Nullable
    private final SendEmailsResponse response;

    public EmailRequestResponseHolder(@Nullable final SendEmailsRequest request,
                                      @Nullable final SendEmailsResponse response) {
        this.request = request;
        this.response = response;
    }

    @Nullable
    public SendEmailsRequest getRequest() {
        return request;
    }

    @Nullable
    public SendEmailsResponse getResponse() {
        return response;
    }

    @NotNull
    public Optional<String> getRequestAsJson() {
        if (request != null) {
            return Optional.ofNullable(GsonUtils.getGson().toJson(request));
        }

        return Optional.empty();
    }

    @NotNull
    public Optional<String> getResponseAsJson() {
        if (response != null) {
            return Optional.ofNullable(GsonUtils.getGson().toJson(response));
        }

        return Optional.empty();
    }
}
