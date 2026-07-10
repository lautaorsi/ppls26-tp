package instructions.continuations;

import java.util.ArrayList;
import java.util.HashMap;

import forms.Form;
import machine.Machine;
import messages.ContinueMessage;
import messages.Message;

public record IfK(Form thenBranch, Form elseBranch, HashMap<String,Object> environment, ArrayList<Object> address) implements Continuation {
    @Override
    public Message executeOn(Machine machine) {
        boolean test = isTrue(machine.getNextValue());  //This is to handle the 0/1 "boolean" way of python (if 1 is the same as if true)
        
        Form branch = test ? thenBranch : elseBranch;
        String tag = test ? "then" : "else";

        ArrayList<Object> newAddress = new ArrayList<Object>(address);
        newAddress.add(tag);

        Ev newEv = new Ev(branch, environment, newAddress);
        machine.pushContinuation(newEv); //We add the expression corresponding to the branch 

        return new ContinueMessage();
    }





    private static boolean isTrue(Object value){
        if(value instanceof Boolean b) return b;
        if(value instanceof Number n) return n.floatValue() != 0f;
        return value != null;
    }
}