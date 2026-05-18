package xiangqi;

public class Carro extends Pieza {

    public Carro(int x, int y, String color) {
        super(x, y, color, "Carro");
    }

    @Override
    public String getSimbolo() {
        return color.equals("rojo") ? "俥" : "車";
    }

    @Override
    public boolean movimientoValido(int nx, int ny, Tablero tablero) {
        if (!dentroYLibre(nx, ny, tablero)) return false;
        if (nx != x && ny != y) return false;
        if (nx == x && ny == y) return false;
        return tablero.contarPiezasEnLinea(x, y, nx, ny) == 0;
    }
}
