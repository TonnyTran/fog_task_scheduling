package org.fog.scheduling.rr;

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.fog.entities.FogDevice;
import org.fog.scheduling.SchedulingAlgorithm;
import org.fog.scheduling.gaEntities.Individual;

public class RRAlgorithm {

	private double minTime;
	private double minCost;
	private Individual solution;

	public RRAlgorithm(int chromosomeLength, int maxValue) {
		this.solution = new Individual(chromosomeLength, maxValue);
		
	}

	public Individual calcSolution(List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList) {
		for (int gene = 0; gene < this.solution.getChromosomeLength(); gene++) {
			this.solution.setGene(gene, gene % (this.solution.getMaxValue()+1));
		}
		this.solution.printGene();
		this.calcFitness(this.solution, fogDevices, cloudletList);
		return this.solution;
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
	 * Calculate fitness for an individual.
	 *
	 * In this case, the fitness score is very simple: it's the number of ones in
	 * the chromosome. Don't forget that this method, and this whole
	 * GeneticAlgorithm class, is meant to solve the problem in the "AllOnesGA"
	 * class and example. For different problems, you'll need to create a different
	 * version of this method to appropriately calculate the fitness of an
	 * individual.
	 *
	 * @param individual the individual to evaluate
	 * @return double The fitness value for individual
	 */
	public double calcFitness(Individual individual, List<FogDevice> fogDevices,
			List<? extends Cloudlet> cloudletList) {

		// clear the fogDevice - task list before calculate
		for (FogDevice fogDevice : fogDevices) {
			fogDevice.getCloudletListAssignment().clear();
		}

		// Loop over individual's genes to all the task assigned to the fogDevice
		for (int geneIndex = 0; geneIndex < individual.getChromosomeLength(); geneIndex++) {
			// add current cloudlet to fog device respectively
			fogDevices.get(individual.getGene(geneIndex)).getCloudletListAssignment().add(cloudletList.get(geneIndex));
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
		individual.setTime(makespan);
		// store cost
		individual.setCost(totalCost);

		// Calculate fitness
		double fitness = SchedulingAlgorithm.TIME_WEIGHT * minTime / makespan
				+ (1 - SchedulingAlgorithm.TIME_WEIGHT) * minCost / totalCost;

		// Store fitness
		individual.setFitness(fitness);
		return fitness;
	}

	public Individual getSolution() {
		return solution;
	}

	public void setSolution(Individual solution) {
		this.solution = solution;
	}

}
