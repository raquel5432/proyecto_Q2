package xiangqi;

import java.util.ArrayList;

public interface InterfazLogDAO {
    void                  guardarLog(LogPartida log);
    ArrayList<LogPartida> obtenerTodos();
}
