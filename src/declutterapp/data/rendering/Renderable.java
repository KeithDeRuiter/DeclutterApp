package declutterapp.data.rendering;

import java.awt.Color;
import java.awt.Graphics2D;

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
     * Renders the renderable using the given {@code Graphics}.
     * @param g2d 
     */
    public abstract void render(Graphics2D g2d);
}
