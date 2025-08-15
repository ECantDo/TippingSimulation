package org.ecando.planebalance.Simulation;

import java.util.ArrayList;
import java.util.Arrays;

public class Plane {
	private boolean updateCG;

	private int length;
	private int height;
	private int pointOfRotation;
	/**
	 * position
	 */
	private double centerOfGravity;
	private double angle;
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

		return Math.cos(this.angle) * this.getTotalMass() * 9.81 *
				(this.getCenterOfGravity() - this.pivots[pivotIdx]);
	}
}
