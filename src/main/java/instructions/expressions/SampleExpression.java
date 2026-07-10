package instructions.expressions;

import java.util.ArrayList;
import java.util.HashMap;

import forms.Form;
import instructions.continuations.Ev;
import instructions.continuations.SampleK;
import machine.Machine;

public class SampleExpression extends Expression{

    @Override
    public void evaluate(Machine machine, ArrayList<Form> tail, HashMap<String,Object> environment, ArrayList<Object> address ){
        SampleK sampleK = new SampleK(address);

        ArrayList<Object> newAddress = new ArrayList<Object>(address);
        newAddress.add("d");
        Ev ev = new Ev(tail.get(0), environment, newAddress);

        machine.pushContinuation(sampleK);
        machine.pushContinuation(ev);   //We evaluate the distribution to sample before evaluating the continuation
    }
}