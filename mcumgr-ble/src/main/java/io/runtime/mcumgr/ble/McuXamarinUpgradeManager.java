package io.runtime.mcumgr.ble;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import androidx.annotation.NonNull;
import io.runtime.mcumgr.McuMgrTransport;
import io.runtime.mcumgr.dfu.FirmwareUpgradeCallback;
import io.runtime.mcumgr.dfu.FirmwareUpgradeManager;
import io.runtime.mcumgr.dfu.model.McuMgrImageSet;

public class McuXamarinUpgradeManager {
    private McuXamarinLogger loggerDelegate;
    private McuMgrImageSet mcuMgrPackage;
    // private McuMgrSuitEnvelope envelope;
    private FirmwareUpgradeManager dfuManager;

    public McuXamarinUpgradeManager(@NonNull Context context, @NonNull BluetoothDevice device, FirmwareUpgradeCallback firmwareUpgradeDelegate, McuXamarinLogger loggerDelegate) {

        McuMgrTransport transporter = new McuMgrBleTransport(context, device);
        this.dfuManager = new FirmwareUpgradeManager(transporter, firmwareUpgradeDelegate);
        this.loggerDelegate = loggerDelegate;
        loggerDelegate.OnLog("McuXamarinUpgradeManager init() completed");
    }

    public void startUpgrade(@NonNull final byte[] data) throws Exception {
        loggerDelegate.OnLog("McuXamarinUpgradeManager startUpgrade()");
        loadImageFileFromData(data);
        loggerDelegate.OnLog("McuXamarinUpgradeManager loadImageFileFromUri() completed");
        start();
    }

    private void loadImageFileFromData(@NonNull final byte[] data) {
        this.mcuMgrPackage = null;
        // this.envelope = null;

        try {
            this.mcuMgrPackage = new McuMgrImageSet().add(data);
        } catch (final Exception e) {
            /*try {
                final ZipPackage zip = new ZipPackage(data);
                images = zip.getBinaries();
            } catch (final Exception e1) {
                errorLiveData.setValue(new McuMgrException("Invalid image file."));
                return;
            }*/
            loggerDelegate.OnErrorLog("envelope not supported");
        }
    }

    private void start() {
        startFirmwareUpgrade(this.mcuMgrPackage);
    }

    private void startFirmwareUpgrade(McuMgrImageSet mcuMgrPackage) {
        boolean eraseSettings = true;

        try {
            dfuManager.setMode(FirmwareUpgradeManager.Mode.CONFIRM_ONLY);
            dfuManager.setEstimatedSwapTime(10000);
            dfuManager.start(mcuMgrPackage, eraseSettings);
        } catch (Exception e) {
            loggerDelegate.OnErrorLog("startFirmwareUpgrade(mcuMgrPackage) error()");
        }
    }

    /*private void startFirmwareUpgrade(McuMgrSuitEnvelope envelope) {
        try {
            String sha256Hash = envelope.getDigest().getHash(HashAlgorithm.SHA256);
            if (sha256Hash == null) {
                throw new McuMgrSuitParseError("Supported algorithm not found");
            }

            dfuManagerConfiguration.setSuitMode(true);
            dfuManagerConfiguration.setUpgradeMode(UpgradeMode.UPLOAD_ONLY);
            dfuManager.start(sha256Hash, envelope.getData(), dfuManagerConfiguration);
        } catch (Exception e) {
            loggerDelegate.OnErrorLog("startFirmwareUpgrade(envelope) error()");
        }
    }*/
}
