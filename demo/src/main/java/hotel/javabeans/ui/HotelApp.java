package hotel.javabeans.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Entry point — run this class to launch the Hotel Management System.
 *
 * Compile (from project root, adjust paths as needed):
 *   javac -d out -sourcepath src $(find src -name "*.java")
 *
 * Run:
 *   java -cp out hotel.javabeans.ui.HotelApp
 */
public class HotelApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}

            // ── Global UIManager overrides for dialogs / option panes ─────────
            UIManager.put("OptionPane.background",          Theme.SURFACE);
            UIManager.put("Panel.background",               Theme.SURFACE);
            UIManager.put("OptionPane.messageForeground",   Theme.TEXT);

            UIManager.put("Button.background",              Theme.CARD);
            UIManager.put("Button.foreground",              Theme.TEXT);

            UIManager.put("TextField.background",           Theme.SURFACE);
            UIManager.put("TextField.foreground",           Theme.TEXT);
            UIManager.put("TextField.caretForeground",      Theme.ACCENT);

            UIManager.put("PasswordField.background",       Theme.SURFACE);
            UIManager.put("PasswordField.foreground",       Theme.TEXT);
            UIManager.put("PasswordField.caretForeground",  Theme.ACCENT);

            UIManager.put("ComboBox.background",            Theme.SURFACE);
            UIManager.put("ComboBox.foreground",            Theme.TEXT);

            UIManager.put("Label.foreground",               Theme.TEXT);

            UIManager.put("ScrollPane.background",          Theme.SURFACE);
            UIManager.put("Viewport.background",            Theme.SURFACE);

            UIManager.put("Table.background",               Theme.SURFACE);
            UIManager.put("Table.foreground",               Theme.TEXT);
            UIManager.put("Table.selectionBackground",      Theme.alpha(Theme.ACCENT, 40));
            UIManager.put("Table.selectionForeground",      Theme.TEXT);
            UIManager.put("TableHeader.background",         Theme.CARD);
            UIManager.put("TableHeader.foreground",         Theme.TEXT_DIM);

            UIManager.put("CheckBox.background",            Theme.SURFACE);
            UIManager.put("CheckBox.foreground",            Theme.TEXT_DIM);

            UIManager.put("Dialog.background",              Theme.SURFACE);

            new LoginFrame().setVisible(true);
        });
    }
}