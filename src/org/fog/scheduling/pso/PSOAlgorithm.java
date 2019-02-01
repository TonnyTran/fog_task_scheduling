package org.fog.scheduling.pso;

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.fog.entities.FogDevice;
import org.fog.scheduling.SchedulingAlgorithm;

/* ParticleSwarm.java
* @author: Tonny Tran
* @version: 1.0
*/

public class PSOAlgorithm {
	private int populationSize;

	private float w;

	private float c1;

	private float c2;

	private double minTime;
	private double minCost;
	public SwarmPopulation swarmPopulation;

	public PSOAlgorithm(int populationSize, float w, float c1, float c2) {
		this.populationSize = populationSize;
		this.w = w;
		this.c1 = c1;
		this.c2 = c2;
	}

	/**
	 * calculate the lower boundary of time and cost
	 *
	 */
	public void calcMinTimeCost(List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList) {
		this.minTime = calcMinTime(fogDevices, cloudletList);
		this.minCost = calcMinCost(fogDevices, cloudletList);
	}

	private double calcMinCost(List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList) {
		double minCost = 0;
		for (Cloudlet cloudlet : cloudletList) {
			double minCloudletCost = Double.MAX_VALUE;
			for (FogDevice fogDevice : fogDevices) {
				double cost = calcCost(cloudlet, fogDevice);
				if (minCloudletCost > cost) {
					minCloudletCost = cost;
				}
			}
			// the minCost is defined as the sum of all minCloudletCost
			minCost += minCloudletCost;
		}
		return minCost;
	}

	// the method calculates the cost (G$) when a fogDevice executes a cloudlet
	private double calcCost(Cloudlet cloudlet, FogDevice fogDevice) {
		double cost = 0;
		// cost includes the processing cost
		cost += fogDevice.getCharacteristics().getCostPerSecond() * cloudlet.getCloudletLength()
				/ fogDevice.getHost().getTotalMips();
		// cost includes the memory cost
		cost += fogDevice.getCharacteristics().getCostPerMem() * cloudlet.getMemRequired();
		// cost includes the bandwidth cost
		cost += fogDevice.getCharacteristics().getCostPerBw()
				* (cloudlet.getCloudletFileSize() + cloudlet.getCloudletOutputSize());
		return cost;
	}

	// the function calculate the lower bound of the solution about time execution
	private double calcMinTime(List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList) {
		double minTime = 0;
		double totalLength = 0;
		double totalMips = 0;
		for (Cloudlet cloudlet : cloudletList) {
			totalLength += cloudlet.getCloudletLength();
		}
		for (FogDevice fogDevice : fogDevices) {
			totalMips += fogDevice.getHost().getTotalMips();
		}
		minTime = totalLength / totalMips;
		return minTime;
	}

	/**
	 * Initialize population
	 *
	 * @param chromosomeLength The length of the particles chromosome
	 * @return population The initial population generated
	 */
	public SwarmPopulation initSwarmPopulation(int chromosomeLength, int maxValue) {
		// Initialize population
		this.swarmPopulation = new SwarmPopulation(this.populationSize, chromosomeLength, maxValue);
		return swarmPopulation;
	}

	/**
	 * Calculate fitness for an particle.
	 *
	 * In this case, the fitness score is very simple: it's the number of ones in
	 * the chromosome. Don't forget that this method, and this whole
	 * GeneticAlgorithm class, is meant to solve the problem in the "AllOnesGA"
	 * class and example. For different problems, you'll need to create a different
	 * version of this method to appropriately calculate the fitness of an particle.
	 *
	 * @param particle the particle to evaluate
	 * @return double The fitness value for particle
	 */
	public double calcFitness(Particle particle, List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList) {

		// clear the fogDevice - task list before calculate
		for (FogDevice fogDevice : fogDevices) {
			fogDevice.getCloudletListAssignment().clear();
		}

		// Loop over particle's genes to all the task assigned to the fogDevice
		for (int geneIndex = 0; geneIndex < particle.getXLength(); geneIndex++) {
			// add current cloudlet to fog device respectively
			fogDevices.get(particle.getGene(geneIndex)).getCloudletListAssignment().add(cloudletList.get(geneIndex));
		}

		// Calculate makespan and cost
		double makespan = 0;
		double execTime = 0;
		double totalCost = 0;
		for (FogDevice fogDevice : fogDevices) {
			double totalLength = 0;
			for (Cloudlet cloudlet : fogDevice.getCloudletListAssignment()) {
				totalLength += cloudlet.getCloudletLength();
				// the total cost is sum of the cost execution of each cloudlet
				totalCost += calcCost(cloudlet, fogDevice);
			}
			// execTime is the time that fogDevice finishes its list cloudlet assignment
			execTime = totalLength / fogDevice.getHostList().get(0).getTotalMips();
			// makespan is defined as when the last cloudlet finished or when all fogDevices
			// finish its work.
			if (execTime > makespan) {
				makespan = execTime;
			}
		}

		// store makespan
		particle.setTime(makespan);
		// store cost
		particle.setCost(totalCost);

		// Calculate fitness
		double fitness = SchedulingAlgorithm.TIME_WEIGHT * minTime / makespan
				+ (1 - SchedulingAlgorithm.TIME_WEIGHT) * minCost / totalCost;

		// Store fitness
		particle.setFitness(fitness);
		return fitness;
	}

