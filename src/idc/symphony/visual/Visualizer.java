package idc.symphony.visual;

import idc.symphony.data.FacultyData;
import idc.symphony.visual.scheduling.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

import javax.xml.soap.Text;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.random;

public class Visualizer extends Pane {

    // Window constants
    final int CAN_WDTH = 720;
    final int CAN_HGHT = 680;
    final Color BG_COLR = new Color(0, 0, 0.2,1);

    // Spacing constants
    final double INFO_X_SPACE = 500;
    final double INFO_Y_OFFSET = 200;
    final double LAYER_HEIGHT = 800;

    // The scheduled visual events to be displayed, sorted in chronological order
    Queue<VisualEvent> schedEvents;

    // All our trails
    ArrayList<Trail> trails;

    // All our infographs, as well as a distribution to right and left infographs
    ArrayList<Infograph> infographs;
    ArrayList<Infograph> rightInfographs;
    ArrayList<Infograph> leftInfographs;

    TextScroller yearText;
    TextScroller eventsText;

    // Maps a faculty ID number to our own 'visID' number, set to be the same for
    // a trail and its corresponding infograph
    HashMap<Integer, Integer> facIDtoVisID;

    // Framing parameters
    double camCenterX, camCenterY, camZoom;

    // Visualization timing
    AnimationTimer mainTimer;
    long simStartTime;

    // The simulation/draw tickrates in milliseconds
    final int DRAW_TICK = 16;
    final int SIM_TICK = 5;


    public Visualizer() {}


