package de.heisluft.minicpu;

import javax.swing.*;
import java.awt.*;

class About {
    private JDialog dialog = new JDialog((Frame) null);
    private static About about = null;

    private About() {
        dialog.setTitle("\u00dcber Minimaschine");

        JPanel jPanel = (JPanel)this.dialog.getContentPane();
        jPanel.setLayout(new BorderLayout());

        JPanel jPanel2 = new JPanel();
        jPanel2.setVisible(true);
        jPanel.add(jPanel2, "Center");
        jPanel2.setLayout(new GridLayout(6, 1));

        JLabel jLabel = new JLabel("Minimaschine");
        jLabel.setHorizontalAlignment(0);
        jPanel2.add(jLabel);
        jLabel = new JLabel("V1.8");
        jLabel.setHorizontalAlignment(0);
        jPanel2.add(jLabel);
        jLabel = new JLabel(" ");
        jPanel2.add(jLabel);
        jLabel = new JLabel("Ein Emulationsprogramm f\u00fcr eine einfache CPU  ");
        jLabel.setHorizontalAlignment(0);
        jPanel2.add(jLabel);
        jLabel = new JLabel("\u00a9 2009-2015 Albert Wiedemann");
        jLabel.setHorizontalAlignment(0);
        jPanel2.add(jLabel);
        jLabel = new JLabel(" ");
        jPanel2.add(jLabel);
        jPanel2 = new JPanel();
        jPanel2.setVisible(true);
        jPanel.add(jPanel2, "South");
        jPanel2.setLayout(new FlowLayout(1));

        JButton jButton = new JButton("Ok");
        jPanel2.add(jButton);
        jButton.addActionListener(actionEvent -> dialog.setVisible(false));
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(false);
    }

    static void show() {
        if (about == null)
            about = new About();
        about.dialog.setVisible(true);
    }

}

