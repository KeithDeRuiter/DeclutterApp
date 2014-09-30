package declutterapp.data;

import declutterapp.data.rendering.RenderableSymbol;
import declutterapp.data.rendering.RenderableText;

/**
 * A renderable converter for a Track.
 * @author adam
 */
public class TrackRenderableConverter {

    /** Creates a new TrackRenderableConverter. */
    public TrackRenderableConverter(){
    }

    public RenderableText generateRenderableText(Track track){
        return new RenderableText(track.getLabelCoords(), track.getName(), track.getColor(), track.getId());
    }

    public RenderableSymbol generateRenderableSymbol(Track track){
        return new RenderableSymbol(track.getCoords(), track.getColor(), track.getId());
    }

}
