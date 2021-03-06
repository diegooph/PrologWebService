package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes;

import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.TipoAcaoAjuste;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.exibicao.*;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.historico.DescricaoAcaoAjusteCreator;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.historico.MarcacaoAjusteHistoricoExibicao;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.inconsistencias
        .DescricaoInconsistenciaCreator;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.inconsistencias.InconsistenciaFimAntesInicio;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.inconsistencias.InconsistenciaSemVinculo;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoInicioFim;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 26/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ControleJornadaAjusteConverter {

    public ControleJornadaAjusteConverter() {
        throw new IllegalStateException(ControleJornadaAjusteConverter.class.getSimpleName()
                + " cannot be instantiated!");
    }

    @NotNull
    static InconsistenciaFimAntesInicio createInconsistenciaFimAntesInicio(@NotNull final ResultSet rSet)
            throws Throwable {
        final InconsistenciaFimAntesInicio inconsistencia = new InconsistenciaFimAntesInicio();
        inconsistencia.setCodMarcacaoInicio(rSet.getLong("COD_MARCACAO_INICIO"));
        inconsistencia.setDataHoraMarcacaoInicio(rSet.getObject("DATA_HORA_MARCACAO_INICIO", LocalDateTime.class));
        inconsistencia.setCodMarcacaoFim(rSet.getLong("COD_MARCACAO_FIM"));
        inconsistencia.setDataHoraMarcacaoFim(rSet.getObject("DATA_HORA_MARCACAO_FIM", LocalDateTime.class));
        inconsistencia.setNomeColaboradorMarcacao(rSet.getString("NOME_COLABORADOR"));
        inconsistencia.setDescricaoInconsistencia(DescricaoInconsistenciaCreator.descricaoFimAntesInicio(
                inconsistencia.getDataHoraMarcacaoInicio(),
                inconsistencia.getDataHoraMarcacaoFim()));
        return inconsistencia;
    }

    @NotNull
    static InconsistenciaSemVinculo createInconsistenciaSemVinculo(@NotNull final ResultSet rSet) throws Throwable {
        final InconsistenciaSemVinculo inconsistencia = new InconsistenciaSemVinculo();
        inconsistencia.setCodMarcacaoSemVinculo(rSet.getLong("COD_MARCACAO_SEM_VINCULO"));
        inconsistencia.setTipoInicioFim(TipoInicioFim.fromString(rSet.getString("TIPO_INICIO_FIM")));
        inconsistencia.setDataHoraMarcacao(rSet.getObject("DATA_HORA_MARCACAO", LocalDateTime.class));
        inconsistencia.setNomeColaboradorMarcacao(rSet.getString("NOME_COLABORADOR"));
        inconsistencia.setDescricaoInconsistencia(DescricaoInconsistenciaCreator.descricaoSemVinculo(
                inconsistencia.getDataHoraMarcacao(),
                inconsistencia.getTipoInicioFim()));
        return inconsistencia;
    }

    @NotNull
    static MarcacaoAjusteHistoricoExibicao createHistoricoAjuste(@NotNull final ResultSet rSet) throws Throwable {
        final MarcacaoAjusteHistoricoExibicao historicoExibicao = new MarcacaoAjusteHistoricoExibicao();
        // Infos da marca????o.
        historicoExibicao.setDataHoraAntiga(rSet.getObject("DATA_HORA_ANTIGA", LocalDateTime.class));
        historicoExibicao.setDataHoraNova(rSet.getObject("DATA_HORA_NOVA", LocalDateTime.class));
        historicoExibicao.setTipoInicioFimMarcacao(TipoInicioFim.fromString(rSet.getString("TIPO_MARCACAO")));
        historicoExibicao.setTipoAcaoAjuste(TipoAcaoAjuste.fromString(rSet.getString("ACAO_AJUSTE")));

        // Infos do ajuste.
        historicoExibicao.setDataHoraAjuste(rSet.getObject("DATA_HORA_AJUSTE", LocalDateTime.class));
        historicoExibicao.setNomeColaboradorAjuste(rSet.getString("NOME_RESPONSAVEL_AJUSTE"));
        historicoExibicao.setNomeJustificativaAjuste(rSet.getString("JUSTIFICATIVA_AJUSTE"));
        historicoExibicao.setObservacaoAjuste(rSet.getString("OBSERVACAO_AJUSTE"));
        historicoExibicao.setDescricaoAcaoRealizada(DescricaoAcaoAjusteCreator.create(
                historicoExibicao.getTipoAcaoAjuste(),
                historicoExibicao.getTipoInicioFimMarcacao(),
                historicoExibicao.getDataHoraAntiga(),
                historicoExibicao.getDataHoraNova(),
                historicoExibicao.getNomeColaboradorAjuste()));
        return historicoExibicao;
    }

    @NotNull
    static List<ConsolidadoMarcacoesDia> createConsolidadoMarcacoesDia(@NotNull final ResultSet rSet) throws Throwable {
        final List<ConsolidadoMarcacoesDia> dias = new ArrayList<>();
        ConsolidadoMarcacoesDia consolidado = null;
        boolean primeiraLinha = true;
        while (rSet.next()) {
            final LocalDate dia = rSet.getObject("DIA", LocalDate.class);
            if (primeiraLinha) {
                consolidado = new ConsolidadoMarcacoesDia();
                consolidado.setDia(dia);
                consolidado.setTotalInconsistenciasDia(-1);
                consolidado.setTotalMarcacoesDia(rSet.getInt("TOTAL_MARCACOES_GERAL_DIA"));
                consolidado.setMarcacoesColaboradores(new ArrayList<>());
                consolidado.getMarcacoesColaboradores().add(createMarcacoesColaborador(rSet));
                dias.add(consolidado);
            } else {
                if (!consolidado.getDia().equals(dia)) {
                    // Cria novo dia.
                    consolidado = new ConsolidadoMarcacoesDia();
                    consolidado.setDia(dia);
                    consolidado.setTotalInconsistenciasDia(-1);
                    consolidado.setTotalMarcacoesDia(rSet.getInt("TOTAL_MARCACOES_GERAL_DIA"));
                    consolidado.setMarcacoesColaboradores(new ArrayList<>());
                    dias.add(consolidado);
                }
                consolidado.getMarcacoesColaboradores().add(createMarcacoesColaborador(rSet));
            }
            primeiraLinha = false;
        }
        return dias;
    }

    @NotNull
    static MarcacaoColaboradorAjuste createMarcacaoColaboradorAjuste(@NotNull final ResultSet rSet) throws Throwable {
        final MarcacaoColaboradorAjuste marcacoesColab = new MarcacaoColaboradorAjuste();
        marcacoesColab.setCodTipoMarcacao(rSet.getLong("COD_TIPO_MARCACAO"));
        marcacoesColab.setNomeTipoMarcacao(rSet.getString("NOME_TIPO_MARCACAO"));
        marcacoesColab.setJaFoiAjustada(
                rSet.getBoolean("FOI_AJUSTADO_INICIO")
                || rSet.getBoolean("FOI_AJUSTADO_FIM"));
        marcacoesColab.setMarcacoes(createMarcacaoAgrupadaAjusteExibicao(rSet));
        return marcacoesColab;
    }

    @NotNull
    private static MarcacaoAgrupadaAjusteExibicao createMarcacaoAgrupadaAjusteExibicao(@NotNull final ResultSet rSet)
            throws Throwable {
        MarcacaoAjusteExibicao inicio = null;
        final LocalDateTime dataHoraInicio = rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class);
        if (dataHoraInicio != null) {
            inicio = new MarcacaoAjusteExibicao();
            inicio.setAtiva(rSet.getBoolean("STATUS_ATIVO_INICIO"));
            inicio.setCodMarcacao(rSet.getLong("COD_MARCACAO_INICIO"));
            inicio.setJaFoiAjustada(rSet.getBoolean("FOI_AJUSTADO_INICIO"));
            inicio.setDataHoraMarcacao(dataHoraInicio);
            inicio.setDeviceImei(rSet.getString("DEVICE_IMEI_INICIO"));
            inicio.setDeviceReconhecido(rSet.getBoolean("DEVICE_IMEI_INICIO_RECONHECIDO"));
            inicio.setMarcaDevice(rSet.getString("DEVICE_MARCA_INICIO"));
            inicio.setModeloDevice(rSet.getString("DEVICE_MODELO_INICIO"));
        }

        MarcacaoAjusteExibicao fim = null;
        final LocalDateTime dataHoraFim = rSet.getObject("DATA_HORA_FIM", LocalDateTime.class);
        if (dataHoraFim != null) {
            fim = new MarcacaoAjusteExibicao();
            fim.setAtiva(rSet.getBoolean("STATUS_ATIVO_FIM"));
            fim.setCodMarcacao(rSet.getLong("COD_MARCACAO_FIM"));
            fim.setJaFoiAjustada(rSet.getBoolean("FOI_AJUSTADO_FIM"));
            fim.setDataHoraMarcacao(dataHoraFim);
            fim.setDeviceImei(rSet.getString("DEVICE_IMEI_FIM"));
            fim.setDeviceReconhecido(rSet.getBoolean("DEVICE_IMEI_FIM_RECONHECIDO"));
            fim.setMarcaDevice(rSet.getString("DEVICE_MARCA_FIM"));
            fim.setModeloDevice(rSet.getString("DEVICE_MODELO_FIM"));
        }

        return new MarcacaoAgrupadaAjusteExibicao(inicio, fim);
    }

    @NotNull
    private static MarcacoesDiaColaborador createMarcacoesColaborador(@NotNull final ResultSet rSet) throws Throwable {
        final MarcacoesDiaColaborador marcacoes = new MarcacoesDiaColaborador();
        marcacoes.setCodColaborador(rSet.getLong("COD_COLABORADOR"));
        marcacoes.setNomeColaborador(rSet.getString("NOME_COLABORADOR"));
        marcacoes.setQtdInconsistenciasDia(-1);
        marcacoes.setQtdMarcacoesDia(rSet.getInt("TOTAL_MARCACOES_COLABORADOR_DIA"));
        return marcacoes;
    }
}