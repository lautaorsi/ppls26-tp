package instructions.continuations;

import java.util.ArrayList;
import java.util.HashMap;

import forms.Form;
import forms.FormList;
import forms.FormLiteral;
import forms.FormSymbol;
import instructions.expressions.CallExpression;
import instructions.expressions.Expression;
import machine.Machine;
import messages.ContinueMessage;
import messages.Message;
import utils.Primitives;

public record Ev(Form expression, HashMap<String,Object> environment, ArrayList<Object> address) implements Continuation {
    @Override
    public Message executeOn(Machine machine) {
        if(expression instanceof FormSymbol symbol){
            String name = symbol.text();
            if(environment.containsKey(name)){
                machine.pushValue(environment.get(name));   //If it's a variable then we add the value
            }
            else if(Primitives.isPrimitive(name)){
                machine.pushValue(Primitives.getPrimitive(name));   //If it's a primitive function we add the function 
            }

        }
        
        else if(expression instanceof FormLiteral literal){
            machine.pushValue(literal.value()); //If it's a value we just add it tot he stack
        }

        else if(expression instanceof FormList list){   //If it's a list (a function)
            Form head = list.get(0);    
            String tag;
            if(head instanceof FormSymbol headSymbol) { //If the head is just an "atomic" operation we get the symbol
                tag = headSymbol.text();
            }else{
                tag = null;
            }

            Expression expressionObject = Expression.createExpression(tag); //we create the exprerssion that corresponds w/ the function

            ArrayList<Form> args;
            if (expressionObject instanceof CallExpression) {   //If the function is not atomic then we preserve it to evaluate it
                args = list.elements();
            } 
            else{
                args = new ArrayList<>(list.elements().subList(1, list.size())); //If the function was atomic we just get the rest of the args
            } 

            expressionObject.evaluate(machine, args, environment, address);
        }

        return new ContinueMessage();
    }
}