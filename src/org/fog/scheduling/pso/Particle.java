package org.fog.scheduling.pso;

import org.fog.scheduling.gaEntities.Service;

/* Particle.java
*
* Particle class used by ParticleSwarmOptimization.java
* Contains the positions of the queens in a solution as well as its conflicts, and velocity. 
* Found at http://mnemstudio.org/ai/pso/pso_tsp_java_ex1.txt
*
* @author: Tonny Tran
* @version: 1.3
*/

public class Particle {
	private int[] position;
	private double cost;
	private double time;
	private double fitness = -1;
	private int maxValue;
	private float[][] velocity; // fitness
	private Particle pBest;
	
	public Particle(int xLength, int maxValue) {
		this.position = new int[xLength];
		this.setVelocity(new float[xLength][maxValue]);
		for (int x=0; x < xLength; x++) {
			for(int y=0; y < maxValue; y++) {
				this.setVElement(x, y, (float) (PSOAlgorithm.VMAX * 2 * (Math.random()-0.5)));
			}			
		}
		this.maxValue = maxValue;
		for (int gene = 0; gene < xLength; gene++) {
			this.setGene(gene, Service.rand(0, maxValue));
		}
		this.pBest = new Particle(xLength, maxValue, 0);
		this.setpBest(this);
	}
// init with fixed value
	public Particle(int xLength, int maxValue, int value) {
		this.position = new int[xLength];
		this.setVelocity(new float[xLength][maxValue]);
		for (int x=0; x < xLength; x++) {
			for(int y=0; y < maxValue; y++) {
				this.setVElement(x, y, 0);
			}			
		}
		this.maxValue = maxValue;
		for (int gene = 0; gene < xLength; gene++) {
			this.setGene(gene, value);
		}
	}
	
	public void printGene() {
		for (int gene = 0; gene < this.getXLength(); gene++) {
			System.out.print(this.getGene(gene) + " ");
		}
	}
	
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public double getFitness() {
		return fitness;
	}
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/**
	 * Gets individual's chromosome
	 * 
	 * @return The individual's chromosome
	 */
	public int[] getChromosome() {
		return this.position;
	}

	/**
	 * Gets individual's chromosome length
	 * 
	 * @return The individual's chromosome length
	 */
	public int getXLength() {
		return this.position.length;
	}

	/**
	 * Set gene at offset
	 * 
	 * @param gene
	 * @param offset
	 * @return gene
	 */
	public void setGene(int offset, int gene) {
		this.position[offset] = gene;
	}

	/**
	 * Get gene at offset
	 * 
	 * @param offset
	 * @return gene
	 */
	public float getVElement(int x, int y) {
		return this.velocity[x][y];
	}

	public void setVElement(int x, int y, float v) {
		this.velocity[x][y] = v;
	}

	/**
	 * Get gene at offset
	 * 
	 * @param offset
	 * @return gene
	 */
	public int getGene(int offset) {
		return this.position[offset];
	}
	
	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public Particle getpBest() {
		return pBest;
	}

	public void setpBest(Particle pBest) {
		for (int gene = 0; gene < pBest.getXLength(); gene++) {
			this.pBest.setGene(gene, pBest.getGene(gene));
		}
		this.pBest.setFitness(pBest.getFitness());
	}
	//end particle

	public float[][] getVelocity() {
		return velocity;
	}

	public void setVelocity(float[][] velocity) {
		this.velocity = velocity;
	}


}