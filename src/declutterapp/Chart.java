package declutterapp;

import declutterapp.data.Track;
import declutterapp.data.TrackRenderableConverter;
import declutterapp.data.clutter.ClutterGroup;
import declutterapp.data.rendering.RenderableBox;
import declutterapp.data.rendering.RenderableLine;
import declutterapp.data.rendering.RenderableSymbol;
import declutterapp.data.rendering.RenderableText;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 *
 * @author adam
 */
public class Chart extends Component {

    private Set<Track> m_tracks;    //TODO prevent concurrent modification if you generate at launch as paint is undoing declutter

    private Map<UUID, RenderableSymbol> m_symbols;

    private Map<UUID, RenderableText> m_text;

    private Set<RenderableLine> m_lines;
    
    private Set<RenderableBox> m_groupBounds;

    private TrackRenderableConverter m_renderableConverter;

    private Color m_oceanColor = Color.BLACK;

    private static final Dimension DEFAULT_DIM = new Dimension(800, 600);

    private DeclutterProcessor m_declutterProcessor;

    private boolean m_declutterEnabled = false;
    
    private boolean m_shuffleEnabled = false;

    private boolean m_drawClutterGroupBoundaries = false;
    
    private boolean m_drawLabelBounds = false;
    
    private final ExecutorService m_singleThreadExecutor = Executors.newSingleThreadExecutor();
    
    private final ScheduledExecutorService m_redrawExecutor = Executors.newSingleThreadScheduledExecutor();

    public Chart(){
        this(DEFAULT_DIM);
    }

