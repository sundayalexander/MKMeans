package com.company;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a MKMeans class
 *
 * @author: Amowe Sunday Alexander
 * @version:
 * @date: 3/21/2019 @4:39 PM
 */
public class MKMeans {
    //Class properties goes here
    private ArrayList<ArrayList> dataset;
    private int clusterSize;
    private ArrayList<ArrayList<ArrayList>> clusters;
    private ArrayList<ArrayList> centroids;
    private ArrayList<Double> distance;

    /**
     * Constructor
     */
    public MKMeans(ArrayList<ArrayList> dataset) {
        //Constructor logic goes here
        this.dataset = dataset;
        this.centroids = new ArrayList();
        this.clusters = new ArrayList();
    }

    /**
     *This method computes the distance between
     * the centroid point and the data point.
     */
    private double computeDistance(ArrayList dataPoint, ArrayList centriod){
        double distance = 0;
        for (int i = 3; i < centriod.size(); i++) {
            distance += Math.pow((double)dataPoint.get(i) - (double)centriod.get(i),2);
        }
        return  (double) Math.sqrt(distance);
    }

    /**
     *
     */
    public void computeClusters(){
        this.clusters = new ArrayList<>();
        for (int i = 0; i < this.clusterSize; i++) {
            this.clusters.add(new ArrayList<>());
        }
        this.dataset.forEach(data -> {
            this.distance = new ArrayList<>();
            this.centroids.forEach(centroid ->{
                this.distance.add(this.computeDistance(data,centroid));
            });
            var min = Math.min(this.distance.get(0),Math.min(this.distance.get(1),this.distance.get(2)));
            this.clusters.get(this.distance.indexOf(min)).add(data);
        });

    }

    /**
     * 
     * @return int
     */
    protected int getclusterSize() {
        return clusterSize;
    }

    /**
     * 
     * @param clusterSize
     */
    protected void setclusterSize(int clusterSize) {
        this.clusterSize = clusterSize;
    }

    /**
     *
     * @return
     */
    protected ArrayList<ArrayList<ArrayList>> getClusters() {
        return clusters;
    }

    /**
     *
     * @param clusters
     */
    protected void setClusters(ArrayList<ArrayList<ArrayList>> clusters) {
        this.clusters = clusters;
    }

    /**
     *
     * @return
     */
    protected ArrayList<ArrayList> getCentroids() {
        return centroids;
    }

    /**
     *
     * @param centroids
     */
    protected void setCentroids(ArrayList<ArrayList> centroids) {
        this.centroids = centroids;
    }

    /**
     *This method compute the new centroids
     * @return
     */
    protected ArrayList<ArrayList> newCentroids(){
        ArrayList<ArrayList> centroids = new ArrayList<>();
        this.clusters.forEach(cluster ->{
            var centroid = (ArrayList)cluster.get(0).clone();
            for (int i = 3; i < cluster.get(0).size(); i++) {
                double mean = 0.0;
                for (int j = 0; j < cluster.size(); j++) {
                    mean += (double)cluster.get(j).get(i);
                }
                centroid.set(i,mean/cluster.size());
            }
            centroids.add(centroid);
        });
        return  centroids;
    }

}
