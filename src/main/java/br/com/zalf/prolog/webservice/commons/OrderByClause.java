package br.com.zalf.prolog.webservice.commons;

import com.google.common.base.CaseFormat;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class OrderByClause {
    @NotNull
    private final String propertyName;
    @NotNull
    private final OrderBySortDirection sortDirection;

    public OrderByClause(@NotNull final String propertyName, @NotNull final OrderBySortDirection sortDirection) {
        this.propertyName = propertyName;
        this.sortDirection = sortDirection;
    }

    @NotNull
    public String getPropertyName() {
        return propertyName;
    }

    @NotNull
    public OrderBySortDirection getSortDirection() {
        return sortDirection;
    }

    @NotNull
    public String toSqlString() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, propertyName).concat(" ").concat(sortDirection.getSqlName());
    }
}