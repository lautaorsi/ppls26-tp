package instructions.expressions;

import java.util.ArrayList;
import java.util.HashMap;

import forms.Form;
import instructions.continuations.Ev;
import instructions.continuations.IfK;
import machine.Machine;

public class IfExpression extends Expression{

    @Override
    public void evaluate(Machine machine, ArrayList<Form> tail, HashMap<String,Object> environment, ArrayList<Object> address ){
        Form test = tail.get(0);
        Form then = tail.get(1);
        Form els = tail.get(2);

        IfK ifK = new IfK(then, els, environment, address);

        ArrayList<Object> newAddress = new ArrayList<Object>(address);
        newAddress.add("test");
        Ev ev = new Ev(test, environment, newAddress);  //We create a continuation because we have to first evaluate the condition 

        machine.pushContinuation(ifK);
        machine.pushContinuation(ev); //So it first processes the conditional and then the ifk 
    }
}