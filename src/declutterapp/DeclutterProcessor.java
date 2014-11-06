package declutterapp;

import declutterapp.data.Coordinates;
import declutterapp.data.Track;
import declutterapp.data.clutter.ClutterGroup;
import declutterapp.data.rendering.Renderable;
import declutterapp.data.rendering.RenderableLine;
import declutterapp.data.rendering.RenderableSymbol;
import declutterapp.data.rendering.RenderableText;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author adam
 */
public class DeclutterProcessor {

    
    private boolean shuffle = false;
    

    public DeclutterProcessor(){
    }

    /**
     * Performs the declutter by actually moving the objects passed in.
     * @param tracks The tracks in the scene.
     * @param symbols The symbols in the scene, mapped from track ID.
     * @param texts The texts in the scene, mapped from track ID.
     * @param lines The lines in the scene.
     * @return The collection of cluttergroups detected.
     */
    public Collection<ClutterGroup> performDeclutter(Collection<Track> tracks, Map<UUID, RenderableSymbol> symbols, Map<UUID, RenderableText> texts, Set<RenderableLine> lines){
        
        // Identify ClutterGroups
        Set<Track> trackSet = new HashSet<>(tracks);

        /// DEBUG CODE
        System.out.println("\nNumber of Tracks:  " + trackSet.size());

        List<ClutterGroup> clutterGroups = identifyClutterGroups(trackSet, symbols, texts);

        // Merge ClutterGroups
        Collection<ClutterGroup> temp = new HashSet<>();
        temp.addAll(mergeGroups(clutterGroups));
        clutterGroups.clear();
        clutterGroups.addAll(temp);

        eliminateOverlap(clutterGroups, tracks, symbols, texts, lines);
        
        return clutterGroups;
    }

    private List<ClutterGroup> identifyClutterGroups(Set<Track> trackSet, Map<UUID, RenderableSymbol> symbols, Map<UUID, RenderableText> texts){
        List<ClutterGroup> clutterGroups = new ArrayList<>();
        
        //Add tracks
        for (Track track : trackSet){
            RenderableSymbol rs = symbols.get(track.getId());
            RenderableText rt = texts.get(track.getId());
            // Check each group to see if there's an overlap of the track's renderables with any of those in the group
            // If so add it to the group and break out of the loop.
            boolean overlapFound = false;
            for (ClutterGroup group : clutterGroups){
                if (group.hasIntersection(rs) || group.hasIntersection(rt)) {
                    group.addTrack(track, rs, rt);
                    overlapFound = true;
                    break;
                }
            }
            if (overlapFound){
                continue;
            }

            // if it did not intersect any existing cluttergroup put the track into a new clutter group
            ClutterGroup newGroup = new ClutterGroup();
            newGroup.addTrack(track, rs, rt);
            clutterGroups.add(newGroup);
        }

        return clutterGroups;
    }
    
    private Collection<ClutterGroup> mergeGroups(Collection<ClutterGroup> clutterGroups){
        // for each group in other groups
            // check to see if test group intersects this group
            // if so
                // combine the groups.  Remove the two previous groups and call this method again.

        ClutterGroup combinedGroup = null;
        ClutterGroup sourceGroupA = null;
        ClutterGroup sourceGroupB = null;

        for (ClutterGroup testGroup : clutterGroups){
            for (ClutterGroup currentGroup : clutterGroups){
                // Don't compare the group against itself.
                if (testGroup.equals(currentGroup)){
                    continue;
                }
                if (testGroup.calculateGroupRect(true).intersects(currentGroup.calculateGroupRect(true))){
                    System.out.println("   Intersection found.");
                    // Generate a new group that is the aggregation of the two that intersected one another.
                    Set<Track> tracks = new HashSet<>();
                    Map<UUID, RenderableText> texts = new HashMap<>();
                    Map<UUID, RenderableSymbol> symbols = new HashMap<>();
                    
                    tracks.addAll(currentGroup.getTracks());
                    tracks.addAll(testGroup.getTracks());
                    texts.putAll(currentGroup.getTexts());
                    texts.putAll(testGroup.getTexts());
                    symbols.putAll(currentGroup.getSymbols());
                    symbols.putAll(testGroup.getSymbols());

                    sourceGroupA = currentGroup;
                    sourceGroupB = testGroup;

                    combinedGroup = new ClutterGroup();
                    combinedGroup.addTracks(tracks, symbols, texts);

                    //break;
                }
            }

            if (combinedGroup != null){
                break;
            }
        }

        if (combinedGroup != null){
            System.out.println("   Group count before combining...:  " + clutterGroups.size());
            clutterGroups.remove(sourceGroupA);
            clutterGroups.remove(sourceGroupB);
            System.out.println("   Group count before adding combined group...:  " + clutterGroups.size());
            clutterGroups.add(combinedGroup);
            System.out.println("   Group count after combining...:  " + clutterGroups.size());

            return mergeGroups(clutterGroups);

        } else {
            System.out.println("   No intersection found.");
            return clutterGroups;
        }
    }

    
    
