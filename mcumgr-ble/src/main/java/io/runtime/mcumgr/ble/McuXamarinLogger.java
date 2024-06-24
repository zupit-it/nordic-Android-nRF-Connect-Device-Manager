package io.runtime.mcumgr.ble;

public interface McuXamarinLogger {

    void OnLog(String message);
    void OnErrorLog(String message);
}
