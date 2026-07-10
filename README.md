# Probablistic Programming Languages 2026 
---

This is the final project for the Introduction to Probabilistic Programming Languages course.

## Execution
---
Running the code is fairly simple as I've provided an executable `.jar` file that automatically runs some "tests" _(note that they are not actuall JUNIT tests but rather console logs comparing expected vs actual results)_, they present a couple of scenarios that I'll explain later on. <br>

For now, to execute the `.jar` you must have [JAVA installed](https://www.java.com/en/download/manual.jsp) and then run from the terminal:
```bash
java -jar TP-1.0.jar
```

Alternatively it is possible to rebuild the .jar file by downloading everthing in this repo and running 

```bash 
mvn clean package
```

After that a `TP-ppls26-1.0.jar` file will appear in the target directory 

## About the project
---

The idea is to showcase how a program is mainly agnostic to inference and stochastic operations. In order to do so we will present a machine capable of delegating those undeterministic responsibilities to an inference controller which subsequently replies with the necessary information for the machine to continue its computations. This also carries the added difficulty of handling interruptable operations which is why we transform all of the code into continuations letting us stop, resume and fork any machine as needed.

With that said, I decided to tackle this in an OOP style with Java. The architecture itself is rather simple as it is pretty much modelled 1:1 with what I said before but with a couple of needed classes that make it just a tad more complex to follow. Most of the code has been commented (even if that is an anti-OOP choice) to help track why things are performed the way they are. 


### Implementation
---

The project's "flow" goes as follows: 

1. [Probabilistic Program](./src/main/java/probabilistic_program/) <br>
    We can execute a program under 3 different controllers:

    - Sequential Monte Carlo (SMC)
    - Single-Site Metropolis Hasting (SSMH)
    - Likelihood Weighting (LW)

    Each program is handled differently thus needing different parameters for each case, but it overall constructs the specific controller + necessary machines and then runs the inference algorithm, note that the LISP code itself can always be the same as it is detached from the stochastic side.



2. [Machine](./src/main/java/machine/) <br>
    
    The program gets parsed by the [Parser](./src/main/java/utils/Parser.java) and transformed into a recursive [Form](./src/main/java/forms/README.md) type that helps unify the program's type. This would not be necessary in a dynamically typed program such as Smalltalk/Python and was a significant downside of using Java. 

    The machine gets initalized by converting the Form program into an Ev continuation. <br>
    
    The machine(s) evaluate all deterministic instuctions when possible and stop execution when they run into any `sample`/`observe` expressions at which point they return a [Message](./src/main/java/messages/README.md) handled by the controller. These instructions must be capable of being interrupted at any point so they are restructured as continuations.
 



3. [Controller](./src/main/java/controllers/) <br>
    
    As previously stated we have 3 subclasses of controllers with different handling policies per stochastic instruction. <br>
    They all share a `sampleFrom(Distribution, RandomNumberGenerator)` and `logProb(Distribution, Observed_Value)` which is defined in the super class Controller as they do not differ per controller type.

> Side note: Techincally the machine (along with it's stack and current instructionK) is the continuation. However, I decided to treat the "stoppable instructions" as continuations and the machine as a context provider/orchestrator.