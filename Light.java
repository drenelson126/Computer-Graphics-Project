package raytracer;

public class Light {
    public Vec3 position;   
    public Vec3 color;       
    public boolean isDirectional; 
    public boolean enabled = true; 


    public Light(Vec3 position, Vec3 color, boolean isDirectional) {
        this.position = position;
        this.color = color;
        this.isDirectional = isDirectional;
        

    }

    
    public Vec3 getDirection(Vec3 point) {
        if (isDirectional) {
            
            return position.normalize().scalarMultiply(-1); 
        } else {
            
            return position.subtraction(point).normalize();
        }
    }
}
