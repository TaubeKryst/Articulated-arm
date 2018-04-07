import com.sun.j3d.utils.geometry.Sphere;
import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;

/**
 *
 * @author Krystian
 */
public class CollisionDetector extends Behavior {
    public static boolean inCollision = false;
    private WakeupOnCollisionEntry wEnter;
    private WakeupOnCollisionExit wExit;
    Sphere element;
   
    
  public CollisionDetector(Sphere object, BoundingSphere sphere) {
    inCollision = false;
    element = object;
    element.setCollisionBounds(sphere);
  }

  // Initialization method.
  public void initialize() {
    wEnter = new WakeupOnCollisionEntry(element);
    wExit = new WakeupOnCollisionExit(element);
    wakeupOn(wEnter);
  }
  public void processStimulus(Enumeration criteria) {
    
    inCollision = !inCollision;

    if (inCollision) {
        wakeupOn(wExit);  
  }
    else {
        wakeupOn(wEnter); 
    }
}
}
