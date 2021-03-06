package jsat.regression;

import static java.lang.Math.pow;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsat.classifiers.ClassificationModelEvaluation;
import jsat.classifiers.DataPoint;
import jsat.datatransform.DataTransformProcess;
import jsat.exceptions.UntrainedModelException;
import jsat.math.OnLineStatistics;
import jsat.utils.SystemInfo;

/**
 * Provides a mechanism to quickly evaluate a regression model on a data set. 
 * This can be done by cross validation or with a separate testing set. 
 * 
 * @author Edward Raff
 */
public class RegressionModelEvaluation
{
    private Regressor regressor;
    
    private RegressionDataSet dataSet;
    
    /**
     * The source of threads
     */
    private ExecutorService threadpool;
    
    private OnLineStatistics sqrdErrorStats;
    
    private long totalTrainingTime = 0, totalClassificationTime = 0;
    
    private DataTransformProcess dtp;

    /**
     * Creates a new RegressionModelEvaluation that will perform parallel training. 
     * @param regressor the regressor model to evaluate
     * @param dataSet the data set to train or perform cross validation from
     * @param threadpool the source of threads for training of models
     */
    public RegressionModelEvaluation(Regressor regressor, RegressionDataSet dataSet, ExecutorService threadpool)
    {
        this.regressor = regressor;
        this.dataSet = dataSet;
        this.threadpool = threadpool;
        this.dtp =new DataTransformProcess();
    }
    
    /**
     * Creates a new RegressionModelEvaluation that will perform serial training
     * @param regressor the regressor model to evaluate
     * @param dataSet the data set to train or perform cross validation from
     */
    public RegressionModelEvaluation(Regressor regressor, RegressionDataSet dataSet)
    {
        this(regressor, dataSet, null);
    }
    
    /**
     * Sets the data transform process to use when performing cross validation. 
     * By default, no transforms are applied
     * @param dtp the transformation process to clone for use during evaluation
     */
    public void setDataTransformProcess(DataTransformProcess dtp)
    {
        this.dtp = dtp.clone();
    }
    
    /**
     * Performs an evaluation of the regressor using the training data set. 
     * The evaluation is done by performing cross validation.
     * @param folds the number of folds for cross validation
     * @throws UntrainedModelException if the number of folds given is less than 2
     */
    public void evaluateCrossValidation(int folds)
    {
        evaluateCrossValidation(folds, new Random());
    }
    
    /**
     * Performs an evaluation of the regressor using the training data set. 
     * The evaluation is done by performing cross validation.
     * @param folds the number of folds for cross validation
     * @param rand the source of randomness for generating the cross validation sets
     * @throws UntrainedModelException if the number of folds given is less than 2
     */
    public void evaluateCrossValidation(int folds, Random rand)
    {
        if(folds < 2)
            throw new UntrainedModelException("Model could not be evaluated because " + folds + " is < 2, and not valid for cross validation");
        
        List<RegressionDataSet> lcds = dataSet.cvSet(folds, rand);
        
        sqrdErrorStats = new OnLineStatistics();
        totalTrainingTime = totalClassificationTime = 0;
        
        for(int i = 0; i < lcds.size(); i++)
        {
            RegressionDataSet trainSet = RegressionDataSet.comineAllBut(lcds, i);
            RegressionDataSet testSet = lcds.get(i);
            evaluationWork(trainSet, testSet);
        }
    }
    
    /**
     * Performs an evaluation of the regressor using the initial data set to 
     * train, and testing on the given data set. 
     * @param testSet the data set to perform testing on
     */
    public void evaluateTestSet(RegressionDataSet testSet)
    {
        sqrdErrorStats = new OnLineStatistics();
        totalTrainingTime = totalClassificationTime = 0;
        evaluationWork(dataSet, testSet);
    }
    
