package declutterapp.data;

import java.util.Collection;
import java.util.HashSet;

/**
 * A class representing a pair of X/Y coordinates.
 *
 * @author adam
 */
public class Coordinates {

    /** The X Coordinate. */
    protected int m_x;

    /** The Y Coordinate. */
    protected int m_y;

    /**
     * Constructor.
     *
     * @param x the x coordinate.
     * @param y the y coordinate.
     */
    public Coordinates(int x, int y){
        m_x = x;
        m_y = y;
    }

    /**
     * Creates a copy of a Coordinate pair from the provided one.
     * @param otherCoords a Coordinate pair to copy.
     */
    public Coordinates(Coordinates otherCoords){
        m_x = otherCoords.getX();
        m_y = otherCoords.getY();
    }

    /**
     * Returns the X value.
     * @return the X value.
     */
    public int getX(){
        return m_x;
    }

    /**
     * Returns the Y value.
     * @return the Y value.
     */
    public int getY(){
        return m_y;
    }


    /**
     * Generates and returns a {@link Collection} of {@link Coordinates} that includes all of the sets of Coordinates
     * that would be inside a box drawn using the provided Coordinates as the corners.  The ordering of the points does
     * not matter.
     *
     * @param firstPoint a pair of Coordinates.
     * @param secondPoint another pair of Coordinates.
     *
     * @return a collection of points that would be inside a box drawn using the two Coordinate pairs as its corners.
     */
    public static Collection<Coordinates> getRange(Coordinates firstPoint, Coordinates secondPoint){
        Collection<Coordinates> range = new HashSet<>();
        if (firstPoint != null && secondPoint != null){

            // Last selected is above the newly selected point
            if (secondPoint.getY() > firstPoint.getY()){
                if (secondPoint.getX() > firstPoint.getX()){
                    // Last selected is to the ABOVE and to the LEFT of the newly selected point.
                    for (int i = firstPoint.getX(); i <= secondPoint.getX(); i++){
                        for (int j = firstPoint.getY(); j <= secondPoint.getY(); j++){
                            range.add(new Coordinates(i, j));
                        }
                    }

                } else {
                    // Last Selected is to the ABOVE and to the RIGHT of the newly selected point.
                    for (int i = secondPoint.getX(); i <= firstPoint.getX(); i++){
                        for (int j = firstPoint.getY(); j <= secondPoint.getY(); j++){
                            range.add(new Coordinates(i, j));
                        }
                    }
                }

            // Last selected is Below the newly selected point
            } else {
                // The newly selected point is above the last selected one.
                if (secondPoint.getX() > firstPoint.getX()){
                    /// Last selected is to the BELOW and to the LEFT of the newly selected point.
                    for (int i = firstPoint.getX(); i <= secondPoint.getX(); i++){
                        for (int j = secondPoint.getY(); j <= firstPoint.getY(); j++){
                            range.add(new Coordinates(i, j));
                        }
                    }
                } else {
                    /// Last selected is to the BELOW and to the RIGHT of the newly selected point.
                    for (int i = secondPoint.getX(); i <= firstPoint.getX(); i++){
                        for (int j = secondPoint.getY(); j <= firstPoint.getY(); j++){
                            range.add(new Coordinates(i, j));
                        }
                    }
                }
            }
        }
        return range;
    }

    @Override
    public String toString(){
        return "[" + m_x + ", " + m_y + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Coordinates)) {
            return false;
        }
        final Coordinates other = (Coordinates) obj;
        if (this.m_x != other.m_x) {
            return false;
        }
        if (this.m_y != other.m_y) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.m_x;
        hash = 89 * hash + this.m_y;
        return hash;
    }
}
