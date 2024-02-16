package view;

import javax.swing.*;

public class Menu {
    private JPanel mainPanel;
    private JButton btnLagrange;
    private JButton btnNewton;
    private JButton btnGregory;
    private ImageIcon icon = new ImageIcon("src/view/function-square.png");

    public Menu() {
        JFrame frame = new JFrame("Interpolação Polinomial");
        frame.setSize(400, 300);
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(icon.getImage());
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        btnLagrange.addActionListener(e -> {
            LagrangeInterpolation lagrangeInterpolation = new LagrangeInterpolation();
        });
        btnNewton.addActionListener(e -> {
            NewtonInterpolation newtonInterpolation = new NewtonInterpolation();
        });
        btnGregory.addActionListener(e -> {
            GregoryNewtonInterpolation gregoryNewtonInterpolation = new GregoryNewtonInterpolation();
        });
    }
}
