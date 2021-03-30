package br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.veiculo.v3.VeiculoV3Service;
import br.com.zalf.prolog.webservice.frota.veiculo.v3._model.VeiculoEntity;
import br.com.zalf.prolog.webservice.v3.frota.afericao.AfericaoService;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.checklist.ChecklistService;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistEntity;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico.ChecklistOrdemServicoService;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoItemEntity;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.*;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao.MovimentacaoProcessoService;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.VeiculoMovimentacao;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu.ServicoPneuService;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.socorrorota.SocorroRotaService;
import br.com.zalf.prolog.webservice.v3.frota.socorrorota._model.AberturaSocorroRotaEntity;
import br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo.TransferenciaVeiculoService;
import br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo._model.TransferenciaVeiculoInformacaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo._model.TransferenciaVeiculoProcessoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
public final class AlteracaoKmProcessoVisitorImpl implements AlteracaoKmProcessoVisitor {
    @NotNull
    private final VeiculoV3Service veiculoService;
    @NotNull
    private final AfericaoService afericaoService;
    @NotNull
    private final ServicoPneuService servicoPneuService;
    @NotNull
    private final ChecklistService checklistService;
    @NotNull
    private final ChecklistOrdemServicoService checklistOrdemServicoService;
    @NotNull
    private final MovimentacaoProcessoService movimentacaoProcessoService;
    @NotNull
    private final SocorroRotaService socorroRotaService;
    @NotNull
    private final TransferenciaVeiculoService transferenciaVeiculoService;

    @Autowired
    public AlteracaoKmProcessoVisitorImpl(@NotNull final VeiculoV3Service veiculoService,
                                          @NotNull final AfericaoService afericaoService,
                                          @NotNull final ServicoPneuService servicoPneuService,
                                          @NotNull final ChecklistService checklistService,
                                          @NotNull final ChecklistOrdemServicoService checklistOrdemServicoService,
                                          @NotNull final MovimentacaoProcessoService movimentacaoProcessoService,
                                          @NotNull final SocorroRotaService socorroRotaService,
                                          @NotNull final TransferenciaVeiculoService transferenciaVeiculoService) {
        this.veiculoService = veiculoService;
        this.afericaoService = afericaoService;
        this.servicoPneuService = servicoPneuService;
        this.checklistService = checklistService;
        this.checklistOrdemServicoService = checklistOrdemServicoService;
        this.movimentacaoProcessoService = movimentacaoProcessoService;
        this.socorroRotaService = socorroRotaService;
        this.transferenciaVeiculoService = transferenciaVeiculoService;
    }

    @NotNull
    @Override
    public AlteracaoKmResponse visit(@NotNull final AfericaoKmProcesso afericaoKmProcesso) {

        final AfericaoEntity entity = afericaoService.getByCodigo(afericaoKmProcesso.getCodProcesso());
        final long kmAntigo = entity.getKmColetadoVeiculo();
        applyValidations(afericaoKmProcesso.getCodEmpresa(),
                         afericaoKmProcesso.getCodVeiculo(),
                         entity.getCodVeiculo());
        afericaoService.updateKmColetado(afericaoKmProcesso.getCodProcesso(), afericaoKmProcesso.getNovoKm());
        return AlteracaoKmResponse.of(kmAntigo);
    }

    @NotNull
    @Override
    public AlteracaoKmResponse visit(@NotNull final ServicoPneuKmProcesso servicoPneuKmProcesso) {
        final ServicoPneuEntity entity = servicoPneuService.getByCodigo(servicoPneuKmProcesso.getCodProcesso());
        final AfericaoEntity afericao = afericaoService.getByCodigo(entity.getCodAfericao());
        final long kmAntigo = entity.getKmColetadoVeiculoFechamentoServico();
        applyValidations(servicoPneuKmProcesso.getCodEmpresa(),
                         servicoPneuKmProcesso.getCodVeiculo(),
                         afericao.getCodVeiculo());
        servicoPneuService.updateKmColetadoFechamento(
                servicoPneuKmProcesso.getCodProcesso(),
                servicoPneuKmProcesso.getNovoKm());
        return AlteracaoKmResponse.of(kmAntigo);
    }

    @NotNull
    @Override
    public AlteracaoKmResponse visit(@NotNull final ChecklistKmProcesso checklistKmProcesso) {
        final ChecklistEntity entity = checklistService.getByCodigo(checklistKmProcesso.getCodProcesso());
        final long kmAntigo = entity.getKmColetadoVeiculo();
        applyValidations(checklistKmProcesso.getCodEmpresa(),
                         checklistKmProcesso.getCodVeiculo(),
                         entity.getCodVeiculo());
        checklistService.updateKmColetado(checklistKmProcesso.getCodProcesso(), checklistKmProcesso.getNovoKm());
        return AlteracaoKmResponse.of(kmAntigo);
    }

