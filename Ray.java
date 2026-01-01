package raytracer;

public class Ray {

	public Vec3 origin;
	public Vec3 dir;
	
	public Ray(Vec3 origin, Vec3 dir) {
		this.origin = origin;
		this.dir = dir;
	}
	 Vec3 pointAt(double root) {
		return (origin.addition(dir.scalarMultiply(root)));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
