package raytracer;

public class Pyramid {
    Vec3 position;
    double size;
    Vec3 Color;

    public Vec3 lastHitPoint;
    public Vec3 lastHitNormal;
    public double lastHitT;

    public Pyramid(Vec3 position, double size, Vec3 color) {
        this.position = position;
        this.size = size;
        this.Color = color;
    }

    private Vec3 getTop() {
        return new Vec3(position.x, position.y + size / 2, position.z);
    }

    private Vec3[] getBaseVerts() {
        return new Vec3[]{
            new Vec3(position.x + size / 2, position.y - size / 2, position.z + size / 2),
            new Vec3(position.x - size / 2, position.y - size / 2, position.z + size / 2),
            new Vec3(position.x - size / 2, position.y - size / 2, position.z - size / 2),
            new Vec3(position.x + size / 2, position.y - size / 2, position.z - size / 2)
        };
    }

    private boolean triangleHit(Vec3 v0, Vec3 v1, Vec3 v2, Ray r, double tMin, double tMax) {
        Vec3 edge1 = v1.subtraction(v0);
        Vec3 edge2 = v2.subtraction(v0);
        Vec3 normal = edge1.crossProduct(edge2).normalize();

        double denom = normal.dotProduct(r.dir);
        if (Math.abs(denom) < 1e-6) return false;

        double t = v0.subtraction(r.origin).dotProduct(normal) / denom;
        if (t < tMin || t > tMax) return false;

        Vec3 P = r.pointAt(t);

        Vec3 c0 = (v1.subtraction(v0)).crossProduct(P.subtraction(v0));
        Vec3 c1 = (v2.subtraction(v1)).crossProduct(P.subtraction(v1));
        Vec3 c2 = (v0.subtraction(v2)).crossProduct(P.subtraction(v2));

        if (c0.dotProduct(normal) >= 0 && c1.dotProduct(normal) >= 0 && c2.dotProduct(normal) >= 0) {
            lastHitT = t;
            lastHitPoint = P;
            lastHitNormal = normal;
            return true;
        }
        return false;
    }

    public boolean pyramidHit(Ray r, double tMin, double tMax) {
        Vec3 top = getTop();
        Vec3[] base = getBaseVerts();
        boolean hit = false;
        double closestT = Double.POSITIVE_INFINITY;
        Vec3 bestNormal = null;

        Vec3[][] faces = {
            {top, base[0], base[1]}, // front
            {top, base[1], base[2]}, // left
            {top, base[2], base[3]}, // back
            {top, base[3], base[0]}, // right
            {base[0], base[1], base[2]}, // base triangle 1
            {base[0], base[2], base[3]}  // base triangle 2
        };

        for (Vec3[] tri : faces) {
            if (triangleHit(tri[0], tri[1], tri[2], r, tMin, tMax)) {
                if (lastHitT < closestT) {
                    closestT = lastHitT;
                    bestNormal = lastHitNormal;
                    hit = true;
                }
            }
        }

        if (hit) {
            lastHitT = closestT;
            lastHitNormal = bestNormal;
            return true;
        }
        return false;
    }
}
