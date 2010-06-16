/*
 * Created on Oct 23, 2005 by Stefan Haustein
 */
package javax.microedition.m3g;


public class RayIntersection {

    // This is just a storage class for call-by-reference 
    // value return from pick
    // members are set directly in Node.pick();
    
	
	float[] textureS;
	float[] textureT;
	
    Node intersected;
    float distance;
    float normalX;
    float normalY;
    float normalZ = 1;
    float[] ray = new float[6]; 
    float dz = 1;
    int submeshIndex = 0;
    
	public float 	getDistance(){
	    return distance;
	}
	
	public Node 	getIntersected(){
	    return intersected;
	}
	
	public float 	getNormalX(){
	    return normalX;
	}
	
	public float getNormalY(){
        return normalY;
	}
	
	public float 	getNormalZ(){
	    return normalZ;
	}

	public void 	getRay(float[] ray){
//    Retrieves the origin (ox oy oz) and direction (dx dy dz) of the pick ray, in that order.
		System.arraycopy(this.ray, 0, ray, 0, 6);

	}

    public int 	getSubmeshIndex(){
        return submeshIndex;
    }

    public float 	getTextureS(int index){
    	return 0;
    }
    
    public float 	getTextureT(int index){
        return 0;
    }
	

}
