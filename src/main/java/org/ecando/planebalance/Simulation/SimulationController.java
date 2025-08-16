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
		plane = new Plane(planeLength, heightOffGround, new int[]{});
		planeBuilder = new BuildPlane(world, cornerPosition, plane);
		this.planeDepth = planeDepth;
	}

	//==================================================================================================================
	// Control Simulation
	//==================================================================================================================
	public void setPlaneDepth(int depth){
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

	public void setLength(int length){
		this.plane.setLength(length);
	}
}
