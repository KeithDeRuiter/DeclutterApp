package declutterapp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * The main object of the app.
 * @author adam
 */
public class App {

    public static final int FRAME_WIDTH = 1000;
    public static final int FRAME_HEIGHT = 650;
    private JFrame m_frame;
    private Chart m_chart;
    private static final Dimension FRAME_DIM = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);

    public App(){
        initialize();
    }

    public void launch(){
        generateTracks();
        m_frame.pack();
        m_frame.setVisible(true);
    }

    private void generateTracks(){
        // # of tracks, region width, region height
        m_chart.generateTracks(200, App.FRAME_WIDTH, App.FRAME_HEIGHT);
    }

    private void initialize(){
        // Initialize Frame
        m_frame = new JFrame("Declutter Test App");
        m_frame.setLayout(new BorderLayout());
        m_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        m_frame.setMinimumSize(FRAME_DIM);
        m_frame.setMaximumSize(FRAME_DIM);
        m_frame.setPreferredSize(FRAME_DIM);

        // Initialize and add the Chart
        m_chart = new Chart(true);
        m_frame.add(new JScrollPane(m_chart), BorderLayout.CENTER);

        // Initialize and add the control panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        final JCheckBox declutterBox = new JCheckBox("Declutter");
        declutterBox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                m_chart.setDeclutterEnabled(declutterBox.isSelected());
            }
        });
        controlPanel.add(declutterBox, BorderLayout.LINE_END);

        JButton button = new JButton("Regenerate Chart");
        button.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                generateTracks();
            }
        });
        controlPanel.add(button, BorderLayout.LINE_START);

        m_frame.add(controlPanel, BorderLayout.PAGE_END);
    }

    /**@param blargs the command line arguments */
    public static void main(String[] blargs){
        App app = new App();
        app.launch();
    }
}
