package utils;

import java.util.ArrayList;

 
public class CustomMath {
    

    public static ArrayList<Float> softmax(ArrayList<Float> logWeights){
        float max = logWeights.get(0);
        for(float lw : logWeights){
            max = Math.max(max, lw);  
        } 

        ArrayList<Float> exps = new ArrayList<Float>(logWeights.size());
        float sum = 0f;

        for(int i = 0; i < logWeights.size(); i++){
            exps.add(i,(float) Math.exp(logWeights.get(i) - max)) ;
            sum += exps.get(i);
        }
        
        for(int i = 0; i < exps.size(); i++){
            exps.add(i, exps.get(i) / sum);
        }
        
        return exps;
    }
}
