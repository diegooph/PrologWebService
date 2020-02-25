package br.com.zalf.prolog.webservice.messaging.email._model;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class EmailRequestResponseHolder {
    @Nullable
    private final MailjetRequest request;
    @Nullable
    private final MailjetResponse response;

    public EmailRequestResponseHolder(@Nullable final MailjetRequest request,
                                      @Nullable final MailjetResponse response) {
        this.request = request;
        this.response = response;
    }

    @Nullable
    public MailjetRequest getRequest() {
        return request;
    }

    @Nullable
    public MailjetResponse getResponse() {
        return response;
    }

    @Nullable
    public String getRequestAsJsonOrNull() {
        return getRequest() != null
                ? GsonUtils.getGson().toJson(getRequest())
                : null;
    }

    @Nullable
    public String getResponseAsJsonOrNull() {
        return getResponse() != null
                ? GsonUtils.getGson().toJson(getResponse())
                : null;
    }
}
