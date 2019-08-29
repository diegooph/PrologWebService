package br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester;

import org.jetbrains.annotations.Nullable;

public interface AvacorpAvilanRequestStatus {
    @Nullable
    String getMensagem();

    boolean isSucesso();
}