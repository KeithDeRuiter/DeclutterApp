package declutterapp.data.rendering;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * The Renderable base class.
 * @author adam
 */
public abstract class Renderable {

    private final Color m_color;

    public Renderable(Color color){
        m_color = color;
    }

    public Color getColor(){
        return m_color;
    }

    public abstract void render(Graphics2D g2d);
}
