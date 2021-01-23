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
}
