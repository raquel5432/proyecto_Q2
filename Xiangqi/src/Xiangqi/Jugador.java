package xiangqi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class Jugador {

    private String  username;
    private String  password;
    private int     puntos;
    private String  fechaCreacion;
    private boolean activo;

    public Jugador(String username, String password) {
        this.username      = username;
        this.password      = password;
        this.puntos        = 0;
        this.fechaCreacion = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.activo        = true;
    }

    public Jugador(String username, String password, int puntos,
                   String fechaCreacion, boolean activo) {
        this.username      = username;
        this.password      = password;
        this.puntos        = puntos;
        this.fechaCreacion = fechaCreacion;
        this.activo        = activo;
    }

    public static boolean passwordValida(String pwd) {
        if (pwd == null) return false;
        return pwd.matches("[a-zA-Z0-9]{5}");
    }

    public String  getUsername()      { return username; }
    public String  getPassword()      { return password; }
    public int     getPuntos()        { return puntos; }
    public String  getFechaCreacion() { return fechaCreacion; }
    public boolean isActivo()         { return activo; }

    public void setPassword(String p) { this.password = p; }
    public void setActivo(boolean a)  { this.activo = a; }
    public void agregarPuntos(int p)  { this.puntos += p; }

    public String serializar() {
        return username + ";" + password + ";" + puntos + ";"
               + fechaCreacion + ";" + activo;
    }

    public static Jugador deserializar(String linea) {
        try {
            String[] p = linea.split(";");
            return new Jugador(p[0], p[1], Integer.parseInt(p[2]),
                               p[3], Boolean.parseBoolean(p[4]));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return username + " [" + puntos + " pts]";
    }
}
