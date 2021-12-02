package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * 
 */
public class ConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("Stop");
    private final JButton up = new JButton("Count Up");
    private final JButton down = new JButton("Count Down");

    /**
     * Builds a GUI.
     */
    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        // Agent
        final Agent agent = new Agent();
        new Thread(agent).start();
        // Button Actions
        stop.addActionListener(e -> agent.stopCounting());
        up.addActionListener(e -> agent.countUp());
        down.addActionListener(e -> agent.countDown());
    }

    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean upFlag = true;
        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public void run() {
            ConcurrentGUI.this.up.setEnabled(false);
            while (!stop) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            ConcurrentGUI.this.display.setText(Agent.this.counter.toString());
                        }
                    });
                    if (this.upFlag) {
                        counter.getAndIncrement();
                    } else {
                        counter.getAndDecrement();
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
            ConcurrentGUI.this.up.setEnabled(false);
            ConcurrentGUI.this.down.setEnabled(false);
            ConcurrentGUI.this.stop.setEnabled(false);
        }

        public void countUp() {
            ConcurrentGUI.this.up.setEnabled(false);
            ConcurrentGUI.this.down.setEnabled(true);
            this.upFlag = true;
        }

        public void countDown() {
            ConcurrentGUI.this.down.setEnabled(false);
            ConcurrentGUI.this.up.setEnabled(true);
            this.upFlag = false;
        }
    }

}
