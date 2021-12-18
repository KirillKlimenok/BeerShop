package modsen.com.service.JsonMapper;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface GetObjFromJson {
    <T> T getObj(String json, Class<T> tClass) throws JsonProcessingException;
}
