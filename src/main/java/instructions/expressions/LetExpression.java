package instructions.expressions;

import java.util.ArrayList;
import java.util.HashMap;

import forms.Form;
import forms.FormList;
import instructions.continuations.Ev;
import instructions.continuations.LetK;
import machine.Machine;

public class LetExpression extends Expression{

    @Override   //If it's a let then it goes like (let [var1 val1 var2 val2 ...] body) and then the tail is [[var1 val1 var2 val2 ...], body]
    public void evaluate(Machine machine, ArrayList<Form> tail, HashMap<String,Object> environment, ArrayList<Object> address ){

        ArrayList<Form> binds = ((FormList) tail.get(0)).elements();    //The binds are the first element of the tail
        ArrayList<Form> body = new ArrayList<>(tail.subList(1, tail.size()));   //Then the following are the instructions

        if(!binds.isEmpty()){   //If it has any variables

            LetK letK = new LetK(binds, 0, body, environment, address);//Then we create a let continuation for ONLY the first variable as the executeOn() method recursively handles the rest (assuming there are more)

            ArrayList<Object> newAddress = new ArrayList<Object>(address);
            newAddress.add("let");
            newAddress.add(0);
            Ev ev = new Ev(binds.get(1), environment, newAddress);  //We create a continuation to evaluate the actual value that will be asigned to the variable

            machine.pushContinuation(letK);
            machine.pushContinuation(ev);
        }
        else{   //If it doesn't have any binds then we add the body for the machine to evaluate the expressions 
            machine.push_body(body, environment, address);
        }
    }
}