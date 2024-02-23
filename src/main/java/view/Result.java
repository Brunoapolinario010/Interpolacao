package view;

import model.Point;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.scilab.forge.jlatexmath.TeXFormula;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class Result extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton btnSave;
    private JPanel contentLabel;
    private String polynomial;
    private JLabel pLabel;
    private JLabel pOfX;
    private JPanel chart;

    public class ModalChartComponent extends JPanel {
        private ArrayList<Point> points;
        private JFreeChart chart;

        public ModalChartComponent(String title, ArrayList<Point> points) {
            this.points = points;
            createAndShowGUI(title);
        }

        private void createAndShowGUI(String title) {
            // Suavizar os pontos
            ArrayList<Point> smoothedPoints = smoothPoints(points);

            // Adicionar os pontos originais em smoothedPoints
            smoothedPoints.addAll(points);

            // Criar um conjunto de dados para o gráfico
            XYSeries series = new XYSeries("Pontos Originais");
            for (Point point : points) {
                series.add(point.getX(), point.getY());
            }

            XYSeries smoothedSeries = new XYSeries("Pontos Suavizados");
            for (Point point : smoothedPoints) {
                smoothedSeries.add(point.getX(), point.getY());
            }

            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(series);
            dataset.addSeries(smoothedSeries);

            // Criar o gráfico
            JFreeChart chart = ChartFactory.createXYLineChart(
                    title,
                    "X",
                    "Y",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            // Configurar o renderizador para exibir apenas os pontos originais sem linhas
            XYPlot plot = (XYPlot) chart.getPlot();
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesLinesVisible(0, false); // Oculta as linhas de conexão para a primeira série de dados
            plot.setRenderer(renderer);

            // Adicionar o gráfico a um painel
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 600));

            this.chart = chart;

            // Adicionar o painel ao componente
            add(chartPanel);
        }

        private ArrayList<Point> smoothPoints(ArrayList<Point> points) {
            ArrayList<Point> smoothedPoints = new ArrayList<>();
            for (int i = 0; i < points.size() - 1; i++) {
                Point currentPoint = points.get(i);
                Point nextPoint = points.get(i + 1);
                double newX = (currentPoint.getX() + nextPoint.getX()) / 2;
                double newY = (currentPoint.getY() + nextPoint.getY()) / 2;
                smoothedPoints.add(new Point(newX, newY));
            }
            return smoothedPoints;
        }

        public JFreeChart getChart() {
            return this.chart;
        }
    }

    public Result(double x, double y, String polynomial, String title, ArrayList<Point> points) {
        this.polynomial = polynomial;
        this.pLabel.setText(polynomial);
        this.pOfX.setText(String.format("P(%.2f) = %.2f", x, y));

        ModalChartComponent smoothChart = new ModalChartComponent(title, points);
        this.chart.setLayout(new BorderLayout());
        this.chart.add(smoothChart, BorderLayout.CENTER);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSave(title, points);
            }
        });
    }

    private void onOK() {
        dispose();
    }

    private void onSave(String title, ArrayList<Point> points) {
        try {
            TeXFormula formula = new TeXFormula(polynomial);
            BufferedImage image = (BufferedImage) formula.createBufferedImage(TeXFormula.SERIF, 20, java.awt.Color.BLACK, null);

            String currentDirectory = System.getProperty("user.dir");
            String currentTime = String.valueOf(System.currentTimeMillis());

            File file = new File(currentDirectory + "/" + currentTime + " " + title + ".png");
            ImageIO.write(image, "png", file);
            JOptionPane.showMessageDialog(null, "Polinômio salvo com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "Erro ao salvar polinômio %s" + e.getMessage());
        }
        try {
            // Salvar o gráfico
            ModalChartComponent smoothChart = new ModalChartComponent(title, points);

            String currentTime = String.valueOf(System.currentTimeMillis());

            ChartUtils.saveChartAsPNG(new File(currentTime + " " + title + ".png"), smoothChart.getChart(), 800, 600);
        } catch (Exception e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "Erro ao salvar gráfico %s" + e.getMessage());
        }
    }
}