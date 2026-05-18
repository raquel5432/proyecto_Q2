package xiangqi;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Ventana del juego: tablero visual + panel lateral con historial en tiempo real.
 */
public class TableroFrame extends JFrame {

    private static final int CELDA  = 60;
    private static final int MARGEN = 36;
    private static final int ANCHO  = CELDA * (Tablero.COLUMNAS - 1) + MARGEN * 2;
    private static final int ALTO   = CELDA * (Tablero.FILAS    - 1) + MARGEN * 2;

    private static final Color COLOR_MOVIMIENTO = new Color(50,  200, 50,  130);
    private static final Color COLOR_CAPTURA    = new Color(255, 140, 0,   180);
    private static final Color COLOR_JAQUE      = new Color(220, 30,  30,  210);

    private final JuegoService servicio;
    private final MenuFrame    menuFrame;
    private Partida            partida;

    private int[]            seleccionada;
    private ArrayList<int[]> movsPosibles;

    private JPanel    panelTablero;
    private JLabel    lblTurno;
    private JLabel    lblJugadores;
    private JLabel    lblContadorTurnos;
    private JTextArea areaHistorial;
    private JTextArea areaCapturas;

    public TableroFrame(MenuFrame menuFrame, JuegoService servicio) {
        this.menuFrame    = menuFrame;
        this.servicio     = servicio;
        this.partida      = servicio.getPartidaActual();
        this.seleccionada = null;
        this.movsPosibles = new ArrayList<>();

        configurar();
        construirUI();
        actualizarEstado();
    }

