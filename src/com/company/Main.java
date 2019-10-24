package com.company;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static ArrayList<ArrayList> dataset;
    private static boolean loop;
    private static double time;
    private static int iterationCount;
    private static int label;

    public static void main(String[] args) {
	// write your code here
        dataset = new ArrayList<>();
        System.out.println("Modified K-Means");
        System.out.println("Enter the path for the data set (.csv file only):");
        Scanner input = new Scanner(System.in);
        try {
            var path = Paths.get(input.nextLine());
            System.out.println("Fetching data, please wait ...");
            //Fetch data set.
            var start = System.currentTimeMillis();
            Files.lines(path).skip(1).forEach(Main::setDataset);
            System.out.println(String.format("%d Records found!",dataset.size()));
            //Apply Modified K-Means to data set.
            System.out.println("Applying Modified K-Means to data set.");
            MKMeans mkMeans = new MKMeans(dataset);
            mkMeans.setclusterSize(3);
            //Compute Initial centroid
            System.out.println("Computing Initial centroid");
            mkMeans.setCentroids(initialCentroid(mkMeans.getclusterSize()));
            Thread thread = new Thread(()->{
                loop = true;
                ArrayList<List> oldCentoids = new ArrayList<>();
                mkMeans.getCentroids().forEach(centroid ->{
                    oldCentoids.add(centroid.subList(3,centroid.size()));
                });
                System.out.println("\t Clustering ");
                System.out.println("----------------------------------");
                iterationCount = 0;
                while (loop){
                    iterationCount++;
                    mkMeans.computeClusters();
                    System.out.println("Most - Relevant: "+String.format("%.2f",
                            ((float)mkMeans.getClusters().get(0).size()/(float) dataset.size())*100
                            )+"% | "+
                            "Relevant: "+String.format("%.2f",
                            ((float)mkMeans.getClusters().get(1).size()/(float) dataset.size())*100
                            )+"% | "+
                            "Irrelevant: "+String.format("%.2f",
                            ((float)mkMeans.getClusters().get(2).size()/(float) dataset.size())*100
                    )+"%");
                    ArrayList<List> newCentroids = new ArrayList<>();
                    var centroids = mkMeans.newCentroids();
                    centroids.forEach(centroid ->{
                        newCentroids.add(centroid.subList(3, centroid.size()));
                    });
                    if(oldCentoids.equals(newCentroids)){
                        loop = false;
                    }
                    oldCentoids.clear();
                    oldCentoids.addAll(newCentroids);
                    mkMeans.setCentroids(centroids);
                }
                time = ((double)System.currentTimeMillis() - (double) start)/1000;
                System.out.println("Time: "+String.format("%.2f seconds",time));
                System.out.println("Total iteration: "+iterationCount);
                //Generate Output
                Random ran = new Random();
                var file = new File("output"+ran.nextInt()+".txt");
                try {
                    BufferedWriter output = new BufferedWriter(new FileWriter(file));
                    System.out.println("\n Generating report.... please wait.");
                    label = 0;
                    output.write("Auto-Generated Report (Modified K-Means Algorithm with three clusters)");
                    output.newLine();
                    output.append("Total Records: "+dataset.size());
                    output.newLine();
                    output.append("Total Clusters: "+mkMeans.getClusters().size());
                    output.newLine();
                    output.append("Most-Relevant: "+mkMeans.getClusters().get(0).size()+
                            "("+String.format("%.2f",
                            ((float)mkMeans.getClusters().get(0).size()/(float) dataset.size())*100
                    )+"%)");
                    output.newLine();
                    output.append("Relevant: "+mkMeans.getClusters().get(1).size()+
                            "("+String.format("%.2f",
                            ((float)mkMeans.getClusters().get(1).size()/(float) dataset.size())*100
                    )+"%)");
                    output.newLine();
                    output.append("Irrelevant: "+mkMeans.getClusters().get(2).size()+
                            "("+String.format("%.2f",
                            ((float)mkMeans.getClusters().get(2).size()/(float) dataset.size())*100
                    )+"%)");
                    output.newLine();
                    output.append("Total Iteration: "+iterationCount);
                    output.newLine();
                    output.append("Time taken: "+String.format("%.2f seconds",time));
                    output.newLine();
                    output.newLine();
                    output.append("-----------------------------------------------------------");
                    output.newLine();
                    output.append("Cluster \t| RanK \t\t\t| Channel Name");
                    output.newLine();
                    output.append("===========================================================");
                    output.newLine();
                    mkMeans.getClusters().forEach(cluster ->{
                        cluster.stream().forEach(data ->{
                            try {
                                if(label == 0){
                                    output.append("Most-Relevant \t|");
                                }else if(label == 1){
                                    output.append("Relevant \t\t|");
                                }else {
                                    output.append("Irrelevant \t\t|");
                                }
                                output.append(data.get(0)+"\t\t\t| "+data.get(2));

                                output.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        label++;
                    });
                    output.close();
                    System.out.println("Report generated to output.txt");

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        input.nextLine();
    }

    /**
     * This method sets the data sets.
     * @param record
     */
    public static void setDataset(String record){
        var filter = record.substring(0,record.lastIndexOf("\"")+1).
                replaceAll("\"","").replaceAll(",","");
        var elements = new ArrayList<>();

        if(filter.length() > 0){
            filter = filter.concat(record.substring(record.lastIndexOf("\"")+1))
                    .replaceAll("-","1")
                    .replaceAll("1 ","1");
        }else{
            filter = record;
        }
        if(!filter.contains("--") && Arrays.asList(filter.split(",")).size() >= 6){
            elements.addAll((Collection) Arrays.asList(filter.split(",")));
            for (int i = 3; i < elements.size(); i++) {
                elements.set(i,Double.parseDouble(String.valueOf(elements.get(i))));
            }
            dataset.add(elements);
        }
    }

    /**
     * This method generates the initial
     * centroid
     * @param clusterSize
     */
    private static ArrayList initialCentroid(int clusterSize){
        Random random = new Random();
        ArrayList centroids = new ArrayList();
        for (int i = 0; i < clusterSize; i++) {
            centroids.add(dataset.get(random.nextInt(dataset.size())));
        }
        return centroids;
    }
}
