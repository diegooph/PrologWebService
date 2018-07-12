package br.com.zalf.prolog.webservice.commons;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.OrderBySortDirection.*;

/**
 * Created on 10/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class OrderByCreator {

    private OrderByCreator() {
        throw new IllegalStateException(OrderByCreator.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static List<OrderByClause> createFrom(@NotNull final List<String> items) {
        final List<OrderByClause> orderBy = new ArrayList<>();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < items.size(); i++) {
            final String sortItem = items.get(i);
            validateSortItem(sortItem);
            final OrderByClause orderByClause;
            if (sortItem.contains(ASC.getIdentifierAsString())) {
                orderByClause = new OrderByClause(sortItem.substring(1), ASC);
            } else if (sortItem.contains(DESC.getIdentifierAsString())) {
                orderByClause = new OrderByClause(sortItem.substring(1), DESC);
            } else {
                // Default ASC.
                orderByClause = new OrderByClause(sortItem, ASC);
            }
            orderBy.add(orderByClause);
        }
        return orderBy;
    }

    private static void validateSortItem(@NotNull final String sortItem) {
        final long totalSortDirectionIdentifiers = sortItem
                .chars()
                .filter(c -> c == ASC.getIdentifier() || c == DESC.getIdentifier())
                .count();
        if (totalSortDirectionIdentifiers > 1) {
            throw new IllegalStateException("ORDER BY CLAUSE ERROR: You can only supply one sort direction per property!");
        } else if (totalSortDirectionIdentifiers == 1 &&
                (!sortItem.startsWith(ASC.getIdentifierAsString()) && !sortItem.startsWith(DESC.getIdentifierAsString()))) {
            throw new IllegalStateException("ORDER BY CLAUSE ERROR: The sort direction must be the first character of the sort expression!");
        }
    }
}