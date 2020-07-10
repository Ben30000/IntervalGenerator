
public class MeshItem {

	private String filePath;
	private double[] worldPosition;
	private double scale;
	private double[] rotationXYZ;
	private String dataType;
	private double targetZ;
	private double minAngleForWall, maxAngleForWall;
	
	public MeshItem(String filePath, double[] worldPosition, double scale, double[] rotationXYZ, String dataType, double targetZ, double minAngleForWall, double maxAngleForWall) {
		
		this.setFilePath(filePath);
		this.setWorldPosition(worldPosition);
		this.setScale(scale);
		this.setRotationXYZ(rotationXYZ);
		this.setDataType(dataType);
		this.setTargetZ(targetZ);
		this.setMinAngleForWall(minAngleForWall);
		this.setMaxAngleForWall(maxAngleForWall);
		
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public double[] getWorldPosition() {
		return worldPosition;
	}

	public void setWorldPosition(double[] worldPosition) {
		this.worldPosition = worldPosition;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public double[] getRotationXYZ() {
		return rotationXYZ;
	}

	public void setRotationXYZ(double[] rotationXYZ) {
		this.rotationXYZ = rotationXYZ;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public double getTargetZ() {
		return targetZ;
	}

	public void setTargetZ(double targetZ) {
		this.targetZ = targetZ;
	}

	public double getMinAngleForWall() {
		return minAngleForWall;
	}

	public void setMinAngleForWall(double minAngleForWall) {
		this.minAngleForWall = minAngleForWall;
	}

	public double getMaxAngleForWall() {
		return maxAngleForWall;
	}

	public void setMaxAngleForWall(double maxAngleForWall) {
		this.maxAngleForWall = maxAngleForWall;
	}
}
