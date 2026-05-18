package xiangqi;

import java.util.ArrayList;

public abstract class Pieza {

    protected int x;
    protected int y;
    protected String color;
    protected String nombre;

    public Pieza(int x, int y, String color, String nombre) {
        this.x      = x;
        this.y      = y;
        this.color  = color;
        this.nombre = nombre;
    }

    public abstract boolean movimientoValido(int nx, int ny, Tablero tablero);

    public ArrayList<int[]> obtenerMovimientos(Tablero tablero) {
        ArrayList<int[]> lista = new ArrayList<>();
        for (int fy = 0; fy < Tablero.FILAS; fy++) {
            for (int fx = 0; fx < Tablero.COLUMNAS; fx++) {
                if (movimientoValido(fx, fy, tablero)) {
                    lista.add(new int[]{fx, fy});
                }
            }
        }
        return lista;
    }

    public final boolean dentroYLibre(int nx, int ny, Tablero tablero) {
        if (nx < 0 || nx >= Tablero.COLUMNAS) return false;
        if (ny < 0 || ny >= Tablero.FILAS)    return false;
        Pieza destino = tablero.getPieza(nx, ny);
        if (destino != null && destino.color.equals(this.color)) return false;
        return true;
    }

    public int    getX()      { return x; }
    public int    getY()      { return y; }
    public String getColor()  { return color; }
    public String getNombre() { return nombre; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public abstract String getSimbolo();

    @Override
    public String toString() {
        return nombre + "(" + x + "," + y + ")[" + color + "]";
    }
}