	/**
	 * Evaluate the whole population
	 *
	 * Essentially, loop over the particles in the population, calculate the fitness
	 * for each, and then calculate the entire population's fitness. The
	 * population's fitness may or may not be important, but what is important here
	 * is making sure that each particle gets evaluated.
	 *
	 * @param population the population to evaluate
	 */
	public SwarmPopulation evalPopulation(List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList) {

		// calculate fitness each particle and its pBest
		for (Particle particle : this.swarmPopulation.getSwarmPopulation()) {
			this.calcFitness(particle, fogDevices, cloudletList);
			this.calcFitness(particle.getpBest(), fogDevices, cloudletList);
		}
		this.updateGBest();
		return this.swarmPopulation;
	}

	// particle move, update new position and its pBest
	public SwarmPopulation updatePosition(List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList) {
		for (Particle particle : this.swarmPopulation.getSwarmPopulation()) {
			// move and update new position
			particle = this.move(particle);
			// update pBest
			if (this.calcFitness(particle, fogDevices, cloudletList) > this.calcFitness(particle.getpBest(), fogDevices,
					cloudletList)) {
				particle.setpBest(particle);
			}
		}

		return this.swarmPopulation;
	}

	public Particle move(Particle particle) {

		// calculate v
		for (int x = 0; x < particle.getXLength(); x++) {
			for (int y = 0; y < particle.getMaxValue(); y++) {
				float vNew;
				float r1 = (float) (Math.random());
				float r2 = (float) (Math.random());
				int pDistance, gDistance;

				if (particle.getGene(x) == y && particle.getpBest().getGene(x) != y) {
					pDistance = -1;
				} else if (particle.getGene(x) != y && particle.getpBest().getGene(x) == y) {
					pDistance = 1;
				} else {
					pDistance = 0;
				}

				if (particle.getGene(x) == y && this.swarmPopulation.getgBest().getGene(x) != y) {
					gDistance = -1;
				} else if (particle.getGene(x) != y && this.swarmPopulation.getgBest().getGene(x) == y) {
					gDistance = 1;
				} else {
					gDistance = 0;
				}

				vNew = w * particle.getVElement(x, y) + r1 * c1 * pDistance + r2 * c2 * gDistance;
				particle.setVElement(x, y, vNew);
			}
		}

		// calculate new position
		for (int x = 0; x < particle.getXLength(); x++) {
			int machine = 0;
			float maxCol = particle.getVElement(x, 0);
			for (int y = 1; y < particle.getMaxValue(); y++) {
				if (maxCol < particle.getVElement(x, y)) {
					maxCol = particle.getVElement(x, y);
					machine = y;
				}
			}
			particle.setGene(x, machine);
		}
		return particle;
	}

	public Particle updateGBest() {
		Particle gBest = this.swarmPopulation.getSwarmPopulation().get(0).getpBest();
		for (Particle particle : this.swarmPopulation.getSwarmPopulation()) {
			if (particle.getpBest().getFitness() > gBest.getFitness()) {
				gBest = particle.getpBest();
			}
		}
		this.swarmPopulation.setgBest(gBest);
		return gBest;
	}
	public double getMinTime() {
		return this.minTime;
	}

	public double getMinCost() {
		return this.minCost;
	}


}