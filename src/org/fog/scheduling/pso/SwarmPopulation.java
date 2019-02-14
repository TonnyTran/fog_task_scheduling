package org.fog.scheduling.pso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SwarmPopulation {
	private List<Particle> swarmPopulation;
	private double populationFitness = -1;
	private Particle gBest;

	/**
	 * Initializes blank population of particles
	 * 
	 * @param populationSize
	 *            The number of particles in the population
	 */
	public SwarmPopulation() {
		// Initial population
		this.swarmPopulation = new ArrayList<Particle>();
	}

	/**
	 * Initializes population of particles
	 * 
	 * @param populationSize
	 *            The number of particles in the population
	 * @param xLength
	 *            The size of each particle's chromosome
	 */
	public SwarmPopulation(int populationSize, int xLength, int maxValue) {
		// Initialize the population as an array of particles
		this.swarmPopulation = new ArrayList<Particle>();

		// Create each particle in turn
		for (int particleCount = 0; particleCount < populationSize; particleCount++) {
			// Create an particle, initializing its chromosome to the give length
			Particle particle = new Particle(xLength, maxValue);
			// Add particle to population
			this.swarmPopulation.add(particle);
			this.gBest = new Particle(xLength, maxValue);
		}
	}

	public void printPopulation() {
		for (int particleCount = 0; particleCount < this.swarmPopulation.size(); particleCount++) {
			System.out.println("\nparticle " + particleCount + ": ");
			this.swarmPopulation.get(particleCount).printGene();
			System.out.print("Fitness: " + this.swarmPopulation.get(particleCount).getFitness()
					+ "  \\\\Time execution :" + this.swarmPopulation.get(particleCount).getTime()
					+ "  \\\\Total cost: " + this.swarmPopulation.get(particleCount).getCost());
		}
	}
	/**
	 * Get particles from the population
	 * 
	 * @return particles particles in population
	 */
	public List<Particle> getSwarmPopulation(){
		return this.swarmPopulation;
	}

	public void setSwarmPopulation(List<Particle> population) {
		this.swarmPopulation = population;
	}
	
	/**
	 * Find an particle in the population by its fitness
	 * 
	 * This method lets you select an particle in order of its fitness. This
	 * can be used to find the single strongest particle (eg, if you're
	 * testing for a solution), but it can also be used to find weak particles
	 * (if you're looking to cull the population) or some of the strongest
	 * particles (if you're using "elitism").
	 * 
	 * @param offset
	 *            The offset of the particle you want, sorted by fitness. 0 is
	 *            the strongest, population.length - 1 is the weakest.
	 * @return particle particle at offset
	 */
	public Particle getFittest(int offset) {
//		sortPopulation();
		// Return the fittest particle
		return this.swarmPopulation.get(offset);
	}

	public void sortPopulation() {
		// Order population by fitness
		Collections.sort(this.swarmPopulation, new Comparator<Particle>() {
			@Override
			public int compare(Particle o1, Particle o2) {
				if (o1.getFitness() > o2.getFitness()) {
					return -1;
				} else if (o1.getFitness() < o2.getFitness()) {
					return 1;
				}
				return 0;
			}
		});
	}

	/**
	 * Set population's group fitness
	 * 
	 * @param fitness
	 *            The population's total fitness
	 */
	public void setPopulationFitness(double fitness) {
		this.populationFitness = fitness;
	}

	/**
	 * Get population's group fitness
	 * 
	 * @return populationFitness The population's total fitness
	 */
	public double getPopulationFitness() {
		return this.populationFitness;
	}

	/**
	 * Set particle at offset
	 * 
	 * @param particle
	 * @param offset
	 * @return particle
	 */
	public Particle setParticle(int offset, Particle particle) {
		return swarmPopulation.set(offset, particle);
	}

	/**
	 * Get particle at offset
	 * 
	 * @param offset
	 * @return particle
	 */
	public Particle getParticle(int offset) {
		return swarmPopulation.get(offset);
	}

	public int size() {
		// TODO Auto-generated method stub
		return swarmPopulation.size();
	}

	public Particle getgBest() {
		return gBest;
	}

	public void setGBest(Particle gBest) {
		for (int gene = 0; gene < gBest.getXLength(); gene++) {
			this.gBest.setGene(gene, gBest.getGene(gene));
		}
		this.gBest.setFitness(gBest.getFitness());
	}
}
