package controllers;
import java.util.ArrayList;
import java.util.Random;

import distributions.Distribution;
import machine.Machine;
import messages.DoneMessage;
import messages.Message;
import messages.ObserveMessage;
import messages.SampleMessage;
import utils.CustomMath;



public class SMC_Controller extends Controller{
        
        ArrayList<Machine> particles;
        int particle_qtty;
        ArrayList<Random> rngs;

        public SMC_Controller(ArrayList<Machine> particles, ArrayList<Random> rngs){//Controller instantiator
            this.particles = particles;
            this.particle_qtty = particles.size();
            this.rngs = rngs;
        }

        
        public ArrayList<Float> runInference(){
            while(true){

                ArrayList<Message> messages = new ArrayList<>();
                
                for(Machine particle : particles){
                    messages.add(this.advance(particle));   //We make the particles execute until they reach an observe/done
                }    
                

                int doneCount = 0;
                int observeCount = 0;
                for(Message message : messages){
                    if(message.isDone()){
                        doneCount += 1;
                    }
                    if(message.isObserve()){
                        observeCount += 1;
                    }
                }

            
                if(doneCount == messages.size()){
                    ArrayList<Float> returnList = new ArrayList<>();
                    for(Message message : messages){
                        if(message instanceof DoneMessage doneMessage){ //If they are all done we add them to the return list
                            returnList.add(((Number) doneMessage.value()).floatValue());
                        }
                    }
                    return returnList;
                }
                if(observeCount != messages.size()){ //If they are not done and also not all observe then their executions diverged at some point
                    throw new Error("Particles reached different breakpoints");
                }


                ArrayList<Float> log_inc = new ArrayList<>();
                ArrayList<Machine> paused = new ArrayList<>();
            
                for(Message message : messages){
                    
                    Distribution d;
                    Float observed_value;
                    Machine machine;

                    if(message instanceof ObserveMessage observeMessage){ //At this point of the program all messages should be observe
                        d = observeMessage.distribution();
                        observed_value = observeMessage.observed_value();
                        machine = observeMessage.machine();
                        
                        Float log_prob = this.calculateLogDensity(d, observed_value);
                        machine.increaseLogW(log_prob);
                        
                        log_inc.add(log_prob);
                        machine.pushValue(observed_value);
                        paused.add(machine);        
                    }  //We calculate all of the particle's log probability and stop them 
                }
            
                //We create a list of the most-likely executions so far and those will be used as fork points
                ArrayList<Integer> ancestors = resample(CustomMath.softmax(log_inc), particle_qtty, rngs.get(0));

                ArrayList<Machine> nextParticles = new ArrayList<>();
                for(int j = 0; j < particle_qtty; j++){
                    Machine ancestor = paused.get(ancestors.get(j));     
                    nextParticles.add(ancestor.fork(rngs.get(j))); //We clone the ancestor with the particle's seed so it's not identical 
                }
                particles = nextParticles;
            }
        }       


        private Message advance(Machine machine){   //Basically this makes it only stop once we reach an observe expression
            Message message = machine.resume();

            while(message.isSample()){
                if(message instanceof SampleMessage sampleMessage){
                    Distribution distribution = sampleMessage.distribution();
                    Random rng = machine.getRNG();

                    Float sample = this.sampleFrom(distribution, rng);

                    machine.pushValue(sample);
                } 
                message = machine.resume();
            }
            return message;
            
        }





    private ArrayList<Integer> resample(ArrayList<Float> probabilities, int n, Random rng){ 
        ArrayList<Integer> result = new ArrayList<Integer>(n);

        for(int j = 0; j < n; j++){

            float oddOfSelection = rng.nextFloat();  
            float cumulativeProbability = 0f;
            int chosen = probabilities.size() - 1;

            for(int i = 0; i < probabilities.size(); i++){
                cumulativeProbability += probabilities.get(i);
                if(oddOfSelection < cumulativeProbability){
                    chosen = i;
                    break;
                }
            }

            result.add(j,chosen);
        }
        return result;
    }

    }
    
