import java.util.ArrayList;
import java.util.Random;

import probabilistic_program.ProbabilisticProgram;

public class Main {
    public static void main(String[] args) {


        String conj = "(let (mu (sample (normal 0 1))) (observe (normal mu 1) 2.3) mu)";
        var chain = ProbabilisticProgram.runSSMHProbabilisticProgram(conj, new Random(0), 60000, 3000);

        float mean = 0f;
        for(Float v : chain){
            mean += v;
        }
        mean /= chain.size();
        float variance = 0f; 
        for(Float v : chain){
            variance += (v - mean) * (v - mean);
        }
        float std = (float) Math.sqrt(variance / chain.size());
        
        System.out.println("conj  SSMH mean = " + mean + " std = " + std + " (exact 1.150, 0.707)"); 




        String bits = "(let [b1 (if (sample (bernoulli 0.5)) 1 0) b2 (if (sample (bernoulli 0.5)) 1 0) b3 (if (sample (bernoulli 0.5)) 1 0) b4 (if (sample (bernoulli 0.5)) 1 0) b5 (if (sample (bernoulli 0.5)) 1 0) b6 (if (sample (bernoulli 0.5)) 1 0) b7 (if (sample (bernoulli 0.5)) 1 0) b8 (if (sample (bernoulli 0.5)) 1 0) total (+ b1 b2 b3 b4 b5 b6 b7 b8)] (observe (normal 7 2) total) total)";
        var chainBits = ProbabilisticProgram.runSSMHProbabilisticProgram(bits,new Random(1), 40000, 3000);
        
        float meanBits = 0f;
        for(Float v : chainBits){
            meanBits += v;
        }
        meanBits /= chainBits.size();

        System.out.println("bits  SSMH mean = " + meanBits  + " (exact 5.014)");


        
        

        // LW
        ArrayList<Float> lwValues = new ArrayList<>();
        ArrayList<Float> lwLogWeights = new ArrayList<>();
        for(int i = 0; i < 100000; i++){
            var result = ProbabilisticProgram.runLWProbabilisticProgram(conj, new Random(2000 + i));
            lwValues.add(((Number) result.first()).floatValue());
            lwLogWeights.add(result.second());
        }
        float lwMean = weightedMean(lwValues, lwLogWeights);

        // SMC
        ArrayList<Random> smcRngs = new ArrayList<>();
        for(int i = 0; i < 20000; i++){
            smcRngs.add(new Random(1000 + i));
        }
        var smcResults = ProbabilisticProgram.runSMCProbabilisticProgram(conj, smcRngs, 20000);
        float smcMean = 0f;
        for(Float v : smcResults){ 
            smcMean += v;
        }
        smcMean /= smcResults.size();

        // SSMH
        var ssmhChain = ProbabilisticProgram.runSSMHProbabilisticProgram(conj, new Random(0), 60000, 3000);
        float ssmhSum = 0f;
        for(Float v : ssmhChain){
            ssmhSum += v;
        }
        Float ssmhMean = ssmhSum / ssmhChain.size();

        System.out.println("LW mean = " + lwMean + "   expected = 1.150");
        System.out.println("SMC mean = " + smcMean + "   expected = 1.150");
        System.out.println("SSMH mean = " + ssmhMean + "   expected = 1.150");

    
        String shift = "(let [make-shift (fn [mu] (fn [x] (+ x mu)))  f (make-shift 10)] (f 3))";
    
        Number lwValue = (Number) ProbabilisticProgram.runLWProbabilisticProgram(shift, new Random(0)).first();
    
        System.out.println("(f 3) = " + lwValue + " exptected 13");

        String geom = "(defn geom [] (if (sample (bernoulli 0.3)) 0 (+ 1 (geom)))) (geom)";

        Random rng = new Random(1);
        ArrayList<Float> lwGeomValues = new ArrayList<>();
        ArrayList<Float> lwGeomLogWeights = new ArrayList<>();
        for(int i = 0; i < 200000; i++){
            var result = ProbabilisticProgram.runLWProbabilisticProgram(geom, rng);
            lwGeomValues.add(((Number) result.first()).floatValue());
            lwGeomLogWeights.add(result.second());
        }
        lwMean = weightedMean(lwGeomValues, lwGeomLogWeights);

        System.out.println("geom mean = " + lwMean + "  exact (1-p)/p = 2.333");
    }




    //credits: claudio @ anthropic
    private static float weightedMean(ArrayList<Float> values, ArrayList<Float> logWeights){
        float maxLogW = Float.NEGATIVE_INFINITY;
        for(float lw : logWeights) maxLogW = Math.max(maxLogW, lw);

        float sumWeights = 0f;
        float weightedSum = 0f;
        for(int i = 0; i < values.size(); i++){
            float w = (float) Math.exp(logWeights.get(i) - maxLogW);
            sumWeights += w;
            weightedSum += w * values.get(i);
        }
        return weightedSum / sumWeights;
    }
}
