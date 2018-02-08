package br.com.zalf.prolog.webservice.dashboard.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 23/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * <p>
 * Todas as classes que herdam de <code>{@link DashboardComponent}</code> devem implementam
 * o padrão de projeto <br>Builder</br>. Para facilitar essa implementção foi criada a interface
 * <code>{@link DashboardComponentBuilder}</code> que contém a assinatura dos métodos genéricos que
 * todos os <br>Componentes</br> devem implementar.
 */
public interface DashboardComponentBuilder {

    DashboardComponentBuilder withCodigo(@NotNull Integer codigo);

    DashboardComponentBuilder withTitulo(@NotNull String titulo);

    DashboardComponentBuilder withSubtitulo(@Nullable String subtitulo);

    DashboardComponentBuilder withDescricao(@NotNull String descricao);

    DashboardComponentBuilder withUrlEndpointDados(@NotNull String urlEndpointDados);

    DashboardComponentBuilder withCodTipoComponente(@NotNull Integer codTipoComponente);

    DashboardComponentBuilder withQtdBlocosHorizontais(int qtdBlocosHorizontais);

    DashboardComponentBuilder withQtdBlocosVerticais(int qtdBlocosVerticais);

    DashboardComponentBuilder withOrdemExibicao(int ordemExibicao);

    DashboardComponent build();
}