package raytracer;

public class Cube {
    public static Vec3 velocityFOR = new Vec3(0,0,0.1);
    public static Vec3 velocityDOWN = new Vec3(0,0,-0.1);
    public static Vec3 velocityLEFT = new Vec3(-0.1,0,0);
    public static Vec3 velocityRIGHT = new Vec3(0.1,0,0);
	Vec3 position;
    double size;
    Vec3[] Verts = new Vec3[8];
    Vec3 Color;
    Vec3 lastHitPoint;
    Vec3 lastHitNormal;
    public double lastHitT;

    public Cube(Vec3 position, double size, Vec3 Color) {
        this.position = position;
        this.size = size;
        this.Color = Color;
        GetVerts(); 
        
    }

    public Vec3[] GetVerts() {
        Verts[0] = new Vec3(position.x - size / 2, position.y + size / 2, position.z + size / 2); // top left front
        Verts[1] = new Vec3(position.x + size / 2, position.y + size / 2, position.z + size / 2); // top right front
        Verts[2] = new Vec3(position.x - size / 2, position.y - size / 2, position.z + size / 2); // bottom left front
        Verts[3] = new Vec3(position.x + size / 2, position.y - size / 2, position.z + size / 2); // bottom right front
        Verts[4] = new Vec3(position.x - size / 2, position.y + size / 2, position.z - size / 2); // top left back
        Verts[5] = new Vec3(position.x + size / 2, position.y + size / 2, position.z - size / 2); // top right back
        Verts[6] = new Vec3(position.x - size / 2, position.y - size / 2, position.z - size / 2); // bottom left back
        Verts[7] = new Vec3(position.x + size / 2, position.y - size / 2, position.z - size / 2); // bottom right back
        return Verts;
    }

    public Vec3 getMin() {
        return new Vec3(position.x - size / 2, position.y - size / 2, position.z - size / 2);
    }

    public Vec3 getMax() {
        return new Vec3(position.x + size / 2, position.y + size / 2, position.z + size / 2);
    }

    public boolean cube_hit(Ray r, double tMin, double tMax) {
        Vec3 min = getMin();
        Vec3 max = getMax();

        
        double tx1 = (min.x - r.origin.x) / r.dir.x;
        double tx2 = (max.x - r.origin.x) / r.dir.x;
        double ty1 = (min.y - r.origin.y) / r.dir.y;
        double ty2 = (max.y - r.origin.y) / r.dir.y;
        double tz1 = (min.z - r.origin.z) / r.dir.z;
        double tz2 = (max.z - r.origin.z) / r.dir.z;

        
        double t_enter = Math.max(Math.max(Math.min(tx1, tx2), Math.min(ty1, ty2)), Math.min(tz1, tz2));
        double t_exit = Math.min(Math.min(Math.max(tx1, tx2), Math.max(ty1, ty2)), Math.max(tz1, tz2));

        if (t_exit < t_enter || t_exit < tMin || t_enter > tMax) return false;

        this.lastHitT = t_enter;
        this.lastHitPoint = r.pointAt(t_enter);

   
        Vec3 normal;
        double epsilon = 1e-4;
        Vec3 p = lastHitPoint;

        if (Math.abs(p.x - min.x) < epsilon) normal = new Vec3(-1, 0, 0); // left
        else if (Math.abs(p.x - max.x) < epsilon) normal = new Vec3(1, 0, 0); // right
        else if (Math.abs(p.y - min.y) < epsilon) normal = new Vec3(0, -1, 0); // bottom
        else if (Math.abs(p.y - max.y) < epsilon) normal = new Vec3(0, 1, 0); // top
        else if (Math.abs(p.z - min.z) < epsilon) normal = new Vec3(0, 0, -1); // back
        else normal = new Vec3(0, 0, 1); // front

        this.lastHitNormal = normal;

        return true;
    }
}
