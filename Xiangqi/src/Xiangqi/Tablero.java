package xiangqi;

import java.util.ArrayList;

public class Tablero {

    public static final int FILAS    = 10;
    public static final int COLUMNAS = 9;

    private Pieza[][] casillas;

    public Tablero() {
        casillas = new Pieza[FILAS][COLUMNAS];
    }

    public Pieza getPieza(int x, int y) {
        if (x < 0 || x >= COLUMNAS || y < 0 || y >= FILAS) return null;
        return casillas[y][x];
    }

    public void setPieza(int x, int y, Pieza p) {
        casillas[y][x] = p;
    }

    public void remover(int x, int y) {
        casillas[y][x] = null;
    }

    public Pieza mover(int ox, int oy, int dx, int dy) {
        Pieza pieza     = casillas[oy][ox];
        Pieza capturada = casillas[dy][dx];

        casillas[dy][dx] = pieza;
        casillas[oy][ox] = null;

        if (pieza != null) {
            pieza.setX(dx);
            pieza.setY(dy);
        }
        return capturada;
    }

    public int contarPiezasEnLinea(int x1, int y1, int x2, int y2) {
        if (x1 == x2) {
            return contarEnColumna(x1, Math.min(y1, y2) + 1, Math.max(y1, y2) - 1);
        } else if (y1 == y2) {
            return contarEnFila(y1, Math.min(x1, x2) + 1, Math.max(x1, x2) - 1);
        }
        return 0;
    }

    private int contarEnColumna(int col, int filaActual, int filaMax) {
        if (filaActual > filaMax) return 0;
        int hay = (casillas[filaActual][col] != null) ? 1 : 0;
        return hay + contarEnColumna(col, filaActual + 1, filaMax);
    }

    private int contarEnFila(int fila, int colActual, int colMax) {
        if (colActual > colMax) return 0;
        int hay = (casillas[fila][colActual] != null) ? 1 : 0;
        return hay + contarEnFila(fila, colActual + 1, colMax);
    }

    public boolean posicionAtacada(int tx, int ty, String colorEnemigo,
                                   ArrayList<Pieza> enemigas, int idx) {
        if (idx >= enemigas.size()) return false;
        Pieza e = enemigas.get(idx);
        if (e.movimientoValido(tx, ty, this)) return true;
        return posicionAtacada(tx, ty, colorEnemigo, enemigas, idx + 1);
    }

    public Pieza buscarGeneral(String color) {
        for (int fy = 0; fy < FILAS; fy++) {
            for (int fx = 0; fx < COLUMNAS; fx++) {
                Pieza p = casillas[fy][fx];
                if (p instanceof General && p.getColor().equals(color)) return p;
            }
        }
        return null;
    }

    public ArrayList<Pieza> getPiezasBando(String color) {
        ArrayList<Pieza> lista = new ArrayList<>();
        for (int fy = 0; fy < FILAS; fy++) {
            for (int fx = 0; fx < COLUMNAS; fx++) {
                Pieza p = casillas[fy][fx];
                if (p != null && p.getColor().equals(color)) lista.add(p);
            }
        }
        return lista;
    }

    public boolean generalesSeMiran() {
        Pieza gr = buscarGeneral("rojo");
        Pieza gn = buscarGeneral("negro");
        if (gr == null || gn == null) return false;
        if (gr.getX() != gn.getX()) return false;
        int yMin = Math.min(gr.getY(), gn.getY());
        int yMax = Math.max(gr.getY(), gn.getY());
        return contarEnColumna(gr.getX(), yMin + 1, yMax - 1) == 0;
    }
}
