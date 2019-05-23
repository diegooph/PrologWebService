package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Classe que contém as informações de uma Ordem de Serviço aberta.
 * <p>
 * Essas informações são recebidas do Sistema Globus. A O.S é aberta no sistema parceiro e estes dados são repassados
 * ao ProLog para que seja aberto uma O.S no nosso sistema também.
 * <p>
 * Created on 23/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class OrdemServicoAbertaGlobus {
    /**
     * Código de identificação da Ordem de Serviço.
     * <p>
     * Esse código é gerado no Sistema Globus e repassado ao ProLog. O ProLog irá tratar esse campo para exibir ao
     * colaborador, de forma que ele tenha a visibilidade do código da mesma Ordem de Serviço entre Globus e ProLog.
     */
    @NotNull
    private final Long codOsGlobus;
    /**
     * Código da Unidade que abriu esta Ordem de Serviço.
     */
    @NotNull
    private final Long codUnidadeItemOs;
    /**
     * Código do checklist que foi responsável pela abertura desta ordem de Serviço.
     */
    @NotNull
    private final Long codChecklistProLog;
    /**
     * Lista de {@link ItemOSAbertaGlobus itens} que compõem a O.S que está sendo aberta.
     */
    @NotNull
    private final List<ItemOSAbertaGlobus> itensOSAbertaGlobus;

    public OrdemServicoAbertaGlobus(@NotNull final Long codOsGlobus,
                                    @NotNull final Long codUnidadeItemOs,
                                    @NotNull final Long codChecklistProLog,
                                    @NotNull final List<ItemOSAbertaGlobus> itensOSAbertaGlobus) {
        this.codOsGlobus = codOsGlobus;
        this.codUnidadeItemOs = codUnidadeItemOs;
        this.codChecklistProLog = codChecklistProLog;
        this.itensOSAbertaGlobus = itensOSAbertaGlobus;
    }

    @NotNull
    public Long getCodOsGlobus() {
        return codOsGlobus;
    }

    @NotNull
    public Long getCodUnidadeItemOs() {
        return codUnidadeItemOs;
    }

    @NotNull
    public Long getCodChecklistProLog() {
        return codChecklistProLog;
    }

    @NotNull
    public List<ItemOSAbertaGlobus> getItensOSAbertaGlobus() {
        return itensOSAbertaGlobus;
    }
}
