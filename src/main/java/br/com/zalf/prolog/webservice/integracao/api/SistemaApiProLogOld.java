package br.com.zalf.prolog.webservice.integracao.api;

import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.Servico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.VeiculoServico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.filtro.VeiculoAberturaServicoFiltro;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.InfosVeiculoEditado;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleCreateDto;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created on 14/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaApiProLogOld extends Sistema {

    public SistemaApiProLogOld(@NotNull final IntegradorProLog integradorProLog,
                               @NotNull final SistemaKey sistemaKey,
                               @NotNull final RecursoIntegrado recursoIntegrado,
                               @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, recursoIntegrado, userToken);
    }

    @Override
    public void insert(
            @NotNull final VehicleCreateDto veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(veiculo.getCodUnidadeAlocado())) {
            throw new BloqueadoIntegracaoException("Para inserir ve??culos utilize o seu sistema de gest??o");
        }
        getIntegradorProLog().insert(veiculo, checklistOfflineListener);
    }

    @NotNull
    @Override
    public InfosVeiculoEditado update(
            @NotNull final Long codColaboradorResponsavelEdicao,
            @NotNull final VeiculoEdicao veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(veiculo.getCodUnidadeAlocado())) {
            throw new BloqueadoIntegracaoException(
                    "Para atualizar os dados do ve??culo utilize o seu sistema de gest??o");
        }
        return getIntegradorProLog().update(codColaboradorResponsavelEdicao, veiculo, checklistOfflineListener);
    }

    @NotNull
    @Override
    public Long insertProcessoTransferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo,
            @NotNull final DadosChecklistOfflineChangedListener dadosChecklistOfflineChangedListener) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(processoTransferenciaVeiculo.getCodUnidadeOrigem())
                && unidadeEstaComIntegracaoAtiva(processoTransferenciaVeiculo.getCodUnidadeDestino())) {
            throw new BloqueadoIntegracaoException("Para transferir ve??culos utilize o seu sistema de gest??o");
        }
        return getIntegradorProLog()
                .insertProcessoTransferenciaVeiculo(processoTransferenciaVeiculo, dadosChecklistOfflineChangedListener);
    }

    @NotNull
    @Override
    public Long insert(final @NotNull Long codigoColaboradorCadastro,
                       @NotNull final Pneu pneu,
                       @NotNull final Long codUnidade,
                       @NotNull final OrigemAcaoEnum origemCadastro) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(codUnidade)) {
            throw new BloqueadoIntegracaoException("Para inserir pneus utilize o seu sistema de gest??o");
        }
        return getIntegradorProLog().insert(codigoColaboradorCadastro, pneu, codUnidade, origemCadastro);
    }

    @NotNull
    @Override
    public List<Long> insert(final @NotNull Long codigoColaboradorCadastro,
                             @NotNull final List<Pneu> pneus) {
        // Esse m??todo ?? usado para importa????o de planilhas, ent??o n??o precisa de valida????o para Unidade nesse momento.
        throw new BloqueadoIntegracaoException("Para inserir pneus utilize o seu sistema de gest??o");
    }

    @Override
    public void update(final @NotNull Long codigoColaboradorEdicao,
                       @NotNull final Pneu pneu,
                       @NotNull final Long codUnidade,
                       @NotNull final Long codOriginalPneu) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(codUnidade)) {
            throw new BloqueadoIntegracaoException("Para atualizar os dados do pneu utilize o seu sistema de gest??o");
        }
        getIntegradorProLog().update(codigoColaboradorEdicao, pneu, codUnidade, codOriginalPneu);
    }

    @NotNull
    @Override
    public Long insertTransferencia(@NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                                    @NotNull final OffsetDateTime dataHoraSincronizacao,
                                    final boolean isTransferenciaFromVeiculo) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(pneuTransferenciaRealizacao.getCodUnidadeOrigem())
                && unidadeEstaComIntegracaoAtiva(pneuTransferenciaRealizacao.getCodUnidadeDestino())) {
            throw new BloqueadoIntegracaoException("Para transferir pneus utilize o seu sistema de gest??o");
        }
        return getIntegradorProLog()
                .insertTransferencia(pneuTransferenciaRealizacao, dataHoraSincronizacao, isTransferenciaFromVeiculo);
    }

    @NotNull
    @Override
    public VeiculoServico getVeiculoAberturaServico(@NotNull final VeiculoAberturaServicoFiltro filtro)
            throws Throwable {
        final Long codUnidadeVeiculo = getIntegradorProLog().getVeiculoByCodigo(filtro.getCodVeiculo()).getCodUnidade();
        if (unidadeEstaComIntegracaoAtiva(codUnidadeVeiculo)
                && getSistemaApiProLog().isServicoMovimentacao(filtro.getCodServico())) {
            throw new BloqueadoIntegracaoException(
                    "O fechamento de servi??o de movimenta????o n??o est?? dispon??vel.\n" +
                            "Utilize o seu sistema para movimentar os pneus.");
        }
        return getIntegradorProLog().getVeiculoAberturaServico(filtro);
    }

    @Override
    public void fechaServico(@NotNull final Long codUnidade,
                             @NotNull final OffsetDateTime dataHorafechamentoServico,
                             @NotNull final Servico servico) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(codUnidade) && servico.getTipoServico().equals(TipoServico.MOVIMENTACAO)) {
            throw new BloqueadoIntegracaoException(
                    "O fechamento de servi??o de movimenta????o est?? sendo integrado e ainda n??o est?? dispon??vel.\n" +
                            "Por enquanto, utilize o seu sistema para movimentar os pneus.");
        }
        getIntegradorProLog().fechaServico(codUnidade, dataHorafechamentoServico, servico);
    }

    @NotNull
    public Long insertAfericao(@NotNull final Long codUnidade,
                               @NotNull final Afericao afericao,
                               final boolean deveAbrirServico) throws Throwable {
        final boolean abrirServico;
        if (unidadeEstaComIntegracaoAtiva(codUnidade)) {
            // Se a unidade possui a integra????o ativada, precisamos saber se existe algo configurado para abertura
            // de servi??os de pneus nesta unidade.
            abrirServico = configUnidadeDeveAbrirServicoPneu(codUnidade);
        } else {
            // Se a unidade n??o tem integra????o ativada, ent??o n??o nos interessa qualquer outra coisa, devemos abrir
            // servi??os de pneus.
            abrirServico = true;
        }
        return getIntegradorProLog().insertAfericao(codUnidade, afericao, abrirServico);
    }

    private boolean unidadeEstaComIntegracaoAtiva(@NotNull final Long codUnidade) throws Throwable {
        // Caso o c??digo da unidade est?? contido na lista de unidades bloqueadas, significa que a unidade
        // N??O EST?? integrada.
        return !getIntegradorProLog()
                .getCodUnidadesIntegracaoBloqueada(getUserToken(), getSistemaKey(), getRecursoIntegrado())
                .contains(codUnidade);
    }

    private boolean configUnidadeDeveAbrirServicoPneu(@NotNull final Long codUnidade) throws Throwable {
        return getIntegradorProLog().getConfigAberturaServicoPneuIntegracao(codUnidade);
    }

    @NotNull
    private SistemaApiProLogDao getSistemaApiProLog() {
        return new SistemaApiProLogDaoImpl();
    }
}
