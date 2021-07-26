package br.com.zalf.prolog.webservice.seguranca.relato;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.seguranca.relato.model.Relato;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Contém métodos para manipular os Relatos
 */
public interface RelatoDao {

	boolean insert(@NotNull final Relato relato,
				   @Nullable final Integer versaoApp) throws SQLException;

	boolean delete(Long codRelato) throws SQLException ;

	Relato getByCod(@NotNull final Long codRelato, @NotNull final String userToken) throws SQLException;

	List<Relato> getAll(Long codUnidade, int limit, long offset, double latitude, double longitude, boolean isOrderByDate, String status) throws SQLException;

	@NotNull
	List<Relato> getRealizadosByColaborador(@NotNull final Long codColaborador,
											final int limit,
											final long offset,
											final double latitude,
											final double longitude,
											final boolean isOrderByDate,
											@NotNull final String status,
											@NotNull final String campoFiltro) throws SQLException;

	@NotNull
	List<Relato> getAllExcetoColaborador(@NotNull final Long codColaborador,
										 final int limit,
										 final long offset,
										 final double latitude,
										 final double longitude,
										 final boolean isOrderByDate,
										 @NotNull final String status) throws SQLException;

	List<Relato> getAllByUnidade(LocalDate dataInicial, LocalDate dataFinal, String equipe,
								 Long codUnidade,long limit, long offset, String status) throws SQLException;

	boolean classificaRelato(Relato relato) throws SQLException;

	boolean fechaRelato(Relato relato) throws SQLException;

	@NotNull
	List<Alternativa> getAlternativas(@NotNull final Long codUnidade, @NotNull final Long codSetor) throws SQLException;
}