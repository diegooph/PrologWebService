package br.com.zalf.prolog.webservice.frota.pneu.recapadoras;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 13/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface RecapadoraDao {

    /**
     * Método para inserir no Banco de Dados uma {@link Recapadora}.
     *
     * @param token      - Token para saber qual {@link Colaborador} está fazendo o cadastro.
     * @param recapadora - Obejto que será inserido no banco.
     * @return O código gerado ao inserir a recapadora.
     * @throws SQLException - Se algum erro acontecer no Banco de Dados.
     */
    @NotNull
    Long insertRecapadora(@NotNull final String token, @NotNull final Recapadora recapadora) throws SQLException;

    /**
     * Método para atualizar uma {@link Recapadora}.
     *
     * @param codEmpresa - Código da empresa que a recapadora será atualizada.
     * @param recapadora - Objeto {@link Recapadora} que será atualizado no Banco de Dados.
     */
    void atualizaRecapadoras(@NotNull final Long codEmpresa, @NotNull final Recapadora recapadora) throws SQLException;

    /**
     * Busca todas as {@link Recapadora}s de uma empresa.
     *
     * @param codEmpresa - Código da empresa para buscar as recapadoras.
     * @param ativas     - Status que será utilizado para buscar as recapadoras.
     * @return - Um {@link List<Recapadora>} contendo todas as recapadoras ativas da empresa.
     */
    List<Recapadora> getRecapadoras(@NotNull final Long codEmpresa, final Boolean ativas) throws SQLException;

    /**
     * Busca uma {@link Recapadora} através do código dela.
     *
     * @param codEmpresa    - Código da empresa para buscar as recapadoras.
     * @param codRecapadora - Código da {@link Recapadora} que será buscada.
     * @return - Uma {@link Recapadora}.
     */
    Recapadora getRecapadora(Long codEmpresa, Long codRecapadora) throws SQLException;

    /**
     * @param token       - Token para saber qual {@link Colaborador} está alterando as {@link Recapadora}s.
     * @param codEmpresa  - Código da {@link Empresa} que as {@link Recapadora}s pertencem.
     * @param recapadoras - {@link Recapadora}s que serão alteradas.
     */
    void alterarStatusRecapadoras(@NotNull final String token,
                                  @NotNull final Long codEmpresa,
                                  @NotNull final List<Recapadora> recapadoras) throws SQLException;
}