package visual;

import javax.swing.text.Position;
import java.awt.*;
import java.util.*;

public class Trail {

    int id;
    Color headColor;
    Color trailColor;

    TPoint head; TPoint tail;
    LinkedList<TPoint> trail;

    int maxTrailLength = 50;
    double trailInterval = 1; double lastExtend;

    double targetR;
    double exp; double targetExp;
    double targetX; double targetY;
    double originX; double originY;

    public Trail(int id, Color headColor, Color trailColor, double xPos, double yPos, Trail parent) {
        this.id = id;
        this.headColor = headColor;
        this.trailColor = trailColor;

        this.head = new TPoint();
        this.trail = new LinkedList<TPoint>();
        this.tail = new TPoint();

        head.x = xPos;
        head.y = yPos;

        tail.x = xPos;
        tail.y = yPos;

        if (parent != null) {

            head.x = parent.head.x;
            head.y = parent.head.y;

            tail.x = parent.head.x;
            tail.y = parent.head.y;

            this.tail.follow(parent.head);
        }

        head.r = 10;
        tail.r = 1;

        originX = xPos;
        originY = yPos;
    }

    public void setTargetX(double x) {
        targetX = x;
    }

    public void setTargetY(double y) {
        targetY = y;
    }

    public void setTargetRadius(double r) { targetR = r; }

    public void setImmRadius(double r) { head.r = r; }

    public void setTargetExp(double exp) { targetExp = exp; }

    public void setImmExp(double exp) { this.exp = exp; }

    /**
     * Extend the trail by adding a new point from the head.
     */
    public void extendTrail() {

        TPoint newTPoint = new TPoint();
        newTPoint.x = head.x;
        newTPoint.y = head.y;
        newTPoint.r = 0;
        trail.addFirst(newTPoint);

        // If the head has followers, make them follow the new point instead
        ArrayList<TPoint> followersToAdd = new ArrayList<>();
        for (TPoint follower : head.followers) {
            followersToAdd.add(follower);
        }
        for (TPoint followerToAdd : followersToAdd) {
            followerToAdd.follow(newTPoint);
        }

        // If we've exceeded our limit, cut off the last point
        if (trail.size() > maxTrailLength) {
            TPoint last = trail.getLast();

            // If the last point has followers, make them follow the tail instead
            followersToAdd = new ArrayList<>();
            for (TPoint follower : last.followers) {
                followersToAdd.add(follower);
            }
            for (TPoint followerToAdd : followersToAdd) {
                followerToAdd.follow(tail);
            }

            trail.removeLast();
        }
    }

    public void update(double timeMul) {

        // Consider the time delta multiplier for frametime independent updates
        timeMul *= 0.005;

        // Head radius
        double rDiff = targetR - head.r;
        head.r += rDiff * timeMul * 5; // TODO: Make this a parameter - the longer the release of a note the slower the change

        // Update our head
        head.aimToXY(targetX, targetY, timeMul);
        head.applySlide(0.2, timeMul);
        head.applyVel(timeMul);

        // Same idea for the trail itself
        if (trail.isEmpty()) { extendTrail(); return; }

        double frac = 0; double seg = 1 / (double)trail.size(); // The fraction in the linked list

        // Update the trail exponent
        double expDiff = targetExp - exp;
        exp += expDiff * timeMul;

        // Iterate the trail downwards from head to tail
        ListIterator trailIt = trail.listIterator(trail.size());
        while (trailIt.hasPrevious()) {

            TPoint tPoint = (TPoint)trailIt.previous();

            // The point above, used for radius propagation
            TPoint aboveTPoint = head;
            if (trailIt.hasPrevious()) {aboveTPoint = (TPoint)trailIt.previous(); trailIt.next();}

            // When frac is closer to 1, we're closer to the tail
            // When fracRev is closer to 1, we're closer to the head
            double fracExp = Math.pow(frac, exp);
            double fracExpRev = 1 - fracExp;
            double fracRev = 1 - frac;

            tPoint.r = aboveTPoint.r * 0.975;

            // Trail aim
            double speed = 1; // The speed of the trail flow
            tPoint.aimToX(tail.x, timeMul * fracExpRev * speed);
            tPoint.aimToY(tail.y, timeMul * fracRev * speed);

            tPoint.applySlide(0.2, timeMul);
            tPoint.applyVel(timeMul);

            // Update the fraction for the next iteration
            frac += seg;
        }

        // Tail aim
        tail.aimToXY(originX, originY, timeMul); // Aim to the origin if we're not following anything
        tail.applySlide(0.2, timeMul);
        tail.applyVel(timeMul);

        // Extend trail when enough time passes
        lastExtend += timeMul;
        while (lastExtend > trailInterval) {
            lastExtend -= trailInterval;
            extendTrail();
        }
    }

