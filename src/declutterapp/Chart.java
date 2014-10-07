package declutterapp;

import declutterapp.data.Coordinates;
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 *
 * @author adam
 */
public class Chart extends Component {

    private Set<Track> m_tracks;

    private Map<UUID, RenderableSymbol> m_symbols;

    private Map<UUID, RenderableText> m_text;

    private Set<RenderableLine> m_lines;

    private TrackRenderableConverter m_renderableConverter;

    private Color m_oceanColor = new Color(150, 200, 255);

    private static final Dimension DEFAULT_DIM = new Dimension(800, 600);

    private DeclutterProcessor m_declutterProcessor;

    private boolean m_declutterEnabled = false;

    private boolean m_drawClutterGroupBoundaries;

    public Chart(){
        this(DEFAULT_DIM, false);
    }

    public Chart(boolean drawClutterGroupBoxes){
        this(DEFAULT_DIM, drawClutterGroupBoxes);
    }

    public Chart(Dimension size, boolean drawClutterGroupBoxes){

        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        m_tracks = new HashSet<>();

        m_lines = new HashSet<>();
        m_symbols = new HashMap<>();
        m_text = new HashMap<>();
        m_renderableConverter = new TrackRenderableConverter();

        m_drawClutterGroupBoundaries = drawClutterGroupBoxes;
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

    public void setDeclutterEnabled(boolean enabled){
        m_declutterEnabled = enabled;
        if (enabled){
            System.out.println("Declutter enabled!");
        } else {
            System.out.println("Declutter disabled!");
        }
        repaint();
    }

    /** {@inheritDoc} */
    @Override
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D)g;

        // Draw Background
        g2d.setColor(m_oceanColor);
        g2d.fillRect(0, 0, getSize().width, getSize().height);

        if (m_declutterEnabled){
            //  Remvoe the old generated data.
            //m_tracks.clear();
            //m_text.clear();

            // Initialize a processor if necessary.
            if (m_declutterProcessor == null){
                m_declutterProcessor = new DeclutterProcessor(g2d.getFontMetrics());
            }

            Collection<ClutterGroup> groups = m_declutterProcessor.performDeclutter(m_tracks, m_symbols, m_text, m_lines);

            if (m_drawClutterGroupBoundaries){
                //Render box for each group
                for (ClutterGroup group : groups){
                    Rectangle groupRect = group.calculateGroupRect(false);
                    Coordinates coords = new Coordinates(groupRect.x, groupRect.y);
                    RenderableBox box = new RenderableBox(coords, groupRect.width, groupRect.height);
                    box.render(g2d);
                }
//                for (ClutterGroup group : groups){
//                    Polygon polygon = group.calculateGroupPolygon(true);
//                    RenderablePolygon rPoly = new RenderablePolygon(polygon);
//                    rPoly.render(g2d);
//                }
            }
            
            //Draw the things in each clutter group TODO - combine with previous loop over group
            for (ClutterGroup group : groups) {
                Set<Track> groupTracks = group.getTracks(); //Get Tracks in this group
                Rectangle groupRect = group.calculateGroupRect(false);
                Coordinates center = new Coordinates((int)groupRect.getCenterX(), (int)groupRect.getCenterY());
                for(Track t : groupTracks) {    //Draw Rays to them
                    Coordinates trackCoords = t.getCoords();
                    g2d.setColor(Color.BLACK);
                    g2d.drawLine(center.getX(), center.getY(), trackCoords.getX(), trackCoords.getY());
                    
                    double scaleFactor = 8.0;
                    
                    double w = trackCoords.getX() - center.getX();
                    double h = trackCoords.getY() - center.getY();
                    double length = Math.hypot(w, h);
                                        
                    double unitX = w / length;
                    double unitY = h / length;
                    
                    double scaledX = unitX * scaleFactor;
                    double scaledY = unitY * scaleFactor;
                    
                    g2d.setColor(Color.RED);
                    g2d.drawLine(trackCoords.getX(), trackCoords.getY(), trackCoords.getX() + (int)scaledX, trackCoords.getY() + (int)scaledY);
                    
                    AffineTransform at = g2d.getTransform();    //Store previous transform
                    g2d.translate(scaledX, scaledY);
                    //t.getRenderableText().render(g2d);
                    g2d.fillOval(0, 0, 5, 5);
                    g2d.setTransform(at);
                }
                
//                // Draw Labels
//                for (RenderableText text : m_text.values()){
//                    text.render(g2d);
//                }

                // Draw Symbols
                for (RenderableSymbol sym : m_symbols.values()){
                    sym.render(g2d);
                }
                
            }
            
        } else {
            // Draw Labels
            for (RenderableText text : m_text.values()){
                text.render(g2d);
            }

            // Draw Symbols
            for (RenderableSymbol sym : m_symbols.values()){
                sym.render(g2d);
            }
        }
    }

    void generateTracks(int quantity, int regionWidth, int regionHeight) {
        clearTracks();
        repaint();
        for (int i = 0; i < quantity; i++){
            addTrack(Track.newRandomTrack(regionWidth, regionHeight));
        }
    }
}
