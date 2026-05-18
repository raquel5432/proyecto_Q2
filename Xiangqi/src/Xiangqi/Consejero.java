package xiangqi;

public class Consejero extends Pieza {

    public Consejero(int x, int y, String color) {
        super(x, y, color, "Consejero");
    }

    @Override
    public String getSimbolo() {
        return color.equals("rojo") ? "仕" : "士";
    }

    @Override
    public boolean movimientoValido(int nx, int ny, Tablero tablero) {
        if (!dentroYLibre(nx, ny, tablero)) return false;

        int yMin = color.equals("rojo") ? 7 : 0;
        int yMax = color.equals("rojo") ? 9 : 2;
        if (ny < yMin || ny > yMax) return false;
        if (nx < 3 || nx > 5) return false;

        int dx = Math.abs(nx - x);
        int dy = Math.abs(ny - y);
        return (dx == 1 && dy == 1);
    }
}
