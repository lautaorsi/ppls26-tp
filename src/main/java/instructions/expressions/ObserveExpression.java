package instructions.expressions;

import java.util.ArrayList;
import java.util.HashMap;

import forms.Form;
import instructions.continuations.Ev;
import instructions.continuations.ObserveK;
import machine.Machine;

public class ObserveExpression extends Expression{

    @Override
    public void evaluate(Machine machine, ArrayList<Form> tail, HashMap<String,Object> environment, ArrayList<Object> address ){
        ObserveK observeK = new ObserveK(address);

        ArrayList<Object> newAddressY = new ArrayList<Object>(address);
        newAddressY.add("Y");
        ArrayList<Object> newAddressD = new ArrayList<Object>(address);
        newAddressD.add("d");

        Ev evY = new Ev(tail.get(1), environment, newAddressY); 
        Ev evD = new Ev(tail.get(0), environment, newAddressD);

        machine.pushContinuation(observeK);
        machine.pushContinuation(evY);
        machine.pushContinuation(evD);  //Before evaluating the observe continuation we evaluate both the observed value and the distribution
    }
}