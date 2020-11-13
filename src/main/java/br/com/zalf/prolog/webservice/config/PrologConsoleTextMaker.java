package br.com.zalf.prolog.webservice.config;

import br.com.zalf.prolog.webservice.commons.util.ProLogUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created on 2020-01-16
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PrologConsoleTextMaker implements ServletContextListener {

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        if (!ProLogUtils.isDebug()) {
            return;
        }

        final StringBuilder builder = new StringBuilder();
        builder.append("      ___                     _              __ _\n");
        builder.append("     | _ \\    _ _    ___     | |     ___    / _` |\n");
        builder.append("     |  _/   | '_|  / _ \\    | |    / _ \\   \\__, |\n");
        builder.append("    _|_|_   _|_|_   \\___/   _|_|_   \\___/   |___/\n");
        builder.append("  _| \"\"\" |_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|\n");
        builder.append("  `-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\n");
        System.out.println();
        System.out.print(builder.toString());
        System.out.println("  DEBUG ENV                              " + BuildConfig.VERSION_CODE);
        System.out.println();
    }
}
