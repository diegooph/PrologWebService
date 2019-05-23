package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos;

import org.jetbrains.annotations.NotNull;

/**
 * Classe que contém as informações de um Item de Ordem de Serviço Aberta.
 * <p>
 * Created on 02/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ItemOSAbertaGlobus {
    /**
     * Código de identificação do item.
     * <p>
     * Esse código é gerado no Sistema Globus e repassado ao ProLog. O ProLog irá salvar este código e utilizar como
     * identificador para o fechamento de Itens de Ordem de Serviço, posteriormente.
     * <p>
     * Deve ser o mesmo código presente no {@link ItemResolvidoGlobus#codItemResolvidoGlobus}.
     */
    @NotNull
    private final Long codItemGlobus;
    /**
     * Código de identificação da Pergunta NOK que originou o item.
     * <p>
     * Este código deve ser o mesmo presente no {@link PerguntaNokGlobus#codPerguntaNok}.
     */
    @NotNull
    private final Long codPerguntaItemOs;
    /**
     * Código de identificação da Alternativa marcada que descreve este item.
     * <p>
     * Este código deve ser o mesmo presente no {@link AlternativaNokGlobus#codAlternativaNok}.
     */
    @NotNull
    private final Long codAlternativaItemOs;

    public ItemOSAbertaGlobus(@NotNull final Long codItemGlobus,
                              @NotNull final Long codPerguntaItemOs,
                              @NotNull final Long codAlternativaItemOs) {
        this.codItemGlobus = codItemGlobus;
        this.codPerguntaItemOs = codPerguntaItemOs;
        this.codAlternativaItemOs = codAlternativaItemOs;
    }

    @NotNull
    public static ItemOSAbertaGlobus getDummy() {
        return new ItemOSAbertaGlobus(
                100L,
                13873L,
                1010L);
    }

    @NotNull
    public Long getCodItemGlobus() {
        return codItemGlobus;
    }

    @NotNull
    public Long getCodPerguntaItemOs() {
        return codPerguntaItemOs;
    }

    @NotNull
    public Long getCodAlternativaItemOs() {
        return codAlternativaItemOs;
    }
}
