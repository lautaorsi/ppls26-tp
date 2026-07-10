# Messages

These messages are basically a communication interface between the machine and the controller, they do not perfom any operations and are solely used for conditional paths and to transport the data needed by the controller.

###  ContinueMessage 
---

Is used by the machine to keep going until it hits a sample/observe (basically acting as a more sophisticated null value) 

### DoneMessage
---

Used to isgnal to the controller that the program is finalized, it stores the final value and the machine object (useful for SSMH and SMC when multiple particles are working)

### SampleMessage 
---

Used to signal to the controller that the machine encountered a sample instruction, it contains the Distribution from which to sample, the machine and the address of the sample

### ObserveMessage
---

Used to signal to the controller that the machine encountered an observe instruction, it contains the Distribution, the observed value, the machine and the address of the observe.

