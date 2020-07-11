import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import org.joml.Matrix4f;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector3d;
import org.joml.Quaternionf; 
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class IntervalGenerator {

	
	// Input: a batch of vertices, normals, indicies, a translation, rotation, and scaling for this mesh,  as well as a line from a x1,z1 to x2,z2 to generate world data
	// Output: a file containing intervals and/or ceilings/sloped walls	

	private static boolean linesIntersect;
	private static double intersectionPointX,intersectionPointY;
	static double landingPrecision = 0.0000000000001;
	private static double tX,tY,tZ, size, rotationX, rotationY, rotationZ;

int test = 0;
	
	public static void main(String[] args) throws IOException {

		

		int vCount;
		int inCount;
		int r = 0;
		boolean readingVertices,readingTextureCoords,readingNormalVecs,readingTangentVecs,readingIndicies;
		double[] stageItemV,stageItemT,stageItemN,stageItemTan;
		int[] stageItemIn;
		
		int intervalCount = 0;
		
		ArrayList<IntervalObject> terrains = new ArrayList<IntervalObject>();
		ArrayList<IntervalObject> walls = new ArrayList<IntervalObject>();
		ArrayList<IntervalObject> ceilings = new ArrayList<IntervalObject>();
		

		
		
		
		
		double x1,x2,y1,y2,z1,z2;
		
		stageItemIn = new int[100];
		stageItemV = new double[100];
		stageItemT = new double[100];
		stageItemN = new double[100];
		stageItemTan = new double[100];
		
		// Standard
		String inputGeometryFile = "C:/StageItems/IntervalGenerator_INPUT.txt";
		
		String outFileName = "C:/StageItems/WORLD_INTERVAL_DATA_";
		
		double precision = 0.0001;
		
	
		
		FileReader fileReaderStageItems = new FileReader(inputGeometryFile);
		BufferedReader bufferedReaderStageItems = new BufferedReader(fileReaderStageItems);
		String stageItemsFileLine = null;
		
		boolean readingStageItem = false, readingPositionData = false, readingScaleData = false, readingRotationXYZData = false;
		
		ArrayList<MeshItem> meshItems = new ArrayList<MeshItem>();
		int currentMeshItemIndex = -1;
		
		while ((stageItemsFileLine = bufferedReaderStageItems.readLine()) != null) {
			
			
			String[] stageItemsFileLineEqualSignSplit = stageItemsFileLine.split("=");
			ArrayList<String> stageItemFilesLineSplit = new ArrayList<String>();
			for (int u = 0; u < stageItemsFileLineEqualSignSplit.length; u++) {
				String[] spaceSplit = stageItemsFileLineEqualSignSplit[u].split("\\s+");
				for (int y = 0; y < spaceSplit.length; y++) {
					if (spaceSplit[y].compareTo("") != 0) {
						stageItemFilesLineSplit.add(spaceSplit[y]);
					}
				}
			}
			
			if (stageItemFilesLineSplit.get(0).compareTo("OBJECT") == 0) {
				currentMeshItemIndex++;
				readingStageItem = true;
				MeshItem newMeshItem = new MeshItem();
				meshItems.add(newMeshItem);
				continue;
			}
			
			
			else if (stageItemFilesLineSplit.get(0).compareTo("path") == 0) {
				String aFilePath = stageItemFilesLineSplit.get(1);
				meshItems.get(currentMeshItemIndex).setFilePath(aFilePath);
			}			
			
			
			else if (stageItemFilesLineSplit.get(0).compareTo("data") == 0) {
				
				String dataType = stageItemFilesLineSplit.get(1);

				if (dataType.compareTo("2D") == 0  &&  stageItemFilesLineSplit.size() != 4) {
					System.out.println("ERROR: 2D Data type was specified, but no targetZ was provided.");
					System.exit(0);
				}
				if (stageItemFilesLineSplit.get(2).compareTo("targetZ") != 0) {
					System.out.println("ERROR: 2D Data type was specified, but an argument other than targetZ was provided.");
					System.exit(0);
				}
				meshItems.get(currentMeshItemIndex).setDataType(dataType);
				meshItems.get(currentMeshItemIndex).setTargetZ(Double.parseDouble(stageItemFilesLineSplit.get(3)));
				
			}
			
			
			else if (stageItemFilesLineSplit.get(0).compareTo("position") == 0) {
				
				if (stageItemFilesLineSplit.size() != 4) {
					System.out.println("ERROR: position needs 3 comma-separated values");
					System.exit(0);
				}
				
				meshItems.get(currentMeshItemIndex).setWorldPosition( new double[] { Double.parseDouble(stageItemFilesLineSplit.get(1)),
						Double.parseDouble(stageItemFilesLineSplit.get(2)), Double.parseDouble(stageItemFilesLineSplit.get(3)) });
			}
			
			
			else if (stageItemFilesLineSplit.get(0).compareTo("scale") == 0) {
				meshItems.get(currentMeshItemIndex).setScale(Double.parseDouble(stageItemFilesLineSplit.get(1)));
			}
			
			
			else if (stageItemFilesLineSplit.get(0).compareTo("rotationXYZ") == 0) {
				meshItems.get(currentMeshItemIndex).setRotationXYZ( new double[] { Double.parseDouble(stageItemFilesLineSplit.get(1)),
						Double.parseDouble(stageItemFilesLineSplit.get(2)), Double.parseDouble(stageItemFilesLineSplit.get(3)) });				
			}
			
			
			else if (stageItemFilesLineSplit.get(0).compareTo("wallAngleRange") == 0) {
				meshItems.get(currentMeshItemIndex).setMinAngleForWall(Double.parseDouble(stageItemFilesLineSplit.get(1)));
				meshItems.get(currentMeshItemIndex).setMaxAngleForWall(Double.parseDouble(stageItemFilesLineSplit.get(2)));
			}	
			
					
			
		}
		System.out.println("******************************");
		System.out.println("******************************");
		for (int h = 0; h < meshItems.size(); h++) {
			double[] pos = meshItems.get(h).getWorldPosition();
			double scale = meshItems.get(h).getScale();
			double[] rotateXYZ = meshItems.get(h).getRotationXYZ();
			double minAngle = meshItems.get(h).getMinAngleForWall();
			double maxAngle = meshItems.get(h).getMaxAngleForWall();
			
			System.out.println("  Mesh item "+h);
			System.out.println("Position = "+new Vector3d(pos[0],pos[1],pos[2]));
			System.out.println("Scale = "+scale);
			System.out.println("RotationXYZ= "+new Vector3d(rotateXYZ[0],rotateXYZ[1],rotateXYZ[2]));
			System.out.println("minAngle = "+minAngle);
			System.out.println("maxAngle = "+maxAngle);
		}
		System.out.println("******************************");
		System.out.println("******************************");		
		//while((fileLine = bufferedReaderStageItems.readLine()) != null) {
		
			System.out.println("meshItems.size() = "+meshItems.size());
		//String[] fileLineTokens = fileLine.split(" = ");
			
			
	for (int p = 0; p < meshItems.size(); p++) {
		
		System.out.println("START OF FOR LOOP");
		
		String theZone = meshItems.get(p).getFilePath();
		
		tX = meshItems.get(p).getWorldPosition()[0];
		tY = meshItems.get(p).getWorldPosition()[1];
		tZ = meshItems.get(p).getWorldPosition()[2];
		
		size = meshItems.get(p).getScale();;
		rotationX = meshItems.get(p).getRotationXYZ()[0];
		rotationY = meshItems.get(p).getRotationXYZ()[1];
		rotationZ = meshItems.get(p).getRotationXYZ()[2];
		
		System.out.println("MESH ITEM "+p+" with FP: "+theZone);
		double targetZValue = meshItems.get(p).getTargetZ();
		
		double terrainMinAngle = meshItems.get(p).getMinAngleForWall();
		double terrainMaxAngle = meshItems.get(p).getMaxAngleForWall();
		terrainMinAngle = Math.toRadians(terrainMinAngle);
		terrainMaxAngle = Math.toRadians(terrainMaxAngle);
		// Generate the transformation matrix (world space)
		
		Matrix4d modelMatrix = new Matrix4d();
		Matrix4d modelWorldMatrix = new Matrix4d();
		Matrix4d normalMatrix = new Matrix4d();
		
		Matrix4d scaleMatrix = (new Matrix4d()).scaling(size);
		Matrix4d rotationXYZMatrix = (new Matrix4d()).rotationXYZ(rotationX,rotationY,rotationZ);
		Matrix4d translationMatrix = (new Matrix4d()).translation(new Vector3d(tX,tY,tZ));
		
		rotationXYZMatrix.mul(scaleMatrix,modelMatrix);
		translationMatrix.mul(modelMatrix,modelWorldMatrix);
		modelMatrix.invert(normalMatrix);
		normalMatrix.transpose();
		
	    
	    
	    
		
        FileReader fileReader = 
                new FileReader(theZone);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            try {
            	
				vCount = Integer.parseInt(bufferedReader.readLine().split("=")[1]);
				inCount = Integer.parseInt(bufferedReader.readLine().split("=")[1]);
				
				stageItemV = new double[3*vCount];
				stageItemT = new double[2*vCount];
				stageItemN = new double[3*vCount];
				stageItemTan = new double[3*vCount];
				stageItemIn = new int[inCount];
				
				String intervalLine;
				readingVertices = false;
				readingTextureCoords = false;
				readingNormalVecs = false;
				readingTangentVecs = false;
				readingIndicies = false;
				
				
				while ((intervalLine = bufferedReader.readLine()) != null) {
					
					

		            if (intervalLine.equals("Vertex Coordinates")) {
		            	
						System.out.println("Vertex Line " );

		            	
		            	r = 0;
		            	
		            	readingVertices = true;
		            	readingTextureCoords = false;
		            	readingNormalVecs = false;
		            	readingTangentVecs = false;
		            	readingIndicies = false;
		            	continue;
		            }
		            if (intervalLine.equals("Texture Coordinates")) {
		            	
		            	r = 0;
		            	
		            	readingVertices = false;
		            	readingTextureCoords = true;
		            	readingNormalVecs = false;
		            	readingTangentVecs = false;
		            	readingIndicies = false;
		            	continue;
		            }
		           if (intervalLine.equals("Smooth Normal Vectors")) {
				
		        	   r = 0;
		        	   
		        	   readingVertices = false;
		        	   readingTextureCoords = false;
		        	   readingNormalVecs = true;
		        	   readingTangentVecs = false;
		        	   readingIndicies = false;
		        	   continue;
		           }
		           if (intervalLine.equals("Smooth Tangent Vectors")) {
						
		        	   r = 0;
		        	   
		        	   readingVertices = false;
		        	   readingTextureCoords = false;
		        	   readingNormalVecs = false;
		        	   readingTangentVecs = true;
		        	   readingIndicies = false;
		        	   continue;
		           }
				if (intervalLine.equals("Indicies")) {
					
					r = 0;
					
					readingVertices = false;
					readingTextureCoords = false;
					readingNormalVecs = false;
					readingTangentVecs = false;
					readingIndicies = true;
					continue;
				}
				
				if (readingVertices) {
					
					String[] vertex = intervalLine.split(" ");
					stageItemV[r] = (Double.parseDouble(vertex[0]));
					stageItemV[r + 1] = (Double.parseDouble(vertex[1]));
					stageItemV[r + 2] =(Double.parseDouble(vertex[2]));
					r = r + 3;
					
				}
				if (readingTextureCoords) {
					
					String[] vertex = intervalLine.split(" ");
					stageItemT[r] = (Double.parseDouble(vertex[0]));
					stageItemT[r + 1] = (1.0f - Double.parseDouble(vertex[1]));
					r = r + 2;
					
				}
				if (readingNormalVecs) {
					
					String[] vertex = intervalLine.split(" ");
					stageItemN[r] = (Double.parseDouble(vertex[0]));
					stageItemN[r + 1] = (Double.parseDouble(vertex[1]));
					stageItemN[r + 2] = (Double.parseDouble(vertex[2]));
					r = r + 3;
					
				}
				if (readingTangentVecs) {
					
					String[] vertex = intervalLine.split(" ");
					stageItemTan[r] = (Double.parseDouble(vertex[0]));
					stageItemTan[r + 1] = (Double.parseDouble(vertex[1]));
					stageItemTan[r + 2] = (Double.parseDouble(vertex[2]));
					r = r + 3;
					
				}
				if (readingIndicies) {
					
					String[] vertex = intervalLine.split(" ");
					stageItemIn[r] = (Integer.parseInt(vertex[0]));
					stageItemIn[r + 1] = (Integer.parseInt(vertex[1]));
					stageItemIn[r + 2] = (Integer.parseInt(vertex[2]));
					r = r + 3;
					
				}
				            
		            
		            
				
		            
					
					
				}
            
				
            
            
            } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
            
          
            
            System.out.println("Length of stageItemIn is " + stageItemIn.length);
            
            /*
             * * * * * * * * * * * *
             * * * * * * * * * * * *
             * GENERATE WORLD DATA *
             * * * * * * * * * * * *
             * * * * * * * * * * * *
             */
            
            for (int a = 0; a < stageItemIn.length; a = a + 3) {
            	
            	
            	
            	int ind1 = stageItemIn[a];
            	int ind2 = stageItemIn[a + 1];
            	int ind3 = stageItemIn[a + 2];
            	
            	
            	double v1X = stageItemV[3*ind1];
            	double v1Y = stageItemV[3*ind1 + 1];
            	double v1Z = stageItemV[3*ind1 + 2];
            	
            	double n1X = stageItemN[3*ind1];
            	double n1Y = stageItemN[3*ind1 + 1];
            	double n1Z = stageItemN[3*ind1 + 2];
            	
            	
            //	System.out.println("B4 xforming v1, it is " + v1X + ", " + v1Y + ", " + v1Z);
            	Vector4d tV1 = modelWorldMatrix.transform(new Vector4d(v1X,v1Y,v1Z,1.0));
            	
            	//System.out.println("v1 " + v1X + " " + v1Y + " " + v1Z);
            	
            	v1X = tV1.x;
            	v1Y = tV1.y;
            	v1Z = tV1.z;
            	
           // 	System.out.println("After xforming v1, it is " + v1X + ", " + v1Y + ", " + v1Z);
            	
            	
            	double v2X = stageItemV[3*ind2];
            	double v2Y = stageItemV[3*ind2 + 1];
            	double v2Z = stageItemV[3*ind2 + 2];
            	
            	double n2X = stageItemN[3*ind2];
            	double n2Y = stageItemN[3*ind2 + 1];
            	double n2Z = stageItemN[3*ind2 + 2];
            	
            	Vector4d tV2 = modelWorldMatrix.transform(new Vector4d(v2X,v2Y,v2Z,1.0));
        
            	v2X = tV2.x;
            	v2Y = tV2.y;
            	v2Z = tV2.z;
            	
            	//System.out.println("v2 " + v2X + " " + v2Y + " " + v2Z);
            	
            	double v3X = stageItemV[3*ind3];
            	double v3Y = stageItemV[3*ind3 + 1];
            	double v3Z = stageItemV[3*ind3 + 2];
            	
            	double n3X = stageItemN[3*ind3];
            	double n3Y = stageItemN[3*ind3 + 1];
            	double n3Z = stageItemN[3*ind3 + 2];
            	
            	Vector4d tV3 = modelWorldMatrix.transform(new Vector4d(v3X,v3Y,v3Z,1.0));
            	
            	v3X = tV3.x;
            	v3Y = tV3.y;
            	v3Z = tV3.z;
            	
            	
            	//System.out.println("v3 " + v3X + " " + v3Y + " " + v3Z);
            	
            	
            	ArrayList<Double> intersectedX = new ArrayList<Double>();
            	ArrayList<Double> intersectedY = new ArrayList<Double>();
            	
            	if (lineIntersection(v1X,v1Z,v2X,v2Z,-1000.0,targetZValue,1000.0,targetZValue) == 1) {
            		//System.out.println("Potentially able to add a solution 1: " + intersectionPointX + ", " + intersectionPointY);
            		
            		if (!(isWithinArray(intersectionPointX,intersectedX) == 1) || !(isWithinArray(intersectionPointY,intersectedY) == 1)) {
            			intersectedX.add(intersectionPointX);
            			intersectedY.add(intersectionPointY);
            			
            		}
            		
            	}
            	
            	if (lineIntersection(v1X,v1Z,v3X,v3Z,-1000.0,targetZValue,1000.0,targetZValue) == 1) {
            		//System.out.println("Potentially able to add a solution 2: " + intersectionPointX + ", " + intersectionPointY);
            		
            		if (!(isWithinArray(intersectionPointX,intersectedX) == 1) || !(isWithinArray(intersectionPointY,intersectedY) == 1)) {
            			intersectedX.add(intersectionPointX);
            			intersectedY.add(intersectionPointY);
            			
            		}
            		
            	}
            	if (lineIntersection(v2X,v2Z,v3X,v3Z,-1000.0,targetZValue,1000.0,targetZValue) == 1) {
            	//	System.out.println("Potentially able to add a solution 3: " + intersectionPointX + ", " + intersectionPointY);
            		
						if (!(isWithinArray(intersectionPointX,intersectedX) == 1) || !(isWithinArray(intersectionPointY,intersectedY) == 1)) {
						intersectedX.add(intersectionPointX);
						intersectedY.add(intersectionPointY);
						
						}
	
            	}
            	
            	if (intersectedX.size() == 3) {
            		
            		System.out.println("Err");
            		System.exit(0);
            	}
            	//System.out.println("Size of intersectedX is " + intersectedX.size());
            	if (intersectedX.size() == 2) {
            		intervalCount++;
            		//System.out.println("Generating an Interval");
            //		System.out.println("X1 and X2 of this interval are " + Math.min(intersectedX.get(0), intersectedX.get(1)) + ", " + Math.max(intersectedX.get(0), intersectedX.get(1)));
          //  	System.out.println("Z1 and Z2 are " + Math.min(intersectedY.get(0), intersectedY.get(1)) + ", " + Math.max(intersectedY.get(0), intersectedY.get(1)));
            x1 = Math.min(intersectedX.get(0), intersectedX.get(1));
            x2 = Math.max(intersectedX.get(0), intersectedX.get(1));
            
            y1 = interpolateHeight(new Vector3d(v1X,v1Y,v1Z),new Vector3d(v2X,v2Y,v2Z),new Vector3d(v3X,v3Y,v3Z),x1,targetZValue);
            y2 = interpolateHeight(new Vector3d(v1X,v1Y,v1Z),new Vector3d(v2X,v2Y,v2Z),new Vector3d(v3X,v3Y,v3Z),x2,targetZValue);
         
            z1 = targetZValue;
            z2 = targetZValue;
            

            	/*
            	 *********************************************************************
            	 ** Determine whether the interval is terrain, a wall, or a ceiling **
            	 *********************************************************************
            	 */
            
            	//System.out.println("The generated interval is [" +x1+","+y1+","+z1+"] to ["+x2+","+y2+","+z2+"]");
            
            	// Construct the planar normal for this polygon
            	Vector3d planarNormal = new Vector3d();        
            	Vector3d transformedWorldPos1 = new Vector3d(tV1.x,tV1.y,tV1.z);
            	Vector3d transformedWorldPos2 = new Vector3d(tV2.x,tV2.y,tV2.z);
            	Vector3d transformedWorldPos3 = new Vector3d(tV3.x,tV3.y,tV3.z);
            	
            	Vector3d smoothedNormal1 = new Vector3d(n1X, n1Y, n1Z);
            	Vector3d smoothedNormal2 = new Vector3d(n2X, n2Y, n2Z);
            	Vector3d smoothedNormal3 = new Vector3d(n3X, n3Y, n3Z);
            	
            	Matrix3d normalMatrix3X3 = new Matrix3d();
            	normalMatrix.get3x3(normalMatrix3X3);
            	normalMatrix3X3.transform(smoothedNormal1,smoothedNormal1);
            	normalMatrix3X3.transform(smoothedNormal2,smoothedNormal2);
            	normalMatrix3X3.transform(smoothedNormal3,smoothedNormal3);
            	smoothedNormal1.normalize();
            	smoothedNormal2.normalize();
            	smoothedNormal3.normalize();
            	
            	
            	Vector3d edge1 = new Vector3d();
            	transformedWorldPos2.sub(transformedWorldPos1,edge1);
            	edge1.normalize();
            	Vector3d edge2 = new Vector3d();
            	transformedWorldPos3.sub(transformedWorldPos1,edge2);
            	edge2.normalize();
            	
            	edge1.cross(edge2,planarNormal);
            	planarNormal.normalize();
            	
            	if (planarNormal.dot(smoothedNormal1) < 0.0 || planarNormal.dot(smoothedNormal2) < 0.0 || planarNormal.dot(smoothedNormal3) < 0.0) {
            	//	planarNormal.mul(-1.0);
            	}
            	
            	Vector3d upVector = new Vector3d(0.0,1.0,0.0);
            	// Angle between up and planar normal
            	double planarNormalAngle = planarNormal.angle(upVector);

            	// Angle of trajectory entity takes on the polygon
            	double platformAngle = Math.atan2(y2 - y1, x2 - x1);
            	
            	if (platformAngle >= terrainMinAngle && platformAngle <= terrainMaxAngle) {
            		if (planarNormalAngle < Math.PI/2.0 || Math.abs(planarNormalAngle - Math.PI/2.0) <= precision) {
            			IntervalObject terrain = new IntervalObject(x1,x2,y1,y2,z1,z2,false,false,1);
            			terrains.add(terrain);
            		} else {
            			IntervalObject ceiling = new IntervalObject(x1,x2,y1,y2,z1,z2,false,false,3);
            			ceilings.add(ceiling);
            		}
            	}
            	else {
            		if (planarNormalAngle < Math.PI/2.0 || Math.abs(planarNormalAngle - Math.PI/2.0) <= precision) {
            			IntervalObject wall = new IntervalObject(x1,x2,y1,y2,z1,z2,false,false,2);
            			walls.add(wall);
            		} else {
            			IntervalObject ceiling = new IntervalObject(x1,x2,y1,y2,z1,z2,false,false,3);
            			ceilings.add(ceiling);
            		}
            	}
            	
            			
            	
            	}
            	
            }
            
            
            System.out.println("IntervalCount is " + intervalCount);
            
            System.out.println("Num terrains: " +terrains.size());
            System.out.println("Num walls: " +walls.size());
            System.out.println("Num ceilings: " +ceilings.size());
            
            
    System.out.println("END OF FOR LOOP");        
	} 
            
            
    System.out.println("AFTER FOR LOOP");        
            
            
            
            
            /*
             * * * * * * * * * * * * *
             *  WRITE OUT THE DATA * *
             * * * * * * * * * * * * *
             */
            
            
            
            
            

            File outFile = new File(outFileName + Integer.toString(1) + ".txt");
            outFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
            
            if (terrains.size() != 0) {
	            writer.write("TERRAIN");
	            writer.write(System.lineSeparator());
	            for (int s = 0; s < terrains.size(); s++) {
	            	
	            	writer.write(Double.toString(terrains.get(s).getX1()) + " " + Double.toString(terrains.get(s).getX2()) + " " + Double.toString(terrains.get(s).getY1()) + " " + Double.toString(terrains.get(s).getY2()) + " " + Double.toString(terrains.get(s).getZ1()) + " " + Double.toString(terrains.get(s).getZ2()) + " " + "false" + " " + "false" );
	            	
	            	if ((s != terrains.size() - 1) || (s == terrains.size() - 1 && walls.size() != 0) || (s == terrains.size() - 1 && ceilings.size() != 0)) {
	            		writer.write(System.lineSeparator());
	            	}
	            }
            }
            
            
            if (walls.size() != 0) {
	            writer.write("WALLS");
	            writer.write(System.lineSeparator());
	            for (int s = 0; s < walls.size(); s++) {
	            	
	            	writer.write(Double.toString(walls.get(s).getX1()) + " " + Double.toString(walls.get(s).getX2()) + " " + Double.toString(walls.get(s).getY1()) + " " + Double.toString(walls.get(s).getY2()) + " " + Double.toString(walls.get(s).getZ1()) + " " + Double.toString(walls.get(s).getZ2()) + " " + "false" + " " + "false" );

	            	if ((s != walls.size() - 1) || (s == walls.size() - 1 && ceilings.size() != 0)) {
	            		writer.write(System.lineSeparator());
	            	}
	            }
            }
            
            
            if (ceilings.size() != 0) {
	            writer.write("CEILINGS");
	            writer.write(System.lineSeparator());
	            for (int s = 0; s < ceilings.size(); s++) {
	            	
	            	writer.write(Double.toString(ceilings.get(s).getX1()) + " " + Double.toString(ceilings.get(s).getX2()) + " " + Double.toString(ceilings.get(s).getY1()) + " " + Double.toString(ceilings.get(s).getY2()) + " " + Double.toString(ceilings.get(s).getZ1()) + " " + Double.toString(ceilings.get(s).getZ2()) + " " + "false" + " " + "false" );
	            	
	            	if (s != ceilings.size() - 1) {
	            		writer.write(System.lineSeparator());
	            	}
	            }
            }
            
	  
            writer.close();
		//} // end main file loop        
            
		
	}

	static public int lineIntersection(double x1, double y1, double x2, double y2, double a1, double b1, double a2,
			double b2) {

		double boundsP = 0.0000000000001;
		//System.out.println("Line Intersection Function");
		//System.out.println("Line 1: " + x1 + ", " + y1 + " and " + x2 + ", " + y2);
		//System.out.println("Line 2: " + a1 + ", " + b1 + " and " + a2 + ", " + b2);

		double A1 = y2 - y1;
		double B1 = x1 - x2;
		double C1 = A1 * x1 + B1 * y1;

		double A2 = b2 - b1;
		double B2 = a1 - a2;
		double C2 = A2 * a1 + B2 * b1;

		double d = A1 * B2 - A2 * B1;

		if (Math.abs(d) <= 0.000000000000000000000001) {

			//System.out.println("Whyd that happen");
			// System.exit(0);
			linesIntersect = false;

			return 0;

		}

		else {

			double pointX = (B2 * C1 - B1 * C2) / d;
			double pointY = (A1 * C2 - A2 * C1) / d;

			//System.out.println("PointX is " + pointX);
			//System.out.println("PointY is " + pointY);

			if ((pointX > Math.min(x1, x2) || Math.abs(pointX - Math.min(x1, x2)) <= boundsP)
					&& (pointX < Math.max(x1, x2) || Math.abs(pointX - Math.max(x1, x2)) <= boundsP)
					&& (pointY > Math.min(y1, y2) || Math.abs(pointY - Math.min(y1, y2)) <= boundsP)
					&& (pointY < Math.max(y1, y2) || Math.abs(pointY - Math.max(y1, y2)) <= boundsP)
					&& (pointX > Math.min(a1, a2) || Math.abs(pointX - Math.min(a1, a2)) <= boundsP)
					&& (pointX < Math.max(a1, a2) || Math.abs(pointX - Math.max(a1, a2)) <= boundsP)
					&& (pointY > Math.min(b1, b2) || Math.abs(pointY - Math.min(b1, b2)) <= boundsP)
					&& (pointY < Math.max(b1, b2) || Math.abs(pointY - Math.max(b1, b2)) <= boundsP)) {

				//System.out.println("Intersection Point Found");
				linesIntersect = true;
				intersectionPointX = pointX;
				intersectionPointY = pointY;
				return 1;
			} else {

			//	System.out.println("Lines Intersect but Solution is oob");
				linesIntersect = false;

				intersectionPointX = pointX;
				intersectionPointY = pointY;
			//	System.out.println("Line 1 is " + x1 + ", "+ y1 + " to " + x2 + ", "+ y2);
			//	System.out.println("Line 2 is " + a1 + ", "+ b1 + " to " + a2 +  ", " + b2);
			//	System.out.println("The lines intsct at " + intersectionPointX + ", " + intersectionPointY);
				
				return 0;
			}
		}

	}
	
	
	static public int isWithinArray(double searchTarget,ArrayList<Double> theList) {
		
		for (int b = 0; b < theList.size(); b++) {
			
			if (Math.abs(theList.get(b) - searchTarget) <= landingPrecision) {
				return 1;
			}
		}
		
		
		return 0;
	}
	
	static public double interpolateHeight(Vector3d pA, Vector3d pB, Vector3d pC, double x, double z) {
	    // Plane equation ax+by+cz+d=0
	    double a = (pB.y - pA.y) * (pC.z - pA.z) - (pC.y - pA.y) * (pB.z - pA.z);
	    double b = (pB.z - pA.z) * (pC.x - pA.x) - (pC.z - pA.z) * (pB.x - pA.x);
	    double c = (pB.x - pA.x) * (pC.y - pA.y) - (pC.x - pA.x) * (pB.y - pA.y);
	    double d = -(a * pA.x + b * pA.y + c * pA.z);
	    // y = (-d -ax -cz) / b
	    double y = (-d - a * x - c * z) / b;
	    return y;
	}

}
