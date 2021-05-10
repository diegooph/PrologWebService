package br.com.zalf.prolog.webservice.commons.util.files;

import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Optional;

/**
 * Created on 17/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();
    private static final String TEMP_DIR_REF = System.getProperty("java.io.tmpdir");

    private FileUtils() {
        throw new IllegalStateException(FileUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static File getTempDir() {
        return new File(TEMP_DIR_REF);
    }

    public static boolean delete(@NotNull final File file) {
        return file.delete();
    }

    public static boolean isAFile(@NotNull final File file) {
        return !file.isDirectory();
    }

    public static boolean isDirEmpty(@NotNull final File file) throws IOException {
        // Using try-with-resources we ensure that the stream's close() method is called.
        try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(file.toPath())) {
            return !directoryStream.iterator().hasNext();
        }
    }

    @NotNull
    public static Optional<FileTime> getFileTimeFromFile(@NotNull final File file) {
        try {
            final FileTime fileTime = Files.readAttributes(file.toPath(), BasicFileAttributes.class)
                    .creationTime();
            return Optional.of(fileTime);
        } catch (final IOException e) {
            Log.e(TAG, "Erro ao tentar ler arquivo: " + file.getName());
            return Optional.empty();
        }
    }
}