package xiangqi;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            JuegoService servicio = new JuegoService();
            LoginFrame login = new LoginFrame(servicio);
            login.setVisible(true);
        });
    }
}
