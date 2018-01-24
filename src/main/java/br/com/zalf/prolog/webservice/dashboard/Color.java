package br.com.zalf.prolog.webservice.dashboard;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 1/24/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class Color {
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