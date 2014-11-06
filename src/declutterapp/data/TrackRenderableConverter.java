package declutterapp.data;

import declutterapp.data.rendering.RenderableSymbol;
import declutterapp.data.rendering.RenderableText;
import java.awt.FontMetrics;

/**
 * A renderable converter for a Track.
 * @author adam
 */
public class TrackRenderableConverter {

    private final FontMetrics m_fontMetrics;

    
    /** Creates a new TrackRenderableConverter. */
    public TrackRenderableConverter(FontMetrics fontMetrics){
        m_fontMetrics = fontMetrics;
    }

    public RenderableText generateRenderableText(Track track){
        return new RenderableText(track.getLabelCoords(), track.getName(), track.getColor(), track.getId(), m_fontMetrics);
    }

    public RenderableSymbol generateRenderableSymbol(Track track){
        return new RenderableSymbol(track.getCoords(), track.getColor(), track.getId());
    }

}
