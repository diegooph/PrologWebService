package br.com.zalf.prolog.webservice.v3.fleet.movimentacao;

import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoProcessoEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface MovimentacaoProcessoDao extends JpaRepository<MovimentacaoProcessoEntity, Long> {
    @NotNull
    @Query("select distinct mpe from MovimentacaoProcessoEntity mpe "
                   + "join fetch mpe.colaboradorRealizacaoProcesso crp "
                   + "join fetch crp.unidade u "
                   + "join fetch mpe.movimentacoes m "
                   + "join fetch m.movimentacaoOrigem mo "
                   + "join fetch m.movimentacaoDestino md "
                   + "join fetch m.pneu "
                   + "left join fetch m.servicosRealizados sr "
                   + "left join fetch sr.tipoServico "
                   + "left join fetch mo.veiculo "
                   + "left join fetch md.veiculo "
                   + "left join fetch md.recapadora r "
                   + "left join fetch r.colaboradorCadastro "
                   + "left join fetch r.colaboradorAlteracaoStatus "
                   + "where mpe.codUnidade in :codUnidades "
                   + "and tz_date(mpe.dataHoraRealizacao, u.timezone) between :dataInicial and :dataFinal "
                   + "and (:codColaborador is null or crp.codigo = :codColaborador) "
                   + "and (:codVeiculo is null or mo.veiculo.codigo = :codVeiculo or md.veiculo.codigo = :codVeiculo) "
                   + "and (:codPneu is null or m.pneu.codigo = :codPneu) "
                   + "order by mpe.codigo, m.codigo")
    List<MovimentacaoProcessoEntity> getListagemMovimentacoes(@NotNull final List<Long> codUnidades,
                                                              @NotNull final LocalDate dataInicial,
                                                              @NotNull final LocalDate dataFinal,
                                                              @Nullable final Long codColaborador,
                                                              @Nullable final Long codVeiculo,
                                                              @Nullable final Long codPneu,
                                                              @NotNull final Pageable pageable);
}
