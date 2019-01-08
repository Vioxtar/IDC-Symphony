package idc.symphony.visual;

import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Pair;

import java.util.*;

public class Trail {

    TPoint head; TPoint tail;
    LinkedList<TPoint> trail;

    public Trail() {
        this.head = new TPoint();
        this.trail = new LinkedList<TPoint>();
        this.tail = new TPoint();
    }

    int id;
    public void setID(int id) { this.id = id; }
    public int getID() { return id; }

    int maxTrailLength = 200;
    public void setMaxTrailLength(int length) { maxTrailLength = length; }

    double trailInterval = 10; double lastExtend;
    public void setTrailInterval(int interval) { trailInterval = interval; }

    Color headColor;
    public void setHeadColor(Color headCol) { headColor = headCol; }

    Color trailColor;
    public void setTrailColor(Color trailCol) { trailColor = trailCol; }

    Color lineColor;
    public void setLineColor(Color lineCol) { lineColor = lineCol; }

    double originX;
    public void setOriginX(double x) { originX = x; }

    double originY;
    public void setOriginY(double y) { originY = y; }

    public void setHeadX(double x) { head.x = x; }
    public void setHeadY(double y) { head.y = y; }

    public void setTailX(double x) { tail.x = x; }
    public void setTailY(double y) { tail.y = y; }

    double targetX;
    public void setTargetX(double x) {
        targetX = x;
    }
    public double getTargetX() { return targetX; }

    double targetY;
    public void setTargetY(double y) {
        targetY = y;
    }
    public double getTargetY() { return targetY; }

    double targetR;
    public void setTargetRadius(double r) { targetR = r; }
    public void setImmRadius(double r) { head.r = r; }

    double changeRSpeed;
    public void setChangeRadiusSpeed(double chngeSpd) {
        changeRSpeed = chngeSpd;
    }

    Trail followTarget;
    ArrayList<Trail> followers;
    public void setFollowTarget(Trail newTarget) {

        if (followTarget != null) {
            if (followTarget.followers == null) {
                followTarget.followers = new ArrayList<>();
            }
            followTarget.followers.remove(this);
        }

        followTarget = newTarget;
        tail.follow(newTarget.head);

        if (newTarget.followers == null) {
            newTarget.followers = new ArrayList<>();
        }
        newTarget.followers.add(this);
    }
    public Trail getFollowTarget(Trail followTarget) { return followTarget; }
    public ArrayList<Trail> getFollowers() { return followers; }

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
        head.aimToRadius(targetR, changeRSpeed * mul);
        head.aimToXY(targetX, targetY, 5 * mul);
        head.applySlide(0.01, mul);
        head.applyVel(mul);

        // Same idea for the trail itself
        if (trail.isEmpty()) { extendTrail(); return; }

        double frac = 0; double seg = 1 / (double)trail.size(); // The fraction in the linked list

        // Iterate the trail downwards from head to tail
        ListIterator trailIt = trail.listIterator(trail.size());
        while (trailIt.hasPrevious()) {

            TPoint tPoint = (TPoint)trailIt.previous();

            // The point above, used for radius propagation
            TPoint aboveTPoint = head;
            if (trailIt.hasPrevious()) {aboveTPoint = (TPoint)trailIt.previous(); trailIt.next();}

            // When frac is closer to 1, we're closer to the tail
            // When fracRev is closer to 1, we're closer to the head
            double fracExp = Math.pow(frac, tPoint.r * 0.2);
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
            tPoint.aimToY(tail.y, frac * speed * mul);

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
        circ.relocate(head.x, head.y); circ.setRadius(head.r); circ.setFill(headColor);
        circles.getChildren().add(circ);



        // Nothing to work with if we don't have a trail
        if (trail.isEmpty()) { return; }

        // Draw the connection between the head and the trail
        TPoint firstPoint = trail.getFirst();
        Line line = new Line();
        line.setStartX(head.x); line.setStartY(head.y);
        line.setEndX(firstPoint.x); line.setEndY(firstPoint.y);
        line.setStroke(lineColor);
        line.setStrokeWidth(head.r);
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
                line.setStroke(lineColor);
                line.setStrokeWidth(tPointA.r);
                lines.getChildren().add(line);

                trailIt.previous();
            }

            if (tPointA.r > 0) {
                circ = new Circle();
                circ.relocate(tPointA.x, tPointA.y); circ.setRadius(tPointA.r); circ.setFill(trailColor);
                circles.getChildren().add(circ);
            }
        }

        TPoint last = trail.getLast();
        line = new Line();
        line.setStartX(last.x); line.setStartY(last.y);
        line.setEndX(tail.x); line.setEndY(tail.y);
        line.setStroke(lineColor);
        line.setStrokeWidth(last.r);
        lines.getChildren().add(line);


        g.getChildren().add(circles);
        g.getChildren().add(lines);

        // Draw a fucking cool as shit glowing circle if we're big :DDD
        double rDiff = head.r - targetR;
        if (rDiff > 0.05) {
            Circle orb = new Circle();
            orb.relocate(head.x, head.y);
            orb.setRadius(rDiff);
            orb.setFill(headColor);

            double blur = rDiff * rDiff * 50;
            orb.setEffect(new BoxBlur(blur, blur, 5));
            orb.setBlendMode(BlendMode.ADD);
            g.getChildren().add(orb);
        }

    }
}
