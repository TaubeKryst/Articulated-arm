
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Krystian*
 * 
 */
public class Robot  extends javax.swing.JFrame implements KeyListener, ActionListener, MouseListener   {

    private Timer timer = new Timer();
    private ViewingPlatform viewPlatform;
    private BranchGroup bg_ball;
     
    //TransformGroup
    private TransformGroup trans_floor;
    private TransformGroup trans_base;
    private TransformGroup trans_cylinder;
    private TransformGroup trans_cylinder2;
    private TransformGroup trans_joint1; 
    private TransformGroup trans_arm1;
    private TransformGroup trans_joint2;
    private TransformGroup trans_arm2, trans_arm21, trans_arm2_end, trans_arm2_end1;
    private TransformGroup trans_primitive;
    private TransformGroup trans_grip;
    private TransformGroup trans_main;
    private TransformGroup trans_ball;
    
    //Transform3d
    private Transform3D trans3d_floor;
    private Transform3D trans3d_base;
    private Transform3D trans3d_cylinder;
    private Transform3D trans3d_cylinder2, trans3d_cylinder2_rot;
    private Transform3D trans3d_joint1, trans3d_joint1_rot;
    private Transform3D trans3d_arm1, trans3d_arm1_rot;
    private Transform3D trans3d_joint2;
    private Transform3D trans3d_arm2, trans3d_arm21, trans3d_arm2_rot, trans3d_arm2_end, trans3d_arm2_end1;
    private Transform3D trans3d_ball, trans3d_ball_rot;
    private Transform3D trans3d_grip;
    
   // private Transform3D ustaw_prymityw
    public Vector3f ball_position = new Vector3f(1.2f,0.0f,0.1f);
    private final Vector3f ball_grip = new Vector3f(0.75f,0.0f,0.0f);
    
    // appearance of objects
    Appearance appear_arm;
    Appearance appear_cylinder;
    Appearance appear_base;
    Appearance appear_ball;
    Appearance appear_grip;
    Appearance appear_sky;
    
    // objects on the stage
    Cylinder cylinder, cylinder1, joint1, joint2;
    Box floor, base, arm1, arm2, arm21, arm2_end, arm2_end1;
    Sphere sky;
    // interpolators - responsible for the rotation of the robot
    private RotationInterpolator Rot_cylinder, Rot_joint1, Rot_joint2, Rot_ball;
    
    // angles of robot movements
    private float k_cylinder = 0f;
    private float k_joint1 = 0f;
    private float k_joint2 = 0f;
    private float limit1 = 3.5f;
    private float limit2 = 4f;
    private float value;
    
    // jump of the arm
    double move = 0.04;
    
    public Boolean w_learn = false;
    public Boolean grip = false;
    private int[] counting;
    public int i;
    public int last;
    
    
    