    private void configurar() {
        setTitle("Xiangqi – En Juego");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (EstiloUI.confirmar(TableroFrame.this,
                        "¿Deseas retirarte? El oponente ganará.")) {
                    partida.retirarse();
                    finalizarPartida();
                }
            }
        });
    }

    private void construirUI() {
        setLayout(new BorderLayout(6, 6));
        getContentPane().setBackground(EstiloUI.FONDO_OSCURO);

        JPanel panelInfo = new JPanel(new GridLayout(3, 1, 0, 2));
        panelInfo.setBackground(EstiloUI.FONDO_OSCURO);
        panelInfo.setBorder(BorderFactory.createEmptyBorder(8, 14, 4, 14));

        lblJugadores = new JLabel("", SwingConstants.CENTER);
        lblJugadores.setFont(EstiloUI.FUENTE_NORMAL);
        lblJugadores.setForeground(EstiloUI.TEXTO_GRIS);

        lblTurno = new JLabel("", SwingConstants.CENTER);
        lblTurno.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTurno.setForeground(EstiloUI.ACENTO_ORO);

        lblContadorTurnos = new JLabel("Movimientos: 0", SwingConstants.CENTER);
        lblContadorTurnos.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblContadorTurnos.setForeground(EstiloUI.TEXTO_GRIS);

        panelInfo.add(lblJugadores);
        panelInfo.add(lblTurno);
        panelInfo.add(lblContadorTurnos);

        panelTablero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                pintarTablero((Graphics2D) g);
            }
        };
        panelTablero.setPreferredSize(new Dimension(ANCHO, ALTO));
        panelTablero.setBackground(EstiloUI.TABLERO_BASE);
        panelTablero.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                manejarClick(e.getX(), e.getY());
            }
        });

        JPanel wrapTablero = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapTablero.setBackground(EstiloUI.FONDO_OSCURO);
        wrapTablero.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 4));
        wrapTablero.add(panelTablero);

        JPanel panelLateral = construirPanelLateral();

        JButton btnRetirar = EstiloUI.crearBotonSecundario("🏳  Retirarse");
        btnRetirar.setPreferredSize(new Dimension(150, 36));
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBtn.setBackground(EstiloUI.FONDO_OSCURO);
        panelBtn.setBorder(BorderFactory.createEmptyBorder(2, 0, 10, 0));
        panelBtn.add(btnRetirar);

        add(panelInfo,    BorderLayout.NORTH);
        add(wrapTablero,  BorderLayout.CENTER);
        add(panelLateral, BorderLayout.EAST);
        add(panelBtn,     BorderLayout.SOUTH);

        pack();

        btnRetirar.addActionListener(e -> {
            if (EstiloUI.confirmar(this, "¿Deseas retirarte? El oponente ganará.")) {
                partida.retirarse();
                finalizarPartida();
            }
        });
    }

    private JPanel construirPanelLateral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(EstiloUI.FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 6, 8, 10));
        panel.setPreferredSize(new Dimension(230, ALTO));

        JLabel lblH = new JLabel("📜 Historial de Movimientos");
        lblH.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblH.setForeground(EstiloUI.ACENTO_ORO);
        lblH.setAlignmentX(Component.LEFT_ALIGNMENT);

        areaHistorial = new JTextArea();
        areaHistorial.setEditable(false);
        areaHistorial.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaHistorial.setBackground(new Color(35, 35, 50));
        areaHistorial.setForeground(EstiloUI.TEXTO_CLARO);
        areaHistorial.setLineWrap(true);
        areaHistorial.setWrapStyleWord(true);
        areaHistorial.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JScrollPane scrollH = new JScrollPane(areaHistorial);
        scrollH.setPreferredSize(new Dimension(218, 220));
        scrollH.setMaximumSize(new Dimension(218, 220));
        scrollH.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollH.getViewport().setBackground(new Color(35, 35, 50));
        scrollH.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 110)));

        JLabel lblC = new JLabel("⚔️ Capturas");
        lblC.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblC.setForeground(new Color(220, 80, 80));
        lblC.setAlignmentX(Component.LEFT_ALIGNMENT);

        areaCapturas = new JTextArea();
        areaCapturas.setEditable(false);
        areaCapturas.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaCapturas.setBackground(new Color(35, 35, 50));
        areaCapturas.setForeground(new Color(255, 160, 160));
        areaCapturas.setLineWrap(true);
        areaCapturas.setWrapStyleWord(true);
        areaCapturas.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JScrollPane scrollC = new JScrollPane(areaCapturas);
        scrollC.setPreferredSize(new Dimension(218, 200));
        scrollC.setMaximumSize(new Dimension(218, 200));
        scrollC.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollC.getViewport().setBackground(new Color(35, 35, 50));
        scrollC.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 110)));

        panel.add(lblH);
        panel.add(Box.createVerticalStrut(4));
        panel.add(scrollH);
        panel.add(Box.createVerticalStrut(12));
        panel.add(lblC);
        panel.add(Box.createVerticalStrut(4));
        panel.add(scrollC);

        return panel;
    }

    // ── Click ────────────────────────────────────────────────────────────────

    private void manejarClick(int px, int py) {
        if (partida.isTerminada()) return;

        int cx = Math.round((float)(px - MARGEN) / CELDA);
        int cy = Math.round((float)(py - MARGEN) / CELDA);

        if (cx < 0 || cx >= Tablero.COLUMNAS || cy < 0 || cy >= Tablero.FILAS) return;

        Tablero tablero = partida.getTablero();
        Pieza pieza = tablero.getPieza(cx, cy);

        if (seleccionada == null) {
            if (pieza != null && pieza.getColor().equals(partida.getColorActual())) {
                seleccionada = new int[]{cx, cy};
                movsPosibles = pieza.obtenerMovimientos(tablero);
            }
        } else {
            if (estaEnMovsPosibles(cx, cy)) {
                boolean ok = partida.realizarMovimiento(
                    seleccionada[0], seleccionada[1], cx, cy);
                seleccionada = null;
                movsPosibles.clear();
                panelTablero.repaint();
                if (ok) {
                    actualizarEstado();
                    actualizarHistorialUI();
                }
            } else if (pieza != null && pieza.getColor().equals(partida.getColorActual())) {
                seleccionada = new int[]{cx, cy};
                movsPosibles = pieza.obtenerMovimientos(tablero);
            } else {
                seleccionada = null;
                movsPosibles.clear();
            }
        }
        panelTablero.repaint();
    }

    private boolean estaEnMovsPosibles(int cx, int cy) {
        for (int[] m : movsPosibles) {
            if (m[0] == cx && m[1] == cy) return true;
        }
        return false;
    }

    private boolean esCasillaDeCapturaEnemiga(int cx, int cy) {
        if (!estaEnMovsPosibles(cx, cy)) return false;
        Pieza p = partida.getTablero().getPieza(cx, cy);
        return p != null && !p.getColor().equals(partida.getColorActual());
    }

    // ── Historial UI ─────────────────────────────────────────────────────────

    private void actualizarHistorialUI() {
        ArrayList<HistorialMovimiento> hist = partida.getHistorial();
        StringBuilder sbMovs = new StringBuilder();
        StringBuilder sbCaps = new StringBuilder();

        for (int i = hist.size() - 1; i >= 0; i--) {
            HistorialMovimiento h = hist.get(i);
            sbMovs.append(h.getDescripcion()).append("\n");
            if (h.esCaptura()) {
                sbCaps.append(h.getDescripcion()).append("\n");
            }
        }

        areaHistorial.setText(sbMovs.toString());
        areaCapturas.setText(sbCaps.toString());
        areaHistorial.setCaretPosition(0);
        areaCapturas.setCaretPosition(0);
    }

    // ── Pintado ───────────────────────────────────────────────────────────────

    private void pintarTablero(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        pintarFondo(g2);
        pintarRio(g2);
        pintarPalacios(g2);
        pintarLineas(g2);
        pintarMovsLibres(g2);       // puntos verdes (casillas vacías)
        pintarResaltadoJaque(g2);   // halo rojo del General en jaque
        pintarPiezas(g2);           // piezas encima de todo lo anterior
        pintarCapturasEncima(g2);   // anillo naranja SOBRE las fichas enemigas
        pintarSeleccion(g2);        // anillo verde de selección
    }

    private void pintarFondo(Graphics2D g2) {
        g2.setColor(new Color(238, 196, 120));
        g2.fillRect(0, 0, ANCHO, ALTO);
    }

    private void pintarRio(Graphics2D g2) {
        int y1 = cy(4), y2 = cy(5);
        g2.setColor(new Color(90, 150, 210, 90));
        g2.fillRect(MARGEN, y1, CELDA * (Tablero.COLUMNAS - 1), y2 - y1);

        g2.setColor(new Color(50, 90, 150, 160));
        g2.setFont(new Font("Serif", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        String txt = "楚  河          漢  界";
        int tx = MARGEN + (CELDA * (Tablero.COLUMNAS - 1) - fm.stringWidth(txt)) / 2;
        int ty = y1 + (y2 - y1 + fm.getAscent()) / 2 - 3;
        g2.drawString(txt, tx, ty);
    }

    private void pintarPalacios(Graphics2D g2) {
        g2.setColor(new Color(255, 215, 90, 70));
        g2.fillRect(cx(3), cy(0), cx(5) - cx(3), cy(2) - cy(0));
        g2.fillRect(cx(3), cy(7), cx(5) - cx(3), cy(9) - cy(7));

        g2.setColor(new Color(140, 100, 30, 160));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(cx(3), cy(0), cx(5), cy(2));
        g2.drawLine(cx(5), cy(0), cx(3), cy(2));
        g2.drawLine(cx(3), cy(7), cx(5), cy(9));
        g2.drawLine(cx(5), cy(7), cx(3), cy(9));
    }

    private void pintarLineas(Graphics2D g2) {
        g2.setColor(new Color(110, 72, 25));
        g2.setStroke(new BasicStroke(1.5f));

        for (int fy = 0; fy < Tablero.FILAS; fy++) {
            g2.drawLine(cx(0), cy(fy), cx(Tablero.COLUMNAS - 1), cy(fy));
        }
        for (int fx = 0; fx < Tablero.COLUMNAS; fx++) {
            if (fx == 0 || fx == Tablero.COLUMNAS - 1) {
                g2.drawLine(cx(fx), cy(0), cx(fx), cy(Tablero.FILAS - 1));
            } else {
                g2.drawLine(cx(fx), cy(0), cx(fx), cy(4));
                g2.drawLine(cx(fx), cy(5), cx(fx), cy(9));
            }
        }
        pintarMarcas(g2);
    }

    private void pintarMarcas(Graphics2D g2) {
        g2.setColor(new Color(110, 72, 25));
        g2.setStroke(new BasicStroke(1.5f));
        int[][] pos = {
            {1,2},{7,2},{1,7},{7,7},
            {0,3},{2,3},{4,3},{6,3},{8,3},
            {0,6},{2,6},{4,6},{6,6},{8,6}
        };
        for (int[] p : pos) pintarEsquinas(g2, cx(p[0]), cy(p[1]));
    }

    private void pintarEsquinas(Graphics2D g2, int cx, int cy) {
        int s = 5;
        g2.drawLine(cx-s, cy-2, cx-2, cy-2); g2.drawLine(cx-2, cy-s, cx-2, cy-2);
        g2.drawLine(cx+2, cy-2, cx+s, cy-2); g2.drawLine(cx+2, cy-s, cx+2, cy-2);
        g2.drawLine(cx-s, cy+2, cx-2, cy+2); g2.drawLine(cx-2, cy+2, cx-2, cy+s);
        g2.drawLine(cx+2, cy+2, cx+s, cy+2); g2.drawLine(cx+2, cy+2, cx+2, cy+s);
    }

    // Pinta un halo rojo pulsante alrededor del General si está en jaque
    private void pintarResaltadoJaque(Graphics2D g2) {
        String colorActual = partida.getColorActual();
        if (!partida.estaEnJaque(colorActual)) return;

        Pieza general = partida.getTablero().buscarGeneral(colorActual);
        if (general == null) return;

        int px = cx(general.getX());
        int py = cy(general.getY());
        int r  = CELDA / 2 + 2;

        g2.setColor(new Color(220, 30, 30, 80));
        g2.fillOval(px - r, py - r, r * 2, r * 2);

        g2.setColor(COLOR_JAQUE);
        g2.setStroke(new BasicStroke(3.5f));
        g2.drawOval(px - r, py - r, r * 2, r * 2);
    }

    // Puntos verdes para casillas vacías alcanzables
    private void pintarMovsLibres(Graphics2D g2) {
        for (int[] m : movsPosibles) {
            if (esCasillaDeCapturaEnemiga(m[0], m[1])) continue;
            int px = cx(m[0]), py = cy(m[1]);
            int r  = CELDA / 2 - 8;
            g2.setColor(new Color(50, 200, 50, 130));
            g2.fillOval(px - r, py - r, r * 2, r * 2);
            g2.setColor(new Color(30, 150, 30, 200));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(px - r, py - r, r * 2, r * 2);
        }
    }

    // Anillo naranja grueso pintado ENCIMA de la ficha enemiga comible
    private void pintarCapturasEncima(Graphics2D g2) {
        for (int[] m : movsPosibles) {
            if (!esCasillaDeCapturaEnemiga(m[0], m[1])) continue;
            int px = cx(m[0]), py = cy(m[1]);
            int r  = CELDA / 2 + 4;   // más grande que la ficha (radio ficha = CELDA/2 - 5)

            // Relleno naranja semitransparente sobre la ficha
            g2.setColor(new Color(255, 140, 0, 90));
            g2.fillOval(px - r, py - r, r * 2, r * 2);

            // Anillo naranja sólido exterior
            g2.setColor(COLOR_CAPTURA);
            g2.setStroke(new BasicStroke(4f));
            g2.drawOval(px - r, py - r, r * 2, r * 2);

            // Segundo anillo interior para más visibilidad
            int r2 = r - 5;
            g2.setColor(new Color(255, 180, 0, 160));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(px - r2, py - r2, r2 * 2, r2 * 2);
        }
    }

    private void pintarPiezas(Graphics2D g2) {
        Tablero tablero = partida.getTablero();
        String colorActual = partida.getColorActual();
        boolean hayJaque   = partida.estaEnJaque(colorActual);

        for (int fy = 0; fy < Tablero.FILAS; fy++) {
            for (int fx = 0; fx < Tablero.COLUMNAS; fx++) {
                Pieza p = tablero.getPieza(fx, fy);
                if (p != null) {
                    boolean enJaque = hayJaque
                            && p instanceof General
                            && p.getColor().equals(colorActual);
                    pintarPieza(g2, p, enJaque);
                }
            }
        }
    }

    private void pintarPieza(Graphics2D g2, Pieza p, boolean enJaque) {
        int px = cx(p.getX());
        int py = cy(p.getY());
        int r  = CELDA / 2 - 5;
        boolean esRojo = p.getColor().equals("rojo");

        // Sombra
        g2.setColor(new Color(0, 0, 0, 55));
        g2.fillOval(px - r + 2, py - r + 2, r * 2, r * 2);

        // Círculo exterior — rojo brillante si está en jaque
        if (enJaque) {
            g2.setColor(new Color(220, 20, 20));
        } else {
            g2.setColor(esRojo ? new Color(170, 25, 25) : new Color(18, 18, 18));
        }
        g2.fillOval(px - r, py - r, r * 2, r * 2);

        // Círculo interior
        int ri = r - 4;
        if (enJaque) {
            g2.setColor(new Color(255, 200, 200));
        } else {
            g2.setColor(esRojo ? new Color(238, 195, 130) : new Color(55, 55, 55));
        }
        g2.fillOval(px - ri, py - ri, ri * 2, ri * 2);

        // Borde interior
        if (enJaque) {
            g2.setColor(new Color(220, 20, 20));
        } else {
            g2.setColor(esRojo ? new Color(170, 25, 25) : new Color(18, 18, 18));
        }
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(px - ri, py - ri, ri * 2, ri * 2);

        // Símbolo
        g2.setFont(new Font("Serif", Font.BOLD, 16));
        if (enJaque) {
            g2.setColor(new Color(180, 0, 0));
        } else {
            g2.setColor(esRojo ? new Color(150, 20, 20) : new Color(215, 215, 215));
        }
        FontMetrics fm = g2.getFontMetrics();
        String sym = p.getSimbolo();
        g2.drawString(sym, px - fm.stringWidth(sym) / 2, py + fm.getAscent() / 2 - 2);
    }

    private void pintarSeleccion(Graphics2D g2) {
        if (seleccionada == null) return;
        int px = cx(seleccionada[0]);
        int py = cy(seleccionada[1]);
        int r  = CELDA / 2 - 2;
        g2.setColor(new Color(70, 220, 70, 170));
        g2.setStroke(new BasicStroke(3f));
        g2.drawOval(px - r, py - r, r * 2, r * 2);
    }

    // ── Coordenadas ───────────────────────────────────────────────────────────

    private int cx(int col)  { return MARGEN + col * CELDA; }
    private int cy(int fila) { return MARGEN + fila * CELDA; }

    // ── Estado ────────────────────────────────────────────────────────────────

    public void actualizarEstado() {
        lblJugadores.setText(
            "🔴 " + partida.getJugadorRojo().getUsername()
            + "   vs   ⚫ " + partida.getJugadorNegro().getUsername());

        lblContadorTurnos.setText("Movimientos: " + partida.getMovimientos());

        if (partida.isTerminada()) {
            finalizarPartida();
            return;
        }

        String colorActual   = partida.getColorActual();
        String nombreJugador = partida.getJugadorActual().getUsername();
        String icono         = colorActual.equals("rojo") ? "🔴" : "⚫";

        if (partida.estaEnJaque(colorActual)) {
            lblTurno.setForeground(new Color(255, 80, 80));
            lblTurno.setText("⚠️  " + nombreJugador + " ESTÁ EN JAQUE  ⚠️");
        } else {
            lblTurno.setForeground(EstiloUI.ACENTO_ORO);
            lblTurno.setText("Turno: " + icono + " " + nombreJugador);
        }

        panelTablero.repaint();
    }

    private void finalizarPartida() {
        String ganador  = partida.getGanador();
        boolean retiro  = partida.isTerminadaPorRetiro();

        String perdedor = ganador.equals(partida.getJugadorRojo().getUsername())
                ? partida.getJugadorNegro().getUsername()
                : partida.getJugadorRojo().getUsername();

        servicio.finalizarPartida();

        String mensaje = retiro
            ? perdedor + " SE HA RETIRADO\n¡FELICIDADES " + ganador + "!\nHAS GANADO 3 PUNTOS"
            : ganador  + " HA VENCIDO A " + perdedor + "\n¡FELICIDADES " + ganador + "!\nHAS GANADO 3 PUNTOS";

        JOptionPane.showMessageDialog(this, mensaje, "Fin de la Partida",
                                      JOptionPane.INFORMATION_MESSAGE);
        dispose();
        menuFrame.actualizarInfo();
        menuFrame.setVisible(true);
    }
}
