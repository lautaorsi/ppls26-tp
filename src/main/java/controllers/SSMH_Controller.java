package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import distributions.Distribution;
import machine.Machine;
import messages.DoneMessage;
import messages.Message;
import messages.ObserveMessage;
import messages.SampleMessage;

public class SSMH_Controller extends Controller {
    
    String program;
    Random rng;
    int steps;
    int warmup;

    public SSMH_Controller(String program, Random rng, int steps, int warmup){
        this.program = program;
        this.rng = rng;
        this.steps = steps;
        this.warmup = warmup;
    }

    public ArrayList<Float> runInference(){

        HashMap<Object, Object> cache = new HashMap<>();

        ArrayList<Object> valueXSO = runSingleExecution(program, rng, null, cache);     //First execution w/o any fixed variables

        Object value = valueXSO.get(0);

        HashMap<Object,Object> trace = (HashMap<Object,Object>) valueXSO.get(1);    

        HashMap<Object,Float> sampleLogProbs = (HashMap<Object,Float>) valueXSO.get(2); 

        HashMap<Object,Float> observeLogProbs = (HashMap<Object,Float>) valueXSO.get(3);    

        ArrayList<Float> markov_chain = new ArrayList<Float>();                                        

        ArrayList<Object> keys = new ArrayList<>(trace.keySet());

        for(int i = 0; i < steps+warmup; i++){
            Object a0 =  keys.get(rng.nextInt(keys.size()));        //Select a variable x0 
        
            ArrayList<Object> value2X2S2O2 = runSingleExecution(program, rng, a0, trace);   //We generate a proposal freezing every other variable 
            
            Object value2 = value2X2S2O2.get(0);

            HashMap<Object,Object> traceProposal = (HashMap<Object,Object>) value2X2S2O2.get(1);

            HashMap<Object,Float> proposedSampleLogProbs = (HashMap<Object,Float>) value2X2S2O2.get(2);

            HashMap<Object,Float> proposedObserveLogProbs = (HashMap<Object,Float>) value2X2S2O2.get(3);
        
            //We accept the proposed run with a probability given by the acceptance atio function
            if(Math.log(rng.nextDouble()) < mh_log_acceptance_ratio(trace,traceProposal,sampleLogProbs,proposedSampleLogProbs,observeLogProbs,proposedObserveLogProbs,a0)){ //I'm terribly sorry about this gigantic function call
                value = value2;
                trace = traceProposal;
                sampleLogProbs = proposedSampleLogProbs;
                observeLogProbs = proposedObserveLogProbs;  //If proposed run is accepted we swap the current values 
            }
            if( i >= warmup){   //We start logging steps after warmup
                markov_chain.add(((Number) value).floatValue());; 
            }
        }
        
        return markov_chain;   
    }


    private ArrayList<Object> runSingleExecution(String program, Random rng, Object x0, HashMap<Object, Object> cache){
        
        Machine machine = Machine.initial_machine(program, rng);
        
        HashMap<Object,Object> trace = new HashMap<>();                     
        HashMap<Object,Float> sampleLogProbs = new HashMap<>();
        HashMap<Object,Float> observeLogProbs = new HashMap<>();

        
        while(true){
            Message message = machine.resume();

            if(message instanceof SampleMessage sampleMessage){
                
                Object address = sampleMessage.address();
                
                Distribution distribution = sampleMessage.distribution();

               Float x = (address.equals(x0) || !cache.containsKey(address)) ? distribution.sample(rng) : ((Number) cache.get(address)).floatValue();   //If the variable is the unfrozen one then we resample, if not we use the cache'd value
            
                trace.put(address, x); 

                sampleLogProbs.put(address, this.calculateLogDensity(distribution, x));     //Either way we stoe the logprob of the sample 
                
                machine.pushValue(x);
            }
            if(message instanceof ObserveMessage observeMessage){

                Object address = observeMessage.address();

                Distribution distribution = observeMessage.distribution();

                Float observedValue = observeMessage.observed_value();

                observeLogProbs.put(address, this.calculateLogDensity(distribution, observedValue));    

                machine.pushValue(observedValue);
            }
            if(message instanceof DoneMessage doneMessage){
                
                ArrayList<Object> returnList = new ArrayList<>();

                returnList.add(doneMessage.value());
                returnList.add(trace);
                returnList.add(sampleLogProbs);
                returnList.add(observeLogProbs);

                return returnList;              //Once the machine finishes we return the "tuple"
            }
        }
    }


    private double mh_log_acceptance_ratio(HashMap<Object,Object> trace,HashMap<Object,Object> traceProposal, HashMap<Object,Float> sampleLogProbs, HashMap<Object,Float> proposedSampleLogProbs, HashMap<Object,Float> observeLogProbs, HashMap<Object,Float> proposedObserveLogProbs, Object a0){
        Set<Object> fwd = new HashSet<>(traceProposal.keySet());    
        fwd.removeAll(trace.keySet());
        fwd.add(a0);    

        Set<Object> rev = new HashSet<>(trace.keySet());
        rev.removeAll(traceProposal.keySet());
        rev.add(a0);

        double num = 0.0;
        for(Map.Entry<Object, Float> entry : proposedSampleLogProbs.entrySet()){
            if(!fwd.contains(entry.getKey())){
                num += entry.getValue();
            }
        }
        for(float o2 : proposedObserveLogProbs.values()){
            num += o2;
        }

        double den = 0.0;
        for(Map.Entry<Object, Float> entry : sampleLogProbs.entrySet()){
            if(!rev.contains(entry.getKey())){
                den += entry.getValue();
            }
        }
        for(float o : observeLogProbs.values()){
            den += o;
        }

        return (Math.log(trace.size()) - Math.log(traceProposal.size())) + (num - den);
    }

}
