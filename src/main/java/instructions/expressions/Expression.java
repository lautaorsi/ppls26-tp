package instructions.expressions;

import java.util.ArrayList;
import java.util.HashMap;

import forms.Form;
import machine.Machine;

public abstract class Expression{

    public static Expression createExpression(String type){
        if("let".equals(type)){ return new LetExpression(); }
        if("if".equals(type)){ return new IfExpression(); }
        if("fn".equals(type)){ return new FnExpression(); }
        if("sample".equals(type)){ return new SampleExpression(); }
        if("observe".equals(type)){ return new ObserveExpression(); }
        return new CallExpression();    //Not much to say here, if it's a LISP-defined opeation then we create the expression, otherwise it's a specific function
    }

    public abstract void evaluate(Machine machine, ArrayList<Form> tail, HashMap<String,Object> environment, ArrayList<Object> address);
}