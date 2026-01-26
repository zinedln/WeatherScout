package org.example.weatherscout.client;

import javafx.scene.Scene;
import org.example.weatherscout.utils.AbstractLogs;
import org.example.weatherscout.utils.Config;

public class ThemeManager extends AbstractLogs {
    private int currentTheme = 0;
    private static final String[] THEMES = {
            "normal-theme.css", "dark-theme.css", "christmas-theme.css", "halloween-theme.css", "lgbtq-theme.css"
    };
    private static final String[] THEME_NAMES = { "Normal", "Dark", "Christmas", "Halloween", "LGBTQ" };

    @Override
    protected String getPrefix() { return "THEME"; }

    public ThemeManager() {
        try {
            currentTheme = Integer.parseInt(Config.getSetting("app.theme", "0"));
            if (currentTheme < 0 || currentTheme >= THEMES.length) currentTheme = 0;
        } catch (NumberFormatException e) { currentTheme = 0; }
    }

    public String getNextThemeName() {
        return THEME_NAMES[(currentTheme + 1) % THEMES.length];
    }

    public String getCurrentThemeName() {
        return THEME_NAMES[currentTheme];
    }

    public void cycleTheme(Scene scene) {
        currentTheme = (currentTheme + 1) % THEMES.length;
        applyTheme(scene);
        Config.saveSetting("app.theme", String.valueOf(currentTheme));
    }

    public void applyCurrentTheme(Scene scene) {
        applyTheme(scene);
    }

    private void applyTheme(Scene scene) {
        if (scene == null) return;
        try {
            String css = getClass().getResource("/org/example/weatherscout/" + THEMES[currentTheme]).toExternalForm();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(css);
            log("Theme gesetzt auf: " + THEME_NAMES[currentTheme]);
        } catch (Exception e) {
            log("Fehler beim Laden des Themes: " + e.getMessage());
        }
    }
}