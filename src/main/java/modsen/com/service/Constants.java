package modsen.com.service;

public enum Constants {
    CONFIG_FILE("./src/main/resources/config/config.properties");

    private final String info;

    Constants(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
