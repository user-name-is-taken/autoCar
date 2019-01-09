package com.example.non_admin.picar;

/**
 * This abstract class is extended sensor and Input services.
 *
 * When these services have data to provide they send that data to a MikeExecutor, which
 * runs a related thread in the background.
 *
 */
public abstract class MikeProvider{
    MikeExecutor mExecutor;

    protected MikeProvider(MikeExecutor mExecutor){
        this.mExecutor = mExecutor;
    }

    protected void provide(String data){
        mExecutor.execute(data);
    }
}
