package br.com.zalf.prolog.webservice.commons.util;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

/**
 * Created on 2020-05-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class YamlUtils {
    private YamlUtils() {
        throw new IllegalStateException(YamlUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static <T> T parseFromResource(@NotNull final String resourceName,
                                          @NotNull final Class<T> classLoader) {
        InputStream inputStream = null;
        try {
            inputStream = getInputStream(resourceName, classLoader);
            final Yaml yaml = new Yaml(new Constructor(classLoader));
            return yaml.load(inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Nullable
    private static <T> InputStream getInputStream(final @NotNull String resourceName,
                                                  final @NotNull Class<T> classLoader) {
        return classLoader
                .getClassLoader()
                .getResourceAsStream(resourceName);
    }
}