    public void draw(Graphics g, double offX, double offY) {

        g.setColor(headColor);
        g.fillOval((int)(head.x - head.r / 2 + offX), (int)(head.y - head.r / 2 + offY), (int)head.r, (int)head.r);

        // Nothing to work with if we don't have a trail
        if (trail.isEmpty()) { return; }

        g.setColor(trailColor);

        // Draw the connection between the head and the trail
        TPoint firstPoint = trail.getFirst();
        g.drawLine((int)(head.x + offX), (int)(head.y + offY), (int)(firstPoint.x + offX), (int)(firstPoint.y + offY));

        // Draw the trail itself
        ListIterator trailIt = trail.listIterator();
        while (trailIt.hasNext()) {
            TPoint tPointA = (TPoint)trailIt.next();
            if (trailIt.hasNext()) { // We're not last, draw a line to the next unit
                TPoint tPointB = (TPoint) trailIt.next();
                g.drawLine((int) (tPointA.x + offX), (int) (tPointA.y + offY), (int) (tPointB.x + offX), (int) (tPointB.y + offY));
                trailIt.previous();
            }

            if (tPointA.r > 5) {
                g.fillOval((int)(tPointA.x - tPointA.r / 2 + offX), (int)(tPointA.y - tPointA.r / 2 + offY), (int)tPointA.r, (int)tPointA.r);
            }
        }

//        g.fillOval((int)(tail.x - tail.r / 2 + offX), (int)(tail.y - tail.r / 2 + offY), (int)tail.r, (int)tail.r);

        TPoint last = trail.getLast();
        g.drawLine((int)(last.x + offX), (int)(last.y + offY), (int)(tail.x + offX), (int)(tail.y + offY));
    }

    public class TPoint {
        public double x, vx;
        public double y, vy;
        public double r;

        HashSet<TPoint> followers;
        TPoint followed;

        public TPoint() {
            this.x = 0;
            this.y = 0;
            this.vx = 0;
            this.vy = 0;

            this.followers = new HashSet<>(); // Who is following us?
            this.followed = null; // Who are we following?
        }

        public void applyVel(double timeMul) {
            this.setX(this.x + this.vx * timeMul);
            this.setY(this.y + this.vy * timeMul);
        }

        public void applySlide(double slide, double timeMul) {
            this.vx *= Math.pow(slide, timeMul); this.vy *= Math.pow(slide, timeMul);
        }

        public void setX(double x) {
            // Set X only if we're not following someone else
            if (followed == null) { this.x = x; }

            // Set the value of our followers as well
            for (TPoint follower : followers) {
                follower.setX(x); // We use this call to support follow cycles
                follower.x = x; // But following points won't set their own values, so set them ourselves
            }
        }

        public void setY(double y) {
            // Set Y only if we're not following someone else
            if (followed == null) { this.y = y; }

            // Set the value of our followers as well
            for (TPoint follower : followers) {
                follower.setY(y); // We use this call to support follow cycles
                follower.y = y; // But following points won't set their own values, so set them ourselves
            }
        }

        public void aimToX(double targetX, double timeMul) {
            vx += (targetX - x) * timeMul;
        }

        public void aimToY(double targetY, double timeMul) {
            vy += (targetY - y) * timeMul;
        }

        public void aimToXY(double targetX, double targetY, double timeMul) {
            aimToX(targetX, timeMul);
            aimToY(targetY, timeMul);
        }

        public void stopFollowing() {
            // Remove ourselves from the followed's followers list
            if (followed != null) {
                if (followed.followers.contains(this)) {
                    followed.followers.remove(this);
                }
            }
            followed = null;
        }

        public void follow(TPoint newFollowed) {
            // Stop following whoever we might be following
            stopFollowing();

            // Start following the new followed
            followed = newFollowed;
            newFollowed.followers.add(this);
        }

        public double distToSqr(TPoint othr) {
            double xDiff = x - othr.x;
            double yDiff = y - othr.y;
            return xDiff * xDiff + yDiff * yDiff;
        }
    }
}
