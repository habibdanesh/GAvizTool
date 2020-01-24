package GA_Visualizer.SymbolMapping.GeneticAlgorithm;

public class Vec3 {
	  protected double x, y, z;
	  
	  public Vec3() {}
	  
	  public Vec3(double x, double y, double z) {
	    	this.x = x;
	    	this.y = y;
	    	this.z = z;
	    }
	  
	  public Vec3(Vec3 v) { 
		  this(v.x,v.y,v.z);
		  }
	  
	  public Object clone() {
		  return new Vec3(this);
		  }
	  
	  public String getString() {
		  return x+"f,"+y+"f,"+z+"f";
	  }
	   
	  public String getKey() {
		  String key = (int)Math.round(x) +":"+ (int)Math.round(y);
			
		  if (Math.abs(z) < 1)
					key += ":0";
		  else				
					key += ":"+(int)Math.round(z);
		  return key;
	  }
	  
	  public void set(double x, double y, double z)
	  {
		  this.x = x;
		  this.y = y;
		  this.z = z;
	  }
	  
	  public double get(int i) {
		  switch (i) {
		  case 0: return x;
		  case 1: return y;
		  case 2: return z;
		  }
		  
		  return 0;
	  }
	  	  
	  public Vec3 add(Vec3 b) {
	    	return new Vec3(x+b.x, y+b.y, z+b.z);
	    }
	  
	  public Vec3 minus(Vec3 b) {
	    	return new Vec3(x-b.x, y-b.y, z-b.z);
	    }
	  
	  public double dot(Vec3 b) {
	    	return (x*b.x + y*b.y + z*b.z);
	    }
	  
	  public Vec3 sub(Vec3 b) {
	    	return new Vec3(x-b.x, y-b.y, z-b.z);
	    }
	  
	  public double mag() {
		  return Math.sqrt(x*x+y*y+z*z);
	  }
	  
	  public void normalize(){
	    	double len = mag();
			this.x = this.x / len;
			this.y = this.y / len;
			this.z = this.z / len;
	    }
	  
	  public void normalize(double m){
	    	double len = m;
			this.x = this.x / len;
			this.y = this.y / len;
			this.z = this.z / len;
	    }
	  
	  public Vec3 scale(double s) {
		  	Vec3 result = new Vec3(x*s, y*s, z*s);
	    	return result;
	    }
	  
	  public Vec3 scaleThis(double s) {
		  	x *= s; 
		  	y *= s; 
		  	z *= s;
	    	return this;
	    }

	  public double distSq2(Vec3 v)
	  {
		  double dx = x - v.x;
		  double dy = y - v.y;
		  return (dx*dx + dy*dy);
	  }

	  
	  public double distSq(Vec3 v)
	  {
		  double dx = x - v.x;
		  double dy = y - v.y;
		  double dz = z - v.z;
		  return (dx*dx + dy*dy + dz*dz);
	  }
	  
	  public static Vec3 crossprod(Vec3 v0, Vec3 v1)
	  {
		  double x = v0.y*v1.z - v0.z*v1.y;
		  double y = v0.z*v1.x - v0.x*v1.z;
		  double z = v0.x*v1.y - v0.y*v1.x;
		  
		  Vec3 norm = new Vec3(x,y,z);
		  return norm;
	  }
	  
	  public double dist(Vec3 v)
	  {
		  double dx = x - v.x;
		  double dy = y - v.y;
		  double dz = z - v.z;
		  return Math.sqrt(dx*dx + dy*dy + dz*dz);
	  }
	  
	  public double dist2(Vec3 v)
	  {
		  double dx = x - v.x;
		  double dy = y - v.y;
		  return Math.sqrt(dx*dx + dy*dy);
	  }
	  
	  public static Vec3 interp(double t, Vec3 p0, Vec3 p1)
	  {
		  Vec3 result = new Vec3(p0.x + (p1.x - p0.x)*t, 
				  				 p0.y + (p1.y - p0.y)*t,
				  				 p0.z + (p1.z - p0.z)*t);
		  return result;
	  }
}