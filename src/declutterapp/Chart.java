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
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    
    private boolean m_drawLabelBounds = false;

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

    /**
     * Enable or disable decluttering.
     * @param enabled True to enable declutter, false to disable.
     */
    public void setDeclutterEnabled(boolean enabled){
        m_declutterEnabled = enabled;
        if (enabled){
            System.out.println("Declutter enabled!");
        } else {
            System.out.println("Declutter disabled!");
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
                    //g2d.drawLine(center.getX(), center.getY(), trackCoords.getX(), trackCoords.getY());
                    
                    double scaleFactor = 30.0;
                    
                    //Get vector pointing to where we will push out the label=========
                    double w = trackCoords.getX() - center.getX();
                    double h = trackCoords.getY() - center.getY();
                    double length = Math.hypot(w, h);
                                        
                    double unitX = w / length;
                    double unitY = h / length;
                    
                    double scaledX = unitX * scaleFactor;
                    double scaledY = unitY * scaleFactor;
                    
                    //Draw that leading line
                    g2d.setColor(t.getColor());
                    g2d.drawLine(trackCoords.getX(), trackCoords.getY(), trackCoords.getX() + (int)scaledX, trackCoords.getY() + (int)scaledY);
                    
                    //Get closest corner and translate ===================================
                    
                    AffineTransform at = g2d.getTransform();    //Store previous transform
                    
                    //Get label's bounding box
                    Rectangle textRect = group.getTextRect(t);
                    textRect.translate((int)scaledX - Track.BOX_SIDE, (int)scaledY);    //Move to end of leader line
                    
                    //Get closest corner
                    Coordinates closestCorner = getClosestCorner(center, textRect);
                    
                    //Move to closest corner
                    textRect.translate(trackCoords.getX() + (int)scaledX - closestCorner.getX(), trackCoords.getY() + (int)scaledY - closestCorner.getY());
                    
                    if(m_drawLabelBounds) {
                        g2d.drawRect(textRect.x, textRect.y, textRect.width, textRect.height);
                    }
                    g2d.translate(textRect.x - t.getRenderableText().getX(), textRect.y + textRect.height - t.getRenderableText().getY());
                    t.getRenderableText().render(g2d);
                    g2d.setTransform(at);
                }
                

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

    private Coordinates getClosestCorner(Coordinates center, Rectangle rect) {
        final int cx = center.getX();
        final int cy = center.getY();
        List<Coordinates> corners = new ArrayList<>();
        corners.add(new Coordinates(rect.x, rect.y));
        corners.add(new Coordinates(rect.x + rect.width, rect.y));
        corners.add(new Coordinates(rect.x, rect.y + rect.height));
        corners.add(new Coordinates(rect.x + rect.width, rect.y + rect.height));
        
        Collections.sort(corners, new Comparator<Coordinates>() {
            @Override
            public int compare(Coordinates o1, Coordinates o2) {
                double d1 = Point2D.distance(cx, cy, o1.getX(), o1.getY());
                double d2 = Point2D.distance(cx, cy, o2.getX(), o2.getY());
                return (d1 < d2) ? -1 : 1;
            }
        });
        
        return corners.get(0);
    }
    
    void generateTracks(int quantity, int regionWidth, int regionHeight) {
        clearTracks();
        repaint();
        for (int i = 0; i < quantity; i++){
            addTrack(Track.newRandomTrack(regionWidth, regionHeight));
        }
    }
}
