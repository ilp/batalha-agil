package batalhaagil.ufrpe.iversonluis.batalhaagil.models;

import java.util.Date;
/**
 * Created by Iverson Luís on 14/02/2017.
 */

public class MapMessage {
    private String responseEscolha;
    private String[] responseMapa;
    private String responseUser;
    private long responseTime;

    public MapMessage(String responseEscolha, String [] responseMapa, String responseUser) {
        this.responseMapa = responseMapa;
        this.responseEscolha = responseEscolha;
        this.responseUser = responseUser;
        this.responseTime = new Date().getTime();
    }

    public String getResponseEscolha() {
        return responseEscolha;
    }

    public void setResponseEscolha(String responseEscolha) {
        this.responseEscolha = responseEscolha;
    }

    public String getResponseUser() {
        return responseUser;
    }

    public void setResponseUser(String responseUser) {
        this.responseUser = responseUser;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}
