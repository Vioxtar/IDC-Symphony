package idc.symphony.visual;

import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Infograph {
    TPoint aimPoint;
    TPoint body;

    public Infograph() {
        aimPoint = new TPoint();
        body = new TPoint();
    }

    int id;
    public void setID(int id) { this.id = id; }
    public int getID() { return id; }

    String titleText;
    public void setTitleText(String txt) {
        titleText = txt;
    }
    String bodyText;
    public void setBodyText(String txt) {
        bodyText = txt;
    }

    int titleSize;
    public void setTitleSize(int size) {
        titleSize = size;
    }

    int textSize;
    public void setTextSize(int size) {
        textSize = size;
    }

    Color titleColor;
    public void setTitleColor(Color col) {
        titleColor = col;
    }
    Color textColor;
    public void setTextColor(Color col) {
        textColor = col;
    }
    Color lineColor;
    public void setLineColor(Color col) {
        lineColor = col;
    }

    double lineWidth;
    public void setLineWidth(double w) {
        lineWidth = w;
    }

    double bodyTargetX;
    public void setBodyTargetX(double x) {
        bodyTargetX = x;
    }
    public double getBodyTargetX() { return bodyTargetX; }

    double bodyTargetY;
    public void setBodyTargetY(double y) {
        bodyTargetY = y;
    }
    public double getBodyTargetY() { return bodyTargetY; }

    boolean poppingIn; double popInFrac; // Pop-in Start (0) ---> Pop-in End (1)
    boolean poppingOut; double popOutFrac; // Pop-out Start (0) ---> Pop-out End (1)

    double popInSpeed;
    public void popIn(double speed) {
        poppingIn = true;
        popInSpeed = speed;
    }

    double popOutSpeed;
    public void popOut(double speed) {
        poppingOut = true;
        popOutSpeed = speed;
    }

    public void setAimTarget(TPoint tPoint) {
        aimPoint.follow(tPoint);
    }
    public TPoint getAimTarget() {
        return aimPoint.followed;
    }

    public void setX(double x) {
        body.x = x;
        aimPoint.x = x;
    }
    public void setY(double y) {
        body.y = y;
        aimPoint.y = y;
    }

    /**
     * Returns the maxs coordinates of this infograph, determined by the text size, aim and body points.
     * @return
     */
    public double[] getMaxs() {
        // We also need to account for the text sizes...
        Text titleTxt = new Text(titleText);
        titleTxt.setFont(new Font(titleSize * popInFrac));
        // Determine the body width as the width of the title
        double bodyWidth = titleTxt.getLayoutBounds().getMaxX() - titleTxt.getLayoutBounds().getMinX();
        double bodyHeight = titleTxt.getLayoutBounds().getMaxY() - titleTxt.getLayoutBounds().getMinY();
        return new double[] {
                Math.max(body.x + bodyWidth, aimPoint.x),
                Math.max(body.y + bodyHeight, aimPoint.y)
        };
    }

    /**
     * Returns the mins coordinates of this infograph, determined by the text size, aim and body points.
     * @return
     */
    public double[] getMins() {
        // We also need to account for the text sizes...
        Text titleTxt = new Text(titleText);
        titleTxt.setFont(new Font(titleSize * popInFrac));
        // Determine the body width as the width of the title
        double bodyWidth = titleTxt.getLayoutBounds().getMaxX() - titleTxt.getLayoutBounds().getMinX();
        double bodyHeight = titleTxt.getLayoutBounds().getMaxY() - titleTxt.getLayoutBounds().getMinY();
        return new double[] {
                Math.min(body.x - bodyWidth, aimPoint.x),
                Math.min(body.y - bodyHeight, aimPoint.y)
        };
    }

    /**
     * A single simulation tick.
     */
    public void update() {

        // Pop in/out
        if (poppingIn) {
            popInFrac += (1 - popInFrac) * popInSpeed;
        }
        if (poppingOut) {
            popOutFrac += (1 - popOutFrac) * popOutSpeed;
        }

        // Body movement
        body.applyVel(0.05);
        body.applySlide(0.01, 0.01);
        body.aimToX(bodyTargetX, 0.005);
        body.aimToY(bodyTargetY, 0.005);
    }

    /**
     * Draws the infograph into a given pane.
     * @param g
     */
    public void draw(Pane g) {

        // Title
        Text titleTxt = new Text(titleText);
        titleTxt.setFont(new Font(titleSize * popInFrac));

        // Determine the body width as the width of the title
        double bodyWidth = titleTxt.getLayoutBounds().getMaxX() - titleTxt.getLayoutBounds().getMinX();

        // Determine leftX/rightX positions of the body
        double leftX = body.x; double rightX = leftX + bodyWidth;
        if (Math.abs(rightX - aimPoint.x) < Math.abs(leftX - aimPoint.x)) {
            leftX = body.x - bodyWidth; rightX = body.x;
        }

        titleTxt.setX(leftX); titleTxt.setY(body.y - titleSize * 1.25 * popInFrac);
        titleTxt.setTextOrigin(VPos.TOP); titleTxt.setTextAlignment(TextAlignment.LEFT);
        titleTxt.setFill(titleColor);
        g.getChildren().add(titleTxt);


        // Body line
        Line bodyLine = new Line();
        bodyLine.setStroke(lineColor); bodyLine.setStrokeWidth(lineWidth);
        bodyLine.setStartX(leftX); bodyLine.setStartY(body.y);
        bodyLine.setEndX(rightX); bodyLine.setEndY(body.y);
        g.getChildren().add(bodyLine);


        // Body to aim point line
        Line aimLine = new Line();
        aimLine.setStroke(lineColor); aimLine.setStrokeWidth(lineWidth);
        aimLine.setStartX(body.x); aimLine.setStartY(body.y);
        // We wish to add a popping out animation to the aim line
        if (popOutFrac > 0.001) {
            double popOutExp = Math.pow(popOutFrac, 300);
            double aimX = aimPoint.x * (1 - popOutExp) + body.x * popOutExp;
            double aimY = aimPoint.y * (1 - popOutExp) + body.y * popOutExp;
            aimLine.setEndX(aimX); aimLine.setEndY(aimY);
            g.getChildren().add(aimLine);
        }
    }


}
