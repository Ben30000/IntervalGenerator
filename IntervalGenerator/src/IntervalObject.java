public class IntervalObject {

	private int a = 2;
	private double x1,x2, y1,y2, z1,z2;
	private boolean leftCliff,rightCliff;
	private int type;
	
	public IntervalObject(double x1, double x2, double y1, double y2, double z1, double z2, boolean leftCliff, boolean rightCliff, int type) {
		
		this.setX1(x1);
		this.setX2(x2);

		this.setY1(y1);
		this.setY2(y2);

		this.setZ1(z1);
		this.setZ2(z2);

		this.type = type;
		
		this.leftCliff = leftCliff;
		this.rightCliff = rightCliff;
		
		
	}

	public double getX1() {
		return x1;
	}

	public void setX1(double x1) {
		this.x1 = x1;
	}

	public double getX2() {
		return x2;
	}

	public void setX2(double x2) {
		this.x2 = x2;
	}

	public double getY1() {
		return y1;
	}

	public void setY1(double y1) {
		this.y1 = y1;
	}

	public double getY2() {
		return y2;
	}

	public void setY2(double y2) {
		this.y2 = y2;
	}

	public double getZ1() {
		return z1;
	}

	public void setZ1(double z1) {
		this.z1 = z1;
	}

	public double getZ2() {
		return z2;
	}

	public void setZ2(double z2) {
		this.z2 = z2;
	}

	public boolean isLeftCliff() {
		return leftCliff;
	}

	public void setLeftCliff(boolean leftCliff) {
		this.leftCliff = leftCliff;
	}

	public boolean isRightCliff() {
		return rightCliff;
	}

	public void setRightCliff(boolean rightCliff) {
		this.rightCliff = rightCliff;
	}
	
	
	
	
}
