package declutterapp.data.clutter;

import declutterapp.data.Coordinates;
import declutterapp.data.Track;
import declutterapp.data.rendering.Renderable;
import declutterapp.data.rendering.RenderableSymbol;
import declutterapp.data.rendering.RenderableText;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author adam
 */
public class ClutterGroup {

    private final Set<Track> m_tracks;
    
    private final Map<UUID, RenderableText> m_texts;
    
    private final Map<UUID, RenderableSymbol> m_symbols;

    private Rectangle m_rect;
    
    private boolean m_currentRectIncludesLabels;

    private Polygon m_polygon;

    public ClutterGroup(){
        m_tracks = new HashSet<>();
        m_texts = new HashMap<>();
        m_symbols = new HashMap<>();
        m_polygon = null;
        m_rect = null;
        m_currentRectIncludesLabels = true;
    }


    public void addTrack(Track track, RenderableSymbol symbol, RenderableText label){
        m_tracks.add(track);
        m_symbols.put(track.getId(), symbol);
        m_texts.put(track.getId(), label);
        m_polygon = null;
    }

    public void addTracks(Collection<Track> tracks, Map<UUID, RenderableSymbol> symbols, Map<UUID, RenderableText> labels){
        m_tracks.addAll(tracks);
        m_symbols.putAll(symbols);
        m_texts.putAll(labels);
        m_polygon = null;
    }
    
    public Set<Track> getTracks(){
        return Collections.unmodifiableSet(m_tracks);
    }

    public Map<UUID, RenderableText> getTexts(){
        return Collections.unmodifiableMap(m_texts);
    }
    
    public Map<UUID, RenderableSymbol> getSymbols() {
        return Collections.unmodifiableMap(m_symbols);
    }
    

    public boolean hasIntersection(Renderable renderable){
        return calculateGroupRect(true).intersects(renderable.getBounds());
    }

    public int getSize(){
        return m_tracks.size();
    }

    public void debugPrintTracks(){
        for (Track track : m_tracks){
            System.out.println("   " + track.getName());
        }
    }

