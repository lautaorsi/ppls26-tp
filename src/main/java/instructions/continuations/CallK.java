package instructions.continuations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import forms.FormSymbol;
import machine.Machine;
import messages.ContinueMessage;
import messages.Message;
import utils.Closure;
import utils.Primitives.Primitive;

public record CallK(int n, ArrayList<Object> address) implements Continuation {
    @Override
    public Message executeOn(Machine machine) {

        ArrayList<Object> arguments = new ArrayList<>();    

        for(int i = 0; i < n; i++){
            arguments.add(machine.getNextValue());  //We get the n instructions 
        }
        
        Collections.reverse(arguments); //They are pushed in reverse oder so we reverse them

        Object f = machine.getNextValue();  //Then the function itself

        if(f instanceof Closure closure){   //If it's a CLosure 
            HashMap<String,Object> newEnv = new HashMap<String,Object>(closure.environment());

            for(int i = 0; i < closure.parameters().size(); i++){
                String paramName = ((FormSymbol) closure.parameters().get(i)).text();
                newEnv.put(paramName, arguments.get(i));    //We add all params to the environment
            }

            machine.push_body(closure.body(), newEnv, address); //and push the closure's code to the machines body
        }
        else{
            machine.pushValue(((Primitive) f).apply(arguments));    //If it's a primitive then we just apply the arguments 
        }

        return new ContinueMessage();
    }
}