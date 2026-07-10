package instructions.continuations;

import java.util.ArrayList;
import java.util.HashMap;

import forms.Form;
import forms.FormSymbol;
import machine.Machine;
import messages.ContinueMessage;
import messages.Message;

public record LetK(ArrayList<Form> binds, int var_index, ArrayList<Form> body, HashMap<String,Object> environment, ArrayList<Object> address) implements Continuation {
    
    @Override
    public Message executeOn(Machine machine) {
    
        String variable_name = ((FormSymbol) binds.get(2 * var_index)).text(); 


        HashMap<String,Object> newEnv = new HashMap<String,Object>(environment);
        newEnv.put(variable_name, machine.getNextValue());
        
        
        if (2 * (var_index + 1) < binds.size()) {   //Here we create a new continuation for the next variable if there is one

            LetK newLetK = new LetK(binds, var_index + 1, body, newEnv, address);   //The letK for the next index variable
            machine.pushContinuation(newLetK);

            ArrayList<Object> newAddress = new ArrayList<Object>(address);
            newAddress.add("let");
            newAddress.add(2 * (var_index + 1));

            Ev newEv = new Ev(binds.get(2 * (var_index + 1) + 1), newEnv, newAddress);  //And we evaluate the actual value for the new variable before assigning it
            machine.pushContinuation(newEv);
        }
        else {  //If there are no more variables to assing then we push the resulting environment to the machine and add the body
            machine.push_body(body, newEnv, address);
        }

        
        return new ContinueMessage();
    }
}