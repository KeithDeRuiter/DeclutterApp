package declutterapp.data.rendering;

import declutterapp.data.Coordinates;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.UUID;

/**
 *
 * @author adam
 */
public class RenderableText extends Renderable {

    private final String m_text;
    private Coordinates m_coords;
    private final UUID m_id;

    private final FontMetrics m_fontMetrics;
    
    public RenderableText(Coordinates coords, String text, Color color, UUID trackGuid, FontMetrics fm){
        super(color);
        m_text = text;
        m_coords = coords;
        m_id = trackGuid;
        m_fontMetrics = fm;
    }

    public String getText(){
        return m_text;
    }

    public int getX(){
        return m_coords.getX();
    }

    public int getY(){
        return m_coords.getY();
    }
    
    public void setCoords(int x, int y) {
        m_coords.setX(x);
        m_coords.setY(y);
    }
    
    public void setCoords(Coordinates coords) {
        m_coords = coords;
    }

    @Override
    public void translate(int x, int y) {
        m_coords.setX(getX() + x);
        m_coords.setY(getY() + y);
    }
    
    public UUID getId(){
        return m_id;
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(getColor());
        g2d.drawString(m_text, m_coords.getX(), m_coords.getY());
//        g2d.setColor(Color.ORANGE);
//        Rectangle b = getBounds();
//        g2d.drawRect(b.x, b.y, b.width, b.height);
    }

    
    @Override
    public Rectangle getBounds() {
        int width = m_fontMetrics.stringWidth(getText());
        int height = m_fontMetrics.getHeight();
        return new Rectangle(getX(), getY() - height, width, height);
    }
}
