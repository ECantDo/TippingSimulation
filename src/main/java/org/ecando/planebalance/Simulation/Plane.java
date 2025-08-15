package org.ecando.planebalance.Simulation;

import java.util.ArrayList;
import java.util.Arrays;

public class Plane {
	public static final double g = 9.81;
	public static final double damping = 0.8;

	private boolean updateCG;

	private int length;
	private int height;
	/**
	 * position
	 */
	private double centerOfGravity;
	private double angle;
	private double angularVelocity = 0;
	private int[] pivots;
	/**
	 * massLocations is an int[] -> [mass, position]
	 */
	private final ArrayList<int[]> massLocations;

	public Plane(int length, int height, int[] pivots) {
		this.length = length;
		this.height = height;
		this.pivots = pivots.clone();
		this.massLocations = new ArrayList<>();

		this.angle = 0;

		this.updateCG = true;
		this.getCenterOfGravity();
	}

	public Plane(int height, int[] pivots) {
		this(15, height, pivots);
	}

	public Plane() {
		this(15, 5, new int[0]);
	}

	//==================================================================================================================
	// Getters
	//==================================================================================================================

	public int getLength() {
		return this.length;
	}

	public int getHeight() {
		return this.height;
	}

	public int getPivot(int idx) {
		return this.pivots[idx];
	}

	public int[] getPivots(boolean getPointer) {
		if (getPointer)
			return this.pivots;

		return this.pivots.clone();
	}

	public int[] getPivots() {
		return this.getPivots(false);
	}

	public int[][] getMassPoints() {
		return this.massLocations.toArray(new int[0][]);
	}

	/**
	 * Gets the angle of the beam in radians. 0 is pointing towards +x, horizontal.
	 *
	 * @return The angle of the beam in radians.
	 */
	public double getAngle() {
		return this.angle;
	}

