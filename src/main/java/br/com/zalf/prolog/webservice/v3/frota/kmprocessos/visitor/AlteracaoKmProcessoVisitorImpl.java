package br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.v3.frota.afericao.AfericaoService;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.checklist.ChecklistService;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistEntity;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico.ChecklistOrdemServicoService;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoItemEntity;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.*;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao.MovimentacaoService;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu.ServicoPneuService;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.socorrorota.SocorroRotaService;
import br.com.zalf.prolog.webservice.v3.frota.socorrorota._model.AberturaSocorroRotaEntity;
import br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo.TransferenciaVeiculoService;
import br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo._model.TransferenciaVeiculoProcessoEntity;
import br.com.zalf.prolog.webservice.v3.geral.unidade.UnidadeService;
import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
public final class AlteracaoKmProcessoVisitorImpl implements AlteracaoKmProcessoVisitor {
    @NotNull
    private final UnidadeService unidadeService;
    @NotNull
    private final AfericaoService afericaoService;
    @NotNull
    private final ServicoPneuService servicoPneuService;
    @NotNull
    private final ChecklistService checklistService;
    @NotNull
    private final ChecklistOrdemServicoService checklistOrdemServicoService;
    @NotNull
    private final MovimentacaoService movimentacaoService;
    @NotNull
    private final SocorroRotaService socorroRotaService;
    @NotNull
    private final TransferenciaVeiculoService transferenciaVeiculoService;

    @Autowired
    public AlteracaoKmProcessoVisitorImpl(@NotNull final UnidadeService unidadeService,
                                          @NotNull final AfericaoService afericaoService,
                                          @NotNull final ServicoPneuService servicoPneuService,
                                          @NotNull final ChecklistService checklistService,
                                          @NotNull final ChecklistOrdemServicoService checklistOrdemServicoService,
                                          @NotNull final MovimentacaoService movimentacaoService,
                                          @NotNull final SocorroRotaService socorroRotaService,
                                          @NotNull final TransferenciaVeiculoService transferenciaVeiculoService) {
        this.unidadeService = unidadeService;
        this.afericaoService = afericaoService;
        this.servicoPneuService = servicoPneuService;
        this.checklistService = checklistService;
        this.checklistOrdemServicoService = checklistOrdemServicoService;
        this.movimentacaoService = movimentacaoService;
        this.socorroRotaService = socorroRotaService;
        this.transferenciaVeiculoService = transferenciaVeiculoService;
    }

    @Override
    public void visit(@NotNull final AfericaoKmProcesso afericaoKmProcesso) {
        final AfericaoEntity entity = afericaoService.getByCodigo(afericaoKmProcesso.getCodProcesso());
        validaUnidadePertenceEmpresa(afericaoKmProcesso.getCodEmpresa(), entity.getCodUnidade());
        final AfericaoEntity updateEntity = entity
                .toBuilder()
                .withKmColetadoVeiculo(afericaoKmProcesso.getNovoKm())
                .build();
        afericaoService.update(updateEntity);
    }

    @Override
    public void visit(@NotNull final ServicoPneuKmProcesso servicoPneuKmProcesso) {
        final ServicoPneuEntity entity = servicoPneuService.getByCodigo(servicoPneuKmProcesso.getCodProcesso());
        validaUnidadePertenceEmpresa(servicoPneuKmProcesso.getCodEmpresa(), entity.getCodUnidade());
        final ServicoPneuEntity updateEntity = entity
                .toBuilder()
                .withKmColetadoVeiculoFechamentoServico(servicoPneuKmProcesso.getNovoKm())
                .build();
        servicoPneuService.update(updateEntity);
    }

    @Override
    public void visit(@NotNull final ChecklistKmProcesso checklistKmProcesso) {
        final ChecklistEntity entity = checklistService.getByCodigo(checklistKmProcesso.getCodProcesso());
        validaUnidadePertenceEmpresa(checklistKmProcesso.getCodEmpresa(), entity.getCodUnidade());
        final ChecklistEntity updateEntity = entity
                .toBuilder()
                .withKmColetadoVeiculo(checklistKmProcesso.getNovoKm())
                .build();
        checklistService.update(updateEntity);
    }