    /**
     * Starts the visualization
     * @param schedEvents
     */
    public void start(Queue<VisualEvent> schedEvents) {

        this.simStartTime = System.nanoTime();

        // Initialize schedueled events queue as soon as we're up
        // for loading prior to simulation
        this.schedEvents = schedEvents;

        this.trails = new ArrayList<>();
        this.infographs = new ArrayList<>();
        this.rightInfographs = new ArrayList<>();
        this.leftInfographs = new ArrayList<>();
        this.facIDtoVisID = new HashMap<>();

        this.yearText = new TextScroller();
        this.yearText.setMaxLines(1);
        this.yearText.setTextSize(230);
        this.yearText.setX(500);
        this.yearText.setY(-500);

        this.eventsText = new TextScroller();
        this.eventsText.setMaxLines(8);
        this.eventsText.setTextSize(100);
        this.eventsText.setX(500);
        this.eventsText.setY(0);
        this.eventsText.setWrappingWidth(2500);

        // Start the main loop timer
        this.mainTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
            simulationTick(now); // For simulation update calls
            stageTick(now); // For drawing calls
            }
        };
        this.mainTimer.start();
    }

    /**
     * Stops the visualization, returns empty to black screen
     */
    public void stop() {
        // Stop the main timer and clear ourselves
        this.mainTimer.stop();
        this.getChildren().clear();

        // Reset our time placement
        simBefore = -1;
        stageBefore = -1;
    }

    /**
     * Calls simulation updates at fixed timings.
     */
    long simBefore = -1;
    final long simTickInterval = TimeUnit.NANOSECONDS.convert(SIM_TICK, TimeUnit.MILLISECONDS);
    public void simulationTick(long now) {
        if (simBefore == -1) {
            simBefore = now;
        }
        while(simBefore < now) {
            simulate(now);
            simBefore += simTickInterval;
        }
    }

    /**
     * Calls draw updates at fixed timings.
     */
    long stageBefore = -1;
    final long stageTickInterval = TimeUnit.NANOSECONDS.convert(DRAW_TICK, TimeUnit.MILLISECONDS);;
    public void stageTick(long now) {
        if (stageBefore == -1) {
            stageBefore = now;
        }
        if (stageBefore <= now) {
            visualize();
            stageBefore += stageTickInterval;
        }
    }


    /**
     * Simulates a given visual event.
     * @param event
     */
    public void visualizeEvent(VisualEvent event) {
        if (event instanceof NotePlayed) {
//            System.out.println("Note Played");

            // TODO: Set a higher immediate radius, draw an additive spreading blur to the background with the trail color
            FacultyData fD = ((NotePlayed) event).faculty;
            if (facIDtoVisID.containsKey(fD.ID)) {
                int trailID = facIDtoVisID.get(fD.ID);
                Trail trail = trails.get(trailID);
                trail.setImmRadius(200 * ((NotePlayed) event).amp);
                trail.setChangeRadiusSpeed(1 / ((NotePlayed) event).duration);
            }

        } else if (event instanceof EventOccured) {
//            System.out.println("Event Occured");

            EventOccured eventOccurance = (EventOccured)event;
            String str = eventOccurance.year + " - " + eventOccurance.description;
            Color col = getPrefferedColor(eventOccurance.facultyID);
            col.interpolate(Color.WHITE, 0.3);
            eventsText.addText(str, col);

        } else if (event instanceof FacultyRoleChanged) {
//            System.out.println("Faculty Role Changed");

            // TODO: Change the saturation of every trail color, shift target positions

        } else if (event instanceof FacultyJoined) {

            FacultyData fD = ((FacultyJoined) event).faculty;

            // The visID of a trail will always be the visID of its corresponding infograph
            int visID = trails.size();
            facIDtoVisID.put(fD.ID, visID);

            // Color TODO: Make this hard coded or something, give fancier colors
            Color facColor = getPrefferedColor(fD.ID);

            Trail trail = new Trail();

            trail.name = fD.name;

            // Radius
            trail.setTargetRadius(20);
            trail.setImmRadius(0);
            trail.setChangeRadiusSpeed(5);

            // Color
            trail.setHeadColor(facColor); trail.setTrailColor(facColor); trail.setLineColor(facColor);

            // Obtain our parent and emerge from it (make sure it's valid first)
            Trail parent = null;
            if (fD.parent != null && facIDtoVisID.containsKey(fD.parent.ID)) {
                // Make sure that we have a parent
                parent = trails.get(facIDtoVisID.get(fD.parent.ID));
            }
            if (parent != null && parent != trail) {

                // Originate from our parent's head
                trail.setOriginX(parent.head.x);
                trail.setOriginY(parent.head.y);
                trail.setHeadX(parent.head.x); trail.setHeadY(parent.head.y);
                trail.setTailX(parent.head.x); trail.setTailY(parent.head.y);

                // Have the trail follow its parent
                trail.setFollowTarget(parent);

                // Aim above the parent
                trail.setTargetY(parent.getTargetY() - LAYER_HEIGHT);

            } else {

                // Set starting position
                trail.setOriginX(0);
                trail.setOriginY(0);
                trail.setHeadX(0); trail.setHeadY(0);
                trail.setTailX(0); trail.setTailY(0);

                // Give the starting height
                trail.setTargetY(-LAYER_HEIGHT);
            }

            trails.add(visID, trail);

            // Add an introductory infograph
            Infograph infG = new Infograph();
            // Title
            infG.setTitleText(fD.name);
            infG.setTitleSize(100);
            // Color
            infG.setTitleColor(facColor);
            infG.setLineColor(facColor);
            // Starting position
            infG.setX(trail.head.x); infG.setY(trail.head.y);
            // Line width
            infG.setLineWidth(5);
            // Aim at our trail's head
            infG.setAimTarget(trail.head);

            // Pop in and out effects
            infG.popIn(0.01);
            infG.popOut(0.01);
            infographs.add(visID, infG);

            // Set unified IDs of the trail and its infograph
            trail.setID(visID);
            infG.setID(visID);

            // If we have a parent, organize its child trail X targets to add room for the new child
            if (parent != null) {
                // This will also shift the children's infographs
                organizeChildTrails(parent, parent.getTargetY());
            } else {
                // If we don't have a parent, shift our infograph ourselves
                shiftTrailsInfograph(trail);
            }

            // Re-organize all infographs
            organizeInfographs(rightInfographs);
            organizeInfographs(leftInfographs);

        } else if (event instanceof YearChanged) {
//            System.out.println("Year Changed");

            // TODO: Do something in the background to show the year...?
            YearChanged yearEvent = (YearChanged)event;
            yearText.addText(String.valueOf(yearEvent.year), new Color(0.5, 0.5, 0.5, 1));

        }
    }

    /**
     * Performs a single simulation tick.
     * @param now
     */
    double minX, maxX, minY, maxY;
    public void simulate(long now) {

        // Update the simulation time
        double simTime = (double)(now - simStartTime) / 10e8;

        // Peek at our next event and determine if we should simulate it
        double simReadRate = 1; // TODO: DEBUG
        VisualEvent nextEvent = schedEvents.peek();
        while (nextEvent != null && nextEvent.time() / simReadRate <= (simTime)) {
            schedEvents.poll();
            visualizeEvent(nextEvent);
            nextEvent = schedEvents.peek();
        }

        // Update all trails
        trails.parallelStream().forEach((trail) -> {
            trail.update();
        });

        // Update all infographs
        infographs.parallelStream().forEach((infG) -> {
            infG.update();
        });

        // Update the year infographic
        yearText.update();

        // Update the events text
        eventsText.update();

        int camInterpolation = 500;

        // Camera center is always the average of our mins/maxs
        camCenterX = (camCenterX * camInterpolation + (maxX + minX) / 2) / (camInterpolation + 1);
        camCenterY = (camCenterY * camInterpolation + (maxY + minY) / 2) / (camInterpolation + 1);

        // Camera zoom is linearly correlated with the biggest span of the scene
        double dist = Math.min((maxX - minX), (maxY - minY));
        camZoom = (camZoom * camInterpolation + 1 / dist) / (camInterpolation + 1);
    }

    /**
     * Draws a single frame.
     */
    public void visualize() {
        // Clear the previous frame
        this.getChildren().clear();

        // Have every trail draw into the new frame
        // Draw the trails in reverse for correct draw order
        for (int i = trails.size() - 1; i >=0; i--) {
            Trail trail = trails.get(i);
            trail.draw(this);
        }

        // Draw the infographics
        for (Infograph infG : infographs) {
            infG.draw(this);
        }

        // Draw the year text
        yearText.draw(this);

        // Draw the events text
        eventsText.draw(this);

        double roomW = this.getWidth();
        double roomH = this.getHeight();

        // We consider room (range) when framing ourselves
        double minSceneRange = Math.min(roomW, roomH);

        // How much do we zoom out?
        double scaleBuffer = 0.8;
        double scale = camZoom * minSceneRange * scaleBuffer;
        scale = Math.min(scale, 1); // We can only zoom out from 1
        this.setScaleX(scale); this.setScaleY(scale);

        // Update mins and maxs (to be used in the camera simulation tick)
        Bounds b = this.getBoundsInLocal();
        minX = b.getMinX();
        maxX = b.getMaxX();
        minY = b.getMinY();
        maxY = b.getMaxY();

        // Finalize our placement
        double camX = camCenterX;
        double camY = camCenterY;
        this.setTranslateX(scale * -(camX - roomW / 2));
        this.setTranslateY(scale * -(camY - roomH / 2));

    }

    /**
     * Re-organizes the X targets of a set of child trails of a given parent, while considering
     * a given Y value for a logarithmic tree structure.
     * @param parent
     * @param y
     */
    public void organizeChildTrails(Trail parent, double y) {
        ArrayList<Trail> children = parent.getFollowers();
        if (children == null) { return; }
        int size = children.size();
        int frac = 0;
        double spread = spreadByY(y);
        double leftMost = parent.getTargetX() - (spread * (size - 1)) / 2;

        // Symmetry hardcoding start
        Trail leftMostTrail = null;
        Trail middleTrail = null;
        Trail rightMostTrail = null;

        Trail preferredLeftMostTrail = null;
        Trail preferredMiddleTrail = null;
        Trail preferredRightMostTrail = null;

        String leftMostName = "Law";
        String middleName = "Business";
        String rightMostName = "Computer Science";
        // Symmetry hardcoding finish

        for (Trail child : children) {

            // Symmetry hardcoding
            boolean isLeftMost = frac == 0;
            boolean isMiddle = frac == (size / 2);
            boolean isRightMost = frac == size - 1;

            if (isLeftMost && !child.name.equals(leftMostName)) {
                leftMostTrail = child;
            } else if (isMiddle && !child.name.equals(middleName)) {
                middleTrail = child;
            } else if (isRightMost && !child.name.equals(rightMostName)) {
                rightMostTrail = child;
            }

            if (child.name.equals(leftMostName)) {
                preferredLeftMostTrail = child;
            } else if (child.name.equals(middleName)) {
                preferredMiddleTrail = child;
            } else if (child.name.equals(rightMostName)) {
                preferredRightMostTrail = child;
            }
            // Symmetry hardcoding finish

            double targetX;
            if (size > 1) {
                 targetX = leftMost + (spread * frac);
            } else {
                 targetX = parent.getTargetX() + parent.getTargetX() * 0.25;
            }
            child.setTargetX(targetX);
            frac++;

            // Every child should organize his own children as well
            organizeChildTrails(child, child.getTargetY());

            // Shift infographs to account for the change
            shiftTrailsInfograph(child);
        }

        // Symmetry hardcoding start
        if (preferredLeftMostTrail != null && leftMostTrail != null) {
            tradeXTargets(preferredLeftMostTrail, leftMostTrail);
        }
        if (preferredMiddleTrail != null && middleTrail != null) {
            tradeXTargets(preferredMiddleTrail, middleTrail);
        }
        if (preferredRightMostTrail != null && rightMostTrail != null) {
            tradeXTargets(preferredRightMostTrail, rightMostTrail);
        }
        // Symmetry hardcoding finish

    }

    private void tradeXTargets(Trail A, Trail B) {
        double oldX = A.getTargetX();
        A.setTargetX(B.getTargetX());
        B.setTargetX(oldX);
        organizeChildTrails(A, A.getTargetY());
        shiftTrailsInfograph(A);
        organizeChildTrails(B, B.getTargetY());
        shiftTrailsInfograph(B);
    }

    /**
     * Shifts a trail's infograph to the right or left depending on its X position.
     * @param trail
     */
    public void shiftTrailsInfograph(Trail trail) {
        // Shift each child's respective infographs
        Infograph infG = infographs.get(trail.getID());
        if (trail.getTargetX() >= 0) {
            if (rightInfographs.size() <= leftInfographs.size()) {
                addInfographToRight(infG);
            } else {
                addInfographToLeft(infG);
            }
        } else {
            addInfographToLeft(infG);
        }
    }

    /**
     * Re-organizes the Y targets of all infographs within a given side column.
     * @param side
     */
    public void organizeInfographs(ArrayList<Infograph> side) {

        // Find out the bottomMost and upMost y values
        double bottomMost = Double.MIN_VALUE; double upMost = Double.MAX_VALUE;
        double rightMost = Double.MIN_VALUE; double leftMost = Double.MAX_VALUE;
        for (Trail trail : trails) {
            double targetY = trail.getTargetY();
            if (targetY < upMost) {
                upMost = targetY;
            }
            if (targetY > bottomMost) {
                bottomMost = targetY;
            }
            double targetX = trail.getTargetX();
            if (targetX < leftMost) {
                leftMost = targetX;
            }
            if (targetX > rightMost) {
                rightMost = targetX;
            }
        }

        int size = side.size();
        double space = (bottomMost - upMost) / (double) size;
        int it = 0;
        for (Infograph infG : side) {
            it++;
            infG.setBodyTargetY(bottomMost - it * space + INFO_Y_OFFSET);
            if (side == rightInfographs) {
                infG.setBodyTargetX(rightMost + INFO_X_SPACE);
            } else {
                infG.setBodyTargetX((leftMost - INFO_X_SPACE));
            }

        }

        // Organize the events and year text while we're at it... :D
        yearText.setX(rightMost + INFO_X_SPACE * 4);
        yearText.setY(upMost - INFO_Y_OFFSET * 2);

        eventsText.setX(rightMost + INFO_X_SPACE * 4);
        eventsText.setY(upMost);
    }

    /**
     * Places a given infograph in the right column.
     * @param infG
     */
    public void addInfographToRight(Infograph infG) {
        leftInfographs.remove(infG);
        int curr = rightInfographs.indexOf(infG);
        if (curr == -1) {
            rightInfographs.add(infG);
        }
    }

    /**
     * Places a given infograph in the left column.
     * @param infG
     */
    public void addInfographToLeft(Infograph infG) {
        rightInfographs.remove(infG);
        int curr = leftInfographs.indexOf(infG);
        if (curr == -1) {
            leftInfographs.add(infG);
        }
    }

    /**
     * Calculates the tree's X spread value given a Y value for a logarithmic tree structure.
     * @param y
     * @return
     */
    public double spreadByY(double y) {
        return 225 - y / 5;
    }

    HashMap<Integer, Color> prefFacColor;
    public Color getPrefferedColor(Integer facID) {
        if (prefFacColor == null) {
            prefFacColor = new HashMap<>();
            prefFacColor.put(1,  Color.web("e93939"));
            prefFacColor.put(2,  Color.web("f3f39e"));
            prefFacColor.put(3,  Color.web("eaa939"));
            prefFacColor.put(4,  Color.web("388be8"));
            prefFacColor.put(5,  Color.web("9c39e8"));
            prefFacColor.put(6,  Color.web("82f1d3"));
            prefFacColor.put(7,  Color.web("e238e4"));
            prefFacColor.put(8,  Color.web("ffffff"));
            prefFacColor.put(9,  Color.web("39ea7c"));
            prefFacColor.put(10, Color.web("b31f5e"));
            prefFacColor.put(11, Color.web("a5f22a"));
            prefFacColor.put(12, Color.web("b7dde2"));
            prefFacColor.put(13, Color.web("b7dde2"));
            prefFacColor.put(14, Color.web("eaeaea"));
            prefFacColor.put(15, Color.web("4838e6"));
            prefFacColor.put(16, Color.web("4de738"));
        }
        if (prefFacColor.containsKey(facID)) {
            return prefFacColor.get(facID);
        } else {
            return Color.web("ffffff");
        }
    }


    /**
     * A helper that provides a random number within a specified range.
     * @param a
     * @param b
     * @return
     */
    public static int ranRange(int a, int b){
        if (a == b) {
            return a;
        }
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        double ranBase = random();
        int diff = max - min + 1;
        int ran = (int)((ranBase * diff) + min);
        return ran;
    }

    /**
     * A helper that provides a random number within a specified range.
     * @param a
     * @param b
     * @return
     */
    public static double ranRange(double a, double b){
        if (a == b) {
            return a;
        }
        double min = Math.min(a, b);
        double max = Math.max(a, b);
        double ranBase = random();
        double diff = max - min;
        double ran = (ranBase * diff) + min;
        return ran;
    }

}
