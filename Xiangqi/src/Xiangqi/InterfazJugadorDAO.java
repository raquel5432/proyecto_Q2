package xiangqi;

import java.util.ArrayList;

public interface InterfazJugadorDAO {
    void               guardar(Jugador jugador);
    Jugador            buscarPorUsername(String username);
    ArrayList<Jugador> obtenerTodos();
    void               actualizar(Jugador jugador);
    void               eliminar(String username);
    boolean            existeUsername(String username);
}
