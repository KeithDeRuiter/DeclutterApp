package declutterapp.data.rendering;

import declutterapp.data.Coordinates;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.UUID;

/**
 *
 * @author adam
 */
public class RenderableText extends Renderable {

    private final String m_text;
    private final Coordinates m_coords;
    private final UUID m_id;

    public RenderableText(Coordinates coords, String text, Color color, UUID trackGuid){
        super(color);
        m_text = text;
        m_coords = coords;
        m_id = trackGuid;
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

    public UUID getId(){
        return m_id;
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(getColor());
        g2d.drawString(m_text, m_coords.getX(), m_coords.getY());
    }
}