    @NotNull
    @Override
    public AlteracaoKmResponse visit(
            @NotNull final ChecklistOrdemServicoItemKmProcesso ordemServicoItemKmProcesso) {
        final ChecklistOrdemServicoItemEntity entity =
                checklistOrdemServicoService.getItemOrdemServicoByCodigo(ordemServicoItemKmProcesso.getCodProcesso());
        final long kmAntigo = entity.getKmColetadoVeiculoFechamentoItem();
        applyValidations(ordemServicoItemKmProcesso.getCodEmpresa(),
                         ordemServicoItemKmProcesso.getCodVeiculo(),
                         entity.getOrdemServico().getChecklist().getCodVeiculo());
        checklistOrdemServicoService.updateKmFechamentoItem(
                ordemServicoItemKmProcesso.getCodProcesso(),
                ordemServicoItemKmProcesso.getNovoKm());
        return AlteracaoKmResponse.of(kmAntigo);
    }

    @NotNull
    @Override
    public AlteracaoKmResponse visit(@NotNull final MovimentacaoKmProcesso movimentacaoKmProcesso) {
        final MovimentacaoProcessoEntity entity =
                movimentacaoProcessoService.getByCodigo(movimentacaoKmProcesso.getCodProcesso());
        final Optional<VeiculoMovimentacao> optional = entity.getVeiculo();
        if (optional.isPresent()) {
            final VeiculoMovimentacao veiculo = optional.get();
            applyValidations(movimentacaoKmProcesso.getCodEmpresa(),
                             movimentacaoKmProcesso.getCodVeiculo(),
                             veiculo.getCodVeiculo());
            final long kmAntigo = veiculo.getKmColetado();
            movimentacaoProcessoService.updateKmColetado(entity, movimentacaoKmProcesso.getNovoKm());
            return AlteracaoKmResponse.of(kmAntigo);
        } else {
            throw new IllegalStateException(String.format(
                    "O veículo %d não está presente no processo de movimentação de %d.",
                    movimentacaoKmProcesso.getCodVeiculo(),
                    movimentacaoKmProcesso.getCodProcesso()));
        }
    }

    @NotNull
    @Override
    public AlteracaoKmResponse visit(@NotNull final SocorroRotaKmProcesso socorroRotaKmProcesso) {
        final AberturaSocorroRotaEntity entity =
                socorroRotaService.getAberturaSocorroRotaByCodSocorro(socorroRotaKmProcesso.getCodProcesso());
        final long kmAntigo = entity.getKmColetadoVeiculoAberturaSocorro();
        applyValidations(socorroRotaKmProcesso.getCodEmpresa(),
                         socorroRotaKmProcesso.getCodVeiculo(),
                         entity.getCodVeiculo());
        socorroRotaService.updateKmColetadoAberturaSocorro(socorroRotaKmProcesso.getCodProcesso(),
                                                           socorroRotaKmProcesso.getNovoKm());
        return AlteracaoKmResponse.of(kmAntigo);
    }

    @NotNull
    @Override
    public AlteracaoKmResponse visit(@NotNull final TransferenciaVeiculoKmProcesso transferenciaVeiculoKmProcesso) {
        final TransferenciaVeiculoProcessoEntity entity =
                transferenciaVeiculoService.getByCodigo(transferenciaVeiculoKmProcesso.getCodProcesso());
        final Optional<TransferenciaVeiculoInformacaoEntity> informacoesTransferenciaVeiculo =
                entity.getInformacoesTransferenciaVeiculo(transferenciaVeiculoKmProcesso.getCodVeiculo());
        if (informacoesTransferenciaVeiculo.isPresent()) {
            final TransferenciaVeiculoInformacaoEntity infoVeiculo = informacoesTransferenciaVeiculo.get();
            applyValidations(transferenciaVeiculoKmProcesso.getCodEmpresa(),
                             transferenciaVeiculoKmProcesso.getCodVeiculo(),
                             infoVeiculo.getCodVeiculo());
            final long kmAntigo = infoVeiculo.getKmColetadoVeiculoMomentoTransferencia();
            transferenciaVeiculoService.updateKmColetadoMomentoTransferencia(
                    transferenciaVeiculoKmProcesso.getCodProcesso(),
                    transferenciaVeiculoKmProcesso.getCodVeiculo(),
                    transferenciaVeiculoKmProcesso.getNovoKm());
            return AlteracaoKmResponse.of(kmAntigo);
        } else {
            throw new IllegalStateException(
                    String.format("O veículo %d não está presente no processo de transferência %d.",
                                  transferenciaVeiculoKmProcesso.getCodVeiculo(),
                                  transferenciaVeiculoKmProcesso.getCodProcesso()));
        }
    }

    private void applyValidations(@NotNull final Long codEmpresaRecebido,
                                  @NotNull final Long codVeiculoRecebido,
                                  @NotNull final Long codVeiculoBanco) {
        if (!codVeiculoRecebido.equals(codVeiculoBanco)) {
            fail();
        }
        final VeiculoEntity veiculo = veiculoService.getByCodigo(codVeiculoBanco);
        // Garantindo que a empresa do veículo é a mesma recebida já garantimos que o processo editado é da empresa
        // em questão.
        if (!codEmpresaRecebido.equals(veiculo.getCodEmpresa())) {
            fail();
        }
    }

    private void fail() {
        throw new GenericException("Só é possível alterar o KM de veículos e processos da sua empresa!");
    }
}
