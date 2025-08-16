package org.ecando.planebalance.Simulation;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3i;
import org.ecando.planebalance.Util.BlockBuilder;

public class SimulationController {

	private Plane plane;
	private BuildPlane planeBuilder;
	private int planeDepth;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public SimulationController(int planeLength, int planeDepth, int heightOffGround,
	                            Vec3i cornerPosition, ServerWorld world) {
		plane = new Plane(planeLength, heightOffGround);
		planeBuilder = new BuildPlane(world, cornerPosition, plane);
		this.planeDepth = planeDepth;
	}

	//==================================================================================================================
	// Control Simulation
	//==================================================================================================================
	public void setPlaneDepth(int depth) {
		this.planeDepth = depth;
	}

	public void addMass(int mass, int position) {
		this.plane.addMassLocation(mass, position);
	}

	public void addPivot(int position) {
		this.plane.addPivot(position);
	}

	public void takeSimulationStep() {
		this.plane.simulationStep();
	}

	public void takeSimulationStep(int delta) {
		this.plane.simulationStep(delta);
	}

	public void render() {
		this.planeBuilder.update(this.planeDepth);
	}

	public void clearArea() {
		this.planeBuilder.clearArea();
	}

	public void setLength(int length) {
		this.plane.setLength(length);
	}

	public void setHeight(int height) {
		this.plane.setHeight(height);
	}

	public boolean removePivot(int idx) {
		return this.plane.removePivot(idx) != -1;
	}

	public boolean removeMass(int idx) {
		return this.plane.removeMassLocation(idx) != null;
	}

	public int[] getPivots() {
		return this.plane.getPivots();
	}

	public int[][] getMasses() {
		return this.plane.getMassPoints();
	}

	public void clearMasses() {
		this.plane.clearMassLocations();
	}

	public void clearPivots() {
		this.plane.clearPivotLocations();
	}
}
