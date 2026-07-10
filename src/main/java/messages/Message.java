package messages;



public interface Message{
    

    default boolean isObserve(){
        return false;
    }

    default boolean isDone(){
        return false;
    }

    default boolean isSample(){
        return false;
    }

}








