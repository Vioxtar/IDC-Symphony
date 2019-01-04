package idc.symphony.visual;

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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.random;

public class Visualizer extends Application {

    final int CAN_WDTH = 800;
    final int CAN_HGHT = 600;

    ArrayList<Trail> trails;

    Scene trailsScene;
    Pane camera;
    double camCenterX, camCenterY, camZoom;

    public Visualizer() {}

    @Override
    public void start(Stage primaryStage) {

        // Initialization
        this.camera = new Pane(); this.camZoom = 1;
        this.trailsScene = new Scene(this.camera, CAN_WDTH, CAN_HGHT, Color.DARKGREY);
        this.trails = new ArrayList<>();


        // Take control of the primary stage
//        primaryStage.setResizable(false);
        primaryStage.setScene(trailsScene);
        primaryStage.show();

        // TODO: DEBUG
        addTrail();

        // Start the main loop timer
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                simulationTick(now); // For update calls
                stageTick(now); // For drawing calls
            }
        }.start();
    }

    long simTime = -1;
    final long simTickInterval = TimeUnit.NANOSECONDS.convert(5, TimeUnit.MILLISECONDS);
    public void simulationTick(long now) {
        if (simTime == -1) {
            simTime = now;
        }
        while(simTime < now) {
            simulate();
            simTime += simTickInterval;
        }
    }

    long stageTime = -1;
    final long stageTickInterval = TimeUnit.NANOSECONDS.convert(10, TimeUnit.MILLISECONDS);;
    public void stageTick(long now) {
        if (stageTime == -1) {
            stageTime = now;
        }
        if (stageTime <= now) {
            visualize();
            stageTime += stageTickInterval;
        }
    }

    public void simulate() {
        // TODO: This is where we do future event calls...
            /*
            for all future events, if simTime >= eventTime, then apply event, remove event from futureevents
             */

        // Update all trails
        trails.parallelStream().forEach((trail) -> {
            trail.update();
        });

        // Obtain mins/maxs of all trails
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

        // Determine the camera center to be the average of our mins/maxs
        camCenterX = (maxX + minX) / 2;
        camCenterY = (maxY + minY) / 2;

        double dist = Math.max((maxX - minX), (maxY - minY));
        camZoom = 1 / dist;

        // Simulate new joins
        if (ranRange(1, 500) == 1 && trails.size() < 16) {
            addTrail();
        }

        // Simulate note plays
        if (ranRange(1, 50) == 1) {
            Trail ran = trails.get(ranRange(0, trails.size() - 1));
            ran.setImmExp(5);
            ran.setImmRadius(50);
        }
    }

    public void visualize() {
        // Clear the previous camera
        Pane camera = new Pane();

        // Have every trail draw into the new camera
        for (Trail trail : trails) {
            trail.draw(camera);
        }

        // Set up the camera framing

        double sceneW = trailsScene.getWidth();
        double sceneH = trailsScene.getHeight();

        // Add a small buffer to the frame
        double frameBuffer = 50;
        sceneW -= frameBuffer;
        sceneH -= frameBuffer;

        // We consider our window size when determining the final zoom
        double sceneRange = Math.min(sceneW, sceneH);

        double scale = camZoom * sceneRange;
        camera.setScaleX(scale); camera.setScaleY(scale);

        // Place the camera in the scene (negated because camera positioning is reversed...)
        camera.setLayoutX(scale * -(camCenterX - sceneW / 2));
        camera.setLayoutY(scale * -(camCenterY - sceneH / 2));

        // TODO: Interpolate the camera movement... mainly good for fresh trail spawns

        // Finalize
        trailsScene.setRoot(camera);
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

    int trailID = -1;
    public void addTrail() {
        double xOrigin = CAN_WDTH / 2;
        Trail ran = null;
        if (!trails.isEmpty()) {
            xOrigin = trails.get(ranRange(0, trails.size() - 1)).targetX;
            if (ranRange(1, 2) == 1) {
                ran = trails.get(ranRange(0, trails.size() - 1));
            }
        }
        Trail newTrail = new Trail(++trailID, Color.WHITE, Color.LIGHTGRAY, xOrigin, CAN_HGHT, ran);
        newTrail.setTargetX(CAN_WDTH / 2 + ranRange(-400, 400) * 3);
        newTrail.setTargetY(ranRange(-400, 400) * 3);
        newTrail.setTargetRadius(5);
        newTrail.setTargetExp(0.25);
        newTrail.setImmRadius(100);
        trails.add(newTrail);
        // TODO: Support connecting trails by having the new tail spawn on the parent's head,
        // TODO: and every time the parent extends its trail, it pushes the child's tail with it
        // TODO: until it reaches its tail, and stays there?
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
