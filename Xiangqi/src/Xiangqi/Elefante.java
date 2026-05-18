package xiangqi;

public class Elefante extends Pieza {

    public Elefante(int x, int y, String color) {
        super(x, y, color, "Elefante");
    }

    @Override
    public String getSimbolo() {
        return color.equals("rojo") ? "相" : "象";
    }

    @Override
    public boolean movimientoValido(int nx, int ny, Tablero tablero) {
        if (!dentroYLibre(nx, ny, tablero)) return false;

        if (color.equals("rojo")  && ny < 5) return false;
        if (color.equals("negro") && ny > 4) return false;

        int dx = nx - x;
        int dy = ny - y;
        if (Math.abs(dx) != 2 || Math.abs(dy) != 2) return false;

        int mx = x + dx / 2;
        int my = y + dy / 2;
        return tablero.getPieza(mx, my) == null;
    }
}
