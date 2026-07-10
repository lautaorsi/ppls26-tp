package controllers;

import java.util.Random;

import distributions.Distribution;
import machine.Machine;




public abstract class Controller{

    String program;
    Random seed;
        

    public void sendValue(Machine machine, Float value){
        machine.pushValue(value);
    }

    public float sampleFrom(Distribution d, Random rng){    
        return d.sample(rng);
    }

    public float calculateLogDensity(Distribution d, Float y){
        return d.logProb(y);
    }

}


