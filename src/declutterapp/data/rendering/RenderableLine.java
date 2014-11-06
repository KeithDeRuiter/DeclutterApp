package declutterapp.data.rendering;

import declutterapp.data.Coordinates;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author adam
 */
public class RenderableLine extends Renderable {
    private final Coordinates m_firstPoint;
    private final Coordinates m_secondPoint;

    public RenderableLine(Coordinates first, Coordinates second, Color color){
        super(color);
        m_firstPoint = first;
        m_secondPoint = second;
    }

    public int getFirstX(){
        return m_firstPoint.getX();
    }

    public int getSecondX() {
        return m_secondPoint.getX();
    }

    public int getFirstY(){
        return m_firstPoint.getY();
    }

    public int getSecondY(){
        return m_secondPoint.getY();
    }


    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(getColor());
        g2d.drawLine(m_firstPoint.getX(), m_firstPoint.getY(), m_secondPoint.getX(), m_secondPoint.getY());
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(Math.min(getFirstX(), getSecondX()), Math.min(getFirstY(), getSecondY()), Math.max(getFirstX(), getSecondX()), Math.max(getFirstY(), getSecondY()));
    }

    @Override
    public void translate(int dx, int dy) {
        m_firstPoint.translate(dx, dy);
        m_secondPoint.translate(dx, dy);
    }
}
