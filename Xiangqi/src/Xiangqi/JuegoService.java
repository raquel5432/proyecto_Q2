package xiangqi;

import java.util.ArrayList;

/**
 * Servicio central: coordina toda la lógica de negocio.
 * Usa las interfaces DAO para desacoplar el almacenamiento.
 */
public class JuegoService {

    private final InterfazJugadorDAO jugadorDAO;
    private final InterfazLogDAO     logDAO;
    private Jugador                  sesionActual;
    private Partida                  partidaActual;

    /** Historial global de capturas de todas las partidas de la sesión. */
    private final ArrayList<HistorialMovimiento> historialCapturas;

    public JuegoService() {
        this.jugadorDAO       = new ImplementacionJugadorDAO();
        this.logDAO           = new ImplementacionLogDAO();
        this.historialCapturas = new ArrayList<>();
    }

    // ── Autenticación ───────────────────────────────────────────────────────

    /**
     * Intenta hacer login. Devuelve true si las credenciales son correctas.
     */
    public boolean login(String username, String password) {
        try {
            Jugador j = jugadorDAO.buscarPorUsername(username);
            if (j == null || !j.isActivo()) return false;
            if (!j.getPassword().equals(password)) return false;
            sesionActual = j;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void logout() {
        sesionActual  = null;
        partidaActual = null;
    }

    public Jugador getSesionActual() { return sesionActual; }

    // ── Gestión de jugadores ────────────────────────────────────────────────

    /**
     * Crea un nuevo jugador. Devuelve null si fue exitoso, o un mensaje de error.
     */
    public String crearJugador(String username, String password) {
        try {
            if (username == null || username.trim().isEmpty())
                return "El username no puede estar vacío.";
            if (jugadorDAO.existeUsername(username.trim()))
                return "Ese username ya está en uso.";
            if (!Jugador.passwordValida(password))
                return "La contraseña debe tener exactamente 5 caracteres alfanuméricos.";

            jugadorDAO.guardar(new Jugador(username.trim(), password));
            return null; // éxito
        } catch (Exception e) {
            return "Error al crear jugador: " + e.getMessage();
        }
    }

    public String cambiarPassword(String nuevaPassword) {
        try {
            if (!Jugador.passwordValida(nuevaPassword))
                return "La contraseña debe tener exactamente 5 caracteres alfanuméricos.";
            sesionActual.setPassword(nuevaPassword);
            jugadorDAO.actualizar(sesionActual);
            return null;
        } catch (Exception e) {
            return "Error al cambiar contraseña: " + e.getMessage();
        }
    }

    public String eliminarCuenta() {
        try {
            sesionActual.setActivo(false);
            jugadorDAO.actualizar(sesionActual);
            sesionActual = null;
            return null;
        } catch (Exception e) {
            return "Error al eliminar cuenta: " + e.getMessage();
        }
    }

    // ── Partida ─────────────────────────────────────────────────────────────

    /**
     * Inicia una partida contra el oponente indicado.
     * Devuelve null si fue exitoso, o un mensaje de error.
     */
    public String iniciarPartida(String usernameOponente) {
        try {
            if (usernameOponente.equalsIgnoreCase(sesionActual.getUsername()))
                return "No puedes jugar contra ti mismo.";
            Jugador oponente = jugadorDAO.buscarPorUsername(usernameOponente);
            if (oponente == null || !oponente.isActivo())
                return "Jugador no encontrado o inactivo.";
            partidaActual = new Partida(sesionActual, oponente);
            return null;
        } catch (Exception e) {
            return "Error al iniciar partida: " + e.getMessage();
        }
    }

    public Partida getPartidaActual() { return partidaActual; }

    /**
     * Finaliza la partida actual: otorga puntos, guarda el log y acumula capturas.
     */
    public void finalizarPartida() {
        try {
            if (partidaActual == null || !partidaActual.isTerminada()) return;

            String ganadorUsername = partidaActual.getGanador();

            // Otorgar +3 puntos al ganador
            Jugador ganador = jugadorDAO.buscarPorUsername(ganadorUsername);
            if (ganador != null) {
                ganador.agregarPuntos(3);
                jugadorDAO.actualizar(ganador);
                // Actualizar sesión si el ganador es el jugador actual
                if (sesionActual != null &&
                    sesionActual.getUsername().equalsIgnoreCase(ganadorUsername)) {
                    sesionActual = ganador;
                }
            }

            // Acumular capturas al historial global
            historialCapturas.addAll(partidaActual.getCapturas());

            // Guardar log
            logDAO.guardarLog(new LogPartida(
                partidaActual.getJugadorRojo().getUsername(),
                partidaActual.getJugadorNegro().getUsername(),
                ganadorUsername,
                partidaActual.getMovimientos(),
                partidaActual.isTerminadaPorRetiro(),
                partidaActual.getHistorial()
            ));

            partidaActual = null;
        } catch (Exception e) {
            System.err.println("Error finalizando partida: " + e.getMessage());
        }
    }

    // ── Reportes ────────────────────────────────────────────────────────────

    /**
     * Devuelve el ranking de jugadores activos ordenado por puntos.
     * Usa ordenarPorPuntos() que es recursiva.
     */
    public ArrayList<Jugador> getRanking() {
        ArrayList<Jugador> todos = jugadorDAO.obtenerTodos();
        ArrayList<Jugador> activos = new ArrayList<>();
        for (Jugador j : todos) {
            if (j.isActivo()) activos.add(j);
        }
        ordenarPorPuntos(activos, 0);
        return activos;
    }

    /**
     * Selection sort recursivo por puntos (mayor a menor).
     * FUNCIÓN RECURSIVA #2 del proyecto.
     */
    private void ordenarPorPuntos(ArrayList<Jugador> lista, int inicio) {
        if (inicio >= lista.size() - 1) return;

        int maxIdx = inicio;
        for (int i = inicio + 1; i < lista.size(); i++) {
            if (lista.get(i).getPuntos() > lista.get(maxIdx).getPuntos()) {
                maxIdx = i;
            }
        }
        if (maxIdx != inicio) {
            Jugador tmp = lista.get(inicio);
            lista.set(inicio, lista.get(maxIdx));
            lista.set(maxIdx, tmp);
        }
        ordenarPorPuntos(lista, inicio + 1); // ← llamada recursiva
    }

    public ArrayList<LogPartida> getLogs() {
        return logDAO.obtenerTodos();
    }

    /**
     * Devuelve el historial global de capturas (todas las partidas de la sesión).
     * Ordenado del más reciente al más antiguo.
     */
    public ArrayList<HistorialMovimiento> getHistorialCapturas() {
        ArrayList<HistorialMovimiento> invertido = new ArrayList<>();
        for (int i = historialCapturas.size() - 1; i >= 0; i--) {
            invertido.add(historialCapturas.get(i));
        }
        return invertido;
    }

    /** Devuelve jugadores activos distintos al usuario en sesión. */
    public ArrayList<Jugador> getJugadoresActivos() {
        ArrayList<Jugador> todos = jugadorDAO.obtenerTodos();
        ArrayList<Jugador> activos = new ArrayList<>();
        for (Jugador j : todos) {
            if (j.isActivo() &&
                !j.getUsername().equalsIgnoreCase(sesionActual.getUsername())) {
                activos.add(j);
            }
        }
        return activos;
    }
}
