
package jsat.clustering;

import static jsat.clustering.SeedSelectionMethods.selectIntialPoints;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsat.DataSet;
import jsat.clustering.SeedSelectionMethods.SeedSelection;
import jsat.linear.Vec;
import jsat.linear.distancemetrics.DistanceMetric;
import jsat.linear.distancemetrics.EuclideanDistance;
import jsat.linear.distancemetrics.TrainableDistanceMetric;
import jsat.math.OnLineStatistics;
import jsat.utils.FakeExecutor;
import jsat.utils.SystemInfo;

/**
 *
 * @author Edward Raff
 */
public class PAM extends KClustererBase
{
    protected DistanceMetric dm;
    protected Random rand;
    private SeedSelection seedSelection;
    protected int repeats = 1;
    protected int iterLimit = 100;
    
    protected int[] medoids;
    protected boolean storeMedoids = true;

    public PAM(DistanceMetric dm, Random rand, SeedSelection seedSelection)
    {
        this.dm = dm;
        this.rand = rand;
        this.seedSelection = seedSelection;
    }

    public PAM(DistanceMetric dm, Random rand)
    {
        this(dm, rand, SeedSelection.KPP);
    }

    public PAM(DistanceMetric dm)
    {
        this(dm, new Random());
    }

    public PAM()
    {
        this(new EuclideanDistance());
    }
    
    /**
     * If set to {@code true} the computed medoids will be stored after clustering
     * is completed, and can then be retrieved using {@link #getMedoids() }. 
     * @param storeMedoids {@code true} if the medoids should be stored for 
     * later, {@code false} to discard them once clustering is complete. 
     */
    public void setStoreMedoids(boolean storeMedoids)
    {
        this.storeMedoids = storeMedoids;
    }

    /**
     * Returns the raw array of indices that indicate which data point acted as 
     * the center for each cluster. 
     * @return the array of medeoid indices
     */
    public int[] getMedoids()
    {
        return medoids;
    }

    /**
     * Sets the method of seed selection used by this algorithm
     * @param seedSelection the method of seed selection to used
     */
    public void setSeedSelection(SeedSelection seedSelection)
    {
        this.seedSelection = seedSelection;
    }

    /**
     * 
     * @return the method of seed selection used by this algorithm
     */
    public SeedSelection getSeedSelection()
    {
        return seedSelection;
    }
   
    
    /**
     * Performs the actual work of PAM. 
     * 
     * @param data the data set to apply PAM to
     * @param medoids the array to store the indices that get chosen as the medoids. The length of the array indicates how many medoids should be obtained. 
     * @param assignments an array of the same length as <tt>data</tt>, each value indicating what cluster that point belongs to. 
     * @return the sum of the squared distance from each point to its closest medoids 
     */
    protected double cluster(DataSet data, int[] medioids, int[] assignments)
    {
        double totalDistance = 0;
        int changes = -1;
        Arrays.fill(assignments, -1);//-1, invalid category!
        
        int[] bestMedCand = new int[medioids.length];
        double[] bestMedCandDist = new double[medioids.length];
        
        TrainableDistanceMetric.trainIfNeeded(dm, data);

        int iter = 0;
        do
        {
            changes = 0;
            totalDistance = 0.0;
            
            for(int i = 0; i < data.getSampleSize(); i++)
            {
                Vec dpVec = data.getDataPoint(i).getNumericalValues();
                int assignment = 0;
                double minDist = dm.dist(data.getDataPoint(medioids[0]).getNumericalValues(), dpVec);

                for (int k = 1; k < medioids.length; k++)
                {
                    double dist = dm.dist(data.getDataPoint(medioids[k]).getNumericalValues(), dpVec);
                    if (dist < minDist)
                    {
                        minDist = dist;
                        assignment = k;
                    }
                }

                //Update which cluster it is in
                if (assignments[i] != assignment)
                {
                    changes++;
                    assignments[i] = assignment;
                }
                totalDistance += minDist * minDist;
      
            }
            
            
            //TODO this update may be faster by using more memory, and actually moiving people about in the assignment loop above
            //Update the medoids
            Arrays.fill(bestMedCandDist, Double.MAX_VALUE);
            for(int i = 0; i < data.getSampleSize(); i++)
            {
                double thisCandidateDistance = 0.0;
                int clusterID = assignments[i];
                Vec medCandadate = data.getDataPoint(i).getNumericalValues();
                for(int j = 0; j < data.getSampleSize(); j++)
                {
                    if(j == i || assignments[j] != clusterID)
                        continue;
                    thisCandidateDistance += Math.pow(dm.dist(medCandadate, data.getDataPoint(j).getNumericalValues()), 2);
                }
                
                if(thisCandidateDistance < bestMedCandDist[clusterID])
                {
                    bestMedCand[clusterID] = i;
                    bestMedCandDist[clusterID] = thisCandidateDistance;
                }
            }
            System.arraycopy(bestMedCand, 0, medioids, 0, medioids.length);
        }
        while( changes > 0 && iter++ < iterLimit);
        
        return totalDistance;
    }

