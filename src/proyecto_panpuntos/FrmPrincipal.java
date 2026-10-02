package proyecto_panpuntos;

import javax.swing.*;

public class FrmPrincipal extends JFrame {

    public FrmPrincipal() {
        setTitle("Pan, Puntos y Premios - Menú Principal");
        setSize(400, 430);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton btn1 = new JButton("1. Gestión de Clientes");
        btn1.setBounds(80, 25, 230, 35);
        add(btn1);

        JButton btn2 = new JButton("2. Gestión de Productos");
        btn2.setBounds(80, 75, 230, 35);
        add(btn2);

        JButton btn3 = new JButton("3. Abastecer Inventario");
        btn3.setBounds(80, 125, 230, 35);
        add(btn3);

        JButton btn4 = new JButton("4. Crear y Cobrar Pedido");
        btn4.setBounds(80, 175, 230, 35);
        add(btn4);

        JButton btn5 = new JButton("5. Canjear Puntos");
        btn5.setBounds(80, 225, 230, 35);
        add(btn5);

        JButton btn6 = new JButton("6. Reportes");
        btn6.setBounds(80, 275, 230, 35);
        add(btn6);

        btn1.addActionListener(e -> new FrmCliente().setVisible(true));
        btn2.addActionListener(e -> new FrmProducto().setVisible(true));
        btn3.addActionListener(e -> new FrmAbastecimiento().setVisible(true));
        btn4.addActionListener(e -> new FrmPedido().setVisible(true));
        btn5.addActionListener(e -> new FrmCanje().setVisible(true));
        btn6.addActionListener(e -> new FrmReportes().setVisible(true));
    }
}