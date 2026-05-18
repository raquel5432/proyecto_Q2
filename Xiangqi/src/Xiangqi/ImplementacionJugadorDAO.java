package xiangqi;

import java.io.*;
import java.util.ArrayList;

public class ImplementacionJugadorDAO implements InterfazJugadorDAO {

    private static final String ARCHIVO = "data/jugadores.txt";

    public ImplementacionJugadorDAO() {
        inicializarArchivo();
    }

    private void inicializarArchivo() {
        try {
            File dir = new File("data");
            if (!dir.exists()) dir.mkdirs();
            File f = new File(ARCHIVO);
            if (!f.exists()) f.createNewFile();
        } catch (Exception e) {
            System.err.println("Error inicializando archivo de jugadores: " + e.getMessage());
        }
    }

    @Override
    public void guardar(Jugador jugador) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO, true))) {
            pw.println(jugador.serializar());
        } catch (IOException e) {
            System.err.println("Error guardando jugador: " + e.getMessage());
        }
    }

    @Override
    public Jugador buscarPorUsername(String username) {
        for (Jugador j : obtenerTodos()) {
            if (j.getUsername().equalsIgnoreCase(username)) return j;
        }
        return null;
    }

    @Override
    public ArrayList<Jugador> obtenerTodos() {
        ArrayList<Jugador> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    Jugador j = Jugador.deserializar(linea);
                    if (j != null) lista.add(j);
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo jugadores: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Jugador jugador) {
        ArrayList<Jugador> todos = obtenerTodos();
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO, false))) {
            for (Jugador j : todos) {
                if (j.getUsername().equalsIgnoreCase(jugador.getUsername())) {
                    pw.println(jugador.serializar());
                } else {
                    pw.println(j.serializar());
                }
            }
        } catch (IOException e) {
            System.err.println("Error actualizando jugador: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(String username) {
        ArrayList<Jugador> todos = obtenerTodos();
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO, false))) {
            for (Jugador j : todos) {
                if (!j.getUsername().equalsIgnoreCase(username)) {
                    pw.println(j.serializar());
                }
            }
        } catch (IOException e) {
            System.err.println("Error eliminando jugador: " + e.getMessage());
        }
    }

    @Override
    public boolean existeUsername(String username) {
        return buscarPorUsername(username) != null;
    }
}
