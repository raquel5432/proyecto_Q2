package xiangqi;

import java.util.ArrayList;

/**
 * Controla el estado de una partida en curso.
 * Registra historial completo de movimientos y capturas.
 */
public class Partida {

    private Tablero tablero;
    private Jugador jugadorRojo;
    private Jugador jugadorNegro;
    private boolean turnoRojo;
    private boolean terminada;
    private String  ganador;
    private int     movimientos;
    private boolean terminadaPorRetiro;

    /** Historial completo de movimientos (incluyendo capturas). */
    private final ArrayList<HistorialMovimiento> historial;

    public Partida(Jugador jugadorRojo, Jugador jugadorNegro) {
        this.jugadorRojo        = jugadorRojo;
        this.jugadorNegro       = jugadorNegro;
        this.turnoRojo          = true;
        this.terminada          = false;
        this.ganador            = null;
        this.movimientos        = 0;
        this.terminadaPorRetiro = false;
        this.tablero            = new Tablero();
        this.historial          = new ArrayList<>();
        inicializarTablero();
    }

    // ── Posición inicial ────────────────────────────────────────────────────

    private void inicializarTablero() {
        // ── Piezas NEGRAS (filas 0-4) ──
        tablero.setPieza(0, 0, new Carro(0, 0, "negro"));
        tablero.setPieza(1, 0, new Caballo(1, 0, "negro"));
        tablero.setPieza(2, 0, new Elefante(2, 0, "negro"));
        tablero.setPieza(3, 0, new Consejero(3, 0, "negro"));
        tablero.setPieza(4, 0, new General(4, 0, "negro"));
        tablero.setPieza(5, 0, new Consejero(5, 0, "negro"));
        tablero.setPieza(6, 0, new Elefante(6, 0, "negro"));
        tablero.setPieza(7, 0, new Caballo(7, 0, "negro"));
        tablero.setPieza(8, 0, new Carro(8, 0, "negro"));
        tablero.setPieza(1, 2, new Canon(1, 2, "negro"));
        tablero.setPieza(7, 2, new Canon(7, 2, "negro"));
        for (int cx : new int[]{0, 2, 4, 6, 8}) {
            tablero.setPieza(cx, 3, new Soldado(cx, 3, "negro"));
        }

        // ── Piezas ROJAS (filas 5-9) ──
        tablero.setPieza(0, 9, new Carro(0, 9, "rojo"));
        tablero.setPieza(1, 9, new Caballo(1, 9, "rojo"));
        tablero.setPieza(2, 9, new Elefante(2, 9, "rojo"));
        tablero.setPieza(3, 9, new Consejero(3, 9, "rojo"));
        tablero.setPieza(4, 9, new General(4, 9, "rojo"));
        tablero.setPieza(5, 9, new Consejero(5, 9, "rojo"));
        tablero.setPieza(6, 9, new Elefante(6, 9, "rojo"));
        tablero.setPieza(7, 9, new Caballo(7, 9, "rojo"));
        tablero.setPieza(8, 9, new Carro(8, 9, "rojo"));
        tablero.setPieza(1, 7, new Canon(1, 7, "rojo"));
        tablero.setPieza(7, 7, new Canon(7, 7, "rojo"));
        for (int cx : new int[]{0, 2, 4, 6, 8}) {
            tablero.setPieza(cx, 6, new Soldado(cx, 6, "rojo"));
        }
    }

    // ── Lógica de movimiento ────────────────────────────────────────────────

    /**
     * Intenta mover la pieza de (ox,oy) a (dx,dy).
     * Devuelve true si el movimiento fue válido y se realizó.
     */
    public boolean realizarMovimiento(int ox, int oy, int dx, int dy) {
        if (terminada) return false;

        Pieza pieza = tablero.getPieza(ox, oy);
        if (pieza == null) return false;

        String colorActual = turnoRojo ? "rojo" : "negro";
        if (!pieza.getColor().equals(colorActual)) return false;

        if (!pieza.movimientoValido(dx, dy, tablero)) return false;

        // Guardar referencia a la pieza en destino antes de mover
        Pieza piezaDestino = tablero.getPieza(dx, dy);

        // Simular movimiento para verificar que no deja al propio General en jaque
        Pieza capturada = tablero.mover(ox, oy, dx, dy);

        // Verificar jaque propio o generales mirándose
        boolean ilegal = tablero.generalesSeMiran();
        if (!ilegal) {
            Pieza gPropio = tablero.buscarGeneral(colorActual);
            if (gPropio != null) {
                String colorEnemigo = turnoRojo ? "negro" : "rojo";
                ArrayList<Pieza> enemigas = tablero.getPiezasBando(colorEnemigo);
                ilegal = tablero.posicionAtacada(
                    gPropio.getX(), gPropio.getY(), colorEnemigo, enemigas, 0);
            }
        }

        if (ilegal) {
            // Revertir el movimiento
            tablero.mover(dx, dy, ox, oy);
            tablero.setPieza(dx, dy, capturada);
            if (capturada != null) {
                capturada.setX(dx);
                capturada.setY(dy);
            }
            pieza.setX(ox);
            pieza.setY(oy);
            return false;
        }

        movimientos++;

        // Registrar en historial
        String jugadorActual = turnoRojo
                ? jugadorRojo.getUsername()
                : jugadorNegro.getUsername();
        String nombrePieza = pieza.getNombre() + " " + pieza.getColor();
        String nombreCapturada = (capturada != null)
                ? capturada.getNombre() + " " + capturada.getColor()
                : null;

        historial.add(new HistorialMovimiento(
                movimientos, jugadorActual,
                nombrePieza, nombreCapturada,
                ox, oy, dx, dy));

        // ¿Se capturó al General enemigo?
        if (capturada instanceof General) {
            ganador   = colorActual.equals("rojo")
                        ? jugadorRojo.getUsername()
                        : jugadorNegro.getUsername();
            terminada = true;
            return true;
        }

        turnoRojo = !turnoRojo;
        return true;
    }

    /** El jugador actual se retira; el oponente gana. */
    public void retirarse() {
        ganador             = turnoRojo ? jugadorNegro.getUsername() : jugadorRojo.getUsername();
        terminada           = true;
        terminadaPorRetiro  = true;
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    public Tablero                      getTablero()           { return tablero; }
    public boolean                      isTurnoRojo()          { return turnoRojo; }
    public boolean                      isTerminada()          { return terminada; }
    public String                       getGanador()           { return ganador; }
    public int                          getMovimientos()       { return movimientos; }
    public Jugador                      getJugadorRojo()       { return jugadorRojo; }
    public Jugador                      getJugadorNegro()      { return jugadorNegro; }
    public boolean                      isTerminadaPorRetiro() { return terminadaPorRetiro; }
    public ArrayList<HistorialMovimiento> getHistorial()       { return historial; }

    public String getColorActual() {
        return turnoRojo ? "rojo" : "negro";
    }

    public Jugador getJugadorActual() {
        return turnoRojo ? jugadorRojo : jugadorNegro;
    }

    /** Devuelve solo las capturas del historial. */
    public ArrayList<HistorialMovimiento> getCapturas() {
        ArrayList<HistorialMovimiento> caps = new ArrayList<>();
        for (HistorialMovimiento h : historial) {
            if (h.esCaptura()) caps.add(h);
        }
        return caps;
    }
}
