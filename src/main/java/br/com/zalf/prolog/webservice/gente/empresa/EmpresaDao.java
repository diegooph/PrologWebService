package br.com.zalf.prolog.webservice.gente.empresa;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Equipe;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Setor;
import br.com.zalf.prolog.webservice.gente.controlejornada.DadosIntervaloChangedListener;
import br.com.zalf.prolog.webservice.permissao.Visao;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilar;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.NoContentException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface EmpresaDao {

    Equipe getEquipe(Long codUnidade, Long codEquipe) throws SQLException;

    AbstractResponse insertEquipe(@NotNull Long codUnidade, @NotNull Equipe equipe) throws SQLException;

    boolean updateEquipe(@NotNull Long codUnidade, @NotNull Long codEquipe, @NotNull Equipe equipe) throws SQLException;

    AbstractResponse insertSetor(@NotNull Long codUnidade, @NotNull Setor setor) throws SQLException;

    Setor getSetor(Long codUnidade, Long codSetor) throws SQLException;

    boolean updateSetor(@NotNull Long codUnidade, @NotNull Long codSetor, @NotNull Setor setor) throws SQLException;

    List<Equipe> getEquipesByCodUnidade(Long codUnidade) throws SQLException;

    List<HolderMapaTracking> getResumoAtualizacaoDados(int ano, int mes, Long codUnidade) throws SQLException, NoContentException;

    List<Setor> getSetorByCodUnidade(Long codUnidade) throws SQLException;

    Cargo getCargo(Long codEmpresa, Long codCargo) throws SQLException;

    List<Empresa> getFiltros(Long cpf) throws SQLException;

    Visao getVisaoCargo(Long codUnidade, Long codCargo) throws SQLException;

    Visao getVisaoUnidade(Long codUnidade) throws SQLException;

    void alterarVisaoCargo(
            @NotNull final Long codUnidade,
            @NotNull final Long codCargo,
            @NotNull final Visao visao,
            @NotNull final DadosIntervaloChangedListener intervaloListener,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    Long getCodEquipeByCodUnidadeByNome(Long codUnidade, String nomeEquipe) throws SQLException;

    Long insertFuncao(Cargo cargo, Long codUnidade) throws SQLException;
    
    List<Pilar> createPilares(ResultSet rSet) throws SQLException;

    @NotNull
    Long getCodEmpresaByCodUnidade(@NotNull final Long codUnidade) throws Throwable;
}