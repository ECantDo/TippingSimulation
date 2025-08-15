package org.ecando.planebalance.Simulation;

import org.ecando.planebalance.Util;

import java.util.ArrayList;
import java.util.Arrays;

public class Plane {
	private int length;
	private int height;
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

	//==================================================================================================================
	// Setters
	//==================================================================================================================
	public void setLength(int length) {
		this.length = length;
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

		return true;
	}

	public void clearMassLocations() {
		this.massLocations.clear();
	}

	public int[] removeMassLocation(int idx) {
		return this.massLocations.remove(idx);
	}

}