    @Override
    public void visit(@NotNull final ChecklistOrdemServicoItemKmProcesso checklistOrdemServicoItemKmProcesso) {
        final ChecklistOrdemServicoItemEntity entity =
                checklistOrdemServicoService.getByCodigo(checklistOrdemServicoItemKmProcesso.getCodProcesso());
        validaUnidadePertenceEmpresa(checklistOrdemServicoItemKmProcesso.getCodEmpresa(), entity.getCodUnidade());
        final ChecklistOrdemServicoItemEntity updateEntity = entity
                .toBuilder()
                .withKmColetadoVeiculoFechamentoItem(checklistOrdemServicoItemKmProcesso.getNovoKm())
                .build();
        checklistOrdemServicoService.update(updateEntity);
    }

    @Override
    public void visit(@NotNull final MovimentacaoKmProcesso movimentacaoKmProcesso) {
        final MovimentacaoEntity entity = movimentacaoService.getByCodigo(movimentacaoKmProcesso.getCodProcesso());
        validaUnidadePertenceEmpresa(movimentacaoKmProcesso.getCodEmpresa(), entity.getCodUnidade());
        final MovimentacaoEntity updateEntity = entity
                .toBuilder()
                // TODO: criar origem e destino.
                .withMovimentacaoOrigem(null)
                .withMovimentacaoDestino(null)
                .build();
        movimentacaoService.update(updateEntity);
    }

    @Override
    public void visit(@NotNull final SocorroRotaKmProcesso socorroRotaKmProcesso) {
        final AberturaSocorroRotaEntity entity =
                socorroRotaService.getAberturaSocorroRotaByCodSocorro(socorroRotaKmProcesso.getCodProcesso());
        validaEmpresasIguais(socorroRotaKmProcesso.getCodEmpresa(), entity.getCodEmpresa());
        final AberturaSocorroRotaEntity updateEntity = entity
                .toBuilder()
                .withKmColetadoVeiculoAberturaSocorro(socorroRotaKmProcesso.getNovoKm())
                .build();
        socorroRotaService.update(updateEntity);
    }

    @Override
    public void visit(@NotNull final TransferenciaVeiculoKmProcesso transferenciaVeiculoKmProcesso) {
        final TransferenciaVeiculoProcessoEntity entity =
                transferenciaVeiculoService.getByCodigo(transferenciaVeiculoKmProcesso.getCodProcesso());
        validaAlgumaUnidadePertenceEmpresa(
                transferenciaVeiculoKmProcesso.getCodEmpresa(),
                List.of(entity.getCodUnidadeColaborador(),
                        entity.getCodUnidadeOrigem(),
                        entity.getCodUnidadeDestino()));
        final TransferenciaVeiculoProcessoEntity updateEntity = entity
                .toBuilder()
                // TODO: criar transferência informações.
                .withTransferenciaVeiculoInformacoes(null)
                .build();
        transferenciaVeiculoService.update(updateEntity);
    }

    private void validaEmpresasIguais(@NotNull final Long codEmpresaRecebido,
                                      @NotNull final Long codEmpresaBanco) {
        if (!codEmpresaRecebido.equals(codEmpresaBanco)) {
            fail();
        }
    }

    private void validaUnidadePertenceEmpresa(@NotNull final Long codEmpresaProcesso,
                                              @NotNull final Long codUnidadeProcesso) {
        final List<Long> codUnidades = getCodUnidadesByCodEmpresa(codEmpresaProcesso);
        if (!codUnidades.contains(codUnidadeProcesso)) {
            fail();
        }
    }

    private void validaAlgumaUnidadePertenceEmpresa(@NotNull final Long codEmpresaProcesso,
                                                    @NotNull final List<Long> codUnidadesProcesso) {
        final List<Long> codUnidadesEmpresa = getCodUnidadesByCodEmpresa(codEmpresaProcesso);
        if (codUnidadesEmpresa.stream().noneMatch(codUnidadesProcesso::contains)) {
            fail();
        }
    }

    @NotNull
    private List<Long> getCodUnidadesByCodEmpresa(@NotNull final Long codEmpresaProcesso) {
        return unidadeService
                .getUnidadesByCodEmpresa(codEmpresaProcesso)
                .stream()
                .map(UnidadeEntity::getCodigo)
                .collect(Collectors.toList());
    }

    private void fail() {
        throw new GenericException("Só é possível alterar o KM de processos da sua empresa!");
    }
}
