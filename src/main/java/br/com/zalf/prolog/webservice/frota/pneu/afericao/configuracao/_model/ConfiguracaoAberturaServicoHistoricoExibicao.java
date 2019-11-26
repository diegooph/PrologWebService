package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 11/25/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ConfiguracaoAberturaServicoHistoricoExibicao {
    /**
     * Nome da Unidade
     * */
    @Nullable
    private final String nomeUnidadeReferente;
    /**
     * Nome da colaborador
     * */
    @Nullable
    private final String nomeColaboradorAjuste;
    /**
     * Data e hora do ajuste
     * */
    @Nullable
    private final LocalDateTime dataHoraAlteracao;
    /**
     * Lista de {@link ConfiguracaoAberturaServico objetos} de configuração de abertura de serviço
     * */
    @Nullable
    private final ConfiguracaoAberturaServico ConfiguracaoAberturaServico;


    public ConfiguracaoAberturaServicoHistoricoExibicao(@Nullable final String nomeUnidadeReferente,
                                                        @Nullable final String nomeColaboradorAjuste,
                                                        @Nullable final LocalDateTime dataHoraAlteracao,
                                                        @Nullable final ConfiguracaoAberturaServico ConfiguracaoAberturaServico) {
        this.nomeUnidadeReferente = nomeUnidadeReferente;
        this.nomeColaboradorAjuste = nomeColaboradorAjuste;
        this.dataHoraAlteracao = dataHoraAlteracao;
        this.ConfiguracaoAberturaServico = ConfiguracaoAberturaServico;
    }

    @NotNull
    public static ConfiguracaoAberturaServicoHistoricoExibicao getDummy() {
        final ConfiguracaoAberturaServico configuracaoAberturaServico = new ConfiguracaoAberturaServico(
                1L,
                3L,
                1L,
                "Sul",
                3L,
                "Unidade Teste Zalf",
                0.1D,
                0.2D,
                11.1D,
                11.2D,
                15,
                7
        );

        return new ConfiguracaoAberturaServicoHistoricoExibicao(
                "Unidade Teste Zalf",
                "Colaborador teste",
                ProLogDateParser.toLocalDateTime("2019-01-10T09:45:00"),
                configuracaoAberturaServico
        );
    }

    public String getNomeUnidadeReferente() { return nomeUnidadeReferente; }

    public String getNomeColaboradorAjuste() { return nomeColaboradorAjuste; }

    public LocalDateTime getDataHoraAlteracao() { return dataHoraAlteracao; }

    public ConfiguracaoAberturaServico getConfiguracaoAberturaServico() { return ConfiguracaoAberturaServico; }
}