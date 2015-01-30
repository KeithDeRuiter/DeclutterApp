package declutterapp.data.rendering;

import declutterapp.data.Coordinates;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author AndersonAN
 */
public class RenderableBox extends Renderable {
    
    private Coordinates m_topLeft;
    private int m_width;
    private int m_height;
    
    public RenderableBox(Coordinates topLeft, int width, int height) {
        this(topLeft, width, height, Color.BLACK);
    }
    
    public RenderableBox(Coordinates topLeft, int width, int height, Color color){
        super(color);
        m_topLeft = topLeft;
        m_width = width;
        m_height = height;
    }
    
    public Coordinates getCenter() {
        return new Coordinates(m_topLeft.getX() + (m_width / 2), m_topLeft.getY() + (m_height / 2));
    }
    
    public static RenderableBox getBoxFromRectangle(Rectangle r) {
        return RenderableBox.getBoxFromRectangle(r, Color.BLACK);
    }
    
    public static RenderableBox getBoxFromRectangle(Rectangle r, Color color) {
        return new RenderableBox(new Coordinates(r.x, r.y), r.width, r.height, color);
    }
    
    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(getColor());
        g2d.drawRect(m_topLeft.getX(), m_topLeft.getY(), m_width, m_height);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(m_topLeft.getX(), m_topLeft.getY(), m_width, m_height);
    }

    @Override
    public void translate(int dx, int dy) {
        m_topLeft.translate(dx, dy);
    }

}
