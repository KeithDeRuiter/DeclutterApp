package declutterapp;

import declutterapp.data.Track;
import declutterapp.data.clutter.ClutterGroup;
import declutterapp.data.rendering.Renderable;
import declutterapp.data.rendering.RenderableBox;
import declutterapp.data.rendering.RenderableLine;
import declutterapp.data.rendering.RenderableSymbol;
import declutterapp.data.rendering.RenderableText;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author Keith
 */
public class RectangleShuffleProcessor {
    
    public void shuffleWithinGroups(Collection<ClutterGroup> clutterGroups,
                        Collection<Track> tracks,
                        Map<UUID, RenderableSymbol> symbols,
                        Map<UUID, RenderableText> texts,
                        Set<RenderableLine> lines) {
        
        //Go through each group
        for(ClutterGroup group : clutterGroups) {
            List<Collision> collisions = new ArrayList<>();
            
            List<Renderable> groupMoveables = new ArrayList<>();
            List<Renderable> groupImmoveables = new ArrayList<>();

            //Store our symbols as immovable and our labels as movable
            for(Track t : group.getTracks()) {
                groupMoveables.add(texts.get(t.getId()));
                groupImmoveables.add(symbols.get(t.getId()));
            }
            
            //Loop to resolve everything
            boolean resolutionNeeded = true;
            while(resolutionNeeded) {
                //In this group, go through every text
                Map<UUID, RenderableText> groupTexts = group.getTexts();
                for(RenderableText text : groupTexts.values()) {
                
                    //For this text, is it overlapping another symbol, text, or cluttergroup
                    for(ClutterGroup other : clutterGroups) {
                        Rectangle otherGroupRect = other.calculateGroupRect(true);
                        if(group.equals(other)) { //Check our group's renderables
                            //Check this text against the group's other texts
                            for(Renderable r : groupMoveables) {
                                if(text.equals(r)) {
                                    continue; //it's us with ourself, move along
                                } else if(text.getBounds().intersects(r.getBounds())) {
                                    collisions.add(new Collision(text, r, false));
                                }
                            }
                            for(Renderable r : groupImmoveables) {
                                if(text.equals(r)) {
                                    continue; //it's us with ourself, move along (shouldn't happen, texts aren't immoveable)
                                } else if(text.getBounds().intersects(r.getBounds())) {
                                    collisions.add(new Collision(text, r, true));
                                }
                            }
                            
                            
                        } else if (text.getBounds().intersects(otherGroupRect)) {   //Collision with other group
                            collisions.add(new Collision(text, RenderableBox.getBoxFromRectangle(otherGroupRect), true));
                        }
                    }
                }
                
                //If there were collisions. nudge all collisions and move renderables
                if(!collisions.isEmpty()) {
                    //Resolve and nudge things
                    for(Collision c : collisions) {
                        resolveCollision(c);
                    }
                    collisions.clear();
                } else {
                    //else break and go on to next group
                    resolutionNeeded = false;
                }
            }
            
            
        }   //End loop for this group
        
        
        
        
    }
    
    
    
    
    
    
    
    
    /**
     * Shuffles the "probes" rectangles around so that they don't intersect each other, and also don't intersect the pylons.
     * Probes are movable, and pylons are stationary.  ACTUALLY MOVES THE RENDERABLES!!
     * @param pylons immoveables
     * @param probes movables
     * @param groupBoundaries
     * @param tracks
     * @param texts
     * @param symbols
     * @param lines
     */
    public void shuffleRectangles(List<Renderable> pylons,
                                  List<Renderable> probes,
                                  Map<Rectangle, ClutterGroup> groupBoundaries,
                                  Collection<Track> tracks,
                                  Map<UUID, RenderableSymbol> symbols,
                                  Map<UUID, RenderableText> texts,
                                  Set<RenderableLine> lines) {
        //Iterate over these, moving probes around pylons
        List<Renderable> moveableObjects = new ArrayList<>();
        List<Renderable> immoveableObjects = new ArrayList<>();
        List<Renderable> allObjects = new ArrayList<>();
        
        //Generate Collideables
        for(Renderable r : pylons) {
            //Mass indicates immobile
            immoveableObjects.add(r);
        }
        for(Renderable r : probes) {
            moveableObjects.add(r);
        }
        for(Rectangle r : groupBoundaries.keySet()) {
            immoveableObjects.add(RenderableBox.getBoxFromRectangle(r));
        }
        
        allObjects.addAll(moveableObjects);
        allObjects.addAll(immoveableObjects);
        
        
        //Update loop
        boolean intersectionsExist = true;
        int i = 0;
        
        while(intersectionsExist) {
            i++;
            //Detect all collisions
            List<Collision> collisions = new ArrayList<>();
            
            intersectionsExist = false;
            for(Renderable probe : moveableObjects) {  //Check moveable things against everything
                for(Renderable b : allObjects) {
                    if (probe.equals(b)) {
                        continue;
                    }
                    
                    
                    
                    if(probe.getBounds().intersects(b.getBounds())) {
                        collisions.add(new Collision(probe, b, false));
                        intersectionsExist = true;
                        System.out.println("Found Collision");
                    }
                }
            }
            
            //Resolve collisions and move everything
            System.out.println("There were " + collisions.size() + " collisions");
            for(Collision c : collisions) {
                System.out.println(c.A.getClass().getSimpleName() + " and " + c.B.getClass().getSimpleName());
                resolveCollision(c);
            }
            
            System.out.println("Done");
        }
        
    }
    