    public int[] cluster(DataSet dataSet, int[] designations)
    {
        return cluster(dataSet, 2, (int)Math.sqrt(dataSet.getSampleSize()/2), designations);
    }

    public int[] cluster(DataSet dataSet, ExecutorService threadpool, int[] designations)
    {
        return cluster(dataSet, 2, (int)Math.sqrt(dataSet.getSampleSize()/2), threadpool, designations);
    }

    public int[] cluster(DataSet dataSet, int clusters, ExecutorService threadpool, int[] designations)
    {
         return cluster(dataSet, clusters, designations);
    }

    public int[] cluster(DataSet dataSet, int clusters, int[] designations)
    {
        if(designations == null)
            designations = new int[dataSet.getSampleSize()];
        medoids = new int[clusters];
        
        selectIntialPoints(dataSet, medoids, dm, rand, seedSelection);
        
        cluster(dataSet, medoids, designations);
        
        if(!storeMedoids)
            medoids = null;
        
        return designations;
    }

    public int[] cluster(DataSet dataSet, int lowK, int highK, int[] designations)
    {
        return cluster(dataSet, lowK, highK, new FakeExecutor(), designations);
    }

    private class ClusterWorker implements Runnable
    {
        private DataSet dataSet;
        private int[] assignments;
        private int[] medioids;
        private volatile double result = -1;
        private volatile BlockingQueue<ClusterWorker> putSelf;

        public ClusterWorker(DataSet dataSet, BlockingQueue<ClusterWorker> putSelf)
        {
            this.dataSet = dataSet;
            this.assignments = new int[dataSet.getSampleSize()];
            this.medioids = medioids;
            this.putSelf = putSelf;
        }

        public void setAssignments(int[] assignments)
        {
            this.assignments = assignments;
        }

        public void setMedioids(int[] medioids)
        {
            this.medioids = medioids;
        }
        
        public int getK()
        {
            return medioids.length;
        }

        public double getResult()
        {
            return result;
        }
        
        public void run()
        {
            result = cluster(dataSet, medioids, assignments);
            putSelf.add(this);
        }
        
    }
    
    public int[] cluster(DataSet dataSet, int lowK, int highK, ExecutorService threadpool, int[] designations)
    {
        double[] totDistances = new double[highK-lowK+1];
        
        BlockingQueue<ClusterWorker> workerQue = new ArrayBlockingQueue<ClusterWorker>(SystemInfo.LogicalCores);
        for(int i = 0; i < SystemInfo.LogicalCores; i++)
            workerQue.add(new ClusterWorker(dataSet, workerQue));
        
        int k = lowK;
        int received = 0;
        while(received < totDistances.length)
        {
            try
            {
                ClusterWorker worker = workerQue.take();
                if(worker.getResult() != -1)//-1 means not really in use
                {
                    totDistances[worker.getK() - lowK] = worker.getResult();
                    received++;
                }
                if(k <= highK)
                {
                    worker.setMedioids(new int[k++]);
                    threadpool.submit(worker);
                }
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(PAM.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Now we process the distance changes
        /**
         * Keep track of the changes
         */
        OnLineStatistics stats = new OnLineStatistics();
        
        double maxChange = Double.MIN_VALUE;
        int maxChangeK = lowK;
        
        for(int i = 1; i < totDistances.length; i++)
        {
            double change = Math.abs(totDistances[i] - totDistances[i-1]);
            stats.add(change);
            if (change > maxChange)
            {
                maxChange = change;
                maxChangeK = i+lowK;
            }
        }
        
        if(maxChange < stats.getStandardDeviation()*2+stats.getMean())
            maxChangeK = lowK;
        
        return cluster(dataSet, maxChangeK, designations);
    }
}
