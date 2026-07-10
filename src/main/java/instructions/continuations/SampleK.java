package instructions.continuations;

import java.util.ArrayList;

import distributions.Distribution;
import machine.Machine;
import messages.Message;
import messages.SampleMessage;


public record SampleK(ArrayList<Object> address) implements Continuation {
    
    @Override
    public Message executeOn(Machine machine) { //When we encounter a sample continuation we fetch the distribution (from Stack Value) and send a message for the controller to handle sampling

        Distribution distribution = (Distribution) machine.getNextValue();

        return new SampleMessage(address, distribution, machine);
    }
}