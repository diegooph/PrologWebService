package br.com.zalf.prolog.webservice.integracao.protheusrodalog.data;

import br.com.zalf.prolog.webservice.commons.util.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.*;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created on 28/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ProtheusRodalogRest {
    /**
     * Método utilizado para mapear as informações que serão utilizadas para conexão e envio da dados da aferição
     * ao sistema integrado do parceiro.
     *
     * <b>Observação 1:</b> Utilizamos a anotação 'Connection:close' para que o servidor force o fim da conexão ao
     * receber os dados.
     * Nos testes passamos por alguns problemas de conexão onde a primeira tentativa não funcionava,
     * na segunda dava certa. Era sempre necessário um segunda tentativa para funcionar. Não descobri o motivo
     * específico, mas comentários no Stack sugeriam que o cliente não estava fechando a conexão e a flag 'close'
     * resolveria o problema. De fato, resolveu!
     *
     * <b>Observação 2:</b> Inserimos, de forma forçada, a flag 'Content-Type: application/json'. Essa flag diz que o
     * conteúdo da mensagem deve ser interpretado em formato JSON.
     * Realizando os teste, ao enviar as informações o sistema não estava interpretando as informações. Ao interceptar
     * a requisição notamos que a formatação dos dados era JSON, como esperamos, porém o sistema não interpretava.
     * Adicionando a flag 'Content-Type: application/json' conseguimos resolver esse problema. A aplicação passou a
     * interpretar corretamente.
     *
     * @param tokenIntegracao Token utilizado para a autenticação no serviço do parceiro.
     * @param codUnidade      Código da Unidade do ProLog a qual os dados pertencem.
     * @param afericao        Objeto {@link AfericaoProtheusRodalog aferição} que contém os dados obtidos da medição
     *                        dos pneus de um veículo.
     * @return Objeto {@link ProtheusRodalogResponseAfericao response} contendo o
     * {@link ProtheusRodalogResponseAfericao#status} da operação, o
     * {@link ProtheusRodalogResponseAfericao#codigoAfericaoInserida} entre outras informações.
     */
    @Headers({"Connection:close", "Content-Type: application/json"})
    @POST("NEWAFERI")
    Call<ProtheusRodalogResponseAfericao> insertAfericao(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Query("codUnidade") @NotNull final Long codUnidade,
            @Body @NotNull final AfericaoProtheusRodalog afericao);

    /**
     * Método utilizado para mapear as informações que serão utilizadas para conexão e dos dados para montar o
     * cronograma de aferição.
     *
     * <b>Observação:</b> Utilizamos a anotação 'Connection:close' para que o servidor force o fim da conexão ao
     * receber os dados.
     * Nos testes passamos por alguns problemas de conexão onde a primeira tentativa não funcionava,
     * na segunda dava certa. Era sempre necessário um segunda tentativa para funcionar. Não descobri o motivo
     * específico, mas comentários no Stack sugeriam que o cliente não estava fechando a conexão e a flag 'close'
     * resolveria o problema. De fato, resolveu!
     *
     * @param tokenIntegracao Token utilizado para a autenticação no serviço do parceiro.
     * @param codUnidade      Código da Unidade do ProLog a qual os dados pertencem.
     * @return Objeto {@link CronogramaAfericaoProtheusRodalog cronograma} contendo as informações necessárias para o
     * ProLog conseguir montar a funcionalidade de Cronograma de Aferição.
     */
    @Headers("Connection:close")
    @GET("CRONOGRAMA")
    Call<CronogramaAfericaoProtheusRodalog> getCronogramaAfericao(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Query("codUnidade") @NotNull final Long codUnidade);

    /**
     * Método utilizado para mapear as informações que serão utilizadas para conexão e dos dados para montar o
     * cronograma de aferição.
     *
     * <b>Observação:</b> Utilizamos a anotação 'Connection:close' para que o servidor force o fim da conexão ao
     * receber os dados.
     * Nos testes passamos por alguns problemas de conexão onde a primeira tentativa não funcionava,
     * na segunda dava certa. Era sempre necessário um segunda tentativa para funcionar. Não descobri o motivo
     * específico, mas comentários no Stack sugeriam que o cliente não estava fechando a conexão e a flag 'close'
     * resolveria o problema. De fato, resolveu!
     *
     * @param tokenIntegracao Token utilizado para a autenticação no serviço do parceiro.
     * @param codUnidade      Código da Unidade do ProLog a qual os dados pertencem.
     * @param placa           Placa que será aferida.
     * @param tipoAfericao    {@link TipoMedicaoAfericaoProtheusRodalog Tipo de aferição} que será realziada na placa.
     * @return Objeto {@link NovaAfericaoPlacaProtheusRodalog nova aferição} que contém as informações necessárias para
     * realizar o processo de coleta de medidas dos pneus.
     */
    @Headers("Connection:close")
    @GET("NEWAFERI")
    Call<NovaAfericaoPlacaProtheusRodalog> getNovaAfericaoPlaca(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Query("codUnidade") @NotNull final Long codUnidade,
            @Query("placa") @NotNull final String placa,
            @Query("tipoAfericao") @NotNull final String tipoAfericao);
}