    public Rectangle calculateGroupRect(boolean includeLabels){
        //if (m_rect == null || m_currentRectIncludesLabels != includeLabels){
            SortedXyValues xyValues = getSortedXyValues(includeLabels);

            int width = xyValues.m_xValues.get(xyValues.m_xValues.size() - 1) - xyValues.m_xValues.get(0);
            int height = xyValues.m_yValues.get(xyValues.m_yValues.size() - 1) - xyValues.m_yValues.get(0);

            m_currentRectIncludesLabels = includeLabels;
            m_rect = new Rectangle(xyValues.m_xValues.get(0), xyValues.m_yValues.get(0), width, height);
        //}
        return m_rect;
    }
    
//    public Polygon calculateGroupPolygon(boolean includeLabels){
//        if (m_polygon == null){
//
//            Polygon polygon = new Polygon();
//
//            List<Coordinates> coords = new ArrayList<>(getAllRenderableVertices(includeLabels));
//            XComparator xComp = new XComparator();
//            YComparator yComp = new YComparator();
//
//            Collections.sort(coords, xComp);
//            // Get and add all of the left most points
//            int leftMostX = coords.get(0).getX();
//            for (int i = 0; coords.get(i).getX() == leftMostX; i++){
//                polygon.addPoint(coords.get(i).getX(), coords.get(i).getY());
//            }
//
//            // get and add all of the upper most points
//            Collections.sort(coords, yComp);
//            int topMost = coords.get(0).getX();
//            for (int i = 0; coords.get(i).getX() == topMost; i++){
//                polygon.addPoint(coords.get(i).getX(), coords.get(i).getY());
//            }
//
//            // get and add all of the right most points
//            Collections.sort(coords, xComp);
//            Collections.reverse(coords);
//            List<Coordinates> rightMostPts = new ArrayList<>();
//            int rightMost = coords.get(0).getX();
//            for (int i = 0; coords.get(i).getX() == rightMost; i++){
//                rightMostPts.add(new Coordinates(coords.get(i).getX(), coords.get(i).getY()));
//                //polygon.addPoint(coords.get(i).getX(), coords.get(i).getY());
//            }
//            Collections.reverse(rightMostPts);
//            for (Coordinates crds : rightMostPts){
//                polygon.addPoint(crds.getX(), crds.getY());
//            }
//
//            //get and add all of the lower most points
//            Collections.sort(coords, yComp);
//            Collections.reverse(coords);
//            int lowerMost = coords.get(0).getX();
//            for (int i = 0; coords.get(i).getX() == lowerMost; i++){
//                polygon.addPoint(coords.get(i).getX(), coords.get(i).getY());
//            }
//
//            m_polygon = polygon;
//        }
//
//        return m_polygon;
//    }
//
//
    /**
     * Helper to get the XY Values of all of the {@link Renderable}s in the clutter group.
     * @param includeLabels true if the labels are to be included in this calculation, false otherwise.
     * @return The XY Values of all of the {@link Renderable}s in the clutter group.
     */
    private SortedXyValues getSortedXyValues(boolean includeLabels){
            List<Integer> xValues = new ArrayList<>();
            List<Integer> yValues = new ArrayList<>();

            for (Track track : m_tracks){
                UUID id = track.getId();
                RenderableSymbol s = m_symbols.get(id);
                Rectangle symbol = s.getBounds();
                int centerX = (int)symbol.getCenterX();
                int centerY = (int)symbol.getCenterY();

                // X Values
                xValues.add(centerX - (int)(symbol.getWidth() / 2));
                xValues.add(centerX + (int)(symbol.getWidth() / 2));

                // Y Values
                yValues.add(centerY - (int)(symbol.getHeight() / 2));
                yValues.add(centerY + (int)(symbol.getHeight() / 2));

                if (includeLabels){
                    Rectangle text = m_texts.get(track.getId()).getBounds();

                    centerX = (int)text.getCenterX();
                    centerY = (int)text.getCenterY();

                    // X Values
                    xValues.add(centerX - (int)(text.getWidth() / 2));
                    xValues.add(centerX + (int)(text.getWidth() / 2));

                    // Y Values
                    yValues.add(centerY - (int)(text.getHeight() / 2));
                    yValues.add(centerY + (int)(text.getHeight() / 2));
                }
            }

            // sort the lists
            Collections.sort(xValues);
            Collections.sort(yValues);

            return new SortedXyValues(xValues, yValues);
    }
//
//    private Collection<Coordinates> getAllRenderableVertices(boolean includeLabels){
//        Collection<Coordinates> coords = new HashSet<>();
//
//        for (Track track : m_tracks){
//            Rectangle symbol = getSymbolRect(track);
//            int centerX = (int)symbol.getCenterX();
//            int centerY = (int)symbol.getCenterY();
//
//            int leftX = centerX - (int)(symbol.getWidth() / 2);
//            int rightX = centerX + (int)(symbol.getWidth() / 2);
//
//            int topY = centerY - (int)(symbol.getHeight() / 2);
//            int bottomY = centerY + (int)(symbol.getHeight() / 2);
//
//            coords.add(new Coordinates(leftX, topY));
//            coords.add(new Coordinates(leftX, bottomY));
//            coords.add(new Coordinates(rightX, topY));
//            coords.add(new Coordinates(rightX, bottomY));
//
//            if (includeLabels){
//                Rectangle textRect = getTextRectForTrack(track);
//                int tCenterX = (int)textRect.getCenterX();
//                int tCenterY = (int)textRect.getCenterY();
//
//                int tLeftX = tCenterX - (int)(textRect.getWidth() / 2);
//                int tRightX = tCenterX + (int)(textRect.getWidth() / 2);
//
//                int textTopY = tCenterY - (int)(textRect.getHeight() / 2);
//                int textBottomY = tCenterY + (int)(textRect.getHeight() / 2);
//
//                coords.add(new Coordinates(tLeftX, textTopY));
//                coords.add(new Coordinates(tLeftX, textBottomY));
//                coords.add(new Coordinates(tRightX, textTopY));
//                coords.add(new Coordinates(tRightX, textBottomY));
//            }
//        }
//
//        return coords;
//    }

//    public Rectangle getSymbolRect(Track track){
//        return new Rectangle(track.getCoords().getX() - (Track.BOX_SIDE / 2), track.getCoords().getY() - (Track.BOX_SIDE / 2), Track.BOX_SIDE, Track.BOX_SIDE);
//    }

//    public Rectangle getTextRectForTrack(Track track){
//        int width = m_fontMetrics.stringWidth(track.getName());
//        int height = m_fontMetrics.getHeight();
//        return new Rectangle(track.getNewRenderableText().getX(), track.getNewRenderableText().getY() - height, width, height);
//    }
    


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.m_tracks);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClutterGroup other = (ClutterGroup) obj;
        if (!Objects.equals(this.m_tracks, other.m_tracks)) {
            return false;
        }
        return true;
    }


    private class SortedXyValues {

        private List<Integer> m_xValues;
        private List<Integer> m_yValues;

        private SortedXyValues(List<Integer> xValues, List<Integer> yValues){
            m_xValues = xValues;
            m_yValues = yValues;
        }
    }

    private class XComparator implements Comparator{
        @Override
        public int compare(Object o1, Object o2) {
            Coordinates c1 = (Coordinates)o1;
            Coordinates c2 = (Coordinates)o2;

            if (c1.getX() > c2.getX()){
                return 1;
            } else if (c1.getX() == c2.getX()){
                return 0;
            } else {
                return -1;
            }
        }
    }

    private class YComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            Coordinates c1 = (Coordinates)o1;
            Coordinates c2 = (Coordinates)o2;

            if (c1.getY() > c2.getY()){
                return 1;
            } else if (c1.getY() == c2.getY()){
                return 0;
            } else {
                return -1;
            }
        }
    }
}
