package declutterapp.data;

import declutterapp.App;
import declutterapp.data.rendering.RenderableSymbol;
import declutterapp.data.rendering.RenderableText;
import java.awt.Color;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author adam
 */
public class Track {

    private final Coordinates m_coords;
    private final Color m_color;
    private final String m_name;
    private final UUID m_id;

    public static final int BOX_SIDE = 8;

    private static final Random GENERATOR = new Random();

    public Track(Coordinates coords, Color color, String name){
        m_coords = coords;
        m_color = color;
        m_name = name;
        m_id = UUID.randomUUID();
    }

    public Coordinates getCoords(){
        return m_coords;
    }

    public Color getColor(){
        return m_color;
    }

    public String getName(){
        return m_name;
    }

    /**
     * Returns the ID of this track.
     * @return the ID of this track.
     */
    public UUID getId(){
        return m_id;
    }

    public Coordinates getLabelCoords(){
        return new Coordinates(m_coords.getX() + BOX_SIDE, m_coords.getY());
    }

    public RenderableText getRenderableText(){
        return new RenderableText(getLabelCoords(), m_name, m_color, m_id);
    }

    public RenderableSymbol getRenderabelSymbol(){
        return new RenderableSymbol(m_coords, m_color, m_id);
    }

    public static Track newRandomTrack(int maxX, int maxY){

        if (maxX > App.FRAME_WIDTH){
            maxX = App.FRAME_WIDTH;
        }
        if (maxY > App.FRAME_HEIGHT){
            maxY = App.FRAME_HEIGHT;
        }

        // Name
        String trackName = "T" + GENERATOR.nextInt(1000);

        // Coordinates
        int x_coord = GENERATOR.nextInt(maxX);
        int y_coord = GENERATOR.nextInt(maxY);
        if (x_coord < 15 && x_coord < (App.FRAME_WIDTH - 15)){
            x_coord += 15;
        }
        if (y_coord < 15 && y_coord < (App.FRAME_HEIGHT - 15)){
            y_coord += 15;
        }
        Coordinates coords = new Coordinates(x_coord, y_coord);

        // Color - limited to the range of darker colors.
        int r = GENERATOR.nextInt(150);
        int g = GENERATOR.nextInt(150);
        int b = GENERATOR.nextInt(150);

        return new Track(coords, new Color(r, g, b), trackName);
    }
}
