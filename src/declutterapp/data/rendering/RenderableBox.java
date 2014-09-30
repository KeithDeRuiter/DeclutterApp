package declutterapp.data.rendering;

import declutterapp.data.Coordinates;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author AndersonAN
 */
public class RenderableBox extends Renderable {
    
    private Coordinates m_center;
    private int m_width;
    private int m_height;
    
    public RenderableBox(Coordinates center, int width, int height) {
        this(Color.BLACK, center, width, height);
    }
    
    public RenderableBox(Color color, Coordinates center, int width, int height){
        super(color);
        m_center = center;
        m_width = width;
        m_height = height;
    }
    
    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(getColor());
        g2d.drawRect(m_center.getX(), m_center.getY(), m_width, m_height);
    }
}
