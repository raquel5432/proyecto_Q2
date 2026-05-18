package xiangqi;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla inicial: Login / Crear jugador / Salir.
 */
public class LoginFrame extends JFrame {

    private final JuegoService servicio;

    public LoginFrame(JuegoService servicio) {
        this.servicio = servicio;
        configurar();
        construirUI();
    }

    private void configurar() {
        setTitle("Xiangqi – Ajedrez Chino");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(EstiloUI.FONDO_OSCURO);
    }

    private void construirUI() {
        setLayout(new BorderLayout());

        // ── Encabezado ──
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(EstiloUI.FONDO_OSCURO);
        header.setBorder(BorderFactory.createEmptyBorder(44, 20, 16, 20));

        JLabel titulo = EstiloUI.crearTitulo("象棋  XIANGQI");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtit = new JLabel("Ajedrez Chino", SwingConstants.CENTER);
        subtit.setFont(EstiloUI.FUENTE_SUBTIT);
        subtit.setForeground(EstiloUI.TEXTO_GRIS);
        subtit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel deco = new JLabel("将  帅  車  馬  炮  象  兵", SwingConstants.CENTER);
        deco.setFont(new Font("Serif", Font.PLAIN, 20));
        deco.setForeground(new Color(170, 130, 50));
        deco.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titulo);
        header.add(Box.createVerticalStrut(8));
        header.add(subtit);
        header.add(Box.createVerticalStrut(10));
        header.add(deco);

        // ── Botones ──
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(EstiloUI.FONDO_OSCURO);
        centro.setBorder(BorderFactory.createEmptyBorder(30, 110, 40, 110));

        JButton btnLogin = EstiloUI.crearBoton("Iniciar Sesión");
        JButton btnCrear = EstiloUI.crearBotonSecundario("Crear Jugador");
        JButton btnSalir = EstiloUI.crearBotonSecundario("Salir");

        for (JButton btn : new JButton[]{btnLogin, btnCrear, btnSalir}) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            centro.add(btn);
            centro.add(Box.createVerticalStrut(14));
        }

        add(header, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> mostrarDialogLogin());
        btnCrear.addActionListener(e -> mostrarDialogCrear());
        btnSalir.addActionListener(e -> System.exit(0));
    }

    private void mostrarDialogLogin() {
        // ── Dialog de Login ──
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(EstiloUI.FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;

        JLabel tit = EstiloUI.crearTitulo("Iniciar Sesión");
        tit.setFont(EstiloUI.FUENTE_SUBTIT);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        panel.add(tit, g);

        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 1; panel.add(EstiloUI.crearLabel("Usuario:"), g);
        JTextField tfUser = EstiloUI.crearCampo(15);
        g.gridx = 1; g.gridy = 1; panel.add(tfUser, g);

        g.gridx = 0; g.gridy = 2; panel.add(EstiloUI.crearLabel("Contraseña:"), g);
        JPasswordField pfPass = EstiloUI.crearCampoPassword(15);
        g.gridx = 1; g.gridy = 2; panel.add(pfPass, g);

        int res = JOptionPane.showConfirmDialog(this, panel,
                  "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            String user = tfUser.getText().trim();
            String pass = new String(pfPass.getPassword());
            if (servicio.login(user, pass)) {
                setVisible(false);
                new MenuFrame(servicio, this).setVisible(true);
            } else {
                EstiloUI.mostrarError(this, "Usuario o contraseña incorrectos.");
            }
        }
    }

    private void mostrarDialogCrear() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(EstiloUI.FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;

        JLabel tit = EstiloUI.crearTitulo("Crear Jugador");
        tit.setFont(EstiloUI.FUENTE_SUBTIT);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        panel.add(tit, g);

        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 1; panel.add(EstiloUI.crearLabel("Username:"), g);
        JTextField tfUser = EstiloUI.crearCampo(15);
        g.gridx = 1; g.gridy = 1; panel.add(tfUser, g);

        g.gridx = 0; g.gridy = 2; panel.add(EstiloUI.crearLabel("Contraseña (5 chars):"), g);
        JPasswordField pfPass = EstiloUI.crearCampoPassword(15);
        g.gridx = 1; g.gridy = 2; panel.add(pfPass, g);

        JLabel nota = new JLabel("* Exactamente 5 caracteres alfanuméricos");
        nota.setFont(new Font("SansSerif", Font.ITALIC, 11));
        nota.setForeground(EstiloUI.TEXTO_GRIS);
        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        panel.add(nota, g);

        int res = JOptionPane.showConfirmDialog(this, panel,
                  "Crear Jugador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            String user = tfUser.getText().trim();
            String pass = new String(pfPass.getPassword());
            String error = servicio.crearJugador(user, pass);
            if (error == null) {
                EstiloUI.mostrarInfo(this, "¡Jugador \"" + user + "\" creado exitosamente!");
            } else {
                EstiloUI.mostrarError(this, error);
            }
        }
    }
}
