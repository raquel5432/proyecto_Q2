package xiangqi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Registra un movimiento o captura ocurrida durante una partida.
 * Almacenado en ArrayList dentro de Partida.
 */
public class HistorialMovimiento {

    private final int    turno;
    private final String jugador;
    private final String piezaMovida;
    private final String piezaCapturada;   // null si no hubo captura
    private final int    origenX;
    private final int    origenY;
    private final int    destinoX;
    private final int    destinoY;
    private final String fecha;
    private final String descripcion;

    public HistorialMovimiento(int turno, String jugador,
                               String piezaMovida, String piezaCapturada,
                               int origenX, int origenY,
                               int destinoX, int destinoY) {
        this.turno          = turno;
        this.jugador        = jugador;
        this.piezaMovida    = piezaMovida;
        this.piezaCapturada = piezaCapturada;
        this.origenX        = origenX;
        this.origenY        = origenY;
        this.destinoX       = destinoX;
        this.destinoY       = destinoY;
        this.fecha          = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        // Construir descripción legible
        String colPieza = piezaMovida.contains("rojo") ? "Rojo" : "Negro";
        String nombrePieza = piezaMovida.replace(" rojo", "").replace(" negro", "");
        String origen  = colToLetra(origenX) + (origenY + 1);
        String destino = colToLetra(destinoX) + (destinoY + 1);

        if (piezaCapturada != null && !piezaCapturada.isEmpty()) {
            String colCap = piezaCapturada.contains("rojo") ? "Rojo" : "Negro";
            String nombreCap = piezaCapturada.replace(" rojo", "").replace(" negro", "");
            this.descripcion = "Turno " + turno + ": " + nombrePieza + " " + colPieza
                    + " comió " + nombreCap + " " + colCap
                    + " (" + origen + " → " + destino + ")";
        } else {
            this.descripcion = "Turno " + turno + ": " + nombrePieza + " " + colPieza
                    + " movió " + origen + " → " + destino;
        }
    }

    /** Convierte columna 0-8 a letra a-i */
    private static String colToLetra(int col) {
        return String.valueOf((char)('a' + col));
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    public int    getTurno()          { return turno; }
    public String getJugador()        { return jugador; }
    public String getPiezaMovida()    { return piezaMovida; }
    public String getPiezaCapturada() { return piezaCapturada; }
    public int    getOrigenX()        { return origenX; }
    public int    getOrigenY()        { return origenY; }
    public int    getDestinoX()       { return destinoX; }
    public int    getDestinoY()       { return destinoY; }
    public String getFecha()          { return fecha; }
    public String getDescripcion()    { return descripcion; }

    public boolean esCaptura() {
        return piezaCapturada != null && !piezaCapturada.isEmpty();
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
