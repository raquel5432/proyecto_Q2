package xiangqi;

public class Caballo extends Pieza {

    public Caballo(int x, int y, String color) {
        super(x, y, color, "Caballo");
    }

    @Override
    public String getSimbolo() {
        return color.equals("rojo") ? "傌" : "馬";
    }

    @Override
    public boolean movimientoValido(int nx, int ny, Tablero tablero) {
        if (!dentroYLibre(nx, ny, tablero)) return false;

        int dx = nx - x;
        int dy = ny - y;

        if (!((Math.abs(dx) == 1 && Math.abs(dy) == 2) ||
              (Math.abs(dx) == 2 && Math.abs(dy) == 1))) return false;

        int px, py;
        if (Math.abs(dx) == 2) {
            px = x + (dx > 0 ? 1 : -1);
            py = y;
        } else {
            px = x;
            py = y + (dy > 0 ? 1 : -1);
        }
        return tablero.getPieza(px, py) == null;
    }
}
