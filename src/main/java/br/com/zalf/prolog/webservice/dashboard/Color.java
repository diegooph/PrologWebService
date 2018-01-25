package br.com.zalf.prolog.webservice.dashboard;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 1/24/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class Color {
    public static final Color BLACK       = Color.fromHex("#000000");
    public static final Color DKGRAY      = Color.fromHex("#444444");
    public static final Color GRAY        = Color.fromHex("#888888");
    public static final Color LTGRAY      = Color.fromHex("#CCCCCC");
    public static final Color WHITE       = Color.fromHex("#FFFFFF");
    public static final Color RED         = Color.fromHex("#FF0000");
    public static final Color GREEN       = Color.fromHex("#00FF00");
    public static final Color BLUE        = Color.fromHex("#0000FF");
    public static final Color YELLOW      = Color.fromHex("#FFFF00");
    public static final Color CYAN        = Color.fromHex("#00FFFF");
    public static final Color MAGENTA     = Color.fromHex("#FF00FF");

    @NotNull
    private final String colorHex;

    private Color(@NotNull String colorHex) {
        this.colorHex = colorHex;
    }

    public static Color fromHex(@NotNull final String colorHex) {
        return new Color(colorHex);
    }

    @NotNull
    public String getHex() {
        return colorHex;
    }

    @Override
    public String toString() {
        return "Color{" +
                "colorHex='" + colorHex + '\'' +
                '}';
    }
}