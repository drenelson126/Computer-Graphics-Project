package raytracer;

public class Sphere {

Vec3 center;
float radius;
Vec3 color;
Vec3 lastHitPoint;
Vec3 lastHitNormal;
public double lastHitT;

public Sphere(Vec3 center, double d, Vec3 color) {
	this.center = center;
    this.radius = (float)d;
    this.color  = color;
}

 
public boolean sphere_hit(Ray r, Sphere s, double t_min, double t_max) {
    
    Vec3 oc = r.origin.subtraction(this.center);

  
    double a = r.dir.dotProduct(r.dir);
    double b = 2.0f * oc.dotProduct(r.dir);
    double c = oc.dotProduct(oc) - radius * radius;

    
    double discriminant = b*b - 4*a*c;
    if (discriminant < 0) {
        return false; 
    }

    float sqrtDisc = (float)Math.sqrt(discriminant);
    double root = (-b - sqrtDisc) / (2*a); 
    if (root < t_min || root > t_max) {
        root = (-b + sqrtDisc) / (2*a); 
        if (root < t_min || root > t_max) {
            return false; 
        }
    }
    
    this.lastHitT = root;
    this.lastHitPoint = r.pointAt(root);
    this.lastHitNormal = this.lastHitPoint.subtraction(this.center).normalize();
  
    return true; // hit!
}

public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
 