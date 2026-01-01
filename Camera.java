package raytracer;

public class Camera {
    public Vec3 center;   
    public double radius; 
    public double angle;  
    public double y;      

    public Camera(Vec3 center, double radius, double angle, double y) {
        this.center = center;
        this.radius = radius;
        this.angle = angle;
        this.y = y;
    }

    public Vec3 getPosition() {
        return new Vec3(
            center.x + radius * Math.cos(angle),
            y,
            center.z + radius * Math.sin(angle)
        );
    }

    public Vec3 getDirection() {
       
        return center.subtraction(getPosition()).normalize();
    }
}
