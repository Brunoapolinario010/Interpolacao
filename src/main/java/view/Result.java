package view;

import org.scilab.forge.jlatexmath.TeXFormula;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Result extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton btnSave;
    private JPanel contentLabel;
    private String polynomial;
    private JLabel pLabel;
    private JLabel pOfX;

    public Result(double x, double y, String polynomial) {
        this.polynomial = polynomial;
        this.pLabel.setText(polynomial);
        this.pOfX.setText(String.format("P(%.2f) = %.2f", x, y));
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
                onSave();
            }
        });
    }

    private void onOK() {
        dispose();
    }

    private void onSave() {
        try {
            TeXFormula formula = new TeXFormula(polynomial);
            BufferedImage image = (BufferedImage) formula.createBufferedImage(TeXFormula.SERIF, 20, java.awt.Color.BLACK, null);

            String currentDirectory = System.getProperty("user.dir");
            String currentTime = String.valueOf(System.currentTimeMillis());

            File file = new File(currentDirectory + "/" + currentTime + ".png");
            ImageIO.write(image, "png", file);
            JOptionPane.showMessageDialog(null, "Polinômio salvo com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "Erro ao salvar polinômio %s" + e.getMessage());
        }
    }
}