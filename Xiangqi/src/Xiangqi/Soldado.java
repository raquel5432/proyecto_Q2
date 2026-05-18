package xiangqi;

public class Soldado extends Pieza {

    public Soldado(int x, int y, String color) {
        super(x, y, color, "Soldado");
    }

    @Override
    public String getSimbolo() {
        return color.equals("rojo") ? "兵" : "卒";
    }

    @Override
    public boolean movimientoValido(int nx, int ny, Tablero tablero) {
        if (!dentroYLibre(nx, ny, tablero)) return false;

        int dx = nx - x;
        int dy = ny - y;

        int avance = color.equals("rojo") ? -1 : 1;
        boolean cruzoRio = color.equals("rojo") ? (y <= 4) : (y >= 5);

        if (dx == 0 && dy == avance) return true;
        if (cruzoRio && dy == 0 && Math.abs(dx) == 1) return true;

        return false;
    }
}
