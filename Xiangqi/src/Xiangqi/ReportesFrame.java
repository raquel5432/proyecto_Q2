package xiangqi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Ventana de reportes: Ranking, Historial de partidas e Historial de capturas.
 */
public class ReportesFrame extends JDialog {

    /**
     * @param tabInicial  0 = Ranking, 1 = Historial partidas, 2 = Capturas
     */
    public ReportesFrame(JFrame padre, JuegoService servicio, int tabInicial) {
        super(padre, "Reportes", true);
        construirUI(servicio, tabInicial);
        setSize(680, 500);
        setLocationRelativeTo(padre);
        setResizable(false);
    }

    /** Compatibilidad con llamadas antiguas (boolean). */
    public ReportesFrame(JFrame padre, JuegoService servicio, boolean mostrarRanking) {
        this(padre, servicio, mostrarRanking ? 0 : 1);
    }

    private void construirUI(JuegoService servicio, int tabInicial) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(EstiloUI.FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel titulo = EstiloUI.crearTitulo("Reportes");
        titulo.setFont(EstiloUI.FUENTE_SUBTIT);
        panel.add(titulo, BorderLayout.NORTH);

        // ── Pestañas ──
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(EstiloUI.FONDO_PANEL);
        tabs.setForeground(EstiloUI.TEXTO_CLARO);
        tabs.setFont(EstiloUI.FUENTE_BOTON);

        tabs.addTab("🏆  Ranking",          crearTablaRanking(servicio));
        tabs.addTab("📋  Historial Partidas", crearTablaLogs(servicio));
        tabs.addTab("⚔️  Capturas",          crearTablaCapturas(servicio));

        int idx = Math.max(0, Math.min(tabInicial, 2));
        tabs.setSelectedIndex(idx);
        panel.add(tabs, BorderLayout.CENTER);

        JButton btnCerrar = EstiloUI.crearBotonSecundario("Cerrar");
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sur.setBackground(EstiloUI.FONDO_PANEL);
        sur.add(btnCerrar);
        panel.add(sur, BorderLayout.SOUTH);

        setContentPane(panel);
        btnCerrar.addActionListener(e -> dispose());
    }

    // ── Pestaña Ranking ──────────────────────────────────────────────────────

    private JScrollPane crearTablaRanking(JuegoService servicio) {
        String[] cols = {"#", "Jugador", "Puntos", "Desde"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        ArrayList<Jugador> ranking = servicio.getRanking();
        for (int i = 0; i < ranking.size(); i++) {
            Jugador j = ranking.get(i);
            modelo.addRow(new Object[]{
                i + 1, j.getUsername(), j.getPuntos(), j.getFechaCreacion()
            });
        }
        return estilizarTabla(new JTable(modelo));
    }

    // ── Pestaña Historial de Partidas ────────────────────────────────────────

    private JScrollPane crearTablaLogs(JuegoService servicio) {
        String[] cols = {"Fecha", "Rojo", "Negro", "Ganador", "Resultado", "Movs"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        ArrayList<LogPartida> logs = servicio.getLogs();
        // Mostrar del más reciente al más antiguo
        for (int i = logs.size() - 1; i >= 0; i--) {
            LogPartida lp = logs.get(i);
            modelo.addRow(new Object[]{
                lp.getFecha(),
                lp.getJugadorRojo(),
                lp.getJugadorNegro(),
                lp.getGanador(),
                lp.fueRetiro() ? "Retiro" : "Captura",
                lp.getMovimientos()
            });
        }
        return estilizarTabla(new JTable(modelo));
    }

    // ── Pestaña Historial de Capturas ────────────────────────────────────────

    private JScrollPane crearTablaCapturas(JuegoService servicio) {
        String[] cols = {"Turno", "Jugador", "Pieza", "Capturó", "Hora"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        ArrayList<HistorialMovimiento> capturas = servicio.getHistorialCapturas();
        for (HistorialMovimiento h : capturas) {
            String nombrePieza = h.getPiezaMovida()
                    .replace(" rojo", " 🔴").replace(" negro", " ⚫");
            String nombreCap = h.getPiezaCapturada() != null
                    ? h.getPiezaCapturada().replace(" rojo", " 🔴").replace(" negro", " ⚫")
                    : "-";
            modelo.addRow(new Object[]{
                h.getTurno(),
                h.getJugador(),
                nombrePieza,
                nombreCap,
                h.getFecha()
            });
        }

        if (modelo.getRowCount() == 0) {
            JPanel vacio = new JPanel(new BorderLayout());
            vacio.setBackground(new Color(52, 52, 72));
            JLabel msg = new JLabel("No hay capturas registradas en esta sesión.",
                                    SwingConstants.CENTER);
            msg.setForeground(EstiloUI.TEXTO_GRIS);
            msg.setFont(EstiloUI.FUENTE_NORMAL);
            vacio.add(msg, BorderLayout.CENTER);
            JScrollPane sp = new JScrollPane(vacio);
            sp.getViewport().setBackground(new Color(52, 52, 72));
            return sp;
        }

        return estilizarTabla(new JTable(modelo));
    }

    // ── Utilidad ─────────────────────────────────────────────────────────────

    private JScrollPane estilizarTabla(JTable tabla) {
        tabla.setBackground(new Color(52, 52, 72));
        tabla.setForeground(EstiloUI.TEXTO_CLARO);
        tabla.setFont(EstiloUI.FUENTE_NORMAL);
        tabla.setRowHeight(28);
        tabla.setGridColor(new Color(75, 75, 100));
        tabla.setSelectionBackground(EstiloUI.ACENTO_ROJO);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.getTableHeader().setBackground(new Color(170, 30, 30));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(EstiloUI.FUENTE_BOTON);
        tabla.setShowGrid(true);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(new Color(52, 52, 72));
        return scroll;
    }
}
