package xiangqi;

public class General extends Pieza {

    public General(int x, int y, String color) {
        super(x, y, color, "General");
    }

    @Override
    public String getSimbolo() {
        return color.equals("rojo") ? "帅" : "将";
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
        return (dx + dy == 1);
    }
}
