package de.heisluft.minicpu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class TimeBarrier {
    private JDialog dialog;
    private static TimeBarrier barrier = null;
    private ControllerInterface controller;

    private TimeBarrier(ControllerInterface controller) {
        this.controller = controller;
        dialog = new JDialog((Frame)null);
        dialog.setTitle("Prozessorzeitschranke");
        JPanel jPanel = (JPanel)this.dialog.getContentPane();
        jPanel.setLayout(new BorderLayout());
        JPanel jPanel2 = new JPanel();
        jPanel2.setVisible(true);
        jPanel.add(jPanel2, "Center");
        jPanel2.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel jLabel = new JLabel("Geben Sie die Zeitschranke in Sekunden ein");
        jPanel2.add(jLabel);
        final JTextField jTextField = new JTextField();
        jTextField.setMinimumSize(new Dimension(800, 30));
        jPanel2.add(jTextField);
        jPanel2 = new JPanel();
        jPanel2.setVisible(true);
        jPanel.add(jPanel2, "South");
        jPanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton jButton = new JButton("Ok");
        jPanel2.add(jButton);
        jButton.addActionListener(actionEvent -> {
			try {
				int n = Integer.parseInt(jTextField.getText());
				this.controller.setTimeBarrier(n);
				dialog.setVisible(false);
			}
			catch (Exception exception) {
				exception.printStackTrace();
				jTextField.selectAll();
			}
		});
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(false);
    }

    static void show(ControllerInterface controller) {
        if (barrier == null)
            barrier = new TimeBarrier(controller);
        barrier.dialog.setVisible(true);
    }

}

