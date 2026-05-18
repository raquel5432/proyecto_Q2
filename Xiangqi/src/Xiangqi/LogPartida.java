package xiangqi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Registro de una partida jugada (para el historial/logs).
 * Incluye resultado (captura del General o retiro) e historial de movimientos.
 */
public class LogPartida {

    private String jugadorRojo;
    private String jugadorNegro;
    private String ganador;
    private int    movimientos;
    private String fecha;
    private String resultado;   // "captura" o "retiro"

    /** Historial de movimientos de esta partida (no se persiste en archivo, solo en memoria). */
    private ArrayList<HistorialMovimiento> historialMovimientos;

    // ── Constructores ────────────────────────────────────────────────────────

    public LogPartida(String jugadorRojo, String jugadorNegro,
                      String ganador, int movimientos, boolean porRetiro,
                      ArrayList<HistorialMovimiento> historial) {
        this.jugadorRojo  = jugadorRojo;
        this.jugadorNegro = jugadorNegro;
        this.ganador      = ganador;
        this.movimientos  = movimientos;
        this.resultado    = porRetiro ? "retiro" : "captura";
        this.fecha        = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.historialMovimientos = (historial != null) ? historial : new ArrayList<>();
    }

    /** Constructor para carga desde archivo (sin historial detallado). */
    public LogPartida(String jugadorRojo, String jugadorNegro,
                      String ganador, int movimientos, String fecha, String resultado) {
        this.jugadorRojo          = jugadorRojo;
        this.jugadorNegro         = jugadorNegro;
        this.ganador              = ganador;
        this.movimientos          = movimientos;
        this.fecha                = fecha;
        this.resultado            = resultado;
        this.historialMovimientos = new ArrayList<>();
    }

    // ── Serialización ────────────────────────────────────────────────────────

    public String serializar() {
        return jugadorRojo + ";" + jugadorNegro + ";"
               + ganador + ";" + movimientos + ";" + fecha + ";" + resultado;
    }

    public static LogPartida deserializar(String linea) {
        try {
            String[] p = linea.split(";");
            String res = (p.length >= 6) ? p[5] : "captura";
            return new LogPartida(p[0], p[1], p[2],
                                  Integer.parseInt(p[3]), p[4], res);
        } catch (Exception e) {
            return null;
        }
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String  getJugadorRojo()   { return jugadorRojo; }
    public String  getJugadorNegro()  { return jugadorNegro; }
    public String  getGanador()       { return ganador; }
    public int     getMovimientos()   { return movimientos; }
    public String  getFecha()         { return fecha; }
    public String  getResultado()     { return resultado; }

    public ArrayList<HistorialMovimiento> getHistorialMovimientos() {
        return historialMovimientos;
    }

    public boolean fueRetiro() {
        return "retiro".equalsIgnoreCase(resultado);
    }

    @Override
    public String toString() {
        return fecha + " | " + jugadorRojo + " vs " + jugadorNegro
               + " | Ganador: " + ganador
               + " | Resultado: " + resultado
               + " | Movs: " + movimientos;
    }
}
