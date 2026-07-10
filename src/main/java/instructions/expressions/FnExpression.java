package instructions.expressions;

import java.util.ArrayList;
import java.util.HashMap;

import forms.Form;
import forms.FormList;
import machine.Machine;
import utils.Closure;

public class FnExpression extends Expression{

    @Override   //When a lambda function is called we just add a closure to the machine's value that will then be added to the body 
    public void evaluate(Machine machine, ArrayList<Form> tail, HashMap<String,Object> environment, ArrayList<Object>address ){

        ArrayList<Form> params = ((FormList) tail.get(0)).elements();   
        ArrayList<Form> body = new ArrayList<>(tail.subList(1, tail.size()));

        Closure closure = new Closure(params, body, environment);   
        machine.pushValue(closure);
    }
}