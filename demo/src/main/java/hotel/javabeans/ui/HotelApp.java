package hotel.javabeans.ui;

import javax.swing.*;
import java.awt.*;

public class HotelApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}

            // Apply pink theme globally to dialogs and option panes
            UIManager.put("OptionPane.background",         Theme.SURFACE);
            UIManager.put("Panel.background",              Theme.SURFACE);
            UIManager.put("OptionPane.messageForeground",  Theme.TEXT);
            UIManager.put("Button.background",             Theme.CARD);
            UIManager.put("Button.foreground",             Theme.TEXT);
            UIManager.put("TextField.background",          Theme.CARD);
            UIManager.put("TextField.foreground",          Theme.TEXT);
            UIManager.put("TextField.caretForeground",     Theme.PINK_SOFT);
            UIManager.put("ComboBox.background",           Theme.CARD);
            UIManager.put("ComboBox.foreground",           Theme.TEXT);
            UIManager.put("Label.foreground",              Theme.TEXT);
            UIManager.put("ScrollPane.background",         Theme.SURFACE);
            UIManager.put("Viewport.background",           Theme.SURFACE);
            UIManager.put("Table.background",              Theme.SURFACE);
            UIManager.put("Table.foreground",              Theme.TEXT);
            UIManager.put("Table.selectionBackground",     Theme.alpha(Theme.PINK, 80));
            UIManager.put("Table.selectionForeground",     Color.WHITE);
            UIManager.put("TableHeader.background",        Theme.CARD);
            UIManager.put("TableHeader.foreground",        Theme.PINK_SOFT);
            UIManager.put("CheckBox.background",           Theme.SURFACE);
            UIManager.put("CheckBox.foreground",           Theme.TEXT_DIM);
            UIManager.put("Dialog.background",             Theme.SURFACE);
            UIManager.put("PasswordField.background",      Theme.CARD);
            UIManager.put("PasswordField.foreground",      Theme.TEXT);
            UIManager.put("PasswordField.caretForeground", Theme.PINK_SOFT);

            new LoginFrame().setVisible(true);
        });
    }
}