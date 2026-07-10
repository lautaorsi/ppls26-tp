package machine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import forms.Form;
import forms.FormList;
import forms.FormSymbol;
import instructions.continuations.Continuation;
import instructions.continuations.Discard;
import instructions.continuations.Ev;
import messages.ContinueMessage;
import messages.DoneMessage;
import messages.Message;
import utils.Closure;
import utils.Parser;

public class Machine{

    private ArrayDeque<Continuation> continuationStack;
    private ArrayDeque<Object> valueStack;
    private HashMap<String,Object> environment;
    private Random rng;
    private float logW;


    private Machine(ArrayDeque<Continuation> continuationStack, ArrayDeque<Object> valueStack, HashMap<String,Object> environment, Random rng, float logW){
        this.continuationStack = continuationStack; //At instantiation the continuationStack will have the mainEv continuation (as it is only called in the initial_machine method)
        this.valueStack = valueStack;
        this.environment = environment;
        this.rng = rng;
        this.logW = logW;
        
    }

    public static Machine initial_machine(String program, Random rng){
        HashMap<String,Object> genv = new HashMap<String,Object>();
        Form main = null;

        ArrayList<Form> parsedProgram = Parser.parse(program);  //We parse it into a form

        //We create an environment containing closures corresponding to all function declared beforer the main program
        for(int i = 0; i < parsedProgram.size(); i++){
            Form form = parsedProgram.get(i);

            if(form instanceof FormList formList && formList.size() != 0   
            && formList.get(0) instanceof FormSymbol formSymbol
            && formSymbol.text().equals("defn")){   //If all these conditions are met then it's a function definition

                
                String name = ((FormSymbol) formList.get(1)).text();    
                ArrayList<Form> params = ((FormList) formList.get(2)).elements();
                ArrayList<Form> body = new ArrayList<>(formList.elements().subList(3, formList.size()));
                //In which case we create the closure and add it to the global env
                genv.put(name, new Closure(params, body, genv));
            }
            else{
                main = form;
            }
        }

        ArrayList<Object> emptyAddresses = new ArrayList<Object>();
        
        //The main continuation that has the closures and main code 
        Ev mainEv = new Ev(main, genv, emptyAddresses);

        ArrayDeque<Continuation> continuationStack = new ArrayDeque<Continuation>();
        continuationStack.add(mainEv);

        ArrayDeque<Object> valueStack = new ArrayDeque<>();

        return new Machine(continuationStack, valueStack, genv, rng, 0.0f);
    }

    public Machine fork(Random seed){ //Foking copies the continuation and value stacks as well as the environment, but can still have it's unique seed (otherwise it'd produce the same results)
        ArrayDeque<Continuation> continuationStackCopy = new ArrayDeque<Continuation>(this.continuationStack);
        ArrayDeque<Object> valueStackCopy = new ArrayDeque<Object>(this.valueStack);
        HashMap<String, Object> environmentCopy = new HashMap<String,Object>(this.environment);

        return new Machine(continuationStackCopy, valueStackCopy, environmentCopy, seed, logW);
    }


    public Object getNextValue(){
        return valueStack.pop();
    }

    public Float getLogW(){
        return this.logW;
    }

    public Random getRNG(){
        return this.rng;
    }


    public void increaseLogW(float value){
        this.logW += value;
    }

    public void pushValue(Object value){
        valueStack.push(value);
    }

    public void pushContinuation(Continuation continuation){
        continuationStack.push(continuation);
    }

    public void push_body(ArrayList<Form> body, HashMap<String, Object> environment, ArrayList<Object> address){
        ArrayList<Continuation> seq = new ArrayList<>();

        Ev ev;
        Discard discard = new Discard();

        for(int i = 0; i < body.size() - 1; i++){//We add all instructions but the last with a discard as it is not a "return" value
            ArrayList<Object> newAddress = new ArrayList<Object>(address);
            newAddress.add("body");
            newAddress.add(i);
            ev = new Ev(body.get(i), environment, newAddress);
            seq.add(ev);
            seq.add(discard);
        }

        ArrayList<Object> newAddress = new ArrayList<Object>(address);
        newAddress.add("body");
        newAddress.add(body.size() - 1);
        ev = new Ev(body.get(body.size() - 1), environment, newAddress);
        seq.add(ev);//The last one is the only w/o a discard

        for(int i = seq.size() - 1; i >= 0; i--){   //We add the evaluate instructions to the continuation stack
            continuationStack.push(seq.get(i));
        }
    }


    public Message resume(){
        Message message;

        while (! continuationStack.isEmpty()) {
        
            Continuation nextContinuation = continuationStack.pop();

            message = nextContinuation.executeOn(this);
        
            if(message instanceof ContinueMessage){
                continue;
            }
            else{
                return message;
            }
        
            
        
        }

        //Once we run out of instructions we have the final value in the stack, retun that and self
        message = new DoneMessage(valueStack.peek(), this);
        return message;
    }


    




}