    private BranchGroup createSceneGraph() {
        
        // floor texture
        Appearance appear_floor = new Appearance();
        appear_floor.setTexture(loadTexture("img/ground.jpg"));
        appear_floor.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        
        // base texture
        Appearance appear_base = new Appearance();
        appear_base.setTexture(loadTexture("img/base.jpg"));
        appear_base.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        
        // cylinder texture 
        Appearance appear_cylinder = new Appearance();
        appear_cylinder.setTexture(loadTexture("img/cylinder.jpg"));
        appear_cylinder.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        
        // ring texture
        Appearance appear_ring = new Appearance();
        appear_ring.setTexture(loadTexture("img/arm_ring.jpg"));
        appear_ring.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        
        // ball texture
        Material m = new Material(new Color3f(0.3f, 0.9f, 0.6f),new Color3f(0.9f, 0.0f, 0.1f), new Color3f(0.3f, 0.9f, 0.6f), new Color3f(1.0f, 2.0f, 1.0f), 100.0f);
        
        appear_ball = new Appearance();
        appear_ball.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        appear_ball.setMaterial(m);
              
        
        Alpha alpha1 = new Alpha(-1, 5000);
        
        BranchGroup scena = new BranchGroup(); 
        bg_ball = new BranchGroup();
        bg_ball.setCapability(bg_ball.ALLOW_DETACH);
        bg_ball.setCapability(bg_ball.ALLOW_CHILDREN_WRITE);
        bg_ball.setCapability(bg_ball.ALLOW_CHILDREN_READ);
        bg_ball.setCapability(bg_ball.ALLOW_CHILDREN_EXTEND);

        trans_floor = new TransformGroup();
        trans_base = new TransformGroup();
        trans_cylinder = new TransformGroup();
        trans_cylinder2 = new TransformGroup();
        trans_joint1 = new TransformGroup();
        trans_arm1 = new TransformGroup();
        trans_joint2 = new TransformGroup();
        trans_arm2 = new TransformGroup();
        trans_arm21 = new TransformGroup();
        trans_arm2_end = new TransformGroup();
        trans_arm2_end1 = new TransformGroup();
        trans_primitive = new TransformGroup();
        trans_grip = new TransformGroup();
        trans_main = new TransformGroup();
        trans_ball = new TransformGroup();
         
        trans_floor.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_floor.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        trans_floor.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        trans_floor.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        
        trans_base.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_base.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        trans_base.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        trans_base.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        
        trans_cylinder.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_cylinder2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_joint1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_arm1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_joint2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_arm2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_arm21.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_arm2_end.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_arm2_end1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_primitive.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_grip.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
         
        trans_grip.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        trans_grip.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        trans_grip.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
         
        trans_main.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        trans_main.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        trans_main.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        
        trans_ball.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_ball.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        trans_ball.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        trans_ball.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
          
         BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0); 
          
        // 0.floor
        trans3d_floor = new Transform3D();
        trans3d_floor.set(new Vector3f(0.0f,-0.1f,0.0f));
        
        // help transformGroup
        TransformGroup floor_p = new TransformGroup();
        floor_p.setTransform(trans3d_floor);
        floor = new Box(10.0f, 0.0f, 10.0f,Box.GENERATE_TEXTURE_COORDS ,appear_floor);
        floor_p.addChild(floor);
        trans_floor.addChild(floor_p);
        trans_floor.addChild(trans_base);
        
        // 1.base
        trans3d_base = new Transform3D();
        trans3d_base.set(new Vector3f(0.0f,0.0f,0.0f));
        base = new Box(0.3f, 0.1f, 0.3f,Box.GENERATE_TEXTURE_COORDS, appear_base);
       
        trans_base.addChild(base);
        
        
        // 2.Cylinder - rotation
        trans3d_cylinder = new Transform3D();
        trans3d_cylinder.set(new Vector3f(0.0f,0.4f,0.0f)); 
        
        Rot_cylinder = new RotationInterpolator(alpha1,trans_cylinder,trans3d_cylinder,0,0);
        Rot_cylinder.setSchedulingBounds(bounds);
        trans_cylinder.addChild(Rot_cylinder);
            // help transformGroup
            TransformGroup p_cylinder = new TransformGroup();
            cylinder = new Cylinder(0.19f, 0.6f, Cylinder.GENERATE_TEXTURE_COORDS ,appear_cylinder); 
            p_cylinder.setTransform(trans3d_cylinder);
            p_cylinder.addChild(cylinder);
        
         trans_cylinder.addChild(p_cylinder);
         trans_base.addChild(trans_cylinder);
        
        // 3.horizontal cylinder
        trans3d_cylinder2_rot = new Transform3D();
        trans3d_cylinder2_rot.rotX(Math.PI/2);
        trans3d_cylinder2 = new Transform3D();
        trans3d_cylinder2.set(new Vector3f(0.0f,0.7f,0.0f));
        trans3d_cylinder2.mul(trans3d_cylinder2_rot);
        trans_cylinder2.setTransform(trans3d_cylinder2);
        cylinder1 = new Cylinder(0.2f, 0.6f, Cylinder.GENERATE_TEXTURE_COORDS ,appear_cylinder);
        trans_cylinder2.addChild(cylinder1);
        
