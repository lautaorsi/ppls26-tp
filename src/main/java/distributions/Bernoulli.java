package distributions;

import java.util.Random;


public class Bernoulli extends Distribution {
    private float p;

    public Bernoulli(float p){
        this.p = p;
    }

    //Credits: Claudio @ Anthropic
    @Override
    public Float logProb(Float x){
        boolean value = x != 0f;
        return (float) Math.log(value ? p : (1 - p));
    }

    @Override
    public float sample(Random rng){
        double x =  rng.nextDouble();
        float value;
        if(x < p){
            value = 1.0f;
        }else{
            value = 0.0f;
        }
        return value;
    }
}