    public Chart(Dimension size){

        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
                addTrack(Track.newRandomTrackAtCoords(e.getX(), e.getY()));
                if(m_declutterEnabled) {
                    handleDeclutter();
                } else {
                    handleUndoDeclutter();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        m_tracks = new HashSet<>();

        m_lines = new HashSet<>();
        m_symbols = new HashMap<>();
        m_text = new HashMap<>();
        m_groupBounds = new HashSet<>();
        
        m_declutterProcessor = new DeclutterProcessor();
    }

    /**
     * Replaces the ocean color with the provided one.  If the supplied color is null, this method call is ignored and
     * the original ocean color remains.
     *
     * @param color the new color for the ocean.
     */
    public void setOceanColor(Color color){
        if (color != null){
            m_oceanColor = color;
        }
    }

    public void clearTracks(){
        m_tracks.clear();
        m_text.clear();
        m_symbols.clear();
        m_groupBounds.clear();
        m_lines.clear();
    }

    public void addTrack(Track track){
        m_tracks.add(track);
        m_symbols.put(track.getId(), m_renderableConverter.generateRenderableSymbol(track));
        m_text.put(track.getId(), m_renderableConverter.generateRenderableText(track));
    }

    public void removeTrack(Track track){
        m_tracks.remove(track);
        m_symbols.remove(track.getId());
        m_text.remove(track.getId());
    }

    
    private void handleDeclutter() {
        m_singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                performDeclutter();
            }
        });
    }
    
    private void handleUndoDeclutter() {
        m_singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // Initialize a processor if necessary.
                if (m_declutterProcessor == null){
                    m_declutterProcessor = new DeclutterProcessor();
                }
                //Back to a clean slate
                m_declutterProcessor.undoDeclutter(m_tracks, m_symbols, m_text, m_lines);
                //m_groupBounds.clear();
                repaint();
            }
        });
    }
    
    /**
     * Enable or disable decluttering.
     * @param enabled True to enable declutter, false to disable.
     */
    public void setDeclutterEnabled(boolean enabled){
        m_declutterEnabled = enabled;
        if (enabled){
            handleDeclutter();
            System.out.println("Declutter enabled!");
        } else {
            handleUndoDeclutter();
            System.out.println("Declutter disabled!");
        }
        repaint();
    }

    /**
     * Enable or disable shuffling.
     * @param enabled True to enable shuffle in declutter, false to disable.
     */
    public void setShuffleEnabled(boolean enabled){
        m_shuffleEnabled = enabled;
        if (enabled){
            System.out.println("Shuffle enabled!");
        } else {
            System.out.println("Shuffle disabled!");
        }
        repaint();
    }

    /**
     * Enable or disable the drawing of boxes around labels.
     * @param enabled True to enable, false to disable.
     */
    public void setLabelBoundsEnabled(boolean enabled){
        m_drawLabelBounds = enabled;
        if (enabled){
            System.out.println("Drawing Label Bounds enabled!");
        } else {
            System.out.println("Drawing Label Bounds disabled!");
        }
        repaint();
    }

    /**
     * Enable or disable the drawing of boxes around groups.
     * @param enabled True to enable, false to disable.
     */
    public void setClutterGroupBoundsEnabled(boolean enabled){
        m_drawClutterGroupBoundaries = enabled;
        if (enabled){
            System.out.println("Drawing Group Bounds enabled!");
        } else {
            System.out.println("Drawing Group Bounds disabled!");
        }
        repaint();
    }

    
    private void performDeclutter() {
        //Start from a clean slate
        m_declutterProcessor.undoDeclutter(m_tracks, m_symbols, m_text, m_lines);
        

        m_declutterProcessor.setShuffle(m_shuffleEnabled);
        Collection<ClutterGroup> groups = m_declutterProcessor.performDeclutter(m_tracks, m_symbols, m_text, m_lines);

        //Generate box for each group
        m_groupBounds.clear();
//        for (ClutterGroup group : groups){
//            Rectangle groupRect = group.calculateGroupRect(true);
//            Coordinates coords = new Coordinates(groupRect.x, groupRect.y);
//            RenderableBox box = new RenderableBox(coords, groupRect.width, groupRect.height);
//            m_groupBounds.add(box);
//        }
        
        m_groupBounds.addAll(m_declutterProcessor.getOriginalGroupBounds());
        
        repaint();
    }
    
    
    /** {@inheritDoc} */
    @Override
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D)g;

        //Initialize font metrics
        if(m_renderableConverter == null) {
            FontMetrics fm = g2d.getFontMetrics();
            m_renderableConverter = new TrackRenderableConverter(fm);
        }
        
        // Draw Background
        g2d.setColor(m_oceanColor);
        g2d.fillRect(0, 0, getSize().width, getSize().height);
 
        //Draw marker lines
        g2d.setColor(Color.GRAY);
        for(int i = 0; i < this.getWidth(); i+=100) { //draw verticals
            g2d.drawLine(i, 0, i, this.getHeight());
        }
        for(int i = 0; i < this.getHeight(); i+=100) { //draw horizontals
            g2d.drawLine(0, i, this.getWidth(), i);
        }
        
        //DRAW STUFF FOR TRACKS ============================
        
        // Draw Labels, symbols, text
        for (RenderableText text : m_text.values()){
            text.render(g2d);
        }

        for (RenderableSymbol sym : m_symbols.values()){
            sym.render(g2d);
        }

        for (RenderableLine line : m_lines) {
            line.render(g2d);
        }
        
        if(m_drawClutterGroupBoundaries) {
            for(RenderableBox box : m_groupBounds) {
                box.render(g2d);
                g2d.fillRect(box.getCenter().getX() - 1, box.getCenter().getY() - 1, 3, 3);
            }
            for(RenderableBox box : m_declutterProcessor.getFinalGroupBounds()) {
                box.render(g2d);
                g2d.fillRect(box.getCenter().getX() - 1, box.getCenter().getY() - 1, 3, 3);
            }
        }
        

    }
    
    void generateTracks(int quantity, int regionWidth, int regionHeight) {
        clearTracks();
        for (int i = 0; i < quantity; i++){
            addTrack(Track.newRandomTrack(0, regionWidth, 0, regionHeight));
        }
        if(m_declutterEnabled) {
            handleDeclutter(); //Declutters and then calls repaint
        } else {
            handleUndoDeclutter();
        }
    }
}