         trans_cylinder.addChild(trans_cylinder2);
         
        //4.join1
        trans3d_joint1_rot = new Transform3D();
        trans3d_joint1_rot.rotY(Math.PI/4);
        trans3d_joint1 = new Transform3D();
        trans3d_joint1.set(new Vector3f(0.0f,0.3f,0.0f));
        trans3d_joint1.mul(trans3d_joint1_rot);
        Rot_joint1 = new RotationInterpolator(alpha1, trans_joint1, trans3d_joint1, 0, 0);
        Rot_joint1.setSchedulingBounds(bounds);
        trans_joint1.addChild(Rot_joint1);
            //help transformGroup
            TransformGroup p_przegub1 = new TransformGroup();
            joint1 = new Cylinder(0.1f, 0.2f, Cylinder.GENERATE_NORMALS | Cylinder.GENERATE_TEXTURE_COORDS,appear_cylinder);
            p_przegub1.setTransform(trans3d_joint1);
            p_przegub1.addChild(joint1);
            
       
        trans_joint1.addChild(p_przegub1);
         trans_cylinder2.addChild(trans_joint1);
         
         //5.arm1
        trans3d_arm1_rot = new Transform3D();
        trans3d_arm1_rot.rotY(Math.PI/4);
        trans3d_arm1 = new Transform3D();
        trans3d_arm1.set(new Vector3f(0.3f,.3f,-0.3f));
        trans3d_arm1.mul(trans3d_arm1_rot);
        trans_arm1.setTransform(trans3d_arm1);
        arm1 = new Box(0.4f,0.1f,0.1f,Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS ,appear_cylinder);
        trans_arm1.addChild(arm1);
         
         trans_joint1.addChild(trans_arm1);
         
        // 6.join2
        trans3d_joint2 = new Transform3D();
        trans3d_joint2.set(new Vector3f(0.4f,-0.05f,0.0f));
        
        Rot_joint2 = new RotationInterpolator(alpha1, trans_joint2, trans3d_joint2, 0, 0);
        Rot_joint2.setSchedulingBounds(bounds);
        trans_joint2.addChild(Rot_joint2);
            //help transformGroup
            TransformGroup p_przegub2 = new TransformGroup();
            joint2 = new Cylinder(0.1f, 0.3f, Cylinder.GENERATE_NORMALS | Cylinder.GENERATE_TEXTURE_COORDS ,appear_cylinder);
            p_przegub2.setTransform(trans3d_joint2);
            p_przegub2.addChild(joint2);
        
        trans_joint2.addChild(p_przegub2);
         trans_arm1.addChild(trans_joint2);
         
        //7.arm2
        trans3d_arm2_rot = new Transform3D();
        trans3d_arm2_rot.rotY(-Math.PI/3);
        trans3d_arm2 = new Transform3D();
        trans3d_arm2.set(new Vector3f(.37f,-0.2f,-0.05f));
        trans3d_arm2.mul(trans3d_arm2_rot);
        trans_arm2.setTransform(trans3d_arm2);
        arm2 = new Box(0.45f,0.1f,0.1f,Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS ,appear_cylinder);
        trans_arm2.addChild(arm2);
        
         trans_joint2.addChild(trans_arm2);
         
        // arm2 end
         trans3d_arm21 = new Transform3D();
         trans3d_arm21.set(new Vector3f(.0f,0.f,0.0f));
         trans_arm21.setTransform(trans3d_arm21);
         arm21 = new Box(0.5f, 0.08f, 0.08f,Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS ,appear_ring);
         trans_arm21.addChild(arm21);
        
          trans_arm2.addChild(trans_arm21);
        
        
         // arm2 ring
         trans3d_arm2_end = new Transform3D();
         trans3d_arm2_end.set(new Vector3f(-.2f,0.f,0.0f));
         trans_arm2_end.setTransform(trans3d_arm2_end);
         arm2_end = new Box(0.04f, 0.11f, 0.11f,Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS ,appear_ring);
         trans_arm2_end.addChild(arm2_end);
         
