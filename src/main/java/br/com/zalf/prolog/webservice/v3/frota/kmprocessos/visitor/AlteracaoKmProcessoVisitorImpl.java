package br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.v3.frota.afericao.AfericaoService;
import br.com.zalf.prolog.webservice.v3.frota.checklist.ChecklistService;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistEntity;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico.ChecklistOrdemServicoService;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.*;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao.MovimentacaoService;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu.ServicoPneuService;
import br.com.zalf.prolog.webservice.v3.frota.socorrorota.SocorroRotaService;
import br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo.TransferenciaVeiculoService;
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
public class AlteracaoKmProcessoVisitorImpl implements AlteracaoKmProcessoVisitor {
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
    public void visitAfericao(@NotNull final AfericaoKmProcesso afericaoKmProcesso) {

    }

    @Override
    public void visitServicoPneu(@NotNull final ServicoPneuKmProcesso afericaoKmProcesso) {

    }

    @Override
    public void visitChecklist(@NotNull final ChecklistKmProcesso checklistKmProcesso) {
        final ChecklistEntity entity = checklistService.getByCodigo(checklistKmProcesso.getCodProcesso());
        validaUnidadePertenceEmpresa(checklistKmProcesso.getCodEmpresa(), entity.getCodUnidade());
        final ChecklistEntity updateEntity = entity
                .toBuilder()
                .withCodigo(checklistKmProcesso.getCodProcesso())
                .withCodUnidade(entity.getCodUnidade())
                .withKmColetadoVeiculo(checklistKmProcesso.getNovoKm())
                .build();
        checklistService.update(updateEntity);
    }

    @Override
    public void visitChecklistOrdemServicoItem(
            @NotNull final ChecklistOrdemServicoItemKmProcesso checklistOrdemServicoItemKmProcesso) {

    }

    @Override
    public void visitMovimentacao(@NotNull final MovimentacaoKmProcesso movimentacaoKmProcesso) {

    }

    @Override
    public void visitSocorroRota(@NotNull final SocorroRotaKmProcesso socorroRotaKmProcesso) {

    }

    @Override
    public void visitTransferenciaVeiculo(
            @NotNull final TransferenciaVeiculoKmProcesso transferenciaVeiculoKmProcesso) {

    }

    private void validaUnidadePertenceEmpresa(@NotNull final Long codEmpresaProcesso,
                                              @NotNull final Long codUnidadeProcesso) {
        final List<Long> codUnidades = unidadeService
                .getUnidadesByCodEmpresa(codEmpresaProcesso)
                .stream()
                .map(UnidadeEntity::getCodigo)
                .collect(Collectors.toList());

        if (!codUnidades.contains(codUnidadeProcesso)) {
            throw new GenericException("Só é possível alterar o KM de processos da sua empresa!");
        }
    }
}
