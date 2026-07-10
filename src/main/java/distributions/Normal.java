package distributions;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Normal extends Distribution {

    private NormalDistribution dist;

    public Normal(float mu, float sigma){
        this.dist = new NormalDistribution(mu, sigma);
    }

    @Override
    public Float logProb(Float x){
        return (float) dist.logDensity(x);
    }

    @Override
    public float sample(Random rng){
        return (float) dist.inverseCumulativeProbability(rng.nextDouble());
    }
}