          trans_arm2.addChild(trans_arm2_end);
         
         // arm2 ring1
         trans3d_arm2_end1 = new Transform3D();
         trans3d_arm2_end1.set(new Vector3f(-.25f,0.f,0.0f));
         trans_arm2_end1.setTransform(trans3d_arm2_end1);
         arm2_end1 = new Box(0.04f, 0.105f, 0.105f,Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS ,appear_ring);
         trans_arm2_end1.addChild(arm2_end1);
         
          trans_arm2.addChild(trans_arm2_end1);
         
         //8.griping element
                         
          trans3d_grip = new Transform3D();       
          Transform3D rot = new Transform3D();     
          rot.rotZ(Math.PI/2);
          trans3d_grip.set(new Vector3f(0.45f,0.0f,0.0f));
          trans3d_grip.mul(rot);
                  
           //help transformGroup
            TransformGroup p_chwytak = new TransformGroup();            
            Cone chwytak = new Cone(0.1f,0.4f, appear_grip);
            p_chwytak.setTransform(trans3d_grip);
            p_chwytak.addChild(chwytak);
                            
           trans_grip.addChild(p_chwytak);         
           trans_arm2.addChild(trans_grip);
         
         //9.ball
         trans3d_ball = new Transform3D();
         trans3d_ball.set(ball_position);
         trans3d_ball_rot = new Transform3D();
         trans3d_ball_rot.set(new Vector3f(0.0f, 0.0f, 0.0f));
         Rot_ball = new RotationInterpolator(alpha1, trans_primitive, trans3d_ball_rot, 0, 0);
         Rot_ball.setSchedulingBounds(bounds); 
         trans_primitive.addChild(Rot_ball);
         
            //help transformGroup
            Sphere prymityw = new Sphere(0.1f, appear_ball);
            
            trans_ball.setTransform(trans3d_ball);
            trans_ball.addChild(prymityw);
         
         trans_primitive.addChild(trans_ball);
         bg_ball.addChild(trans_primitive);
         trans_main.addChild(bg_ball);
         

        // light
        Color3f light1Color = new Color3f(0.5f, 0.3f, 0.4f);
        Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        scena.addChild(light1);
         
        Color3f light2Color = new Color3f(0.5f, 0.3f, 0.4f);
        Vector3f light2Direction = new Vector3f(-4.0f, 7.0f, 12.0f);
        DirectionalLight light2 = new DirectionalLight(light2Color, light2Direction);
        light2.setInfluencingBounds(bounds);
        scena.addChild(light2);
  
         
       scena.addChild(trans_floor);
       scena.addChild(trans_main);
       
       // collision detector
        CollisionDetector detect = new CollisionDetector(prymityw, new BoundingSphere(new Point3d(), 0.065d));
        detect.setSchedulingBounds(bounds);
        scena.addChild(detect);
      
