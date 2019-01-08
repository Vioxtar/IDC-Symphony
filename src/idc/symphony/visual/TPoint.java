package idc.symphony.visual;

import java.util.ArrayList;
import java.util.HashSet;

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