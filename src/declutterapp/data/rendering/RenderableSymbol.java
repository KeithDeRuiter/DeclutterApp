package declutterapp.data.rendering;

import declutterapp.data.Coordinates;
import declutterapp.data.Track;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.UUID;

/**
 *
 * @author adam
 */
public class RenderableSymbol extends Renderable {
    private final Coordinates m_point;
    private final UUID m_id;

    public RenderableSymbol(Coordinates coords, Color color, UUID trackGuid){
        super(color);
        m_point = coords;
        m_id = trackGuid;
    }

    public int getX(){
        return m_point.getX();
    }

    public int getY() {
        return m_point.getY();
    }

    public UUID getId(){
        return m_id;
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(getColor());
        g2d.drawRect(m_point.getX() - (Track.BOX_SIDE / 2), m_point.getY() - (Track.BOX_SIDE / 2), Track.BOX_SIDE, Track.BOX_SIDE);
    }
}
