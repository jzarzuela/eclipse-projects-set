
package jsat.linear;

import java.util.Iterator;

import jsat.math.Function;

/**
 * This class provides efficient implementations of use full vector 
 * operations and updates. The implementations are efficient for 
 * both dense and sparse vectors. 
 * 
 * @author Edward Raff
 */
public class VecOps 
{
    /**
     * Bad value to be given as a default so that the index returned is always invalid. 
     * Thus, we can avoid null checks and just check on the index - avoiding additional code.
     */
    private static final IndexValue badIV = new IndexValue(-1, Double.NaN);
    
    /**
     * Computes the result of <big>&sum;</big><sub>&forall; i &isin; |w|</sub> w<sub>i</sub>
     * f(x<sub>i</sub>-y<sub>i</sub>)
     * @param w the vector of weight values to multiply on the results
     * @param x the first vector of values in the difference
     * @param y the second vector of values in the difference
     * @param f the single variate function to apply to the difference computed 
     * @return the accumulated sum of the evaluations
     */
    public static double accumulateSum(final Vec w, final Vec x, final Vec y, final Function f)
    {
        double val = 0;
        final boolean skipZeros = f.f(0) == 0;
        final boolean wSparse = w.isSparse();
        final boolean xSparse = x.isSparse();
        final boolean ySparse = y.isSparse();

        //skip zeros applied to (x_i-y_i) == 0. We can always skip zeros in w

        if (wSparse && !xSparse && !ySparse)
        {
            for (IndexValue wiv : w)
            {
                final int idx = wiv.getIndex();
                val += wiv.getValue() * f.f(x.get(idx) - y.get(idx));
            }
        }
        else if (!wSparse && !xSparse && !ySparse)//w is dense
        {
            for (int i = 0; i < w.length(); i++)
                val += w.get(i) * f.f(x.get(i) - y.get(i));
        }
        else //Best for all sparse, but also works well in general
        {
            Iterator<IndexValue> xIter = x.iterator();
            Iterator<IndexValue> yIter = y.iterator();

            IndexValue xiv = xIter.hasNext() ? xIter.next() : badIV;
            IndexValue yiv = yIter.hasNext() ? yIter.next() : badIV;

            for (IndexValue wiv : w)
            {
                int index = wiv.getIndex();
                double w_i = wiv.getValue();

                while (xiv.getIndex() < index && xIter.hasNext())
                    xiv = xIter.next();
                while (yiv.getIndex() < index && yIter.hasNext())
                    yiv = yIter.next();


                final double x_i, y_i;
                if (xiv.getIndex() == index)
                    x_i = xiv.getValue();
                else
                    x_i = 0;
                if (yiv.getIndex() == index)
                    y_i = yiv.getValue();
                else
                    y_i = 0;

                if (skipZeros && x_i == 0 && y_i == 0)
                    continue;
                val += w_i * f.f(x_i - y_i);
            }
        }
        
        return val;
    }
}
