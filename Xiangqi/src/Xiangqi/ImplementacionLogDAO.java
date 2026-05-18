package xiangqi;

import java.io.*;
import java.util.ArrayList;

public class ImplementacionLogDAO implements InterfazLogDAO {

    private static final String ARCHIVO = "data/logs.txt";

    public ImplementacionLogDAO() {
        try {
            File dir = new File("data");
            if (!dir.exists()) dir.mkdirs();
            File f = new File(ARCHIVO);
            if (!f.exists()) f.createNewFile();
        } catch (Exception e) {
            System.err.println("Error inicializando archivo de logs: " + e.getMessage());
        }
    }

    @Override
    public void guardarLog(LogPartida log) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO, true))) {
            pw.println(log.serializar());
        } catch (IOException e) {
            System.err.println("Error guardando log: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<LogPartida> obtenerTodos() {
        ArrayList<LogPartida> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    LogPartida lp = LogPartida.deserializar(linea);
                    if (lp != null) lista.add(lp);
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo logs: " + e.getMessage());
        }
        return lista;
    }
}