	//==================================================================================================================
	// Setters
	//==================================================================================================================
	public void setLength(int length) {
		this.length = length;
		this.updateCG = true;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setPivots(int[] pivots) {
		this.pivots = pivots.clone();
	}

	public void addPivots(int[] pivots) {
		int[] prev = this.pivots;
		this.pivots = new int[prev.length + pivots.length];

		System.arraycopy(prev, 0, this.pivots, 0, prev.length);
		System.arraycopy(pivots, 0, this.pivots, prev.length, pivots.length);
	}

	public void addPivot(int pivot) {
		this.pivots = Arrays.copyOf(this.pivots, this.pivots.length + 1);
		this.pivots[this.pivots.length - 1] = pivot;
	}

	/**
	 * Add a new mass object to the plane
	 *
	 * @param mass     The mass for the object, in Kg
	 * @param position The position along the plane for the mass to be located
	 * @return Returns True if successful addition, False otherwise
	 */
	public boolean addMassLocation(int mass, int position) {
		if (mass <= 0)
			return false;

		if (position < 0 || position >= length)
			return false;

		this.massLocations.add(new int[]{mass, position});
		this.updateCG = true;
		return true;
	}

	public void clearMassLocations() {
		this.updateCG = true;
		this.massLocations.clear();
	}

	public int[] removeMassLocation(int idx) {
		if (idx < 0 || idx >= this.massLocations.size())
			return null;

		this.updateCG = true;
		return this.massLocations.remove(idx);
	}

	public void clearPivotLocations() {
		this.pivots = new int[]{};
	}

	//==================================================================================================================
	// Simulation
	//==================================================================================================================

	/**
	 * Compute the center of gravity, provided it doesn't need recomputing
	 *
	 * @return The center of gravity -> Position
	 */
	public double getCenterOfGravity() {
		if (!this.updateCG)
			return this.centerOfGravity;

		int totalMass = this.length; // Just for the bar/beam itself
		int weightedSum = (this.length * this.length + this.length) / 2; // just for the bar/beam itself

		for (int[] m : this.massLocations) {
			totalMass += m[0];
			weightedSum += m[0] * m[1];
		}

		this.updateCG = false;
		this.centerOfGravity = (double) weightedSum / (double) totalMass;

		return this.centerOfGravity;
	}

	public int getTotalMass() {
		int mass = this.length;
		for (int[] masses : this.massLocations)
			mass += masses[0];
		return mass;

	}

	public double getTorque(int pivotIdx) {
		if (pivotIdx < 0 || pivotIdx >= this.pivots.length)
			throw new IndexOutOfBoundsException();

		return Math.cos(this.angle) * this.getTotalMass() * Plane.g *
				(this.getCenterOfGravity() - this.pivots[pivotIdx]);
	}

	/**
	 * Takes in the location of the pivot and computes the moment of inertia of the beam.
	 *
	 * @param pivotLocation
	 * @return
	 */
	public double getMomentOfInertia(int pivotLocation) {
		if (pivotLocation < 0 || pivotLocation >= this.length)
			throw new IndexOutOfBoundsException();

		// Beam mass and length
		double M = this.length; // assuming 1 kg per unit length
		double L = this.length;

		// Distance from pivot to beam center
		double d = Math.abs(pivotLocation - L / 2.0);

		// Beam moment of inertia about pivot
		double Ibeam = (1.0 / 12.0) * M * L * L + M * d * d;

		// Add discrete masses
		double I_masses = 0;
		for (int[] massPos : this.massLocations) {
			double m = massPos[0];
			double x = massPos[1];
			double r = x - pivotLocation;
			I_masses += m * r * r;
		}

		return Ibeam + I_masses;
	}

	public void simulationStep() {
		this.simulationStep(1);
	}

	/**
	 * Make a simulation step
	 *
	 * @param delta Number of ticks to simulate through; 1 tick = 1/20 seconds
	 */
	public void simulationStep(int delta) {
		// Don't do anything if there aren't any pivot points
		if (this.pivots.length == 0) {
			this.angularVelocity = 0;
			return;
		}

		int pivotMinLoc = this.length, pivotMaxLoc = 0;
		for (int p : this.pivots) {
			// Compute both incase there is 1 pivot, needs to be in the same place
			if (p < pivotMinLoc)
				pivotMinLoc = p;
			if (p > pivotMaxLoc)
				pivotMaxLoc = p;
		}

		double cg = this.getCenterOfGravity();
		// If the pivot point is right under the CG, skip, or between the min and max points
		if (cg >= pivotMinLoc && cg <= pivotMaxLoc && this.angle == 0) {
			this.angularVelocity = 0;
			return;
		}

		int activePivotLoc;
		if (this.angle > 0)
			activePivotLoc = pivotMinLoc; // left side lower
		else if (this.angle < 0)
			activePivotLoc = pivotMaxLoc; // right side lower
		else {
			// Beam is level → check which way it's rotating
			if (this.angularVelocity > 0) {
				// Tipping leftward
				activePivotLoc = pivotMinLoc;
			} else if (this.angularVelocity < 0) {
				// Tipping rightward
				activePivotLoc = pivotMaxLoc;
			} else {
				// No rotation or motion — base it on CG
				if (cg < pivotMinLoc)
					activePivotLoc = pivotMinLoc;
				else if (cg > pivotMaxLoc)
					activePivotLoc = pivotMaxLoc;
				else
					return; // Cant compute anything, return.
			}
		}

		double momentOfInertia = this.getMomentOfInertia(activePivotLoc);

		double timeStep = delta / 20.0; // At 20tps

		double torque = this.getTorque(activePivotLoc);

		double angularAcceleration = torque / momentOfInertia;
		this.angularVelocity += angularAcceleration * timeStep;
		this.angularVelocity *= damping;

		double newAngle = this.angle + this.angularVelocity * timeStep;
		// If the angle goes past the vertical stop it
		double MAX_ANGLE = 1.55334; // ≈ 89 degrees
		if (newAngle >= MAX_ANGLE) {
			this.angle = MAX_ANGLE;
			this.angularVelocity = 0;
			return;
		} else if (newAngle <= -MAX_ANGLE) {
			this.angle = -MAX_ANGLE;
			this.angularVelocity = 0;
			return;
		}

		double leftTipY = this.height - activePivotLoc * Math.sin(newAngle);
		double rightTipY = this.height + (this.length - activePivotLoc) * Math.sin(newAngle);

		if (leftTipY <= 0) {
			this.angularVelocity = 0;
			double safeRatio = Math.max(-1.0, Math.min(1.0, this.height / (double) activePivotLoc));
			this.angle = Math.asin(safeRatio);
			return;
		} else if (rightTipY <= 0) {
			this.angularVelocity = 0;
			double safeRatio = Math.max(-1.0, Math.min(1.0, -this.height / (double) (this.length - activePivotLoc)));
			this.angle = Math.asin(safeRatio);
			return;
		}

		this.angle = newAngle;
	}
}
