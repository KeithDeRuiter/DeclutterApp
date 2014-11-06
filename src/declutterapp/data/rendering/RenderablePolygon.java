package declutterapp.data.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

/**
 *
 * @author AndersonAN
 */
public class RenderablePolygon extends Renderable {
    
    private Polygon m_polygon;
    
    public RenderablePolygon(Polygon polygon) {
        this(Color.BLACK, polygon);
    }
    
    public RenderablePolygon(Color color, Polygon polygon){
        super(color);
        m_polygon = polygon;
    }
    
    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(getColor());
        g2d.drawPolygon(m_polygon);
    }

    @Override
    public Rectangle getBounds() {
        return m_polygon.getBounds();
    }

    @Override
    public void translate(int dx, int dy) {
        m_polygon.translate(dy, dy);
    }
}
