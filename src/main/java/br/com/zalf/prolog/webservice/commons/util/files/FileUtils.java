package br.com.zalf.prolog.webservice.commons.util.files;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    
    public static void createTempDir() {
        final String baseName = getBaseName();
        final File baseDir = new File(TEMP_DIR_REF);
        final File tmpDir = new File(baseDir, baseName);
        if (!tmpDir.exists()) {
            Log.i(TAG, "Realizando criação do diretório:" + tmpDir.getName());
            if (!tmpDir.mkdir()) {
                throw new IllegalStateException("Failed to create temp directory: " + baseName);
            }
        }
    }

    @NotNull
    public static File getTempDir() {
        return new File(TEMP_DIR_REF);
    }

    @NotNull
    public static List<File> getAllCreatedTempDirs() {
        final File baseTempDir = new File(TEMP_DIR_REF);
        return Arrays.stream(baseTempDir.listFiles())
                .filter(File::isDirectory)
                .filter(subDir -> DateUtils.isValid(subDir.getName(), "dd-MM-yyyy"))
                .peek(subDir -> Log.i(TAG, "Diretório adquirido: " + subDir.getName()))
                .collect(Collectors.toList());
    }

    @NotNull
    private static String getBaseName() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}