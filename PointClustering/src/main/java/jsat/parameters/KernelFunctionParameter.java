
package jsat.parameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jsat.distributions.empirical.kernelfunc.BiweightKF;
import jsat.distributions.empirical.kernelfunc.EpanechnikovKF;
import jsat.distributions.empirical.kernelfunc.GaussKF;
import jsat.distributions.empirical.kernelfunc.KernelFunction;
import jsat.distributions.empirical.kernelfunc.TriweightKF;
import jsat.distributions.empirical.kernelfunc.UniformKF;

/**
 * A default Parameter semi-implementation for classes that require a 
 * {@link KernelFunction} to be specified. 
 * 
 * @author Edward Raff
 */
public abstract class KernelFunctionParameter extends ObjectParameter<KernelFunction>
{
    private final static List<KernelFunction> kernelFuncs = Collections.unmodifiableList(new ArrayList<KernelFunction>()
    {{
        add(UniformKF.getInstance());
        add(EpanechnikovKF.getInstance());
        add(GaussKF.getInstance());
        add(BiweightKF.getInstance());
        add(TriweightKF.getInstance());
    }});

    @Override
    public List<KernelFunction> parameterOptions()
    {
        return kernelFuncs;
    }

    @Override
    public String getASCIIName()
    {
        return "Kernel Function";
    }
}
