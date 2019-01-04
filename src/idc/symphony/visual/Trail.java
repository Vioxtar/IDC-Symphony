package idc.symphony.visual;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Pair;

import java.util.*;

public class Trail {

    int id;
    Color headColor;
    Color trailColor;

    TPoint head; TPoint tail;
    LinkedList<TPoint> trail;

    int maxTrailLength = 200;
    double trailInterval = 10; double lastExtend;

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

            tail.follow(parent.head);
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

    public double[] getMaxs() {
        return new double[] {Math.max(head.x, tail.x), Math.max(head.y, tail.y)};
    }

    public double[] getMins() {
        return new double[] {Math.min(head.x, tail.x), Math.min(head.y, tail.y)};
    }

    /**
     * Extend the trail by adding a new point from the head.
     */
    public void extendTrail() {

        TPoint newTPoint = new TPoint();
        newTPoint.x = head.x;
        newTPoint.y = head.y;
        newTPoint.r = 0;
        trail.addFirst(newTPoint);

        // If we've exceeded our limit, cut off the last point
        if (trail.size() > maxTrailLength) {
            trail.removeLast();
        }
    }

    public void update() {

        double mul = 0.005;

        // Update our head
        head.aimToRadius(targetR, 5 * mul);
        head.aimToXY(targetX, targetY, 10 * mul);
        head.applySlide(0.01, mul);
        head.applyVel(mul);

        // Same idea for the trail itself
        if (trail.isEmpty()) { extendTrail(); return; }

        double frac = 0; double seg = 1 / (double)trail.size(); // The fraction in the linked list

        // Update the trail exponent before iteration
        exp += (targetExp - exp) * 20 * mul;

        // Iterate the trail downwards from head to tail
        ListIterator trailIt = trail.listIterator(trail.size());
        while (trailIt.hasPrevious()) {

            TPoint tPoint = (TPoint)trailIt.previous();

            // The point above, used for radius propagation
            TPoint aboveTPoint = head;
            if (trailIt.hasPrevious()) {aboveTPoint = (TPoint)trailIt.previous(); trailIt.next();}

            // When frac is closer to 1, we're closer to the tail
            // When fracRev is closer to 1, we're closer to the head
            double fracExp = Math.pow(frac, tPoint.r);
            double fracExpRev = 1 - fracExp;
            double fracRev = 1 - frac;

            double propDecay = 0.975;
            double propSpeed = 40;
            tPoint.aimToRadius(aboveTPoint.r * propDecay, propSpeed * mul);

            // Trail interpolation (assist each point with actually arriving at the tail)
            double intFraction = Math.pow(fracRev, 10);
            tPoint.intToX(tail.x, intFraction);
            tPoint.intToY(tail.y, intFraction);

            // Trail aim
            double speed = 5; // The speed of the trail flow
            tPoint.aimToX(tail.x, fracExpRev * speed * mul);
            tPoint.aimToY(tail.y, fracRev * speed * mul);

            // Trail slide
            double slide = 0.1;
            tPoint.applySlide(slide, mul);
            tPoint.applyVel(mul);

            // Update the fraction for the next iteration
            frac += seg;
        }

        // Tail aim
        tail.aimToXY(originX, originY, mul); // Aim to the origin if we're not following anything
        tail.applySlide(0.2, mul);
        tail.applyVel(mul);

        // Extend trail when enough time passes
        lastExtend += 1;
        while (lastExtend > trailInterval) {
            lastExtend -= trailInterval;
            extendTrail();
        }
    }

    public void draw(Pane g) {

        Group circles = new Group();
        Group lines = new Group();

        Circle circ = new Circle();
        circ.relocate(head.x, head.y); circ.setRadius(head.r); circles.getChildren().add(circ);

        // Nothing to work with if we don't have a trail
        if (trail.isEmpty()) { return; }

        // Draw the connection between the head and the trail
        TPoint firstPoint = trail.getFirst();
        Line line = new Line();
        line.setStartX(head.x); line.setStartY(head.y);
        line.setEndX(firstPoint.x); line.setEndY(firstPoint.y);
        lines.getChildren().add(line);

        // Draw the trail itself
        ListIterator trailIt = trail.listIterator();
        while (trailIt.hasNext()) {
            TPoint tPointA = (TPoint)trailIt.next();
            if (trailIt.hasNext()) { // We're not last, draw a line to the next unit
                TPoint tPointB = (TPoint) trailIt.next();

                line = new Line();
                line.setStartX(tPointA.x); line.setStartY(tPointA.y);
                line.setEndX(tPointB.x); line.setEndY(tPointB.y);
                lines.getChildren().add(line);

                trailIt.previous();
            }

            if (tPointA.r > 0) {
                circ = new Circle();
                circ.relocate(tPointA.x, tPointA.y); circ.setRadius(tPointA.r); circles.getChildren().add(circ);
            }
        }

        TPoint last = trail.getLast();
        line = new Line();
        line.setStartX(last.x); line.setStartY(last.y);
        line.setEndX(tail.x); line.setEndY(tail.y);
        lines.getChildren().add(line);

        g.getChildren().add(circles);
        g.getChildren().add(lines);

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

        public void passFollower(TPoint follower, TPoint other) {
            follower.follow(other);
        }

        public void passFollowers(TPoint other) {
            // If the last point has followers, make them follow the tail instead
            ArrayList<TPoint> followersToAdd = new ArrayList<>();
            for (TPoint follower : this.followers) {
                followersToAdd.add(follower);
            }
            for (TPoint followerToAdd : followersToAdd) {
                followerToAdd.follow(other);
            }
        }

        public void aimToRadius(double targetR, double mul) {
            mul = Math.min(mul, 1);
            double rDiff = targetR - this.r;
            this.r += rDiff * mul;
        }

        public void applyVel(double mul) {
            this.setX(this.x + this.vx * mul);
            this.setY(this.y + this.vy * mul);
        }

        public void applySlide(double slide, double mul) {
            this.vx *= Math.pow(slide, mul); this.vy *= Math.pow(slide, mul);
        }

        public void setX(double x) {
            // Set X only if we're not following someone else
            if (followed == null) { this.x = x; }

            // Set the value of our followers as well
            for (TPoint follower : followers) {
                follower.setX(x); // We use this call to support follow cycles
                follower.intToX(x, 0.5); // But following points won't set their own values, so set them ourselves
            }
        }

        public void setY(double y) {
            // Set Y only if we're not following someone else
            if (followed == null) { this.y = y; }

            // Set the value of our followers as well
            for (TPoint follower : followers) {
                follower.setY(y); // We use this call to support follow cycles
                follower.intToY(y, 0.5); // But following points won't set their own values, so set them ourselves
            }
        }

        public void intToX(double targetX, double fraction) {
            x += (targetX - x) * fraction;
        }

        public void intToY(double targetY, double fraction) {
            y += (targetY - y) * fraction;
        }

        public void aimToX(double targetX, double mul) {
            vx += (targetX - x) * mul;
        }

        public void aimToY(double targetY, double mul) {
            vy += (targetY - y) * mul;
        }

        public void aimToXY(double targetX, double targetY, double mul) {
            aimToX(targetX, mul);
            aimToY(targetY, mul);
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
