package view;

import model.Point;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GregoryNewtonInterpolation {
    private JPanel mainPanel;
    private JTextField textPointX;
    private JTextField textPointY;
    private JButton btnAddPoint;
    private JTable tablePoints;
    private JTextField textXtoCalc;
    private JButton btnCalc;
    private ArrayList<Point> points;
    StringBuilder newton = new StringBuilder();
    private JPanel panelView = new JPanel();

    private boolean pointAlreadyExists(Point p) {
        for (Point point : points) {
            if (point.getX() == p.getX()) {
                return true;
            }
        }
        return false;
    }

    private double calcGregoryNewton(double x) {
        double result = 0;

        double[] y = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            y[i] = points.get(i).getY();
        }
        double[][] dividedDifferences = new double[points.size()][points.size()];
        for (int i = 0; i < points.size(); i++) {
            dividedDifferences[i][0] = y[i];
        }
        for (int i = 1; i < points.size(); i++) {
            for (int j = 0; j < points.size() - i; j++) {
                dividedDifferences[j][i] = (dividedDifferences[j + 1][i - 1] - dividedDifferences[j][i - 1]) / (points.get(j + i).getX() - points.get(j).getX());
            }
        }
        for (int i = 0; i < points.size(); i++) {
            double temp = dividedDifferences[0][i];
            for (int j = 0; j < i; j++) {
                temp *= (x - points.get(j).getX());
            }
            result += temp;
        }
        for (int i = 0; i < points.size(); i++) {
            newton.append(dividedDifferences[0][i]);
            for (int j = 0; j < i; j++) {
                newton.append("(x - ").append(points.get(j).getX()).append(")");
            }
            if (i < points.size() - 1) {
                newton.append(" + ");
            }
        }
        System.out.println(newton);
        System.out.println(String.format("P(%f) = %s", x, result));

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

    public GregoryNewtonInterpolation() {
        this.panelView.add(mainPanel);
        JFrame frame = new JFrame("Interpolação de Gregory-Newton");
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
                    double y = calcGregoryNewton(x);
                    Result dialog = new Result(x, y, newton.toString(), "Interpolação de Gregory-Newton", points);
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
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
