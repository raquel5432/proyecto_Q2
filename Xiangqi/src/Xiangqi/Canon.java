package xiangqi;

public class Canon extends Pieza {

    public Canon(int x, int y, String color) {
        super(x, y, color, "Canon");
    }

    @Override
    public String getSimbolo() {
        return color.equals("rojo") ? "炮" : "砲";
    }

    @Override
    public boolean movimientoValido(int nx, int ny, Tablero tablero) {
        if (nx < 0 || nx >= Tablero.COLUMNAS) return false;
        if (ny < 0 || ny >= Tablero.FILAS)    return false;
        if (nx == x && ny == y) return false;
        if (nx != x && ny != y) return false;

        Pieza destino = tablero.getPieza(nx, ny);
        int piezasEnMedio = tablero.contarPiezasEnLinea(x, y, nx, ny);

        if (destino == null) {
            return piezasEnMedio == 0;
        } else {
            return piezasEnMedio == 1 && !destino.getColor().equals(this.color);
        }
    }
}
