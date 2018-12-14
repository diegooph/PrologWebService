package br.com.zalf.prolog.webservice.integracao.logger;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 13/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class LogRequisicao {
    @Nullable
    private String classResource;
    @Nullable
    private String methodResource;
    @Nullable
    private String httpMethod;
    @Nullable
    private String urlAcesso;
    @Nullable
    private String headers;
    @Nullable
    private String pathParamns;
    @Nullable
    private String queryParamns;
    @Nullable
    private String bodyRequest;
    private LocalDateTime dataHoraRequisicao;

    public LogRequisicao() {

    }

    @Nullable
    public String getClassResource() {
        return classResource;
    }

    public void setClassResource(@Nullable final String classResource) {
        this.classResource = classResource;
    }

    @Nullable
    public String getMethodResource() {
        return methodResource;
    }

    public void setMethodResource(@Nullable final String methodResource) {
        this.methodResource = methodResource;
    }

    @Nullable
    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(@Nullable final String httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Nullable
    public String getUrlAcesso() {
        return urlAcesso;
    }

    public void setUrlAcesso(@Nullable final String urlAcesso) {
        this.urlAcesso = urlAcesso;
    }

    @Nullable
    public String getHeaders() {
        return headers;
    }

    public void setHeaders(@Nullable final String headers) {
        this.headers = headers;
    }

    @Nullable
    public String getPathParamns() {
        return pathParamns;
    }

    public void setPathParamns(@Nullable final String pathParamns) {
        this.pathParamns = pathParamns;
    }

    @Nullable
    public String getQueryParamns() {
        return queryParamns;
    }

    public void setQueryParamns(@Nullable final String queryParamns) {
        this.queryParamns = queryParamns;
    }

    @Nullable
    public String getBodyRequest() {
        return bodyRequest;
    }

    public void setBodyRequest(@Nullable final String bodyRequest) {
        this.bodyRequest = bodyRequest;
    }

    public LocalDateTime getDataHoraRequisicao() {
        return dataHoraRequisicao;
    }

    public void setDataHoraRequisicao(final LocalDateTime dataHoraRequisicao) {
        this.dataHoraRequisicao = dataHoraRequisicao;
    }

    public boolean isEmpty() {
        return (classResource == null || classResource.isEmpty())
                && (methodResource == null || methodResource.isEmpty())
                && (httpMethod == null || httpMethod.isEmpty())
                && (urlAcesso == null || urlAcesso.isEmpty())
                && (headers == null || headers.isEmpty())
                && (pathParamns == null || pathParamns.isEmpty())
                && (queryParamns == null || queryParamns.isEmpty())
                && (bodyRequest == null || bodyRequest.isEmpty());
    }
}
