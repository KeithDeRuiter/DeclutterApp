package declutterapp;

import declutterapp.data.Track;
import declutterapp.data.clutter.ClutterGroup;
import declutterapp.data.rendering.RenderableLine;
import declutterapp.data.rendering.RenderableSymbol;
import declutterapp.data.rendering.RenderableText;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Collection;
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

    private final FontMetrics m_fontMetrics;

    public DeclutterProcessor(FontMetrics fontMetrics){
        m_fontMetrics = fontMetrics;
    }

    public Collection<ClutterGroup> performDeclutter(Collection<Track> tracks, Map<UUID, RenderableSymbol> symbols, Map<UUID, RenderableText> text, Set<RenderableLine> lines){

        // Identify ClutterGroups
        Set<Track> trackSet = new HashSet<>(tracks);

        /// DEBUG CODE
        System.out.println("\nNumber of Tracks:  " + trackSet.size());

        List<ClutterGroup> clutterGroups = identifyClutterGroups(trackSet);

        // Merge ClutterGroups
        Collection<ClutterGroup> temp = new HashSet<>();
        temp.addAll(mergeGroups(clutterGroups));
        clutterGroups.clear();
        clutterGroups.addAll(temp);

        /// DEBUG CODE
        System.out.println("\nNumber of clutter groups:  " + clutterGroups.size());

        int i = 0;
        for (ClutterGroup group : clutterGroups){
            i++;
            System.out.println("Group " + i);
            System.out.println("Group size:  " + group.getSize());
            group.debugPrintTracks();
            System.out.println("");
            System.out.println("");
        }

        return clutterGroups;

        /// END DEBUG CODE

        // ######## GENERATE CLUTTERGROUP POLYGON
        // generate the polygon that is formed by the edges of each of the corners of the symbols in a cluttergroup
        // need more detail here, but thats all I have for now.

        // For each polygon in the cluttergroup
            // determine if it is on the right or the left side of the clutter group and sort them into two groups right and left

            // for each side (right and left)
                // sort them into upper and lower halves.

        // now you should have an UL, UR, LL, LR quadrant each one containing 0 or more tracks locations.
        // for each quadrant:
            // order the tracks by which is the northmost first, to the southmost last.
            // try to fit the labels such that the lowest label for each



        // Generate Master DeclutterGroup
//        RenderableGroup group = new RenderableGroup();
//        return group;
    }

    private List<ClutterGroup> identifyClutterGroups(Set<Track> trackSet){
        // ####### IDENTIFY CLUTTER GROUPS
        List<ClutterGroup> clutterGroups = new ArrayList<>();
        for (Track track : trackSet){
            // Check each group to see if there's an overlap of the track's renderables with any of those in the group
            // If so add it to the group and break out of the loop.
            boolean overlapFound = false;
            for (ClutterGroup group : clutterGroups){
                if (group.hasIntersection(track)){
                    group.addTrack(track);
                    overlapFound = true;
                    break;
                }
            }
            if (overlapFound){
                continue;
            }

            // if it did not intersect any existing cluttergroup put the track into a new clutter group
            //System.out.println("\n\nNo overlap detected - creating new cluttergroup for " + track.getName());
            ClutterGroup newGroup = new ClutterGroup(m_fontMetrics);
            newGroup.addTrack(track);
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
                    tracks.addAll(currentGroup.getTracks());
                    tracks.addAll(testGroup.getTracks());

                    sourceGroupA = currentGroup;
                    sourceGroupB = testGroup;

                    combinedGroup = new ClutterGroup(m_fontMetrics);
                    combinedGroup.addTracks(tracks);

                    break;
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
}
