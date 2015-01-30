package declutterapp.data.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * The Renderable base class.
 * @author adam
 */
public abstract class Renderable {

    /** The color of this renderable. */
    private final Color m_color;

    /**
     * Constructs a new renderable.
     * @param color The color for this renderable.
     */
    public Renderable(Color color){
        m_color = color;
    }

    /**
     * Gets the color of this renderable.
     * @return The color of this renderable.
     */
    public Color getColor(){
        return m_color;
    }

    /**
     * Gets the bounding box of this renderable.
     * @return 
     */
    public abstract Rectangle getBounds();
    
    /**
     * Moves the renderable by (dx, dy)
     * @param dx x movement
     * @param dy  y movement
     */
    public abstract void translate(int dx, int dy);
    
    /**
     * Renders the renderable using the given {@code Graphics}.
     * @param g2d 
     */
    public abstract void render(Graphics2D g2d);
    
    
    @Override
    public String toString() {
        return "(" + getBounds().x + ", " + getBounds().y + ")  " + getBounds().width + " x " + getBounds().height;
    }
}
