package view;

import model.Point;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LagrangeInterpolation {
    private JPanel mainPanel;
    private JTextField textPointX;
    private JTextField textPointY;
    private JButton btnAddPoint;
    private JTable tablePoints;
    private JButton btnCalc;
    private JTextField textXtoCalc;
    private ArrayList<Point> points;
    private StringBuilder lagrange = new StringBuilder();

    private boolean pointAlreadyExists(Point p) {
        for (Point point : points) {
            if (point.getX() == p.getX()) {
                return true;
            }
        }
        return false;
    }

    private double calcLagrange(double x) {
        double result = 0;

        for (Point point : points) {
            double numerator = 1;
            double denominator = 1;
            for (Point point2 : points) {
                if (point2.getX() != point.getX()) {
                    numerator *= (x - point2.getX());
                    denominator *= (point.getX() - point2.getX());
                    System.out.println(String.format("L%.0f(x) = x - %f/%f", point.getX(), point2.getX(), denominator));
                    lagrange.append("x - ").append(point.getX()).append("/" + point2.getY()).append(" * ").append(point.getY()).append(" + ");
                }
            }
            result += (numerator / denominator) * point.getY();
        }
        lagrange = new StringBuilder(lagrange.substring(0, lagrange.length() - 3));
        System.out.println(lagrange);
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

    public LagrangeInterpolation() {
        JFrame frame = new JFrame("Interpolação de Lagrange");
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
                    double y = calcLagrange(x);
                    Result dialog = new Result(x, y, lagrange.toString(), "Interpolação de Lagrange", points);
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                } catch (Exception exception) {
                    System.out.println("Invalid input");
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