    void resolveCollision( Collision c ) {
        Renderable A = c.A;
        Renderable B = c.B;
        
        // Calculate relative velocity
        //Vec2 relVel = B.velocity.subtract(A.velocity);
        Vec2 aPos = new Vec2((float)A.getBounds().getCenterX(), (float)A.getBounds().getCenterY());
        Vec2 bPos = new Vec2((float)B.getBounds().getCenterX(), (float)B.getBounds().getCenterY());
        Vec2 normal = bPos.subtract(aPos).normalize();
        
        // Calculate relative velocity in terms of the normal direction
        //float velAlongNormal = relVel.dotProduct(normal);

        // Do not resolve if velocities are separating
//        if(velAlongNormal > 0) {
//            return;
//        }

        // Calculate restitution, set to 1 here since bounciness doesn't matter
        float e = 100;

        // Calculate impulse scalar    
        float j = 5;
        //float j = -(1 + e) * velAlongNormal;
        //j /= (1 / A.mass) + (1 / B.mass);

        // Apply impulse and update velocity if the mass is greater than 0 (5 represents immobile)
        Vec2 impulse = normal.scale(j);
        
        System.out.println("Impulse: (" + impulse.x + "  " + impulse.y + ")");
        System.out.println("A = " + c.A.toString());
        System.out.println("B = " + c.B.toString());
        System.out.println("");
        
        A.translate((int)-impulse.x, (int)-impulse.y);
        if(!c.bIsPylon) {
            B.translate((int)impulse.x, (int)impulse.y);
        }
        
        //if(A.mass < 3) {
        //    A.velocity.subtract(normal.scale(j));
        //}
        //if(B.mass < 3) {
        //    B.velocity.add(normal.scale(j));
        //}
    }
    
    
    private class Collision {
        public final Renderable A;
        public final Renderable B;
        public final boolean bIsPylon;
        
        public Collision(Renderable A, Renderable B, boolean bIsPylon) {
            this.A = A;
            this.B = B;
            this.bIsPylon = bIsPylon;
        }
    }
    
    /**
     * 2D float vector
     */
    private class Vec2 {
        public final float x;
        public final float y;
        
        public Vec2(float x, float y) {
            this.x = x;
            this.y = y;
        }
        
        public Vec2 add(Vec2 other) {
            return new Vec2(x + other.x, y + other.y);
        }
        
        public Vec2 subtract(Vec2 other) {
            return new Vec2(x - other.x, y - other.y);
        }
        
        public float dotProduct(Vec2 other) {
            return (this.x * other.x) + (this.y * other.y);
        }
        
        public Vec2 scale(float s) {
            return new Vec2(this.x * s, this.y * s);
        }
        
        public float magnitude() {
            return (float)Math.hypot(x, y);
        }
        
        public Vec2 normalize() {
            float mag = this.magnitude();
            return new Vec2(x / mag, y / mag);
        }
    }
    
}
