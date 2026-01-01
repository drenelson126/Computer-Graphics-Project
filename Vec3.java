package raytracer;

public class Vec3 {
    public double x;
    public double y;
    public double z;

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3 addition(Vec3 num){
        return new Vec3(this.x + num.x, this.y + num.y, this.z + num.z);
    }

    public Vec3 subtraction(Vec3 num){
        return new Vec3(this.x - num.x, this.y - num.y, this.z - num.z);
    }

    public Vec3 scalarMultiply(double root) {
        return new Vec3(x * root, y * root, z * root);
    }

    public double dotProduct(Vec3 num) {
        return this.x * num.x + this.y * num.y + this.z * num.z;
    }

    public Vec3 crossProduct(Vec3 num) {
        return new Vec3(
            y * num.z - z * num.y,
            z * num.x - x * num.z,
            x * num.y - y * num.x
        );
    }

    public Vec3 normalize() {
        double length = Math.sqrt(x*x + y*y + z*z);
        if (length == 0) {
            
            return new Vec3(0, 0, 0);
        }
        return new Vec3(x / length, y / length, z / length);
    }
    
    public Vec3 reflect(Vec3 normal) {
        
        return this.subtraction(normal.scalarMultiply(2 * this.dotProduct(normal)));
    }

    
    }

     

