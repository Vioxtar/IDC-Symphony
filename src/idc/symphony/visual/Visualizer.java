package idc.symphony.visual;

import idc.symphony.data.FacultyData;
import idc.symphony.visual.scheduling.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.random;

public class Visualizer extends Application {

    final int CAN_WDTH = 720;
    final int CAN_HGHT = 680;
    final Color BG_COLR = Color.BLACK;

    final double yJump = 700;


    ArrayList<Trail> trails;
    Queue<VisualEvent> schedEvents;
    HashMap<Integer, Integer> facIDtoTrails;

    Scene trailsScene;
    Pane camera;
    double camCenterX, camCenterY, camZoom;
    long simStartTime;

    public Visualizer(Queue<VisualEvent> schedEvents) {
        // Initialize schedueled events queue as soon as we're up
        // for loading prior to simulation
        this.schedEvents = schedEvents;
    }

    @Override
    public void start(Stage primaryStage) {

        // Initialization
        this.camera = new Pane();
        this.trailsScene = new Scene(this.camera, CAN_WDTH, CAN_HGHT, BG_COLR);
        this.trails = new ArrayList<>();
        this.simStartTime = System.nanoTime();
        this.facIDtoTrails = new HashMap<>();

        // Take control of the primary stage
        primaryStage.setScene(trailsScene);
        primaryStage.show();

        // Start the main loop timer
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                simulationTick(now); // For simulation update calls
                stageTick(now); // For drawing calls
            }
        }.start();
    }

    long simBefore = -1;
    final long simTickInterval = TimeUnit.NANOSECONDS.convert(5, TimeUnit.MILLISECONDS);
    public void simulationTick(long now) {
        if (simBefore == -1) {
            simBefore = now;
        }
        while(simBefore < now) {
            simulate(now);
            simBefore += simTickInterval;
        }
    }

    long stageBefore = -1;
    final long stageTickInterval = TimeUnit.NANOSECONDS.convert(16, TimeUnit.MILLISECONDS);;
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
            if (facIDtoTrails.containsKey(fD.ID)) {
                int trailID = facIDtoTrails.get(fD.ID);
                Trail trail = trails.get(trailID);
                trail.setImmRadius(100);
                trail.setChangeRadiusSpeed(1 / ((NotePlayed) event).duration);
            }

        } else if (event instanceof EventOccured) {
//            System.out.println("Event Occured");

            // TODO: Display text alligned with the corresponding trail

        } else if (event instanceof FacultyRoleChanged) {
//            System.out.println("Faculty Role Changed");

            // TODO: Change the saturation of every trail color, shift target positions

        } else if (event instanceof FacultyJoined) {
//            System.out.println("Faculty Joined");

            FacultyData fD = ((FacultyJoined) event).faculty;

            Trail trail = new Trail();
            {
                // Radius
                trail.setTargetRadius(20);
                trail.setImmRadius(0);
                trail.setChangeRadiusSpeed(5);

                // Color TODO: Make this hard coded or something, give fancier colors
                Color tempCol = Color.rgb(ranRange(0, 255), ranRange(0, 255), ranRange(0, 255));
                trail.setHeadColor(tempCol); trail.setTrailColor(tempCol); trail.setLineColor(tempCol);

                // Obtain our parent and emerge from it
                Trail parent = null;
                if (fD.parent != null && facIDtoTrails.containsKey(fD.parent.ID)) {
                    // Make sure that we have a parent
                    parent = trails.get(facIDtoTrails.get(fD.parent.ID));
                }
                if (parent != null && parent != trail) {
                    // Originate from our parent's head
                    trail.setOriginX(parent.head.x);
                    trail.setOriginY(parent.head.y);
                    trail.setHeadX(parent.head.x); trail.setHeadY(parent.head.y);
                    trail.setTailX(parent.head.x); trail.setTailY(parent.head.y);

                    // Have the trail follow its parent
                    trail.setFollowTarget(parent);

                    // Aim slightly above the parent
                    trail.setTargetY(parent.getTargetY() - yJump);

                    // Reorganize the parent's children to add room for the new child
                    organizeChildXTargets(parent, parent.getTargetY());

                } else {

                    // Set starting position
                    trail.setOriginX(0);
                    trail.setOriginY(0);
                    trail.setHeadX(0); trail.setHeadY(0);
                    trail.setTailX(0); trail.setTailY(0);

                    trail.setTargetY(-yJump);
                }
            }
            // Register the new trail
            int size = trails.size();
            trails.add(size, trail);
            facIDtoTrails.put(fD.ID, size);


        } else if (event instanceof YearChanged) {
//            System.out.println("Year Changed");

            // TODO: Do something in the background to show the year...?

        }
    }

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

        // Obtain mins/maxs of all trails for framing purposes
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

        for (Trail trail : trails) {
            double[] mins = trail.getMins();
            double mnX = mins[0], mnY = mins[1];
            if (mnX < minX) {
                minX = mnX;
            }
            if (mnY < minY) {
                minY = mnY;
            }
            double[] maxs = trail.getMaxs();
            double mxX = maxs[0], mxY = maxs[1];
            if (mxX > maxX) {
                maxX = mxX;
            }
            if (mxY > maxY) {
                maxY = mxY;
            }
        }


        // Camera center is always the average of our mins/maxs
        camCenterX = (maxX + minX) / 2;
        camCenterY = (maxY + minY) / 2;

        // Camera zoom is linearly correlated with the biggest span of the scene
        double dist = Math.max((maxX - minX), (maxY - minY));
        camZoom = 1 / dist;
    }

    public void visualize() {
        // Clear the previous camera
        Pane camera = new Pane();

        // Have every trail draw into the new camera
        int i = trails.size() - 1;
        while (i >= 0) {
            Trail trail = trails.get(i--);
            trail.draw(camera);
        }

        // Set up the camera framing

        double sceneW = trailsScene.getWidth();
        double sceneH = trailsScene.getHeight();


        // We consider our window size (scene range) when determining the final zoom
        double minSceneRange = Math.min(sceneW, sceneH);

        double scaleBuffer = 0.8;
        double scale = camZoom * minSceneRange * scaleBuffer;
        scale = Math.min(scale, 1); // We can only zoom out from 1
        camera.setScaleX(scale); camera.setScaleY(scale);

        // Place the camera in the scene (negated because camera positioning is reversed)
        double camX = camCenterX;
        double camY = camCenterY;
        camera.setLayoutX(scale * -(camX - sceneW / 2));
        camera.setLayoutY(scale * -(camY - sceneH / 2));


        // TODO: Interpolate the camera movement... mainly good for fresh trail spawns

        // Finalize
        trailsScene.setRoot(camera);
    }

    public void organizeChildXTargets(Trail parent, double y) {
        ArrayList<Trail> children = parent.getFollowers();
        if (children == null) { return; }
        int size = children.size();
        int frac = 0;
        double spread = spreadByY(y);
        double leftMost = parent.getTargetX() - (spread * (size - 1)) / 2;
        for (Trail child : children) {
            double targetX = leftMost + (spread * frac);
            child.setTargetX(targetX);
            frac++;

            organizeChildXTargets(child, child.getTargetY());
        }
    }

    public double spreadByY(double y) {
        return 250000 * (yJump / Math.pow(y, 2));
    }

    public void testDraw(Pane root) {


        Group circles = new Group();
        for (int i = 0; i < 30; i++) {
            Circle circle = new Circle(150, Color.web("white", 0.05));
            circle.setStrokeType(StrokeType.OUTSIDE);
            circle.setStroke(Color.web("white", 0.16));
            circle.setStrokeWidth(4);
            circles.getChildren().add(circle);
            circle.relocate(ranRange(0, CAN_WDTH), ranRange(0, CAN_HGHT));
        }
        Rectangle colors = new Rectangle(trailsScene.getWidth(), trailsScene.getHeight(),
                new LinearGradient(0f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE, new Stop[]{
                        new Stop(0, Color.web("#f8bd55")),
                        new Stop(Math.random(), Color.web("#c0fe56")),
                        new Stop(Math.random(), Color.web("#5dfbc1")),
                        new Stop(Math.random(), Color.web("#64c2f8")),
                        new Stop(Math.random(), Color.web("#be4af7")),
                        new Stop(Math.random(), Color.web("#ed5fc2")),
                        new Stop(Math.random(), Color.web("#ef504c")),
                        new Stop(1, Color.web("#f2660f")),}));
        colors.widthProperty().bind(trailsScene.widthProperty());
        colors.heightProperty().bind(trailsScene.heightProperty());
        Group blendModeGroup =
                new Group(new Group(circles, colors));
        colors.setBlendMode(BlendMode.OVERLAY);
        root.getChildren().add(blendModeGroup);
        circles.setEffect(new BoxBlur(ranRange(0, 100), ranRange(0, 100), ranRange(1, 1)));

    }

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
