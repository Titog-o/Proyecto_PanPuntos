/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_panpuntos;

/**
 *
 * @author Tito Gomez
 */
import javax.swing.*;

public class FrmPrincipal extends JFrame {

    public FrmPrincipal() {
        setTitle("Pan, Puntos y Premios - Menú Principal");
        setSize(400, 400);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton btn1 = new JButton("1. Gestión de Clientes");
        btn1.setBounds(80, 30, 230, 35);
        add(btn1);

        JButton btn2 = new JButton("2. Abastecer Inventario");
        btn2.setBounds(80, 80, 230, 35);
        add(btn2);

        JButton btn3 = new JButton("3. Crear y Cobrar Pedido");
        btn3.setBounds(80, 130, 230, 35);
        add(btn3);

        JButton btn4 = new JButton("4. Canjear Puntos");
        btn4.setBounds(80, 180, 230, 35);
        add(btn4);

        JButton btn5 = new JButton("5. Reportes");
        btn5.setBounds(80, 230, 230, 35);
        add(btn5);

        btn1.addActionListener(e -> new FrmCliente().setVisible(true));
        btn2.addActionListener(e -> new FrmAbastecimiento().setVisible(true));
        btn3.addActionListener(e -> new FrmPedido().setVisible(true));
        btn4.addActionListener(e -> new FrmCanje().setVisible(true));
        btn5.addActionListener(e -> new FrmReportes().setVisible(true));
    }
}
