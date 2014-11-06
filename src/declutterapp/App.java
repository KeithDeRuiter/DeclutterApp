package declutterapp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The main object of the app.
 * @author adam
 */
public class App {

    public static final int FRAME_WIDTH = 1000;
    public static final int FRAME_HEIGHT = 750;
    private JFrame m_frame;
    private Chart m_chart;
    private static final Dimension FRAME_DIM = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);
    private int m_numTracks;
    
    public App(int initialNumTracks){
        m_numTracks = initialNumTracks;
        initialize();
    }

    public void launch(){
        //generateTracks(m_numTracks);
        m_frame.pack();
        m_frame.setVisible(true);
        //m_chart.initializeTextBounds();
    }

    private void generateTracks(int qty){
        // # of tracks, region width, region height
        m_chart.generateTracks(qty, App.FRAME_WIDTH - 50, App.FRAME_HEIGHT - 50);
    }

    private void initialize(){
        // Initialize Frame
        m_frame = new JFrame("Declutter Test App");
        m_frame.setLayout(new BorderLayout());
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //m_frame.setMinimumSize(FRAME_DIM);
        //m_frame.setMaximumSize(FRAME_DIM);
        m_frame.setPreferredSize(FRAME_DIM);

        // Initialize and add the Chart
        m_chart = new Chart();
        m_frame.add(m_chart, BorderLayout.CENTER);

        // Initialize and add the control panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        
        //Right side (options)
        JPanel optionsPanel = new JPanel();
        
        //label bounds checkbox
        final JCheckBox labelBoundsBox = new JCheckBox("Label Bounds");
        labelBoundsBox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                m_chart.setLabelBoundsEnabled(labelBoundsBox.isSelected());
            }
        });
        optionsPanel.add(labelBoundsBox);
        
        //group bounds checkbox
        final JCheckBox groupBoundsBox = new JCheckBox("Group Bounds");
        groupBoundsBox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                m_chart.setClutterGroupBoundsEnabled(groupBoundsBox.isSelected());
            }
        });
        
        //Declutter checkbox
        final JCheckBox declutterBox = new JCheckBox("Declutter");
        declutterBox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                m_chart.setDeclutterEnabled(declutterBox.isSelected());
            }
        });
        
        //shuffle checkbox
        final JCheckBox shuffleBox = new JCheckBox("Shuffle");
        shuffleBox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                m_chart.setShuffleEnabled(shuffleBox.isSelected());
                m_chart.setDeclutterEnabled(true);
                declutterBox.setSelected(true);
            }
        });
        
        optionsPanel.add(groupBoundsBox);
        optionsPanel.add(shuffleBox);
        optionsPanel.add(declutterBox);

        //Left side (generation)
        JPanel generationPanel = new JPanel();
        final JTextField trackQuantityField = new JTextField(String.valueOf(m_numTracks), 4);
        generationPanel.add(trackQuantityField);
        
        JButton button = new JButton("Regenerate Chart");
        button.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    m_numTracks = Integer.valueOf(trackQuantityField.getText());
                } catch (NumberFormatException ex) {
                    //Do nothing, m_numTracks remains the same
                }
                generateTracks(m_numTracks);
            }
        });
        generationPanel.add(button);
        
        JButton repaintButton = new JButton("Repaint Chart");
        repaintButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                m_chart.repaint();
            }
        });
        generationPanel.add(repaintButton);
        
        //Add final two panels
        controlPanel.add(optionsPanel, BorderLayout.LINE_END);
        controlPanel.add(generationPanel, BorderLayout.LINE_START);

        m_frame.add(controlPanel, BorderLayout.SOUTH);
    }

}
