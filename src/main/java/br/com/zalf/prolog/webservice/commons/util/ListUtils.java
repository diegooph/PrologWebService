package br.com.zalf.prolog.webservice.commons.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 17/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ListUtils {

    private ListUtils() {
        throw new IllegalStateException(ListUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static String[] toArray(@NotNull final List<String> list) {
        return list.toArray(new String[0]);
    }

    public static boolean hasNoElements(@Nullable final List<?> elements) {
        return !hasElements(elements);
    }

    public static boolean hasElements(@Nullable final List<?> elements) {
        return elements != null && elements.size() > 0;
    }

    public static boolean allSameSize(@NotNull final List<?>... lists) {
        if (lists.length == 0) {
            return true;
        }

        final List<?> firsList = lists[0];
        for (final List<?> list : lists) {
            if (list.size() != firsList.size()) {
                return false;
            }
        }

        return true;
    }

    public static boolean constainsSomeInOrder(@NotNull final List<? extends Number> numbersToVerify,
                                               @NotNull final Number... containedNumbers) {
        return internalConstainsInOrder(numbersToVerify, containedNumbers);
    }

    public static boolean constainsAllInOrder(@NotNull final List<? extends Number> numbersToVerify,
                                              @NotNull final Number... containedNumbers) {
        if (numbersToVerify.size() != containedNumbers.length) {
            return false;
        }
        return internalConstainsInOrder(numbersToVerify, containedNumbers);
    }

    @Nullable
    public static <T> List<T> combineInNew(@Nullable final List<T> list1,
                                           @Nullable final List<T> list2) {
        if (list1 == null && list2 == null) {
            return null;
        }
        if (list1 != null && list2 != null) {
            final List<T> combinedList = new ArrayList<>(list1);
            combinedList.addAll(list2);
            return combinedList;
        }
        return list1 != null
                ? new ArrayList<>(list1)
                : new ArrayList<>(list2);
    }

    private static boolean internalConstainsInOrder(@NotNull final List<? extends Number> numbersToVerify,
                                                    @NotNull final Number... containedNumbers) {
        for (int i = 0; i < numbersToVerify.size(); i++) {
            if (!numbersToVerify.get(i).equals(containedNumbers[i])) {
                return false;
            }
        }
        return true;
    }
}
