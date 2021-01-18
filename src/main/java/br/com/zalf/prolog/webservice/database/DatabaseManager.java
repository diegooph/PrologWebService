package br.com.zalf.prolog.webservice.database;

import com.google.common.annotations.VisibleForTesting;

/**
 * Created on 25/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DatabaseManager {

    @VisibleForTesting
    public static void init() {
        throw new IllegalStateException("This class is not available anymore to manage a database." +
                                                "Now we do the spring way! See: " +
                                                SpringDatabaseManager.class.getSimpleName());
    }

    @VisibleForTesting
    public static void finish() {
        throw new IllegalStateException("This class is not available anymore to manage a database." +
                                                "Now we do the spring way! See: " +
                                                SpringDatabaseManager.class.getSimpleName());
    }
}