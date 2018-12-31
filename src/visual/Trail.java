package visual;

import javax.swing.text.Position;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class Trail {

    int id;
    Color headColor;
    Color trailColor;

    TPoint head; TPoint tail;
    LinkedList<TPoint> trail;

    int maxTrailLength = 200;
    double trailInterval = 0.5; double lastExtend;

    double targetR;
    double exp; double targetExp;
    double targetX; double targetY;
    double originX; double originY;


    public Trail(int id, Color headColor, Color trailColor, double xPos, double yPos) {
        this.id = id;
        this.headColor = headColor;
        this.trailColor = trailColor;

        this.head = new TPoint();
        this.trail = new LinkedList<TPoint>();
        this.tail = new TPoint();

        head.x = xPos;
        head.y = yPos;
        head.r = 10;

        tail.x = xPos;
        tail.y = yPos;
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

    public void extendTrail() {
        TPoint newTPoint = new TPoint();
        newTPoint.x = head.x;
        newTPoint.y = head.y;
        newTPoint.r = 0;
        trail.addFirst(newTPoint);

        if (trail.size() > maxTrailLength) {
            trail.removeLast();
        }
    }

    public void update(double timeMul) {

        // Consider the time delta multiplier for frametime independent updates
        timeMul *= 0.005;

        // Head radius
        double rDiff = targetR - head.r;
        head.r += rDiff * timeMul * 5; // TODO: Make this a parameter - the longer the release of a note the slower the change

        // Head aim
        head.aimToXY(targetX, targetY, timeMul);

        // Head friction
        double slide = 0.2; // The smaller the higher the friction - 1 maintains constant energy
        head.vx *= Math.pow(slide, timeMul); head.vy *= Math.pow(slide, timeMul);

        // Head velocities
        head.x += head.vx * timeMul;
        head.y += head.vy * timeMul;

        // Same idea for the trail itself
        if (trail.isEmpty()) { extendTrail(); return; }

        double frac = 0; double seg = 1 / (double)trail.size(); // The fraction in the linked list

        // Update the trail exponent
        double expDiff = targetExp - exp;
        exp += expDiff * timeMul;

        // Iterate the trail downwards from head to tail
        ListIterator trailIt = trail.listIterator(trail.size());
        while (trailIt.hasPrevious()) {
            TPoint tPoint = (TPoint)trailIt.previous(); // Get the next trail point
            TPoint aboveTPoint = head;
            if (trailIt.hasPrevious()) {aboveTPoint = (TPoint)trailIt.previous(); trailIt.next();}

            // When frac is closer to 1, we're closer to the tail
            // When fracRev is closer to 1, we're closer to the head
            double fracExp = Math.pow(frac, exp);
            double fracExpRev = 1 - fracExp;
            double fracRev = 1 - frac;

            tPoint.r = aboveTPoint.r * 0.975;

            // Trail aim
            tPoint.aimToX(tail.x, timeMul * fracExpRev);
            tPoint.aimToY(tail.y, timeMul * fracRev * 0.3);

            // Trail friction
            slide = 0.2;
            tPoint.vx *= Math.pow(slide, timeMul); tPoint.vy *= Math.pow(slide, timeMul);

            // Trail velocities
            tPoint.x += tPoint.vx * timeMul;
            tPoint.y += tPoint.vy * timeMul;

            // Update the fraction for the next iteration
            frac += seg;
        }

        // Tail aim
        tail.aimToXY(originX, originY, timeMul);

        // Tail friction
        tail.vx *= Math.pow(slide, timeMul); tail.vy *= Math.pow(slide, timeMul);

        // Tail velocities
        tail.x += tail.vx * timeMul;
        tail.y += tail.vy * timeMul;

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
            g.fillOval((int)(tPointA.x - tPointA.r / 2 + offX), (int)(tPointA.y - tPointA.r / 2 + offY), (int)tPointA.r, (int)tPointA.r);
        }

        g.fillOval((int)(tail.x - tail.r / 2 + offX), (int)(tail.y - tail.r / 2 + offY), (int)tail.r, (int)tail.r);

        TPoint last = trail.getLast();
        g.drawLine((int)(last.x + offX), (int)(last.y + offY), (int)(tail.x + offX), (int)(tail.y + offY));
    }

    public class TPoint {
        public double x, vx;
        public double y, vy;
        public double r;

        public TPoint() {
            this.x = 0;
            this.y = 0;
            this.vx = 0;
            this.vy = 0;
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

        public double distToSqr(TPoint othr) {
            double xDiff = x - othr.x;
            double yDiff = y - othr.y;
            return xDiff * xDiff + yDiff * yDiff;
        }
    }
}
