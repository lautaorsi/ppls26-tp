package controllers;

import java.util.Random;

import distributions.Distribution;
import machine.Machine;
import messages.DoneMessage;
import messages.Message;
import messages.ObserveMessage;
import messages.SampleMessage;
import utils.Tuple;

public class LW_Controller extends Controller{

    Machine machine;
    Random rng;
    
    public LW_Controller(Machine machine, Random rng){

        this.machine = machine;
        this.rng = rng;
    }


    public Tuple<Object, Float> runInference(){
        while(true){    //We run the program "once" (we make it resume its execution until it finishes)
            
        Message message = machine.resume();

            if(message instanceof SampleMessage sampleMessage){ //When machine encountes a sample it communicates w/ controller to perform stochastic sampling

                Distribution d = sampleMessage.distribution();

                float sampleValue = this.sampleFrom(d, rng);     

                machine.pushValue(sampleValue); //Send the sampled value back to the machine (sending it is basically pushing to the stackValue) 
            }
            if(message instanceof ObserveMessage observeMessage){
                    
                    Distribution d = observeMessage.distribution();
                    Float observed_value = observeMessage.observed_value();

                    Float logWIncrement = this.calculateLogDensity(d, observed_value); //log p_d(y)
                    
                    machine.increaseLogW(logWIncrement);    //Osbserve expessions increase the logW by log p_d(y)

                    machine.pushValue(observed_value);  //And send back to the machine the same observed value (y)
            }
            if(message instanceof DoneMessage doneMessage){
                    Tuple<Object, Float> return_value =  new Tuple<Object, Float>(doneMessage.value(), machine.getLogW());
                    return return_value;    //Once machine is done we return the final program's result + machine's logW
            }
        }
    }
    



        
}
