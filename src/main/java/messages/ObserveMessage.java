package messages;


import java.util.ArrayList;

import distributions.Distribution;
import machine.Machine;

public record ObserveMessage(ArrayList<Object> address, Distribution distribution, Float observed_value, Machine machine) implements Message{

    @Override
    public boolean isObserve(){
        return true;
    }

}
