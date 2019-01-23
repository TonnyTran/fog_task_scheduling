package org.fog.entities;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.power.PowerDatacenterBroker;
import org.fog.scheduling.SchedulingAlgorithm;
import org.fog.scheduling.gaEntities.Individual;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class FogBroker extends PowerDatacenterBroker{

        private List<FogDevice> fogDevices;

        public FogBroker(String name) throws Exception {
                super(name);
                // TODO Auto-generated constructor stub
        }

        @Override
        public void startEntity() {
                // TODO Auto-generated method stub

        }

        @Override
        public void processEvent(SimEvent ev) {
                // TODO Auto-generated method stub

        }

        @Override
        public void shutdownEntity() {
                // TODO Auto-generated method stub

        }

        public List<FogDevice> getFogDevices() {
                return fogDevices;
        }

        public void setFogDevices(List<FogDevice> fogDevices) {
                this.fogDevices = fogDevices;
        }

        public Individual assignCloudlet(String schedulingStrategy) {
                Individual individual = new Individual(cloudletList.size());
                switch (schedulingStrategy) {
                        case SchedulingAlgorithm.GA:
                                individual = SchedulingAlgorithm.runGeneticAlgorithm(fogDevices, cloudletList);
                                break;
                        case SchedulingAlgorithm.LOCAL_SEARCH:
                                individual = SchedulingAlgorithm.runLocalSearchAlgorithm(fogDevices, cloudletList);
                                break;
                        case SchedulingAlgorithm.TABU_SEARCH:
                                individual = SchedulingAlgorithm.runTabuSearchAlgorithm(fogDevices, cloudletList);
                                break;
                        case SchedulingAlgorithm.BEE:
                                individual = SchedulingAlgorithm.runBeeAlgorithm(fogDevices, cloudletList);
                                break;
                }
                return individual;
        }

        public void assignCloudletloop(String schedulingStrategy, String filename_ouput) {


                 WritableWorkbook workbook;
                // create workbook
                try {
                    workbook = Workbook.createWorkbook(new File(filename_ouput));

                    // create sheet
                    WritableSheet sheet1 = workbook.createSheet("output", 0);

                    double totalFitness = 0;
                    double totalTime = 0;
                    double totalCost = 0;

                    for(int col=0; col < 20; col++) {
                                Individual individual = new Individual(cloudletList.size());
                                individual = this.assignCloudlet(schedulingStrategy);

                                sheet1.addCell(new Label(col, 0, String.valueOf(col + 1)));
                                sheet1.addCell(new Label(col, 1, String.valueOf(individual.getFitness())));
                                sheet1.addCell(new Label(col, 2, String.valueOf(individual.getTime())));
                                sheet1.addCell(new Label(col, 3, String.valueOf(individual.getCost())));

                                totalFitness += individual.getFitness();
                                totalTime += individual.getTime();
                                totalCost += individual.getCost();
                        }

                    sheet1.addCell(new Label(20, 0, "TB"));
                        sheet1.addCell(new Label(20, 1, String.valueOf(totalFitness/20)));
                        sheet1.addCell(new Label(20, 2, String.valueOf(totalTime/20)));
                        sheet1.addCell(new Label(20, 3, String.valueOf(totalCost/20)));

                    // write file
                    workbook.write();

                    // close
                    workbook.close();
                } catch (IOException e) {
                    System.out.println("Error create file\n" + e.toString());
                } catch (RowsExceededException e) {
                    System.out.println("Error write file\n" + e.toString());
                } catch (WriteException e) {
                    System.out.println("Error write file\n" + e.toString());
                }
                System.out.println("create and write success");
        }
}
