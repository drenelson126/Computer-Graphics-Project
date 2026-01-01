package raytracer;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Main2 {

    static int width = 600;
    static int height = 400;
    static BufferedImage image;
    static JFrame frame;

    public static void main(String[] args) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        frame = new JFrame("Interactive Phong Ray Tracer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);

        // --- Scene setup ---
        ArrayList<Sphere> spheres = new ArrayList<>();
        spheres.add(new Sphere(new Vec3(0, 0, -3), 0.5, new Vec3(0.7, 0.3, 0.3)));   // center
        spheres.add(new Sphere(new Vec3(0, -100.5, -3), 100, new Vec3(0.8, 0.8, 0.0))); // ground

        Cube cubes1 = new Cube(new Vec3(-3, 0, -6), 1.0, new Vec3(0.8, 0.3, 0.3));
        cubes1.position = new Vec3(-3, 0, -6);

        Cube cubes2 = new Cube(new Vec3(3, 0, -6), 1.0, new Vec3(0.8, 0.3, 0.3));
        cubes2.position = new Vec3(3, 0, -6);

        Cube cubes3 = new Cube(new Vec3(3, 0, 0), 1.0, new Vec3(0.8, 0.3, 0.3));
        cubes3.position = new Vec3(3, 0, 0);

        Cube cubes4 = new Cube(new Vec3(-3, 0, 0), 1.0, new Vec3(0.8, 0.3, 0.3));
        cubes4.position = new Vec3(-3, 0, 0);

        ArrayList<Pyramid> pyramids = new ArrayList<>();
        pyramids.add(new Pyramid(new Vec3(0, 0, -4), 1.0, new Vec3(0.3, 0.3, 0.8)));

        ArrayList<Light> lights = new ArrayList<>();
        lights.add(new Light(new Vec3(1, 1, 1), new Vec3(1, 1, 1), true));   
        lights.add(new Light(new Vec3(2, 1, 0), new Vec3(1, 0.8, 0.8), false)); 

        // --- Camera setup ---
        Camera camera = new Camera(new Vec3(0, 0, -3), 3.0, 0.0, 1.0); 

        // --- KeyListener for interaction ---
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_S -> camera.radius += 0.1;
                    case KeyEvent.VK_W -> camera.radius -= 0.1;
                    case KeyEvent.VK_A -> camera.angle += 0.1;
                    case KeyEvent.VK_D -> camera.angle -= 0.1;
                    case KeyEvent.VK_L -> lights.get(0).enabled = !lights.get(0).enabled;
                }
            }
        });

        // --- Animation loop ---
        boolean AnimationON = true;
        while (AnimationON) {
            cubes1.position = cubes1.position.addition(Cube.velocityRIGHT);
            if (cubes1.position.x >= 3) {
                cubes1.position = new Vec3(-3, 0, -6);
            }

            cubes2.position = cubes2.position.addition(Cube.velocityFOR);
            if (cubes2.position.z >= 0) {
                cubes2.position = new Vec3(3, 0, -6);
            }

            cubes3.position = cubes3.position.addition(Cube.velocityLEFT);
            if (cubes3.position.x <= -3) {
                cubes3.position = new Vec3(3, 0, 0);
            }

            cubes4.position = cubes4.position.addition(Cube.velocityDOWN);
            if (cubes4.position.z <= -6) {
                cubes4.position = new Vec3(-3, 0, 0);
            }

            renderScene(camera, spheres, cubes1, cubes2, cubes3, cubes4, pyramids, lights);
            frame.repaint();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // --- Render the scene with camera + lights ---
    public static void renderScene(Camera camera, ArrayList<Sphere> spheres, Cube cubes1, Cube cubes2, Cube cubes3, Cube cubes4,
                                   ArrayList<Pyramid> pyramids, ArrayList<Light> lights) {
        Vec3 camPos = camera.getPosition();
        Vec3 forward = camera.getDirection(); 
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = forward.crossProduct(up).normalize();
        Vec3 trueUp = right.crossProduct(forward).normalize();

        double viewportHeight = 2.0;
        double viewportWidth = (double) width / height * viewportHeight;

        Vec3 horizontal = right.scalarMultiply(viewportWidth);
        Vec3 vertical = trueUp.scalarMultiply(viewportHeight);
        Vec3 lowerLeftCorner = camPos
                .addition(forward)
                .subtraction(horizontal.scalarMultiply(0.5))
                .subtraction(vertical.scalarMultiply(0.5));

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double u = (double) i / (width - 1);
                double v = (double) j / (height - 1);

                Vec3 dir = lowerLeftCorner
                        .addition(horizontal.scalarMultiply(u))
                        .addition(vertical.scalarMultiply(v))
                        .subtraction(camPos)
                        .normalize();

                Ray r = new Ray(camPos, dir);
                Vec3 color = rayColor(r, spheres, lights, cubes1, cubes2, cubes3, cubes4, pyramids);

                int rC = (int) (Math.min(1.0, color.x) * 255);
                int gC = (int) (Math.min(1.0, color.y) * 255);
                int bC = (int) (Math.min(1.0, color.z) * 255);
                int rgb = (rC << 16) | (gC << 8) | bC;

                image.setRGB(i, height - j - 1, rgb);
            }
        }
    }

    // --- Phong reflection ---
    public static Vec3 rayColor(Ray r, ArrayList<Sphere> spheres, ArrayList<Light> lights, Cube cubes1, Cube cubes2, Cube cubes3, Cube cubes4,
                                ArrayList<Pyramid> pyramids) {
        double closestT = Double.POSITIVE_INFINITY;
        Vec3 hitNormal = null;
        Vec3 hitPoint = null;
        Vec3 objectColor = null;

        // Check spheres
        for (Sphere s : spheres) {
            if (s.sphere_hit(r, s, 0.001, closestT)) {
                closestT = s.lastHitT;
                hitPoint = s.lastHitPoint;
                hitNormal = s.lastHitNormal;
                objectColor = s.color;
            }
        }

        // Check cubes
        if (cubes1.cube_hit(r, 0.001, closestT)) {
            closestT = cubes1.lastHitT;
            hitPoint = cubes1.lastHitPoint;
            hitNormal = cubes1.lastHitNormal;
            objectColor = cubes1.Color;
        }

        if (cubes2.cube_hit(r, 0.001, closestT)) {
            closestT = cubes2.lastHitT;
            hitPoint = cubes2.lastHitPoint;
            hitNormal = cubes2.lastHitNormal;
            objectColor = cubes2.Color;
        }

        if (cubes3.cube_hit(r, 0.001, closestT)) {
            closestT = cubes3.lastHitT;
            hitPoint = cubes3.lastHitPoint;
            hitNormal = cubes3.lastHitNormal;
            objectColor = cubes3.Color;
        }

        if (cubes4.cube_hit(r, 0.001, closestT)) {
            closestT = cubes4.lastHitT;
            hitPoint = cubes4.lastHitPoint;
            hitNormal = cubes4.lastHitNormal;
            objectColor = cubes4.Color;
        }

        // Check pyramids
        for (Pyramid p : pyramids) {
            if (p.pyramidHit(r, 0.001, closestT)) {
                closestT = p.lastHitT;
                hitPoint = p.lastHitPoint;
                hitNormal = p.lastHitNormal;
                objectColor = p.Color;
            }
        }

        if (hitNormal == null || hitPoint == null || objectColor == null)
            return new Vec3(0.2, 0.3, 0.5); // background

        Vec3 totalColor = new Vec3(0, 0, 0);
        Vec3 viewDir = r.dir.scalarMultiply(-1).normalize();

        // Lighting calculation
        for (Light light : lights) {
            if (!light.enabled) continue;

            Vec3 lightDir = light.getDirection(hitPoint);
            Vec3 reflectDir = lightDir.scalarMultiply(-1).reflect(hitNormal);

            // Ambient
            Vec3 ambient = objectColor.scalarMultiply(0.1);

            // Diffuse
            double diff = Math.max(0.0, hitNormal.dotProduct(lightDir));
            Vec3 diffuse = objectColor.scalarMultiply(diff);

            // Specular
            double spec = Math.pow(Math.max(0.0, viewDir.dotProduct(reflectDir)), 32);
            Vec3 specular = light.color.scalarMultiply(spec);

            totalColor = totalColor.addition(ambient.addition(diffuse).addition(specular));
        }

        return totalColor;
    }
}

