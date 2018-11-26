package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Esta é a classe utilizada para mostrar a listagem de Ordens de Serviço em Abertas.
 *
 * Created on 22/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class OrdemServicoAbertaListagem extends OrdemServicoListagem {
    static final String TIPO_SERIALIZACAO = "ORDEM_SERVICO_ABERTA";

    public OrdemServicoAbertaListagem() {
        super(TIPO_SERIALIZACAO);
    }

    @NotNull
    public static OrdemServicoAbertaListagem createDummy() {
        final OrdemServicoAbertaListagem ordem = new OrdemServicoAbertaListagem();
        ordem.setCodOrdemServico(1L);
        ordem.setCodUnidadeOrdemServico(5L);
        ordem.setPlacaVeiculo("AAA1234");
        ordem.setDataHoraAbertura(LocalDateTime.now());
        ordem.setQtdItensPendentes(10);
        ordem.setQtdItensPendentes(3);
        return ordem;
    }
}