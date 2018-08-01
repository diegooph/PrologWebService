package br.com.zalf.prolog.webservice.commons;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum  OrderBySortDirection {
    ASC("ASC", '+'),
    DESC("DESC", '-');

    @NotNull
    private final String sqlName;
    @NotNull
    private final char identifier;

    OrderBySortDirection(@NotNull final String sqlName, final char identifier) {
        this.sqlName = sqlName;
        this.identifier = identifier;
    }

    @NotNull
    public String getSqlName() {
        return sqlName;
    }

    public char getIdentifier() {
        return identifier;
    }

    @NotNull
    public String getIdentifierAsString() {
        return String.valueOf(identifier);
    }
}