       scena.compile();
       return scena;
    }
    
     public void griping(){
                
                if( grip == false ){
                trans3d_ball.set(new Vector3f(ball_grip));     
                Rot_ball.setMaximumAngle(0);
                Rot_ball.setMinimumAngle(0);
                trans_ball.setTransform(trans3d_ball);
                
                trans_main.removeChild(bg_ball);
                trans_grip.addChild(bg_ball);
                
                }
                else{
                           
                trans3d_ball.set(ball_position);        
                Rot_ball.setMinimumAngle(k_cylinder);
                Rot_ball.setMaximumAngle(k_cylinder);
                trans_ball.setTransform(trans3d_ball);
                
                trans_grip.removeChild(bg_ball);
                trans_main.addChild(bg_ball);
                
                }
                grip = !grip;
        
    }
    private void addPanel(JPanel panel) {
       
        int x_size, y_size;
        x_size = window.getWidth();
        y_size = window.getHeight();
        panel.setSize(x_size, y_size);
        window.add(panel);
    }
    
    public Robot()    {
        
       counting = new int[1000];
        
        
        initComponents();
        
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
         Canvas3D canvas = new Canvas3D((new GraphicsConfigTemplate3D()).getBestConfiguration(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getConfigurations()));
         SimpleUniverse univ = new SimpleUniverse(canvas);
         univ.getViewingPlatform().setNominalViewingTransform();
         canvas.addKeyListener(this);
         
         BranchGroup scene = createSceneGraph();
         scene.compile();
         
         OrbitBehavior observer = new OrbitBehavior(canvas);
         BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
         observer.setSchedulingBounds(bounds);
         viewPlatform = univ.getViewingPlatform();
         Transform3D temp = new Transform3D();
         temp.set(new Vector3f(0f,0.6f,5.0f));
         viewPlatform.getViewPlatformTransform().setTransform(temp);
         viewPlatform.setViewPlatformBehavior(observer);
         
         scene.compile();
         univ.addBranchGraph(scene);
         panel.add("Center", canvas);
         addPanel(panel);
         setResizable(false);
         createBufferStrategy(2);
         
         timer.scheduleAtFixedRate(new Ruch(), 1, 1);
         
    }
    
   
    
    public void repetition() throws InterruptedException {
      
      if ( w_learn == true)
        {
     k_cylinder = 0f;
     k_joint1 = 0f;
     k_joint2 = 0f;
     
     if(grip == false)  
     {
     Rot_ball.setMaximumAngle(0);
     Rot_ball.setMinimumAngle(0);  
     }
     
     if (grip == true) 
     griping();
        
        
        int variable;
        for( int k = 0 ; k  <  i; k++)
        {
           variable = counting[k];
           
           if (variable == 1){
              k_cylinder -= move; 
              Thread.sleep(40);
            
              variable = 0;
           }
           if (variable == 2){
              k_cylinder += move; 
              Thread.sleep(40);
              variable = 0;
           }
           if (variable == 3){
             
            k_joint1 -= move; 
            Thread.sleep(40);
              variable = 0;
           }
           if (variable == 4){
              
            k_joint1 += move; 
            Thread.sleep(40);
              variable = 0;
           }
           if (variable == 5){
               
                k_joint2 -= move;
                Thread.sleep(40);
               variable = 0;
           }
           if (variable == 6){
               
                k_joint2 += move;  
                Thread.sleep(40);
                variable = 0;
           }
           if (variable == 7){     
               griping(); 
           }       
        }   
        w_learn = false;
        i = 0;              
        }       
    };
        
        

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        window = new javax.swing.JPanel();
        left = new javax.swing.JButton();
        right = new javax.swing.JButton();
        up = new javax.swing.JButton();
        down = new javax.swing.JButton();
        elbow1 = new javax.swing.JButton();
        elbow2 = new javax.swing.JButton();
        learn = new javax.swing.JButton();
        repeat = new javax.swing.JButton();
        gripp = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        window.setPreferredSize(new java.awt.Dimension(600, 1000));

        left.setText("waist -");
        left.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftActionPerformed(evt);
            }
        });

        right.setText("waist +");
        right.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightActionPerformed(evt);
            }
        });

        up.setText("shoulder +");
        up.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upActionPerformed(evt);
            }
        });

        down.setText("shoulder -");
        down.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downActionPerformed(evt);
            }
        });

        elbow1.setText("elbow -");
        elbow1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                elbow1ActionPerformed(evt);
            }
        });

        elbow2.setText("elbow +");
        elbow2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                elbow2ActionPerformed(evt);
            }
        });

        learn.setText("start learning");
        learn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                learnActionPerformed(evt);
            }
        });

        repeat.setText("repeat");
        repeat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repeatActionPerformed(evt);
            }
        });

        gripp.setText("grip");
        gripp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grippActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout windowLayout = new javax.swing.GroupLayout(window);
        window.setLayout(windowLayout);
        windowLayout.setHorizontalGroup(
            windowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(windowLayout.createSequentialGroup()
                .addGroup(windowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(windowLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(left)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(right))
                    .addGroup(windowLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(up)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(windowLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(down)
                .addGap(64, 64, 64)
                .addComponent(elbow1)
                .addGap(18, 18, 18)
                .addComponent(elbow2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 311, Short.MAX_VALUE)
                .addComponent(gripp)
                .addGap(31, 31, 31)
                .addComponent(learn)
                .addGap(33, 33, 33)
                .addComponent(repeat)
                .addGap(49, 49, 49))
        );
        windowLayout.setVerticalGroup(
            windowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, windowLayout.createSequentialGroup()
                .addGap(0, 597, Short.MAX_VALUE)
                .addComponent(up)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(windowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(right)
                    .addComponent(left))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(windowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(down)
                    .addComponent(elbow1)
                    .addComponent(elbow2)
                    .addComponent(learn)
                    .addComponent(repeat)
                    .addComponent(gripp))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(window, javax.swing.GroupLayout.PREFERRED_SIZE, 1015, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(window, javax.swing.GroupLayout.PREFERRED_SIZE, 699, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void leftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftActionPerformed
              
         if((CollisionDetector.inCollision == false || grip == true) || (CollisionDetector.inCollision == true && last == 2))
         {
           k_cylinder -= move;
              if(w_learn == true)
            {
                counting[i] = 1;
                i++;
            }
              last =  1;
         }
    }//GEN-LAST:event_leftActionPerformed

    private void rightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightActionPerformed
             if((CollisionDetector.inCollision == false || grip == true)||(CollisionDetector.inCollision == true && last == 1))
             {
             k_cylinder += move;
              if(w_learn == true)
            {
                counting[i] = 2;
                i++;
            }
              last = 2;
             }
    }//GEN-LAST:event_rightActionPerformed
             
    private void upActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upActionPerformed
              
        if(((k_joint1 < Math.PI/6 && CollisionDetector.inCollision == false)||(k_joint1 < Math.PI/6 && grip == true))||(CollisionDetector.inCollision == true && last == 3))
        {
                k_joint1 += move;
              if(w_learn == true)
            {
                counting[i] = 4;
                i++;
            }
              last = 4;
        }
    }//GEN-LAST:event_upActionPerformed

    private void downActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downActionPerformed
        if(grip == false)
            value = limit1;
        else value = limit2;
        
        
         if(((k_joint1 > -Math.PI/value && CollisionDetector.inCollision == false)||(k_joint1 > -Math.PI/value && grip == true))||(CollisionDetector.inCollision == true && last == 4))
        {
            k_joint1 -= move;
             if(w_learn == true)
            {
                counting[i] = 3;
                i++;
            }
             last = 3;
        }
    }//GEN-LAST:event_downActionPerformed

    private void elbow1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elbow1ActionPerformed
        if(((k_joint2 > -Math.PI/4 && CollisionDetector.inCollision == false)||(k_joint2 > -Math.PI/4 && grip == true))||(CollisionDetector.inCollision == true && last == 6))
        {
                k_joint2 -= move;
        if(w_learn == true)
            {
                counting[i] = 5;
                i++;
            }
        last = 5;
        }
    }//GEN-LAST:event_elbow1ActionPerformed

    private void elbow2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elbow2ActionPerformed
       if(((k_joint2 < Math.PI/12 && CollisionDetector.inCollision == false)||(k_joint2 < Math.PI/12 && grip == true))||(CollisionDetector.inCollision == true && last == 5))
       {
            k_joint2 += move;
       if(w_learn == true)
            {
                counting[i] = 6;
                i++;
            }
       last = 6;
       }
    }//GEN-LAST:event_elbow2ActionPerformed

    private void learnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_learnActionPerformed
            w_learn = true;
        
    }//GEN-LAST:event_learnActionPerformed

    private void repeatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_repeatActionPerformed
        try {
            repetition();
        } catch (InterruptedException ex) {
            Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_repeatActionPerformed

    private void grippActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grippActionPerformed
       if(CollisionDetector.inCollision == true || grip == true)
       {
        griping();
        counting[i] = 7;
        i++;
       }
    }//GEN-LAST:event_grippActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])  {
        
        
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Robot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Robot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Robot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Robot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Robot().setVisible(true);
                
            }
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {
   //     throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent e) {
       
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
             if((CollisionDetector.inCollision == false || grip == true) || (CollisionDetector.inCollision == true && last == 2))
             {
            if(w_learn == true)
            {
                counting[i] = 1;
                i++;
            }
                
            k_cylinder -= move;
            last = 1;
             }
        }
        
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
             if((CollisionDetector.inCollision == false || grip == true)||(CollisionDetector.inCollision == true && last == 1)){
            if(w_learn == true)
            {
                counting[i] = 2;
                i++;
            }
            k_cylinder += move;
            last = 2;
             }
        }
        
        
        if(e.getKeyCode() == KeyEvent.VK_DOWN){
            if(((k_joint1 > -Math.PI/value && CollisionDetector.inCollision == false)||(k_joint1 > -Math.PI/value && grip == true))||(CollisionDetector.inCollision == true && last == 4)){
            if(w_learn == true)
            {
                counting[i] = 3;
                i++;
            }
            if(grip == false)
            value = limit1;
            else value = limit2;
            
            if(k_joint1 > -Math.PI/value)
                k_joint1 -= move;
            last = 3;
            }
        }
        
        if(e.getKeyCode() == KeyEvent.VK_UP){
            
            if(((k_joint1 < Math.PI/6 && CollisionDetector.inCollision == false)||(k_joint1 < Math.PI/6 && grip == true))||(CollisionDetector.inCollision == true && last == 3)){
            
            k_joint1 += move;
            last = 4;
            
            if(w_learn == true)
            {
                counting[i] = 4;
                i++;
            }
            }
            
            
        }
        
        if(e.getKeyCode() == KeyEvent.VK_S){
            if(((k_joint2 > -Math.PI/4 && CollisionDetector.inCollision == false)||(k_joint2 > -Math.PI/4 && grip == true))||(CollisionDetector.inCollision == true && last == 6)){
            if(w_learn == true)
            {
                counting[i] = 5;
                i++;
            }
            
            if(k_joint2 > -Math.PI/4)
                k_joint2 -= move;
            last = 5;
            }
            }
        
        if(e.getKeyCode() == KeyEvent.VK_W){
            if(((k_joint2 < Math.PI/12 && CollisionDetector.inCollision == false)||(k_joint2 < Math.PI/12 && grip == true))||(CollisionDetector.inCollision == true && last == 5)){
            if(w_learn == true)
            {
                counting[i] = 6;
                i++;
            }           
            if(k_joint2 < Math.PI/12)
            k_joint2 += move;
            last = 6;
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE)
           if(CollisionDetector.inCollision == true || grip == true)
        {
        griping();
        counting[i] = 7;
        i++;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
     //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt) {
       
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Texture loadTexture(String sciezka) {
         // Załadowanie tekstury
        TextureLoader loader = new TextureLoader(sciezka, null);
        ImageComponent2D image = loader.getImage();

        if (image == null) {
          System.out.println("Nie udało się załadować tekstury");
        }

        Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
        texture.setMagFilter(Texture.NICEST);
        texture.setMinFilter(Texture.NICEST);
        texture.setImage(0, image);

        return texture;
         
    }

    private class Ruch extends TimerTask{
        
        public void run(){
            Rot_cylinder.setMinimumAngle(k_cylinder);
            Rot_cylinder.setMaximumAngle(k_cylinder);
            Rot_joint1.setMinimumAngle(k_joint1);
            Rot_joint1.setMaximumAngle(k_joint1);
            Rot_joint2.setMinimumAngle(k_joint2);
            Rot_joint2.setMaximumAngle(k_joint2);
            
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton down;
    private javax.swing.JButton elbow1;
    private javax.swing.JButton elbow2;
    private javax.swing.JButton gripp;
    private javax.swing.JButton learn;
    private javax.swing.JButton left;
    private javax.swing.JButton repeat;
    private javax.swing.JButton right;
    private javax.swing.JButton up;
    private javax.swing.JPanel window;
    // End of variables declaration//GEN-END:variables
}
