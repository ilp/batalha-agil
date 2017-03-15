package batalhaagil.ufrpe.iversonluis.batalhaagil.bluetooth;

/**
 * Created by Iverson Luís on 08/02/2017.
 */

public class BluetoothAdapterInfo {
    private String name;
    private String address;

    public BluetoothAdapterInfo(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
