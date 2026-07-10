package distributions;

import java.util.Random;

public abstract class Distribution {
    public abstract Float logProb(Float x);
    public abstract float sample(Random rng);
}




