package xiangqi;

import javax.swing.*;
import java.awt.*;

/**
 * CLASE FINAL con constantes de estilo y utilidades para toda la GUI.
 */
public final class EstiloUI {

    // ── Colores ─────────────────────────────────────────────────────────────
    public static final Color FONDO_OSCURO  = new Color(28, 28, 38);
    public static final Color FONDO_PANEL   = new Color(42, 42, 58);
    public static final Color ACENTO_ROJO   = new Color(190, 40, 40);
    public static final Color ACENTO_ORO    = new Color(215, 175, 45);
    public static final Color TEXTO_CLARO   = new Color(228, 228, 228);
    public static final Color TEXTO_GRIS    = new Color(150, 150, 165);
    public static final Color TABLERO_BASE  = new Color(238, 196, 120);
    public static final Color SELECCION     = new Color(70, 210, 70);

    // ── Fuentes ─────────────────────────────────────────────────────────────
    public static final Font FUENTE_TITULO  = new Font("Serif", Font.BOLD, 30);
    public static final Font FUENTE_SUBTIT  = new Font("Serif", Font.BOLD, 18);
    public static final Font FUENTE_NORMAL  = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FUENTE_BOTON   = new Font("SansSerif", Font.BOLD, 13);

    private EstiloUI() {} // no instanciable

    // ── Fábrica de componentes ───────────────────────────────────────────────

    public static JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FUENTE_BOTON);
        btn.setBackground(ACENTO_ROJO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(210, 42));
        return btn;
    }

    public static JButton crearBotonSecundario(String texto) {
        JButton btn = crearBoton(texto);
        btn.setBackground(new Color(65, 65, 88));
        return btn;
    }

    public static JLabel crearTitulo(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(FUENTE_TITULO);
        lbl.setForeground(ACENTO_ORO);
        return lbl;
    }

    public static JTextField crearCampo(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(FUENTE_NORMAL);
        tf.setBackground(new Color(58, 58, 78));
        tf.setForeground(TEXTO_CLARO);
        tf.setCaretColor(TEXTO_CLARO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(95, 95, 125)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return tf;
    }

    public static JPasswordField crearCampoPassword(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setFont(FUENTE_NORMAL);
        pf.setBackground(new Color(58, 58, 78));
        pf.setForeground(TEXTO_CLARO);
        pf.setCaretColor(TEXTO_CLARO);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(95, 95, 125)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return pf;
    }

    public static JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FUENTE_NORMAL);
        lbl.setForeground(TEXTO_GRIS);
        return lbl;
    }

    // ── Diálogos ─────────────────────────────────────────────────────────────

    public static void mostrarError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error",
                                      JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Información",
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirmar",
               JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
