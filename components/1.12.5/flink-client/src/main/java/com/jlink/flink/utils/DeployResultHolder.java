package com.jlink.flink.utils;

public class DeployResultHolder {
    private static final ThreadLocal<DeployResult> DEPLOY_RESULT_THREAD_LOCAL=new ThreadLocal<>();

    public static synchronized DeployResult get(){
        if(DEPLOY_RESULT_THREAD_LOCAL.get()==null){
            DEPLOY_RESULT_THREAD_LOCAL.set(new DeployResult());
        }
        return DEPLOY_RESULT_THREAD_LOCAL.get();
    }
    public static synchronized void clear(){
        DEPLOY_RESULT_THREAD_LOCAL.remove();
    }
}
