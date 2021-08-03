package br.com.zalf.prolog.webservice.interno.suporte;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public interface SuporteDao {
    void alteraImagemLogoEmpresa(@NotNull final Long codEmpresa,
                                 @NotNull final String urlImagem);
}
