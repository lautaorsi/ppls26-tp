package instructions.continuations;

import machine.Machine;
import messages.Message;

public interface Continuation{
        Message executeOn(Machine machine);
}





















