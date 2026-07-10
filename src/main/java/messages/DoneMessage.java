package messages;

import machine.Machine;

public record DoneMessage(Object value, Machine machine) implements Message{
    public boolean isDone(){
        return true;
    }
}