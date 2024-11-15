package scheduling.policies;

import scheduler.processing.SimpleProcess;

public class procesadorFCFS extends Thread{
    SimpleProcess proceso;
    public procesadorFCFS(SimpleProcess proceso){
        this.proceso = proceso;
    }
    @Override
    public void run(){
        
    }
}
