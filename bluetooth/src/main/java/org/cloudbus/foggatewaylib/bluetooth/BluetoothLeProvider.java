package org.cloudbus.foggatewaylib.bluetooth;

import android.bluetooth.BluetoothGatt;

import org.cloudbus.foggatewaylib.core.AndroidProvider;
import org.cloudbus.foggatewaylib.core.Data;

import java.util.UUID;

/**
 * Base class for providers dealing with Bluetooth Low-Energy.
 *
 * @param <T> the input data type
 * @param <S> the output data type
 *
 * @author Riccardo Mancini
 */
public abstract class BluetoothLeProvider<T extends Data, S extends Data>
        extends AndroidProvider<T, S> {

    private BluetoothLeHandler bluetoothLeHandler;

    private UUID service;
    private UUID characteristic;
    private BluetoothGatt gatt;

    private static int attachedBluetoothProviders = 0;

    /**
     * Constructs a provider that handles callbacks for the given GATT server, service and
     * characteristic.
     *
     * @param gatt the GATT server or {@code null} if all GATT servers should be selected.
     * @param service the service or {@code null} if all services in the given GATT server(s)
     *                should be selected.
     * @param characteristic the characteristic or {@code null} if all characteristics in the given
     *                       service(s) should be selected.
     * @param inputType the type of the input data.
     * @param outputType the type of the output data.
     */
    public BluetoothLeProvider(BluetoothGatt gatt, UUID service, UUID characteristic,
                               Class<T> inputType, Class<S> outputType) {
        super(inputType, outputType);
        this.service = service;
        this.characteristic = characteristic;
        this.gatt = gatt;
    }

    /**
     * Returns a reference to the {@link BluetoothLeHandler} instance.
     *
     * @see BluetoothLeHandler#getInstance()
     */
    public BluetoothLeHandler getBluetoothLeHandler() {
        if (bluetoothLeHandler == null)
            bluetoothLeHandler = BluetoothLeHandler.getInstance();
        return bluetoothLeHandler;
    }

    /**
     * Returns the service.
     */
    public UUID getService() {
        return service;
    }

    /**
     * Returns the characteristic.
     */
    public UUID getCharacteristic() {
        return characteristic;
    }

    /**
     * Returns the GATT server.
     */
    public BluetoothGatt getGatt() {
        return gatt;
    }

    /**
     * Increments the number of bluetooth providers attached to the {@link BluetoothLeHandler}
     * instance.
     */
    private static synchronized int incrementAttachedBluetoothProviders(){
        attachedBluetoothProviders++;
        return attachedBluetoothProviders;
    }

    /**
     * Decrements the number of bluetooth providers attached to the {@link BluetoothLeHandler}
     * instance.
     */
    private static synchronized int decrementAttachedBluetoothProviders(){
        attachedBluetoothProviders--;
        return attachedBluetoothProviders;
    }

    /**
     * When the provider is attached to an {@link org.cloudbus.foggatewaylib.core.ExecutionManager}
     * and if this is the first {@link BluetoothLeProvider}, sets the {@link BluetoothLeHandler}
     * {@link android.content.Context} to the one of the
     * {@link org.cloudbus.foggatewaylib.core.ExecutionManager}.
     */
    @Override
    public void onAttach() {
        if (incrementAttachedBluetoothProviders() == 1)
            getBluetoothLeHandler().setContext(getContext());
    }

    /**
     * When the provider is detached from an {@link org.cloudbus.foggatewaylib.core.ExecutionManager}
     * and if this is the last {@link BluetoothLeProvider}, sets the {@link BluetoothLeHandler}
     * {@link android.content.Context} to null.
     */
    @Override
    public void onDetach() {
        if (decrementAttachedBluetoothProviders() == 0)
            getBluetoothLeHandler().setContext(null);
    }

    /**
     * Returns the {@link BluetoothLeHandler.ServiceCharacteristicPair} handled by this server.
     */
    public BluetoothLeHandler.ServiceCharacteristicPair getHandledCharacteristic(){
        return new BluetoothLeHandler.ServiceCharacteristicPair(service, characteristic);
    }
}