    private void evaluationWork(RegressionDataSet trainSet, RegressionDataSet testSet)
    {
        trainSet = trainSet.shallowClone();
        DataTransformProcess curProccess = dtp.clone();
        curProccess.learnApplyTransforms(trainSet);
        
        long startTrain = System.currentTimeMillis();
        if(threadpool != null)
            regressor.train(trainSet, threadpool);
        else
            regressor.train(trainSet);            
        totalTrainingTime += (System.currentTimeMillis() - startTrain);
        
        CountDownLatch latch;
        if(testSet.getSampleSize() < SystemInfo.LogicalCores || threadpool == null)
        {
            latch = new CountDownLatch(1);
            new Evaluator(testSet, curProccess, 0, testSet.getSampleSize(), latch).run();
        }
        else//go parallel!
        {
            latch = new CountDownLatch(SystemInfo.LogicalCores);
            final int blockSize = testSet.getSampleSize()/SystemInfo.LogicalCores;
            int extra = testSet.getSampleSize()%SystemInfo.LogicalCores;
            
            int start = 0;
            while(start < testSet.getSampleSize())
            {
                int end = start+blockSize;
                if(extra-- > 0)
                    end++;
                threadpool.submit(new Evaluator(testSet, curProccess, start, end, latch));
                start = end;
            }
        }
        try
        {
            latch.await();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(ClassificationModelEvaluation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private class Evaluator implements Runnable
    {
        RegressionDataSet testSet;
        DataTransformProcess curProccess;
        int start, end;
        CountDownLatch latch;
        long localPredictionTime;

        public Evaluator(RegressionDataSet testSet, DataTransformProcess curProccess, int start, int end, CountDownLatch latch)
        {
            this.testSet = testSet;
            this.curProccess = curProccess;
            this.start = start;
            this.end = end;
            this.latch = latch;
            localPredictionTime = 0;
        }

        @Override
        public void run()
        {
            for(int i = start; i < end; i++)
            {
                DataPoint di = testSet.getDataPoint(i);
                double trueVal = testSet.getTargetValue(i);
                DataPoint tranDP = curProccess.transform(di);
                long startTime = System.currentTimeMillis();
                double predVal = regressor.regress(tranDP);
                localPredictionTime += (System.currentTimeMillis() - startTime);

                double sqrdError = pow(trueVal-predVal, 2);

                synchronized(sqrdErrorStats)
                {
                    sqrdErrorStats.add(sqrdError, di.getWeight());
                }
            }
            synchronized(sqrdErrorStats)
            {
                totalClassificationTime += localPredictionTime;
            }
            latch.countDown();
        }
        
    }
    
    /**
     * Returns the minimum squared error from all runs. 
     * @return the minimum observed squared error
     */
    public double getMinError()
    {
        return sqrdErrorStats.getMin();
    }
    
    /**
     * Returns the maximum squared error observed from all runs. 
     * @return the maximum observed squared error
     */
    public double getMaxError()
    {
        return sqrdErrorStats.getMax();
    }
    
    /**
     * Returns the mean squared error from all runs. 
     * @return the overall mean squared error
     */
    public double getMeanError()
    {
        return sqrdErrorStats.getMean();
    }
    
    /**
     * Returns the standard deviation of the error from all runs
     * @return the overall standard deviation of the errors
     */
    public double getErrorStndDev()
    {
        return sqrdErrorStats.getStandardDeviation();
    }
    
    /***
     * Returns the total number of milliseconds spent training the regressor. 
     * @return the total number of milliseconds spent training the regressor. 
     */
    public long getTotalTrainingTime()
    {
        return totalTrainingTime;
    }

    /**
     * Returns the total number of milliseconds spent performing regression on the testing set. 
     * @return the total number of milliseconds spent performing regression on the testing set. 
     */
    public long getTotalClassificationTime()
    {
        return totalClassificationTime;
    }
    
    /**
     * Returns the regressor that was to be evaluated
     * @return the regressor original given
     */
    public Regressor getRegressor()
    {
        return regressor;
    }
    
}