    /**
     * Finds the coordinates of the closest corner of the rectangle to the point passed in.
     * @param reference The reference point for the corner finding.
     * @param rect The rectangle whose corners are to be evaluated.
     * @return The coordinates of the rectangle's closest corner to the point passed in.
     */
    private Coordinates getClosestCorner(Coordinates reference, Rectangle rect) {
        final int cx = reference.getX();
        final int cy = reference.getY();
        List<Coordinates> corners = new ArrayList<>();
        corners.add(new Coordinates(rect.x, rect.y));
        corners.add(new Coordinates(rect.x + rect.width, rect.y));
        corners.add(new Coordinates(rect.x, rect.y + rect.height));
        corners.add(new Coordinates(rect.x + rect.width, rect.y + rect.height));
        
        Collections.sort(corners, new Comparator<Coordinates>() {
            @Override
            public int compare(Coordinates o1, Coordinates o2) {
                double d1 = Point2D.distance(cx, cy, o1.getX(), o1.getY());
                double d2 = Point2D.distance(cx, cy, o2.getX(), o2.getY());
                return (d1 < d2) ? -1 : 1;
            }
        });
        
        return corners.get(0);
    }
    
    /**
     * Moves the text around so that it does not intersect with text, symbols, or lines.
     * @param tracks
     * @param symbols
     * @param texts
     * @param lines 
     */
    private void eliminateOverlap(Collection<ClutterGroup> groups,
                                    Collection<Track> tracks,
                                    Map<UUID, RenderableSymbol> symbols,
                                    Map<UUID, RenderableText> texts,
                                    Set<RenderableLine> lines) {  
        
        
        //Store all moveables and immoveables
        List<Renderable> allMoveableRenderables = new ArrayList<>();
        List<Renderable> allImmoveableRenderables = new ArrayList<>();
        Map<Rectangle, ClutterGroup> clutterGroupBoundaries = new HashMap<>();
        allMoveableRenderables.addAll(texts.values());
        allImmoveableRenderables.addAll(symbols.values());
        
        //Turn single group labels into immoveables
        for (ClutterGroup group : groups) {
            if(group.getSize() == 1) {
                allMoveableRenderables.removeAll(group.getTexts().values());
                allImmoveableRenderables.addAll(group.getTexts().values());
            }
        }
        
        //Expand out label positions for each track's text
        for (ClutterGroup group : groups) {
            Set<Track> groupTracks = group.getTracks(); //Get Tracks in this group
            Rectangle groupRect = group.calculateGroupRect(false);
            clutterGroupBoundaries.put(groupRect, group);
            Coordinates center = new Coordinates((int)groupRect.getCenterX(), (int)groupRect.getCenterY());
            
            for(Track t : groupTracks) {
   
                //Compute Rays to each track in the group
                Coordinates trackCoords = t.getCoords();

                double scaleFactor = 30.0;

                //Get vector pointing to where we will push out the label=========
                double w = trackCoords.getX() - center.getX();
                double h = trackCoords.getY() - center.getY();
                double length = Math.hypot(w, h);

                double unitX = w / length;
                double unitY = h / length;

                //The vector components out to where this will be drawn
                double scaledX = unitX * scaleFactor;
                double scaledY = unitY * scaleFactor;
                
                Coordinates expandedCoords = new Coordinates(trackCoords.getX() + (int)scaledX, trackCoords.getY() + (int)scaledY);
                
                
                //Get closest corner and translate ===================================
                //Get label's bounding box for track t
                Rectangle textRect = texts.get(t.getId()).getBounds();
                textRect.translate((int)scaledX - Track.BOX_SIDE, (int)scaledY);    //Move to end of leader line

                //Get closest corner and move rect to closest corner
                Coordinates closestCorner = getClosestCorner(center, textRect);
                textRect.translate(expandedCoords.getX() - closestCorner.getX(), expandedCoords.getY() - closestCorner.getY());
                
                //Translate the text itself
                RenderableText text = texts.get(t.getId());
                text.translate(textRect.x + Track.BOX_SIDE - text.getX(), textRect.y + textRect.height - text.getY());
            }
        }
        
        
        //Shuffle around the labels so they do not overlap
        if(shuffle) {
            RectangleShuffleProcessor rsp = new RectangleShuffleProcessor();
            //rsp.shuffleRectangles(allImmoveableRenderables, allMoveableRenderables, clutterGroupBoundaries, tracks, symbols, texts, lines);
            rsp.shuffleWithinGroups(groups, tracks, symbols, texts, lines);
        }
        
        for(Track t : tracks) {
            //Draw that leading line
            lines.add(new RenderableLine(t.getCoords(), getClosestCorner(t.getCoords(), texts.get(t.getId()).getBounds()), t.getColor()));
        }
    }
    
    /**
     * Puts everything back where it belongs.
     * @param tracks
     * @param symbols
     * @param text
     * @param lines 
     */
    public void undoDeclutter(Collection<Track> tracks, Map<UUID, RenderableSymbol> symbols, Map<UUID, RenderableText> text, Set<RenderableLine> lines) {
        for(Track t : tracks) {
            UUID id = t.getId();
            text.get(id).setCoords(t.getLabelCoords());
        }
        lines.clear();
    }
    
    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }
}
