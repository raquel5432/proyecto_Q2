package xiangqi;

import javax.swing.*;
import java.awt.*;

/**
 * Menú principal tras el login.
 */
public class MenuFrame extends JFrame {

    private final JuegoService servicio;
    private final LoginFrame   loginFrame;
    private JLabel             lblInfo;

    public MenuFrame(JuegoService servicio, LoginFrame loginFrame) {
        this.servicio    = servicio;
        this.loginFrame  = loginFrame;
        configurar();
        construirUI();
    }

    private void configurar() {
        setTitle("Xiangqi – Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 640);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(EstiloUI.FONDO_OSCURO);
    }

    private void construirUI() {
        setLayout(new BorderLayout());

        // ── Header ──
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(EstiloUI.FONDO_OSCURO);
        header.setBorder(BorderFactory.createEmptyBorder(28, 20, 10, 20));

        JLabel titulo = EstiloUI.crearTitulo("象棋  XIANGQI");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblInfo = new JLabel("", SwingConstants.CENTER);
        lblInfo.setFont(EstiloUI.FUENTE_NORMAL);
        lblInfo.setForeground(EstiloUI.TEXTO_GRIS);
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        actualizarInfo();

        header.add(titulo);
        header.add(Box.createVerticalStrut(8));
        header.add(lblInfo);

        // ── Botones ──
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(EstiloUI.FONDO_OSCURO);
        centro.setBorder(BorderFactory.createEmptyBorder(20, 110, 40, 110));

        JButton btnJugar    = EstiloUI.crearBoton("♟  Jugar Xiangqi");
        JButton btnCuenta   = EstiloUI.crearBotonSecundario("👤  Mi Cuenta");
        JButton btnRanking  = EstiloUI.crearBotonSecundario("🏆  Ranking");
        JButton btnLogs     = EstiloUI.crearBotonSecundario("📋  Historial Partidas");
        JButton btnCapturas = EstiloUI.crearBotonSecundario("⚔️  Historial Capturas");
        JButton btnLogout   = EstiloUI.crearBotonSecundario("🚪  Cerrar Sesión");

        for (JButton btn : new JButton[]{btnJugar, btnCuenta, btnRanking, btnLogs, btnCapturas, btnLogout}) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            centro.add(btn);
            centro.add(Box.createVerticalStrut(12));
        }

        add(header, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        // ── Acciones ──
        btnJugar.addActionListener(e    -> seleccionarOponente());
        btnCuenta.addActionListener(e   -> mostrarMiCuenta());
        btnRanking.addActionListener(e  -> new ReportesFrame(this, servicio, 0).setVisible(true));
        btnLogs.addActionListener(e     -> new ReportesFrame(this, servicio, 1).setVisible(true));
        btnCapturas.addActionListener(e -> new ReportesFrame(this, servicio, 2).setVisible(true));
        btnLogout.addActionListener(e  -> {
            servicio.logout();
            dispose();
            loginFrame.setVisible(true);
        });
    }

    private void seleccionarOponente() {
        java.util.ArrayList<Jugador> activos = servicio.getJugadoresActivos();
        if (activos.isEmpty()) {
            EstiloUI.mostrarInfo(this, "No hay otros jugadores registrados.");
            return;
        }

        String[] nombres = new String[activos.size()];
        for (int i = 0; i < activos.size(); i++) {
            nombres[i] = activos.get(i).getUsername()
                         + "  [" + activos.get(i).getPuntos() + " pts]";
        }

        JList<String> lista = new JList<>(nombres);
        lista.setBackground(new Color(55, 55, 75));
        lista.setForeground(EstiloUI.TEXTO_CLARO);
        lista.setFont(EstiloUI.FUENTE_NORMAL);
        lista.setSelectionBackground(EstiloUI.ACENTO_ROJO);
        lista.setSelectionForeground(Color.WHITE);
        lista.setFixedCellHeight(30);

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setPreferredSize(new Dimension(280, 180));

        int res = JOptionPane.showConfirmDialog(this,
            new Object[]{"Selecciona tu oponente:", scroll},
            "Seleccionar Oponente",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            int idx = lista.getSelectedIndex();
            if (idx < 0) { EstiloUI.mostrarError(this, "Selecciona un oponente."); return; }
            String error = servicio.iniciarPartida(activos.get(idx).getUsername());
            if (error != null) {
                EstiloUI.mostrarError(this, error);
            } else {
                new TableroFrame(this, servicio).setVisible(true);
            }
        }
    }

    private void mostrarMiCuenta() {
        Jugador j = servicio.getSesionActual();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(EstiloUI.FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel tit = EstiloUI.crearTitulo("Mi Cuenta");
        tit.setFont(EstiloUI.FUENTE_SUBTIT);
        tit.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(tit);
        panel.add(Box.createVerticalStrut(12));

        agregarFila(panel, "Username:",       j.getUsername());
        agregarFila(panel, "Puntos:",         String.valueOf(j.getPuntos()));
        agregarFila(panel, "Fecha creación:", j.getFechaCreacion());
        agregarFila(panel, "Estado:",         j.isActivo() ? "Activo" : "Inactivo");

        panel.add(Box.createVerticalStrut(12));

        String[] opciones = {"Cambiar Contraseña", "Eliminar Cuenta", "Cerrar"};
        int res = JOptionPane.showOptionDialog(this, panel, "Mi Cuenta",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, opciones, opciones[2]);

        if (res == 0) cambiarPassword();
        else if (res == 1) eliminarCuenta();
    }

    private void agregarFila(JPanel panel, String etiqueta, String valor) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        fila.setBackground(EstiloUI.FONDO_PANEL);
        JLabel lbl = EstiloUI.crearLabel(etiqueta);
        JLabel val = new JLabel(valor);
        val.setFont(new Font("SansSerif", Font.BOLD, 14));
        val.setForeground(EstiloUI.TEXTO_CLARO);
        fila.add(lbl);
        fila.add(val);
        panel.add(fila);
    }

    private void cambiarPassword() {
        JPasswordField pf = EstiloUI.crearCampoPassword(12);
        int res = JOptionPane.showConfirmDialog(this,
            new Object[]{"Nueva contraseña (5 chars alfanuméricos):", pf},
            "Cambiar Contraseña", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String error = servicio.cambiarPassword(new String(pf.getPassword()));
            if (error == null) EstiloUI.mostrarInfo(this, "Contraseña actualizada.");
            else EstiloUI.mostrarError(this, error);
        }
    }

    private void eliminarCuenta() {
        if (EstiloUI.confirmar(this,
                "¿Seguro que deseas eliminar tu cuenta? No se puede deshacer.")) {
            String error = servicio.eliminarCuenta();
            if (error == null) {
                EstiloUI.mostrarInfo(this, "Cuenta eliminada.");
                dispose();
                loginFrame.setVisible(true);
            } else {
                EstiloUI.mostrarError(this, error);
            }
        }
    }

    /** Refresca el label de info (puntos pueden haber cambiado). */
    public void actualizarInfo() {
        Jugador j = servicio.getSesionActual();
        if (j != null) {
            lblInfo.setText("Bienvenido, " + j.getUsername()
                            + "  |  Puntos: " + j.getPuntos());
        }
    }
}
