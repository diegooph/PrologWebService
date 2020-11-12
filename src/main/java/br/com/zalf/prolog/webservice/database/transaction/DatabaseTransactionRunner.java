package br.com.zalf.prolog.webservice.database.transaction;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Created on 2020-11-12
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@FunctionalInterface
public interface DatabaseTransactionRunner<T> {
    T run(@NotNull final Connection connection);
}
