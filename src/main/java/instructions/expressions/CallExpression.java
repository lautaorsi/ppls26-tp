package instructions.expressions;

import java.util.ArrayList;
import java.util.HashMap;

import forms.Form;
import instructions.continuations.CallK;
import instructions.continuations.Ev;
import machine.Machine;

public class CallExpression extends Expression{

    @Override
    public void evaluate(Machine machine, ArrayList<Form> tail, HashMap<String,Object> environment, ArrayList<Object>  address ){

        int n = tail.size() - 1; // Number of arguments, excluding operator 

        CallK callK = new CallK(n, address);
        machine.pushContinuation(callK);    //We create the call continuation

        for(int i = n; i >= 1; i--){
            ArrayList<Object>  newAddress = new ArrayList<Object>(address);
            newAddress.add(i - 1);

            Ev ev = new Ev(tail.get(i), environment, newAddress);
            machine.pushContinuation(ev);   //We add the parameters in an Ev continuation because they have to first be evaluated in order to actually call the function
        }

        ArrayList<Object>  newAddress = new ArrayList<Object>(address);
        newAddress.add("fn");

        Ev ev = new Ev(tail.get(0), environment, newAddress);
        machine.pushContinuation(ev);
    }
}