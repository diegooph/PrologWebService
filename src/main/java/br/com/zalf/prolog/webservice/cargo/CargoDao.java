package br.com.zalf.prolog.webservice.cargo;

import br.com.zalf.prolog.webservice.cargo.model.CargoEmUso;
import br.com.zalf.prolog.webservice.cargo.model.CargoNaoUtilizado;
import br.com.zalf.prolog.webservice.cargo.model.CargoSelecao;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.permissao.pilares.PilarProlog;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface CargoDao {

    /**
     * Este método é utilizado para buscar todos os {@link CargoSelecao cargos} da Unidade selecionada.
     *
     * @param codUnidade Código da Unidade a qual os cargos serão buscados.
     * @return Uma lista com todos os {@link CargoSelecao cargos} da unidade.
     * @throws Throwable Caso ocorra qualquer na busca dos dados.
     */
    @NotNull
    List<CargoSelecao> getTodosCargosUnidade(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Busca os cargos que estão em uso na unidade informada como parâmetro. Entende-se um cargo como <b>em uso</b>,
     * caso ele tenha pelo menos 1 colaborador vinculado. Colaboradores inativos também são considerados para validar
     * um cargo como <b>em uso</b>.
     *
     * @param codUnidade Código da unidade utilizada para filtrar os cargos.
     * @return Uma lista contendo os {@link CargoSelecao cargos} que estão em uso.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    @NotNull
    List<CargoEmUso> getCargosEmUsoUnidade(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Busca os cargos que não estão senso utilizados na unidade informada como parâmetro. Entende-se um cargo como
     * <b>não utilizado</b>, caso nenhum colaborador esteja vinculado ele, estando esse colaborador ativo ou não.
     *
     * @param codUnidade Código da unidade utilizada para filtrar os cargos.
     * @return Uma lista contendo os cargos que não estão sendo utilizados.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    @NotNull
    List<CargoNaoUtilizado> getCargosNaoUtilizadosUnidade(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Busca as permissões detalhadas do prolog a partir do código da unidade.
     *
     * @param codUnidade código da unidade
     * @return Lista detalhada das permissões da {@link Unidade}
     * @throws SQLException caso não seja possível realizar a busca
     */
    List<PilarProlog> getPermissoesDetalhadasUnidade(Long codUnidade) throws SQLException;
}