package GA_Visualizer.SymbolMapping.GeneticAlgorithm;

public class Vec3SortByX extends Vec3 implements Comparable<Vec3SortByX> {

	public Vec3SortByX() {}
	
	public Vec3SortByX(double x, double y, double z) {
		super(x,y,z);
	}
	
	@Override
	public int compareTo(Vec3SortByX o) {
		if (this.x == o.x)
			return 0;
		return (this.x > o.x) ? 1 : -1;
	}

}
