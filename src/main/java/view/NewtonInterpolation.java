package view;

import model.Point;
import java.lang.Math;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class NewtonInterpolation {
    private JPanel mainPanel;
    private JTextField textPointY;
    private JButton btnAddPoint;
    private JTextField textPointX;
    private JTable tablePoints;
    private JTextField textXtoCalc;
    private JButton btnCalc;
    private ArrayList<Point> points;
    private boolean pointAlreadyExists(Point p) {
        for (Point point : points) {
            if (point.getX() == p.getX()) {
                return true;
            }
        }
        return false;
    }

    private double calcNewton(double x) {
        double result = 0;
        StringBuilder newton = new StringBuilder();

        double[] y = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            y[i] = points.get(i).getY();
        }

        for (int i = 0; i < points.size() - 1; i++) {
            for (int j = points.size() - 1; j > i; j--) {
                y[j] = (y[j] - y[j - 1]) / (points.get(j).getX() - points.get(j - i - 1).getX());
            }
        }

        for (int i = 0; i < points.size(); i++) {
            double temp = y[i];
            for (int j = 0; j < i; j++) {
                temp *= (x - points.get(j).getX());
            }
            result += temp;
        }

        for (int i = 0; i < points.size(); i++) {
            newton.append(y[i]);
            for (int j = 0; j < i; j++) {
                if (points.get(j).getX() < 0)
                    newton.append("(x + ").append(Math.abs(points.get(j).getX())).append(")");
                else
                    newton.append("(x - ").append(points.get(j).getX()).append(")");
                if(j != i - 1 && j >= 0)
                    newton.append(" * ");
            }
            newton.append(" + ");
        }

        newton = new StringBuilder(newton.substring(0, newton.length() - 3));
        System.out.println(newton);
        return result;
    }

    private void updateTable(ArrayList<Point> points) {
        Object[][] data = new Object[points.size()][3];

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            data[i][0] = i;
            data[i][1] = point.getX();
            data[i][2] = point.getY();
        }

        DefaultTableModel model = new DefaultTableModel(data, new String[]{"#", "X", "Y"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablePoints.setModel(model);
    }

    public NewtonInterpolation() {
        JFrame frame = new JFrame("Interpolação de Newton");
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(mainPanel);

        this.points = new ArrayList();
        updateTable(points);

        btnAddPoint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double x = Double.parseDouble(textPointX.getText());
                    double y = Double.parseDouble(textPointY.getText());
                    Point p = new Point(x, y);

                    if (pointAlreadyExists(p)) {
                        for (Point point : points) {
                            if (point.getX() == p.getX()) {
                                point.setY(y);
                                updateTable(points);
                                return;
                            }
                        }
                    }

                    points.add(p);
                    updateTable(points);
                } catch (Exception exception) {

                    if(exception.getMessage() != null)
                        System.out.println(exception.getMessage());
                    else
                        System.out.println("Invalid input");
                }
            }
        });
        btnCalc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double x = Double.parseDouble(textXtoCalc.getText());
                    double y = calcNewton(x);
                    JOptionPane.showMessageDialog(null, "y = " + y);
                } catch (Exception exception) {
                    System.out.println("Invalid input " + exception.getMessage());
                }
            }
        });
        tablePoints.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tablePoints.rowAtPoint(evt.getPoint());
                int col = tablePoints.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    Point p = points.get(row);
                    if (evt.getButton() == 3) {
                        points.remove(p);
                        updateTable(points);
                        return;
                    }
                    textPointX.setText(String.valueOf(p.getX()));
                    textPointY.setText(String.valueOf(p.getY()));
                }
            }
        });
    }
}
